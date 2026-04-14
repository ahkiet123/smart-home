<script setup>
import { ref } from "vue";
import API_BASE from "../../services/api";

const props = defineProps(["email", "otp"]);

const password = ref("");
const confirm = ref("");
const showPassword = ref(false);
const showConfirm = ref(false);

const submit = async () => {
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;

  if (!passwordRegex.test(password.value)) {
    return alert("Mật khẩu yếu");
  }

  if (password.value !== confirm.value) {
    return alert("Không khớp");
  }

  try {
    const res = await fetch(`${API_BASE}/auth/reset-password`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: props.email,
        otp: props.otp,
        newPassword: password.value,
      }),
    });

    const data = await res.json();

    if (res.ok) {
      alert("Đổi mật khẩu thành công!");

      // 👉 quay về login
      window.location.href = "/login";
    } else {
      alert(data.message);
    }
  } catch (e) {
    alert("Lỗi server");
  }
};
</script>

<template>
  <div id="auth-view">
    <div class="auth-container">
      <div class="auth-form">
        <form @submit.prevent="submit">
          <div class="form-group">
            <label>Mật khẩu mới</label>
            <div style="position: relative">
              <input
                type="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                placeholder="Tạo mật khẩu mới"
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

          <button class="btn-auth">Xác nhận</button>
        </form>
      </div>
    </div>
  </div>
</template>
