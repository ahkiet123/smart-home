<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import API_BASE from "../services/api";

const router = useRouter();
const route = useRoute();
const currentDate = ref(
  new Date().toLocaleDateString("vi-VN", {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
  }),
);

// Quản lý trạng thái tab nội bộ
const currentTab = ref("overview");
const VALID_TABS = ["overview", "energy-page", "profile-page"];

const profileLoading = ref(false);
const profileSaving = ref(false);
const profileError = ref("");
const profileMessage = ref("");
const profileForm = ref({
  fullName: "",
  email: "",
  phone: "",
  address: "",
  city: "",
  country: "",
});
const originalProfile = ref(null);

const energyLoading = ref(false);
const energyError = ref("");
const energyMessage = ref("");
const energyFromDate = ref("");
const energyToDate = ref("");
const energyGroupBy = ref("room");
const energyPresetDays = ref(7);
const energyData = ref(null);

const quickProfileOpen = ref(false);
const quickOldPassword = ref("");
const quickNewPassword = ref("");
const quickPasswordLoading = ref(false);
const quickPasswordError = ref("");
const quickPasswordMessage = ref("");

const resolveTabFromQuery = () => {
  const queryTab = route.query.tab;
  if (typeof queryTab === "string" && VALID_TABS.includes(queryTab)) {
    return queryTab;
  }
  return "overview";
};

const toIsoDate = (value) => {
  const year = value.getFullYear();
  const month = `${value.getMonth() + 1}`.padStart(2, "0");
  const day = `${value.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const applyEnergyPreset = (days) => {
  const to = new Date();
  const from = new Date();
  from.setDate(to.getDate() - (days - 1));

  energyFromDate.value = toIsoDate(from);
  energyToDate.value = toIsoDate(to);
  energyPresetDays.value = days;
};

const formatNumber = (value) => Number(value || 0).toFixed(2);
const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));

const formatDateLabel = (value) => {
  if (!value) return "";
  const parsed = new Date(`${value}T00:00:00`);
  if (Number.isNaN(parsed.getTime())) return String(value);
  return parsed.toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit" });
};

const profileInitials = computed(() => {
  const name = (profileForm.value.fullName || "").trim();
  if (!name) return "AD";

  const parts = name.split(/\s+/);
  if (parts.length >= 2) {
    return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
  }
  return name.slice(0, 2).toUpperCase();
});

const energySeries = computed(() => {
  if (!Array.isArray(energyData.value?.dailySeries)) return [];
  return energyData.value.dailySeries;
});

const energyBreakdown = computed(() => {
  if (!Array.isArray(energyData.value?.breakdown)) return [];
  return energyData.value.breakdown;
});

const energyCostPerKwh = computed(() => {
  const totalKwh = Number(energyData.value?.totalKwh || 0);
  const totalCost = Number(energyData.value?.totalEstimatedCost || 0);
  if (totalKwh > 0 && totalCost > 0) {
    return totalCost / totalKwh;
  }
  return 2500;
});

const energyMaxKwh = computed(() => {
  if (energySeries.value.length === 0) return 0;
  return Math.max(...energySeries.value.map((point) => Number(point.kwh || 0)));
});

const energyBars = computed(() => {
  const max = energyMaxKwh.value;

  return energySeries.value.map((point) => {
    const kwh = Number(point.kwh || 0);
    const estimatedCost = Number(point.estimatedCost || 0);
    const height = max > 0 ? Math.max(8, (kwh / max) * 100) : 8;

    return {
      date: point.date,
      label: formatDateLabel(point.date),
      kwh,
      estimatedCost,
      height,
    };
  });
});

const energyYAxisTicks = computed(() => {
  const max = energyMaxKwh.value;
  const anchor = max > 0 ? max : 1;
  return [1, 0.75, 0.5, 0.25, 0].map((ratio) =>
    Number((anchor * ratio).toFixed(2)),
  );
});

const energyBreakdownRich = computed(() => {
  const baseItems = energyBreakdown.value.map((item) => ({ ...item }));
  const totalKwh = Number(energyData.value?.totalKwh || 0);
  const currentBreakdownTotal = baseItems.reduce(
    (sum, item) => sum + Number(item.kwh || 0),
    0,
  );

  const remainder = totalKwh - currentBreakdownTotal;
  if (remainder > 0.01) {
    baseItems.push({
      id: "others",
      name: "Khác",
      kwh: remainder,
      estimatedCost: remainder * energyCostPerKwh.value,
    });
  }

  const total = baseItems.reduce((sum, item) => sum + Number(item.kwh || 0), 0);

  return baseItems.map((item, index) => {
    const kwh = Number(item.kwh || 0);
    return {
      ...item,
      rank: index + 1,
      kwh,
      percent: total > 0 ? (kwh / total) * 100 : 0,
    };
  });
});

const deltaMeta = computed(() => {
  const delta = Number(energyData.value?.deltaKwh || 0);
  if (delta > 0) {
    return {
      icon: "▲",
      text: "Tăng so với kỳ trước",
      className: "text-rose-600 bg-rose-50 border-rose-100",
    };
  }

  if (delta < 0) {
    return {
      icon: "▼",
      text: "Giảm so với kỳ trước",
      className: "text-emerald-600 bg-emerald-50 border-emerald-100",
    };
  }

  return {
    icon: "•",
    text: "Không thay đổi",
    className: "text-slate-600 bg-slate-50 border-slate-100",
  };
});

const hasEnergyData = computed(() => Boolean(energyData.value?.hasData));

const getToken = () => {
  const token = localStorage.getItem("token");
  if (!token) {
    doLogout();
    throw new Error("Phiên đăng nhập đã hết hạn");
  }
  return token;
};

const parseResponse = async (response) => {
  const contentType = response.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) {
    return { message: "Phản hồi server không hợp lệ" };
  }
  return response.json();
};

const requestAuthApi = async (path, options = {}) => {
  const token = getToken();
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`,
    },
  });

  const payload = await parseResponse(response);

  if (response.status === 401 || response.status === 403) {
    doLogout();
    throw new Error("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
  }

  if (!response.ok) {
    throw new Error(payload?.message || "Có lỗi xảy ra từ server");
  }

  return payload?.data;
};

