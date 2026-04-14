<script setup>
import { onMounted, onUnmounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const currentDate = ref(new Date().toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }));

// Quản lý trạng thái tab nội bộ
const currentTab = ref('overview');

// Hàm chuyển đổi tab
const switchDashboardTab = (tabName) => {
  currentTab.value = tabName;
  if (window.lucide) window.lucide.createIcons();
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
const openQuickProfileModal = () => console.log("Mở profile nhanh");

onMounted(() => {
  // Gán hàm vào window nếu các file script bên ngoài (JS thuần) cần dùng
  window.doLogout = doLogout;
  window.switchDashboardTab = switchDashboardTab;

  if (typeof window.initDashboard === "function") {
    window.initDashboard();
  }

  if (window.lucide) {
    window.lucide.createIcons();
  }
});

onUnmounted(() => {
  delete window.doLogout;
  delete window.switchDashboardTab;
});
</script>

<template>
  <div id="dashboard-view" class="h-screen w-full overflow-hidden flex active">
    <aside class="hidden lg:flex flex-col w-64 bg-white border-r border-gray-200 z-30">
      <div class="h-16 flex items-center px-6 border-b border-gray-100">
        <i data-lucide="zap" class="text-blue-600 mr-2 w-7 h-7"></i>
        <span class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-cyan-500">
          SmartHome
        </span>
      </div>
      <nav class="p-4 space-y-2 flex-1">
        <a href="#" @click.prevent="switchDashboardTab('overview')"
           :class="[currentTab === 'overview' ? 'text-blue-700 bg-blue-50' : 'text-gray-600 hover:bg-gray-50']"
           class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer">
          <i data-lucide="layout-dashboard" class="mr-3 w-5 h-5"></i> Tổng quan
        </a>
        <RouterLink to="/rooms" class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors">
          <i data-lucide="home" class="mr-3 w-5 h-5"></i> Phòng ốc
        </RouterLink>
        <a href="#" @click.prevent="switchDashboardTab('energy-page')"
           :class="[currentTab === 'energy-page' ? 'text-blue-700 bg-blue-50' : 'text-gray-600 hover:bg-gray-50']"
           class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer">
          <i data-lucide="activity" class="mr-3 w-5 h-5"></i> Thống kê điện
        </a>
        <RouterLink to="/calculator"
                    class="flex items-center px-4 py-3 text-gray-600 hover:bg-blue-50 hover:text-blue-700 rounded-xl font-medium transition-colors">
          <i data-lucide="calculator" class="mr-3 w-5 h-5"></i> Máy tính điện
        </RouterLink>
        <a href="#" @click.prevent="switchDashboardTab('profile-page')"
           :class="[currentTab === 'profile-page' ? 'text-blue-700 bg-blue-50' : 'text-gray-600 hover:bg-gray-50']"
           class="flex items-center px-4 py-3 rounded-xl font-medium transition-colors cursor-pointer">
          <i data-lucide="user" class="mr-3 w-5 h-5"></i> Hồ sơ cá nhân
        </a>
        <RouterLink to="/blog" class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors">
          <i data-lucide="book-open" class="mr-3 w-5 h-5"></i> Blog
        </RouterLink>
      </nav>
      <div class="p-4 border-t border-gray-100">
        <a href="#" @click.prevent="doLogout" class="flex items-center px-4 py-3 text-red-600 hover:bg-red-50 rounded-xl font-medium transition-colors cursor-pointer">
          <i data-lucide="log-out" class="mr-3 w-5 h-5"></i> Đăng xuất
        </a>
      </div>
    </aside>

    <main class="flex-1 flex flex-col h-screen overflow-hidden relative">
      <header class="h-16 flex items-center justify-between px-6 bg-white/80 backdrop-blur-md border-b border-gray-100 sticky top-0 z-10">
        <div class="flex items-center">
          <i data-lucide="menu" class="w-6 h-6 text-gray-600 lg:hidden mr-4 cursor-pointer"></i>
          <h1 class="text-xl font-semibold hidden sm:block">Chào mừng trở lại!</h1>
        </div>
        <div class="flex items-center space-x-4">
          <button class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full transition-colors relative">
            <i data-lucide="bell" class="w-5 h-5"></i>
            <span class="absolute top-1 right-1 w-2.5 h-2.5 bg-red-500 rounded-full border-2 border-white"></span>
          </button>
          <div @click="openQuickProfileModal" class="w-9 h-9 rounded-full bg-gradient-to-tr from-blue-500 to-purple-500 text-white flex items-center justify-center font-bold shadow-md cursor-pointer">
            AD
          </div>
        </div>
      </header>

      <div class="flex-1 overflow-auto p-4 sm:p-8 pb-24">
        <div v-if="currentTab === 'overview'" class="block animate-in fade-in duration-500">
          <section class="mb-10">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">Gợi ý cho bạn</h2>
              <button class="text-sm font-medium text-blue-600 hover:text-blue-700 flex items-center">
                Xem thêm <i data-lucide="chevron-right" class="w-4 h-4 ml-1"></i>
              </button>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6" id="articles-container"></div>
          </section>

          <section class="mb-10">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">Quản lý & Tiêu thụ điện</h2>
              <span class="text-sm font-medium text-gray-500 bg-gray-200/50 px-3 py-1 rounded-full flex items-center">
                <i data-lucide="trending-up" class="w-3.5 h-3.5 mr-1 text-blue-500"></i> Nhật ký
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div @click="showPowerDetail" class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm flex flex-col cursor-pointer hover:border-blue-300 transition-all group">
                <div class="flex justify-between items-start mb-4">
                  <div class="p-3 bg-amber-50 text-amber-600 rounded-xl group-hover:bg-amber-100">
                    <i data-lucide="activity" class="w-6 h-6"></i>
                  </div>
                  <span class="text-[10px] text-amber-500 font-bold uppercase tracking-wider">Xem chi tiết</span>
                </div>
                <h3 class="text-gray-500 font-medium mb-1">Công suất sử dụng</h3>
                <div class="flex items-baseline space-x-1">
                  <span class="text-3xl font-bold text-gray-800" id="stat-power">0.00</span>
                  <span class="text-gray-500 font-medium">kW</span>
                </div>
                <p class="text-sm text-gray-400 mt-2" id="stat-active-devices">0 thiết bị</p>
              </div>

              <div @click="showTodayDetail" class="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm flex flex-col border-l-4 border-l-blue-500 cursor-pointer hover:border-blue-300 transition-all group">
                <div class="flex justify-between items-start mb-4">
                  <div class="p-3 bg-blue-50 text-blue-600 rounded-xl group-hover:bg-blue-100">
                    <i data-lucide="zap" class="w-6 h-6"></i>
                  </div>
                  <span class="text-xs font-medium text-blue-600 bg-blue-50 px-2 py-1 rounded-md tracking-wider">Hôm nay</span>
                </div>
                <h3 class="text-gray-500 font-medium mb-1">Chi phí hôm nay</h3>
                <div class="flex flex-col">
                  <span class="text-3xl font-bold text-gray-800" id="stat-cost-today">0 ₫</span>
                  <div class="flex items-center text-sm font-medium text-blue-500 mt-1">
                    <span id="stat-kwh">0.00</span><span class="ml-1">kWh</span>
                  </div>
                </div>
              </div>

              <div @click="showMonthDetail" class="bg-gradient-to-br from-slate-800 to-slate-900 rounded-2xl p-6 shadow-md flex flex-col text-white cursor-pointer hover:opacity-90 transition-all group">
                <div class="flex justify-between items-start mb-4">
                  <div class="p-3 bg-white/10 text-white rounded-xl backdrop-blur-sm group-hover:bg-white/20">
                    <i data-lucide="calendar" class="w-6 h-6"></i>
                  </div>
                  <span class="text-xs font-medium text-slate-300 bg-white/10 px-2 py-1 rounded-md tracking-wider">Dự kiến</span>
                </div>
                <h3 class="text-slate-300 font-medium mb-1">Tổng tiền tháng này</h3>
                <div class="flex items-baseline space-x-1 mb-1">
                  <span class="text-3xl font-bold text-white" id="stat-cost-month">0 ₫</span>
                </div>
                <p class="text-xs text-slate-400 mt-2 opacity-70 italic">* Xem dự báo hóa đơn</p>
              </div>
            </div>
          </section>

          <section>
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">Thiết bị trong nhà</h2>
              <button @click="toggleEditMode" class="text-sm font-medium flex items-center px-3 py-1.5 rounded-lg transition-colors text-gray-600 hover:bg-gray-100">
                <i data-lucide="edit-2" class="w-4 h-4 mr-1.5"></i> <span>Quản lý</span>
              </button>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4" id="devices-container"></div>
          </section>
        </div>

        <div v-if="currentTab === 'energy-page'" class="animate-in slide-in-from-bottom duration-500 max-w-6xl mx-auto space-y-6">
          <h2 class="text-2xl font-bold text-gray-800">Thống kê điện chi tiết</h2>
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <div style="height: 340px">
              <canvas id="energyBarChart"></canvas>
            </div>
          </div>
        </div>

        <div v-if="currentTab === 'profile-page'" class="animate-in fade-in duration-500 max-w-4xl mx-auto space-y-6">
          <h2 class="text-2xl font-bold text-gray-800">Hồ sơ cá nhân</h2>
          <div class="bg-white p-8 rounded-2xl border border-gray-100 shadow-sm">
            <p class="text-gray-500">Chức năng cập nhật thông tin đang được xử lý...</p>
          </div>
        </div>
      </div>

      <button @click="openAddModal" class="absolute bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg hover:scale-105 transition-transform z-40">
        <i data-lucide="plus" class="w-7 h-7"></i>
      </button>
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