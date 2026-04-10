import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/auth/LoginView.vue";
import RegisterView from "../views/auth/RegisterView.vue";
import ForgotPasswordView from "../views/auth/ForgotPasswordView.vue";
import DeviceManager from "../views/device/DeviceManager.vue";

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    component: LoginView,
  },
  {
    path: "/register",
    component: RegisterView,
  },
  {
    path: "/forgot-password",
    component: ForgotPasswordView,
  },
  {
    path: "/devices",
    component: DeviceManager,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;