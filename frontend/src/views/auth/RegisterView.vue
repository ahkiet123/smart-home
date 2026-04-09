<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";
import RegisterOtpForm from "../../components/auth/RegisterOtpForm.vue";

const router = useRouter();

const step = ref(1);
const fullName = ref("");
const email = ref("");
const password = ref("");
const confirm = ref("");
const loading = ref(false);
const showPassword = ref(false);
const showConfirm = ref(false);

const parseApiResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  }

  const text = await response.text();
  return { message: text };
};

const submitRegister = async () => {
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

    const result = await parseApiResponse(response);

    if (!response.ok) {
      alert(result.message || "Đăng ký thất bại");
      return;
    }

    alert(
      "Đăng ký thông tin thành công. Vui lòng nhập OTP đã gửi tới email của bạn",
    );
    step.value = 2;
  } catch (error) {
    alert("Lỗi server");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const handleRegistered = () => {
  alert("Đăng ký thành công!");
  router.push("/login");
};

const goToLogin = () => router.push("/login");
</script>

<template>
  <div id="auth-view">
    <div class="auth-container">
      <div v-if="step === 1" class="auth-form">
        <h2>Tạo Tài Khoản</h2>

        <form @submit.prevent="submitRegister">
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
            Đã có tài khoản?
            <a @click="goToLogin" class="ps-1"> Đăng nhập ngay</a>
          </div>
        </form>
      </div>

      <RegisterOtpForm
        v-else
        :full-name="fullName"
        :email="email"
        :password="password"
        @verified="handleRegistered"
      />
    </div>
  </div>
</template>
