import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/auth/LoginView.vue";
import RegisterView from "../views/auth/RegisterView.vue";
import ForgotPasswordView from "../views/auth/ForgotPasswordView.vue";
import HomeView from "../views/home.vue";
import BlogView from "../views/BlogView.vue";

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
    path: "/home",
    component: HomeView,
    beforeEnter: () => {
      const token = localStorage.getItem("token");
      if (!token) {
        return "/login";
      }

      return true;
    },
  },
  {
    path: "/blog",
    component: BlogView,
    beforeEnter: () => {
      const token = localStorage.getItem("token");
      if (!token) {
        return "/login";
      }

      return true;
    },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