const loadUserProfile = async () => {
  profileLoading.value = true;
  profileError.value = "";
  profileMessage.value = "";

  try {
    const data = await requestAuthApi("/users/profile");
    profileForm.value = {
      fullName: data?.fullName || "",
      email: data?.email || "",
      phone: data?.phone || "",
      address: data?.address || "",
      city: data?.city || "",
      country: data?.country || "",
    };
    originalProfile.value = { ...profileForm.value };

    if (profileForm.value.email) {
      localStorage.setItem("userEmail", profileForm.value.email);
    }
  } catch (error) {
    profileError.value = error.message || "Không tải được hồ sơ người dùng";
  } finally {
    profileLoading.value = false;
  }
};

const submitUpdateProfile = async () => {
  const fullName = profileForm.value.fullName.trim();
  const phone = profileForm.value.phone.trim();
  const phoneRegex = /^(0|\+84)(3|5|7|8|9)\d{8}$/;

  profileError.value = "";
  profileMessage.value = "";

  if (!fullName) {
    profileError.value = "Họ và tên không được để trống.";
    return;
  }

  if (phone && !phoneRegex.test(phone)) {
    profileError.value = "Số điện thoại không hợp lệ (định dạng VN).";
    return;
  }

  const payload = {
    fullName,
    phone,
    address: profileForm.value.address.trim(),
    city: profileForm.value.city.trim(),
    country: profileForm.value.country.trim(),
  };

  const normalizedCurrent = JSON.stringify(payload);
  const normalizedOriginal = JSON.stringify({
    fullName: originalProfile.value?.fullName?.trim() || "",
    phone: originalProfile.value?.phone?.trim() || "",
    address: originalProfile.value?.address?.trim() || "",
    city: originalProfile.value?.city?.trim() || "",
    country: originalProfile.value?.country?.trim() || "",
  });

  if (normalizedCurrent === normalizedOriginal) {
    profileMessage.value = "Thông tin chưa thay đổi.";
    return;
  }

  profileSaving.value = true;

  try {
    await requestAuthApi("/users/profile", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    profileForm.value.fullName = fullName;
    profileForm.value.phone = phone;
    originalProfile.value = { ...profileForm.value };
    profileMessage.value = "Đã cập nhật hồ sơ thành công.";
  } catch (error) {
    profileError.value = error.message || "Cập nhật hồ sơ thất bại.";
  } finally {
    profileSaving.value = false;
  }
};

const loadDailyEnergyAnalytics = async () => {
  energyLoading.value = true;
  energyError.value = "";
  energyMessage.value = "";

  if (!energyFromDate.value || !energyToDate.value) {
    energyLoading.value = false;
    energyError.value = "Vui lòng chọn khoảng ngày hợp lệ.";
    return;
  }

  if (energyFromDate.value > energyToDate.value) {
    energyLoading.value = false;
    energyError.value = "Từ ngày không được lớn hơn đến ngày.";
    return;
  }

  try {
    const params = new URLSearchParams({
      fromDate: energyFromDate.value,
      toDate: energyToDate.value,
      groupBy: energyGroupBy.value,
    });

    const data = await requestAuthApi(`/energy/analytics/daily?${params.toString()}`);
    energyData.value = data || null;

    if (!data?.hasData) {
      energyMessage.value =
        "Không có dữ liệu trong khoảng thời gian này. Bạn có thể đổi khoảng ngày để xem lại.";
    } else {
      energyMessage.value = "";
    }
  } catch (error) {
    energyError.value =
      error.message || "Không tải được dữ liệu thống kê điện.";
  } finally {
    energyLoading.value = false;
  }
};

// Hàm chuyển đổi tab
const switchDashboardTab = (tabName) => {
  const nextTab = VALID_TABS.includes(tabName) ? tabName : "overview";
  currentTab.value = nextTab;
  router.replace({
    path: "/home",
    query: nextTab === "overview" ? {} : { tab: nextTab },
  });
  if (window.lucide) window.lucide.createIcons();

  if (nextTab === "profile-page") {
    loadUserProfile();
  }
  if (nextTab === "energy-page") {
    loadDailyEnergyAnalytics();
  }
};

// Hàm đăng xuất
const doLogout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("userEmail");
  router.push("/login");
};

