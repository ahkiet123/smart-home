<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";

const router = useRouter();
const props = defineProps(["email"]);
const emit = defineEmits(["next"]);

const otp = ref("");

const submit = async () => {
  try {
    const response = await fetch(`${API_BASE}/auth/verify-otp`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: props.email,
        otp: otp.value,
      }),
    });

    const result = await response.json();

    if (response.ok) {
      alert("OTP đúng!");
      emit("next", otp.value);
    } else {
      alert(result.message || "OTP sai hoặc hết hạn");
    }
  } catch (error) {
    alert("Lỗi server");
    console.error(error);
  }
};

const goToLogin = () => router.push("/login");
</script>

<template>
  <div id="auth-view">
    <div class="auth-container">
      <div class="auth-form">
        <h2>Xác Thực OTP</h2>
        <p class="subtitle">
          Vui lòng nhập mã gồm 6 chữ số vừa được gửi đến email của bạn.
        </p>
        <form @submit.prevent="submit">
          <div class="form-group">
            <label style="text-align: center">Nhập mã bảo mật</label>
            <input
              type="text"
              v-model="otp"
              class="otp-input"
              required
              maxlength="6"
              placeholder="------"
            />
          </div>
          <button class="btn-auth">Xác nhận</button>
          <div class="links text-center">
            <a @click="goToLogin">Hủy bỏ</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
