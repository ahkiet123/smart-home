<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import API_BASE from "../../services/api";

const router = useRouter();
const emit = defineEmits(["next"]);

const email = ref("");
const loading = ref(false);

const submit = async () => {
  if (!email.value) return alert("Nhập email");

  loading.value = true;

  try {
    const res = await fetch(`${API_BASE}/auth/forgot-password`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: email.value }),
    });

    const data = await res.json();

    if (res.ok) {
      alert("OTP đã gửi!");
      emit("next", email.value);
    } else {
      alert(data.message);
    }
  } catch (e) {
    alert("Lỗi server");
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
        <p class="subtitle">
          Đừng lo lắng! Hãy nhập email bạn đã đăng ký, chúng tôi sẽ gửi mã OTP
          để đặt lại mật khẩu.
        </p>
        <form @submit.prevent="submit">
        <div class="form-group">
            <label>Email của bạn</label>
            <input
              v-model="email"
              id="forgot-email"
              required
              placeholder="Nhập email của bạn"
            />
          </div>
          <button class="btn-auth">
            {{ loading ? "Đang gửi..." : "Gửi OTP" }}
          </button>

           <div class="links text-center">
            <a @click="goToLogin"">Quay lại Đăng nhập</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