// Khai báo các hàm modal/detail để template gọi được (Bạn cần bổ sung logic xử lý cụ thể vào đây)
const showPowerDetail = () => console.log("Chi tiết công suất");
const showTodayDetail = () => console.log("Chi tiết hôm nay");
const showMonthDetail = () => console.log("Chi tiết tháng");
const toggleEditMode = () => console.log("Bật chế độ sửa");
const openAddModal = () => console.log("Mở modal thêm thiết bị");

const resetQuickPasswordState = () => {
  quickOldPassword.value = "";
  quickNewPassword.value = "";
  quickPasswordLoading.value = false;
  quickPasswordError.value = "";
  quickPasswordMessage.value = "";
};

const openQuickProfileModal = () => {
  resetQuickPasswordState();
  quickProfileOpen.value = true;
};

const closeQuickProfileModal = () => {
  quickProfileOpen.value = false;
  resetQuickPasswordState();
};

const submitChangePassword = async () => {
  const oldPassword = quickOldPassword.value.trim();
  const newPassword = quickNewPassword.value.trim();

  quickPasswordError.value = "";
  quickPasswordMessage.value = "";

  if (!oldPassword || !newPassword) {
    quickPasswordError.value = "Vui lòng nhập đầy đủ mật khẩu cũ và mật khẩu mới.";
    return;
  }

  if (oldPassword === newPassword) {
    quickPasswordError.value = "Mật khẩu mới không được trùng mật khẩu cũ.";
    return;
  }

  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;
  if (!passwordRegex.test(newPassword)) {
    quickPasswordError.value =
      "Mật khẩu mới phải có chữ hoa, chữ thường, số, ký tự đặc biệt và tối thiểu 6 ký tự.";
    return;
  }

  quickPasswordLoading.value = true;

  try {
    await requestAuthApi("/auth/change-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ oldPassword, newPassword }),
    });

    quickPasswordMessage.value =
      "Đổi mật khẩu thành công. Bạn sẽ được đăng xuất để đăng nhập lại.";

    setTimeout(() => {
      closeQuickProfileModal();
      doLogout();
    }, 1200);
  } catch (error) {
    quickPasswordError.value = error.message || "Đổi mật khẩu thất bại.";
  } finally {
    quickPasswordLoading.value = false;
  }
};

const setEnergyRangeAndLoad = (days) => {
  applyEnergyPreset(days);
  if (currentTab.value === "energy-page") {
    loadDailyEnergyAnalytics();
  }
};

const applyCustomEnergyRange = () => {
  energyPresetDays.value = null;
  if (currentTab.value === "energy-page") {
    loadDailyEnergyAnalytics();
  }
};

onMounted(() => {
  currentTab.value = resolveTabFromQuery();
  applyEnergyPreset(7);

  // Gán hàm vào window nếu các file script bên ngoài (JS thuần) cần dùng
  window.doLogout = doLogout;
  window.switchDashboardTab = switchDashboardTab;
  window.loadUserProfile = loadUserProfile;
  window.loadDailyEnergyAnalytics = loadDailyEnergyAnalytics;

  if (typeof window.initDashboard === "function") {
    window.initDashboard();
  }

  if (currentTab.value === "profile-page") {
    loadUserProfile();
  }

  if (currentTab.value === "energy-page") {
    loadDailyEnergyAnalytics();
  }

  if (window.lucide) {
    window.lucide.createIcons();
  }
});

