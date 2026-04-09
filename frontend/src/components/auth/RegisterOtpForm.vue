<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";

const router = useRouter();
const props = defineProps({
  fullName: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    required: true,
  },
  password: {
    type: String,
    required: true,
  },
});
const emit = defineEmits(["verified"]);

const otp = ref("");
const loading = ref(false);
const sendingOtp = ref(false);

const parseApiResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  }

  const text = await response.text();
  return { message: text };
};

const onOtpInput = (event) => {
  otp.value = event.target.value.replace(/\D/g, "").slice(0, 6);
};

const sendOtp = async () => {
  sendingOtp.value = true;

  try {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        fullName: props.fullName,
        email: props.email,
        password: props.password,
      }),
    });

    const result = await parseApiResponse(response);

    if (response.ok) {
      alert(result.message || "OTP đã được gửi lại tới email của bạn");
      return;
    }

    alert(result.message || "Không thể gửi lại OTP");
  } catch (error) {
    alert("Lỗi server");
    console.error(error);
  } finally {
    sendingOtp.value = false;
  }
};

const submit = async () => {
  if (!/^\d{6}$/.test(otp.value)) {
    alert("Vui lòng nhập OTP gồm đúng 6 chữ số");
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(`${API_BASE}/auth/register/verify`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        fullName: props.fullName,
        email: props.email,
        password: props.password,
        otp: otp.value,
      }),
    });

    const result = await parseApiResponse(response);

    if (response.ok) {
      alert(result.message || "Đăng ký thành công");
      emit("verified");
      return;
    }

    alert(result.message || "OTP sai hoặc hết hạn");
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
  <div class="auth-form">
    <h2>Xác Thực Đăng Ký</h2>
    <p class="subtitle">
      Vui lòng nhập mã gồm 6 chữ số vừa được gửi đến email để hoàn tất đăng ký.
    </p>
    <p class="subtitle" style="margin-top: -12px">
      Chưa nhận được mã? Bạn có thể gửi lại OTP ngay bên dưới.
    </p>
    <form @submit.prevent="submit">
      <div class="form-group">
        <label style="text-align: center">Nhập mã bảo mật</label>
        <input
          type="text"
          v-model="otp"
          @input="onOtpInput"
          class="otp-input"
          required
          maxlength="6"
          placeholder="------"
          inputmode="numeric"
          autocomplete="one-time-code"
        />
      </div>
      <button class="btn-auth" :disabled="loading || sendingOtp">
        {{ loading ? "Đang xác nhận..." : "Xác nhận đăng ký" }}
      </button>
      <button
        type="button"
        class="btn-auth"
        :disabled="sendingOtp || loading"
        style="margin-top: 10px; background: #4a5568"
        @click="sendOtp"
      >
        {{ sendingOtp ? "Đang gửi lại..." : "Gửi lại OTP" }}
      </button>
      <div class="links text-center">
        <a @click="goToLogin">Hủy bỏ</a>
      </div>
    </form>
  </div>
</template>
