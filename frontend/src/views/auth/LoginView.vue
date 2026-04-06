<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";

const router = useRouter();

const email = ref("");
const password = ref("");
const loading = ref(false);
const showPassword = ref(false);

const parseApiResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  }

  const text = await response.text();
  return { message: text };
};

const doLogin = async () => {
  if (!email.value || !password.value) {
    alert("Vui lòng nhập đầy đủ email và mật khẩu");
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email.value,
        password: password.value,
      }),
    });

    const result = await parseApiResponse(response);

    if (!response.ok) {
      alert(result.message || "Đăng nhập thất bại");
      return;
    }

    const token = result?.data?.token;

    if (!token) {
      alert("Không nhận được token từ server");
      return;
    }

    // ✅ lưu token
    localStorage.setItem("token", token);
    localStorage.setItem("userEmail", email.value);

    // ✅ chuyển trang
    window.location.href = "/";
  } catch (error) {
    console.error(error);
    alert("Không thể kết nối tới server");
  } finally {
    loading.value = false;
  }
};

// 👉 điều hướng
const goToForgot = () => router.push("/forgot-password");
const goToRegister = () => router.push("/register");
</script>

<template>
  <div id="auth-view">
    <div class="auth-container">
      <div class="auth-form">
        <h2>Đăng Nhập</h2>

        <form @submit.prevent="doLogin">
          <!-- EMAIL -->
          <div class="form-group">
            <label>Email</label>
            <input
              v-model="email"
              type="email"
              required
              placeholder="Nhập email của bạn"
            />
          </div>

          <!-- PASSWORD -->
          <div class="form-group">
            <label>Mật khẩu</label>

            <div style="position: relative">
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                placeholder="Nhập mật khẩu"
              />

              <i
                class="fa-solid"
                :class="showPassword ? 'fa-eye-slash' : 'fa-eye'"
                @click="showPassword = !showPassword"
                style="
                  position: absolute;
                  right: 15px;
                  top: 50%;
                  transform: translateY(-50%);
                  cursor: pointer;
                "
              ></i>
            </div>
          </div>

          <!-- BUTTON -->
          <button class="btn-auth" :disabled="loading">
            {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
          </button>

          <!-- LINKS -->
          <div class="links">
            <a @click="goToForgot">Quên mật khẩu?</a>
            <a @click="goToRegister">Đăng ký ngay</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