watch(
  () => route.query.tab,
  () => {
    currentTab.value = resolveTabFromQuery();
    if (currentTab.value === "profile-page") {
      loadUserProfile();
    }
    if (currentTab.value === "energy-page") {
      loadDailyEnergyAnalytics();
    }
    if (window.lucide) {
      window.lucide.createIcons();
    }
  },
);

onUnmounted(() => {
  delete window.doLogout;
  delete window.switchDashboardTab;
  delete window.loadUserProfile;
  delete window.loadDailyEnergyAnalytics;
  quickProfileOpen.value = false;
});
</script>

<template>
  <div id="dashboard-view" class="h-screen w-full overflow-hidden flex active">
    <aside
      class="hidden lg:flex flex-col w-64 bg-white border-r border-gray-200 z-30"
    >
      <div class="h-16 flex items-center px-6 border-b border-gray-100">
        <i data-lucide="zap" class="text-blue-600 mr-2 w-7 h-7"></i>
        <span
          class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-cyan-500"
        >
          SmartHome
        </span>
      </div>
      <nav class="p-4 space-y-2 flex-1">
        <a
          href="#"
          @click.prevent="switchDashboardTab('overview')"
          :class="[
            currentTab === 'overview'
              ? 'text-blue-700 bg-blue-50'
              : 'text-gray-600 hover:bg-gray-50',
          ]"
          class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="layout-dashboard" class="mr-3 w-5 h-5"></i> Tổng quan
        </a>
        <RouterLink
          to="/rooms"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="home" class="mr-3 w-5 h-5"></i> Phòng ốc
        </RouterLink>
        <a
          href="#"
          @click.prevent="switchDashboardTab('energy-page')"
          :class="[
            currentTab === 'energy-page'
              ? 'text-blue-700 bg-blue-50'
              : 'text-gray-600 hover:bg-gray-50',
          ]"
          class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="activity" class="mr-3 w-5 h-5"></i> Thống kê điện
        </a>
        <RouterLink
          to="/calculator"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-blue-50 hover:text-blue-700 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="calculator" class="mr-3 w-5 h-5"></i> Máy tính điện
        </RouterLink>
        <a
          href="#"
          @click.prevent="switchDashboardTab('profile-page')"
          :class="[
            currentTab === 'profile-page'
              ? 'text-blue-700 bg-blue-50'
              : 'text-gray-600 hover:bg-gray-50',
          ]"
          class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="user" class="mr-3 w-5 h-5"></i> Hồ sơ cá nhân
        </a>
        <RouterLink
          to="/blog"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="book-open" class="mr-3 w-5 h-5"></i> Blog
        </RouterLink>
      </nav>
      <div class="p-4 border-t border-gray-100">
        <a
          href="#"
          @click.prevent="doLogout"
          class="flex items-center px-4 py-3 text-red-600 hover:bg-red-50 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="log-out" class="mr-3 w-5 h-5"></i> Đăng xuất
        </a>
      </div>
    </aside>

    <main class="flex-1 flex flex-col h-screen overflow-hidden relative">
      <header
        class="h-16 flex items-center justify-between px-6 bg-white/80 backdrop-blur-md border-b border-gray-100 sticky top-0 z-10"
      >
        <div class="flex items-center">
          <i
            data-lucide="menu"
            class="w-6 h-6 text-gray-600 lg:hidden mr-4 cursor-pointer"
          ></i>
          <h1 class="text-xl font-semibold hidden sm:block">
            Chào mừng trở lại!
          </h1>
        </div>
        <div class="flex items-center space-x-4">
          <button
            class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full transition-colors relative"
          >
            <i data-lucide="bell" class="w-5 h-5"></i>
            <span
              class="absolute top-1 right-1 w-2.5 h-2.5 bg-red-500 rounded-full border-2 border-white"
            ></span>
          </button>
          <div
            @click="openQuickProfileModal"
            class="w-9 h-9 rounded-full bg-gradient-to-tr from-blue-500 to-purple-500 text-white flex items-center justify-center font-bold shadow-md cursor-pointer"
          >
            {{ profileInitials }}
          </div>
        </div>
      </header>

      <div class="flex-1 overflow-auto p-4 sm:p-8 pb-24">
        <div
          v-if="currentTab === 'overview'"
          class="block animate-in fade-in duration-500"
        >
          <section class="mb-10">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">Gợi ý cho bạn</h2>
              <button
                class="text-sm font-medium text-blue-600 hover:text-blue-700 flex items-center"
              >
                Xem thêm
                <i data-lucide="chevron-right" class="w-4 h-4 ml-1"></i>
              </button>
            </div>
            <div
              class="grid grid-cols-1 md:grid-cols-3 gap-6"
              id="articles-container"
            ></div>
          </section>

          <section class="mb-10">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">
                Quản lý & Tiêu thụ điện
              </h2>
              <span
                class="text-sm font-medium text-gray-500 bg-gray-200/50 px-3 py-1 rounded-full flex items-center"
              >
                <i
                  data-lucide="trending-up"
                  class="w-3.5 h-3.5 mr-1 text-blue-500"
                ></i>
                Nhật ký
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div
                @click="showPowerDetail"
                class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm flex flex-col cursor-pointer hover:border-blue-300 transition-all group"
              >
                <div class="flex justify-between items-start mb-4">
                  <div
                    class="p-3 bg-amber-50 text-amber-600 rounded-xl group-hover:bg-amber-100"
                  >
                    <i data-lucide="activity" class="w-6 h-6"></i>
                  </div>
                  <span
                    class="text-[10px] text-amber-500 font-bold uppercase tracking-wider"
                    >Xem chi tiết</span
                  >
                </div>
                <h3 class="text-gray-500 font-medium mb-1">
                  Công suất sử dụng
                </h3>
                <div class="flex items-baseline space-x-1">
                  <span class="text-3xl font-bold text-gray-800" id="stat-power"
                    >0.00</span
                  >
                  <span class="text-gray-500 font-medium">kW</span>
                </div>
                <p class="text-sm text-gray-400 mt-2" id="stat-active-devices">
                  0 thiết bị
                </p>
              </div>

              <div
                @click="showTodayDetail"
                class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm flex flex-col border-l-4 border-l-blue-500 cursor-pointer hover:border-blue-300 transition-all group"
              >
                <div class="flex justify-between items-start mb-4">
                  <div
                    class="p-3 bg-blue-50 text-blue-600 rounded-xl group-hover:bg-blue-100"
                  >
                    <i data-lucide="zap" class="w-6 h-6"></i>
                  </div>
                  <span
                    class="text-xs font-medium text-blue-600 bg-blue-50 px-2 py-1 rounded-md tracking-wider"
                    >Hôm nay</span
                  >
                </div>
                <h3 class="text-gray-500 font-medium mb-1">Chi phí hôm nay</h3>
                <div class="flex flex-col">
                  <span
                    class="text-3xl font-bold text-gray-800"
                    id="stat-cost-today"
                    >0 ₫</span
                  >
                  <div
                    class="flex items-center text-sm font-medium text-blue-500 mt-1"
                  >
                    <span id="stat-kwh">0.00</span><span class="ml-1">kWh</span>
                  </div>
                </div>
              </div>

              <div
                @click="showMonthDetail"
                class="bg-gradient-to-br from-slate-800 to-slate-900 rounded-2xl p-6 shadow-md flex flex-col text-white cursor-pointer hover:opacity-90 transition-all group"
              >
                <div class="flex justify-between items-start mb-4">
                  <div
                    class="p-3 bg-white/10 text-white rounded-xl backdrop-blur-sm group-hover:bg-white/20"
                  >
                    <i data-lucide="calendar" class="w-6 h-6"></i>
                  </div>
                  <span
                    class="text-xs font-medium text-slate-300 bg-white/10 px-2 py-1 rounded-md tracking-wider"
                    >Dự kiến</span
                  >
                </div>
                <h3 class="text-slate-300 font-medium mb-1">
                  Tổng tiền tháng này
                </h3>
                <div class="flex items-baseline space-x-1 mb-1">
                  <span
                    class="text-3xl font-bold text-white"
                    id="stat-cost-month"
                    >0 ₫</span
                  >
                </div>
                <p class="text-xs text-slate-400 mt-2 opacity-70 italic">
                  * Xem dự báo hóa đơn
                </p>
              </div>
            </div>
          </section>

          <section>
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">
                Thiết bị trong nhà
              </h2>
              <button
                @click="toggleEditMode"
                class="text-sm font-medium flex items-center px-3 py-1.5 rounded-lg transition-colors text-gray-600 hover:bg-gray-100"
              >
                <i data-lucide="edit-2" class="w-4 h-4 mr-1.5"></i>
                <span>Quản lý</span>
              </button>
            </div>
            <div
              class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4"
              id="devices-container"
            ></div>
          </section>
        </div>

        <div
          v-if="currentTab === 'energy-page'"
          class="animate-in slide-in-from-bottom duration-500 max-w-6xl mx-auto space-y-6"
        >
          <div
            class="rounded-3xl p-6 sm:p-8 bg-gradient-to-br from-sky-600 via-blue-600 to-cyan-500 text-white shadow-xl"
          >
            <div class="flex flex-wrap items-start justify-between gap-4">
              <div>
                <div class="text-sm font-medium text-blue-100">Theo dõi điện năng</div>
                <h2 class="text-3xl font-extrabold tracking-tight mt-1">
                  Thống kê điện chi tiết
                </h2>
                <p class="text-blue-50 mt-2 text-sm">
                  Giai đoạn {{ energyFromDate || "--" }} đến {{ energyToDate || "--" }}
                </p>
              </div>

              <div class="flex flex-wrap gap-2">
                <button
                  @click="setEnergyRangeAndLoad(7)"
                  :class="[
                    'px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                    energyPresetDays === 7
                      ? 'bg-white text-blue-700'
                      : 'bg-white/20 text-white hover:bg-white/30',
                  ]"
                >
                  7 ngày
                </button>
                <button
                  @click="setEnergyRangeAndLoad(14)"
                  :class="[
                    'px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                    energyPresetDays === 14
                      ? 'bg-white text-blue-700'
                      : 'bg-white/20 text-white hover:bg-white/30',
                  ]"
                >
                  14 ngày
                </button>
                <button
                  @click="setEnergyRangeAndLoad(30)"
                  :class="[
                    'px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                    energyPresetDays === 30
                      ? 'bg-white text-blue-700'
                      : 'bg-white/20 text-white hover:bg-white/30',
                  ]"
                >
                  30 ngày
                </button>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
            <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-6 gap-3">
              <input
                v-model="energyFromDate"
                type="date"
                class="xl:col-span-1 px-3 py-2 rounded-lg border border-gray-200"
              />
              <input
                v-model="energyToDate"
                type="date"
                class="xl:col-span-1 px-3 py-2 rounded-lg border border-gray-200"
              />
              <select
                v-model="energyGroupBy"
                class="xl:col-span-1 px-3 py-2 rounded-lg border border-gray-200"
              >
                <option value="room">Theo phòng</option>
                <option value="device">Theo thiết bị</option>
              </select>
              <button
                @click="applyCustomEnergyRange"
                :disabled="energyLoading"
                class="xl:col-span-1 px-4 py-2 rounded-lg bg-slate-800 text-white font-medium disabled:opacity-60"
              >
                {{ energyLoading ? "Đang tải..." : "Áp dụng" }}
              </button>
              <button
                @click="loadDailyEnergyAnalytics"
                :disabled="energyLoading"
                class="xl:col-span-1 px-4 py-2 rounded-lg bg-blue-600 text-white font-medium disabled:opacity-60"
              >
                Làm mới
              </button>
            </div>
          </div>

          <div
            v-if="energyError"
            class="bg-red-50 text-red-700 border border-red-200 rounded-xl px-4 py-3 text-sm"
          >
            {{ energyError }}
          </div>

          <div
            v-if="energyMessage && !energyError"
            class="bg-amber-50 text-amber-700 border border-amber-200 rounded-xl px-4 py-3 text-sm"
          >
            {{ energyMessage }}
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-white rounded-2xl border border-sky-100 shadow-sm p-5">
              <div class="text-sm text-gray-500">Tổng điện năng</div>
              <div class="text-3xl font-bold text-gray-800 mt-2">
                {{ formatNumber(energyData?.totalKwh) }}
                <span class="text-base text-gray-500 font-semibold">kWh</span>
              </div>
            </div>

            <div class="bg-white rounded-2xl border border-emerald-100 shadow-sm p-5">
              <div class="text-sm text-gray-500">Tổng chi phí ước tính</div>
              <div class="text-3xl font-bold text-gray-800 mt-2">
                {{ formatCurrency(energyData?.totalEstimatedCost) }}
              </div>
            </div>

            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5">
              <div class="text-sm text-gray-500">Biến động so kỳ trước</div>
              <div class="text-3xl font-bold text-gray-800 mt-2">
                {{ formatNumber(energyData?.deltaKwh) }}
                <span class="text-base text-gray-500 font-semibold">kWh</span>
              </div>
              <div
                class="inline-flex items-center gap-2 mt-3 px-3 py-1.5 rounded-full border text-xs font-semibold"
                :class="deltaMeta.className"
              >
                <span>{{ deltaMeta.icon }}</span>
                <span>{{ deltaMeta.text }}</span>
              </div>
            </div>
          </div>

          <div
            class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6"
          >
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-800">Biểu đồ điện năng theo ngày</h3>
              <span class="text-xs text-gray-500">Đơn vị: kWh</span>
            </div>

            <div v-if="energyLoading" class="h-64 rounded-xl bg-gray-50 animate-pulse"></div>
            <div v-else-if="!hasEnergyData" class="text-gray-500 py-10 text-center">
              Chưa có dữ liệu trong khoảng này.
            </div>
            <div v-else class="overflow-x-auto pb-1">
              <div class="min-w-[680px]">
                <div class="relative h-72 pt-2">
                  <div class="absolute left-0 inset-y-0 w-12 flex flex-col justify-between text-[11px] text-gray-400">
                    <span v-for="tick in energyYAxisTicks" :key="tick">{{ formatNumber(tick) }}</span>
                  </div>

                  <div class="absolute left-12 right-0 inset-y-0 grid grid-rows-4 pointer-events-none">
                    <div class="border-t border-dashed border-gray-200"></div>
                    <div class="border-t border-dashed border-gray-200"></div>
                    <div class="border-t border-dashed border-gray-200"></div>
                    <div class="border-t border-dashed border-gray-200"></div>
                  </div>

                  <div class="relative ml-12 h-full flex items-end gap-2">
                    <div
                      v-for="bar in energyBars"
                      :key="bar.date"
                      class="flex-1 min-w-[28px] h-full flex flex-col items-center group"
                    >
                      <div class="w-full flex-1 flex items-end">
                        <div
                          class="w-full rounded-t-xl bg-gradient-to-t from-sky-500 to-cyan-400 transition-all duration-300 group-hover:from-blue-600 group-hover:to-cyan-500"
                          :style="{
                            height: `${bar.height}%`,
                            minHeight: bar.kwh > 0 ? '10px' : '2px',
                            opacity: bar.kwh > 0 ? 1 : 0.35,
                          }"
                          :title="`${bar.date} • ${formatNumber(bar.kwh)} kWh • ${formatCurrency(bar.estimatedCost)}`"
                        ></div>
                      </div>
                      <div class="mt-2 text-[11px] text-gray-500">{{ bar.label }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-3">
              Top {{ energyGroupBy === 'device' ? 'thiết bị' : 'phòng' }} tiêu thụ
            </h3>

            <div v-if="energyBreakdownRich.length === 0" class="text-gray-500 py-4">
              Không có dữ liệu breakdown.
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="item in energyBreakdownRich"
                :key="item.id"
                class="rounded-xl border border-gray-100 p-4"
              >
                <div class="flex items-center justify-between">
                  <div>
                    <div class="text-sm text-gray-400">#{{ item.rank }}</div>
                    <div class="font-semibold text-gray-800">{{ item.name }}</div>
                  </div>
                  <div class="text-right">
                    <div class="font-semibold text-blue-700">{{ formatCurrency(item.estimatedCost) }}</div>
                    <div class="text-sm text-gray-500">{{ formatNumber(item.kwh) }} kWh</div>
                  </div>
                </div>

                <div class="mt-3 h-2 rounded-full bg-gray-100 overflow-hidden">
                  <div
                    class="h-full bg-gradient-to-r from-blue-500 to-cyan-400"
                    :style="{ width: `${Math.max(item.percent, 4)}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div
          v-if="currentTab === 'profile-page'"
          class="animate-in fade-in duration-500 max-w-4xl mx-auto space-y-6"
        >
          <div class="rounded-3xl bg-gradient-to-r from-slate-900 to-slate-700 text-white p-6 sm:p-8 shadow-lg">
            <div class="flex flex-wrap items-center justify-between gap-4">
              <div class="flex items-center gap-4">
                <div class="w-16 h-16 rounded-2xl bg-white/15 flex items-center justify-center text-2xl font-bold">
                  {{ profileInitials }}
                </div>
                <div>
                  <div class="text-white/70 text-sm">Tài khoản đang đăng nhập</div>
                  <h2 class="text-2xl font-bold">{{ profileForm.fullName || "Người dùng" }}</h2>
                  <p class="text-sm text-white/70">{{ profileForm.email || "--" }}</p>
                </div>
              </div>

              <div class="text-sm text-white/70">{{ currentDate }}</div>
            </div>
          </div>

          <div
            v-if="profileError"
            class="bg-red-50 text-red-700 border border-red-200 rounded-xl px-4 py-3 text-sm"
          >
            {{ profileError }}
          </div>

          <div
            v-if="profileMessage && !profileError"
            class="bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-xl px-4 py-3 text-sm"
          >
            {{ profileMessage }}
          </div>

          <div class="bg-white p-8 rounded-2xl border border-gray-100 shadow-sm">
            <div v-if="profileLoading" class="text-gray-500">Đang tải hồ sơ...</div>

            <form v-else class="space-y-4" @submit.prevent="submitUpdateProfile">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="rounded-xl border border-blue-100 bg-blue-50/60 p-4">
                  <div class="text-xs font-semibold text-blue-600 uppercase tracking-wide">Bảo mật</div>
                  <div class="text-sm text-blue-900 mt-1">
                    Cần đổi mật khẩu nhanh? Nhấn avatar góc phải để mở popup đổi mật khẩu.
                  </div>
                </div>
                <div class="rounded-xl border border-emerald-100 bg-emerald-50/60 p-4">
                  <div class="text-xs font-semibold text-emerald-600 uppercase tracking-wide">Mẹo</div>
                  <div class="text-sm text-emerald-900 mt-1">
                    Cập nhật đầy đủ thông tin để hệ thống hiển thị cá nhân hóa tốt hơn.
                  </div>
                </div>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="text-sm text-gray-500">Họ và tên</label>
                  <input
                    v-model="profileForm.fullName"
                    type="text"
                    class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                  />
                </div>
                <div>
                  <label class="text-sm text-gray-500">Số điện thoại</label>
                  <input
                    v-model="profileForm.phone"
                    type="text"
                    placeholder="VD: 09xxxxxxxx"
                    class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                  />
                </div>
              </div>

              <div>
                <label class="text-sm text-gray-500">Email</label>
                <input
                  v-model="profileForm.email"
                  type="email"
                  readonly
                  class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200 bg-gray-50 text-gray-500"
                />
              </div>

              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label class="text-sm text-gray-500">Địa chỉ</label>
                  <input
                    v-model="profileForm.address"
                    type="text"
                    class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                  />
                </div>
                <div>
                  <label class="text-sm text-gray-500">Thành phố</label>
                  <input
                    v-model="profileForm.city"
                    type="text"
                    class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                  />
                </div>
                <div>
                  <label class="text-sm text-gray-500">Quốc gia</label>
                  <input
                    v-model="profileForm.country"
                    type="text"
                    class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                  />
                </div>
              </div>

              <button
                type="submit"
                :disabled="profileSaving"
                class="px-5 py-2 rounded-lg bg-blue-600 text-white font-medium disabled:opacity-60"
              >
                {{ profileSaving ? "Đang lưu..." : "Lưu thay đổi" }}
              </button>
            </form>
          </div>
        </div>
      </div>

      <button
        @click="openAddModal"
        class="absolute bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg hover:scale-105 transition-transform z-40"
      >
        <i data-lucide="plus" class="w-7 h-7"></i>
      </button>

      <div
        v-if="quickProfileOpen"
        class="fixed inset-0 z-50 bg-slate-900/45 backdrop-blur-[2px] flex items-center justify-center px-4"
        @click.self="closeQuickProfileModal"
      >
        <div class="w-full max-w-md bg-white rounded-3xl border border-gray-100 shadow-2xl overflow-hidden">
          <div class="px-6 py-5 bg-gradient-to-r from-blue-600 to-cyan-500 text-white">
            <div class="flex items-center justify-between">
              <h3 class="text-lg font-semibold">Đổi mật khẩu</h3>
              <button
                type="button"
                @click="closeQuickProfileModal"
                class="w-8 h-8 rounded-full hover:bg-white/20 text-white"
              >
                ✕
              </button>
            </div>
            <p class="text-sm text-blue-50 mt-1">
              Mật khẩu mới cần có chữ hoa, chữ thường, số và ký tự đặc biệt.
            </p>
          </div>

          <div class="p-6">
            <div
              v-if="quickPasswordError"
              class="mb-3 bg-red-50 text-red-700 border border-red-200 rounded-lg px-3 py-2 text-sm"
            >
              {{ quickPasswordError }}
            </div>

            <div
              v-if="quickPasswordMessage && !quickPasswordError"
              class="mb-3 bg-emerald-50 text-emerald-700 border border-emerald-200 rounded-lg px-3 py-2 text-sm"
            >
              {{ quickPasswordMessage }}
            </div>

            <form class="space-y-3" @submit.prevent="submitChangePassword">
              <div>
                <label class="text-sm text-gray-500">Mật khẩu cũ</label>
                <input
                  v-model="quickOldPassword"
                  type="password"
                  class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                />
              </div>

              <div>
                <label class="text-sm text-gray-500">Mật khẩu mới</label>
                <input
                  v-model="quickNewPassword"
                  type="password"
                  class="mt-1 w-full px-3 py-2 rounded-lg border border-gray-200"
                />
              </div>

              <div class="flex justify-end gap-2 pt-2">
                <button
                  type="button"
                  @click="closeQuickProfileModal"
                  class="px-4 py-2 rounded-lg bg-gray-100 text-gray-700"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  :disabled="quickPasswordLoading"
                  class="px-4 py-2 rounded-lg bg-blue-600 text-white disabled:opacity-60"
                >
                  {{ quickPasswordLoading ? "Đang xử lý..." : "Xác nhận" }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* Thêm hiệu ứng chuyển tab mượt hơn */
.animate-in {
  animation-duration: 0.3s;
  animation-fill-mode: both;
}
</style>
