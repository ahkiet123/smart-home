import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/auth/LoginView.vue";
import RegisterView from "../views/auth/RegisterView.vue";
import ForgotPasswordView from "../views/auth/ForgotPasswordView.vue";
import HomeView from "../views/home.vue";
import BlogView from "../views/BlogView.vue";
import RoomListView from "../views/room/RoomListView.vue";
import RoomDetailView from "../views/room/RoomDetailView.vue";

const requireAuth = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    return "/login";
  }

  return true;
};

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
    beforeEnter: requireAuth,
  },
  {
    path: "/homes",
    redirect: "/rooms",
  },
  {
    path: "/rooms",
    component: RoomListView,
    beforeEnter: requireAuth,
  },
  {
    path: "/rooms/:id",
    component: RoomDetailView,
    beforeEnter: requireAuth,
  },
  {
    path: "/blog",
    component: BlogView,
    beforeEnter: requireAuth,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
