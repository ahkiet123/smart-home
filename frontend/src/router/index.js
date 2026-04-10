import { createRouter, createWebHistory } from "vue-router";

// Import các trang Auth
import LoginView from "../views/auth/LoginView.vue";
import RegisterView from "../views/auth/RegisterView.vue";
import ForgotPasswordView from "../views/auth/ForgotPasswordView.vue";

// Import các trang Dashboard
import HomeView from "../views/home.vue";
import ElectricityCalculatorView from "../views/ElectricityCalculatorView.vue";

const routes = [
    {
        path: "/",
        redirect: "/login",
    },
    {
        path: "/login",
        name: "Login",
        component: LoginView,
    },
    {
        path: "/register",
        name: "Register",
        component: RegisterView,
    },
    {
        path: "/forgot-password",
        name: "ForgotPassword",
        component: ForgotPasswordView,
    },
    {
        path: "/home",
        name: "Home",
        component: HomeView,
        beforeEnter: (to, from, next) => {
            const token = localStorage.getItem("token");
            if (!token) {
                next("/login");
            } else {
                next();
            }
        },
    },
    {
        path: "/calculator",
        name: "Calculator",
        component: ElectricityCalculatorView,
        beforeEnter: (to, from, next) => {
            const token = localStorage.getItem("token");
            if (!token) {
                next("/login");
            } else {
                next();
            }
        },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;