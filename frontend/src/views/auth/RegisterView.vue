<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";

const router = useRouter();

const fullName = ref("");
const email = ref("");
const password = ref("");
const confirm = ref("");
const loading = ref(false);
const showPassword = ref(false);
const showConfirm = ref(false);

const doRegister = async () => {
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;

  if (!passwordRegex.test(password.value)) {
    alert("Mật khẩu phải có chữ hoa, chữ thường, số, ký tự đặc biệt");
    return;
  }

  if (password.value !== confirm.value) {
    alert("Mật khẩu không khớp");
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        fullName: fullName.value,
        email: email.value,
        password: password.value,
      }),
    });

    const text = await response.text();

    if (!response.ok) {
      if (text.includes("Email address already in use")) {
        alert("Email đã tồn tại!");
      } else {
        alert("Đăng ký thất bại: " + text);
      }
      return;
    }

    alert("Đăng ký thành công!");

    // 👉 quay về login
    router.push("/login");
  } catch (error) {
    alert("Lỗi server");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const goToLogin = () => router.push("/login");
</script>

<template>
  <div id="auth-view">
    <div class="auth-container">
      <div class="auth-form">
        <h2>Tạo Tài Khoản</h2>

        <form @submit.prevent="doRegister">
          <div class="form-group">
            <label>Họ và Tên</label>
            <input
              type="text"
              v-model="fullName"
              required
              placeholder="Nhập họ và tên"
            />
          </div>
          <div class="form-group">
            <label>Email</label>
            <input
              type="email"
              v-model="email"
              required
              placeholder="Nhập email của bạn"
            />
          </div>
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
          <div class="form-group">
            <label>Xác nhận mật khẩu</label>
            <div style="position: relative">
              <input
                v-model="confirm"
                :type="showConfirm ? 'text' : 'password'"
                required
                placeholder="Nhập lại mật khẩu"
              />
              <i
                class="fa-solid"
                :class="showConfirm ? 'fa-eye-slash' : 'fa-eye'"
                @click="showConfirm = !showConfirm"
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

          <button class="btn-auth" :disabled="loading">
            {{ loading ? "Đang đăng ký..." : "Đăng ký" }}
          </button>

          <div class="links text-center">
            Đã có tài khoản? <a @click="goToLogin" class="ps-1"> Đăng nhập ngay</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
