<script setup>
import { ref } from 'vue'

import ForgotEmailForm from '../../components/auth/ForgotEmailForm.vue'
import OtpForm from '../../components/auth/OtpForm.vue'
import ResetPasswordForm from '../../components/auth/ResetPasswordForm.vue'

const step = ref(1)
const email = ref('')
const otp = ref('')

const handleEmail = (e) => {
  email.value = e
  step.value = 2
}

const handleOtp = (o) => {
  otp.value = o   // ✅ FIX
  step.value = 3
}
</script>

<template>
  <ForgotEmailForm v-if="step === 1" @next="handleEmail" />

  <OtpForm v-if="step === 2" :email="email" @next="handleOtp" />

  <ResetPasswordForm
    v-if="step === 3"
    :email="email"
    :otp="otp"
  />
</template>