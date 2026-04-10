<script setup>
import { onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

onMounted(() => {
  window.doLogout = function doLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    router.push("/login");
  };

  if (typeof window.initDashboard === "function") {
    window.initDashboard();
  }

  if (window.lucide) {
    window.lucide.createIcons();
  }
});

onUnmounted(() => {
  delete window.doLogout;
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
          onclick="switchDashboardTab('overview')"
          id="tab-overview"
          class="flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="layout-dashboard" class="mr-3 w-5 h-5"></i> Tổng quan
        </a>
        <a
          href="#"
          onclick="showRoomStats()"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="home" class="mr-3 w-5 h-5"></i> Phòng ốc
        </a>
        <a
          href="#"
          onclick="showEnergyChart()"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="activity" class="mr-3 w-5 h-5"></i> Thống kê điện
        </a>

        <a
          href="#"
          onclick="switchDashboardTab('profile-page')"
          id="tab-profile-page"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer"
        >
          <i data-lucide="user" class="mr-3 w-5 h-5"></i> Hồ sơ cá nhân
        </a>

        <RouterLink
          to="/blog"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
          id="nav-blog"
        >
          <i data-lucide="book-open" class="mr-3 w-5 h-5"></i> Blog
        </RouterLink>
      </nav>
      <div class="p-4 border-t border-gray-100">
        <a
          href="#"
          onclick="doLogout()"
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
            onclick="openQuickProfileModal()"
            id="header-avatar"
            class="w-9 h-9 rounded-full bg-gradient-to-tr from-blue-500 to-purple-500 text-white flex items-center justify-center font-bold shadow-md cursor-pointer"
            title="Cài đặt tài khoản"
          >
            AD
          </div>
        </div>
      </header>

      <div class="flex-1 overflow-auto p-4 sm:p-8 pb-24" id="main-content-area">
        <div id="overview-content" class="block">
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
                Nhật ký sử dụng
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div
                onclick="showPowerDetail()"
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
                  <span class="text-gray-500 font-medium text-gray-400"
                    >kW</span
                  >
                </div>
                <p class="text-sm text-gray-400 mt-2" id="stat-active-devices">
                  0 thiết bị
                </p>
              </div>

              <div
                onclick="showTodayDetail()"
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
                onclick="showMonthDetail()"
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
                  * Click để xem dự báo hóa đơn
                </p>
              </div>
            </div>
          </section>

          <section>
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-lg font-bold text-gray-800">
                Thiết bị trong nhà
              </h2>
              <div class="flex items-center space-x-3">
                <button
                  onclick="toggleEditMode()"
                  id="btn-edit-mode"
                  class="text-sm font-medium flex items-center px-3 py-1.5 rounded-lg transition-colors text-gray-600 hover:bg-gray-100"
                >
                  <i data-lucide="edit-2" class="w-4 h-4 mr-1.5"></i>
                  <span>Quản lý</span>
                </button>
              </div>
            </div>
            <div
              class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4"
              id="devices-container"
            ></div>
          </section>
        </div>

        <div
          id="profile-page-content"
          class="hidden max-w-4xl mx-auto space-y-6"
        >
          <h2 class="text-2xl font-bold text-gray-800 mb-6">
            Chi tiết Hồ sơ cá nhân
          </h2>

          <div
            class="bg-white p-8 rounded-2xl border border-gray-100 shadow-sm flex items-center space-x-6"
          >
            <div
              class="w-24 h-24 rounded-full bg-gradient-to-tr from-blue-500 to-purple-500 text-white flex items-center justify-center text-3xl font-bold shadow-lg"
              id="page-avatar"
            >
              AD
            </div>
            <div class="flex-1">
              <h3
                class="text-2xl font-semibold text-gray-800"
                id="page-display-name"
              >
                Admin User
              </h3>
              <p class="text-gray-500 flex items-center mt-1">
                <i data-lucide="mail" class="w-4 h-4 mr-2"></i>
                <span id="page-display-email">admin@example.com</span>
              </p>
              <span
                class="inline-block mt-2 px-3 py-1 bg-green-100 text-green-700 text-xs font-medium rounded-full"
                >Thành viên Hệ thống</span
              >
            </div>
          </div>

          <div
            class="bg-white p-8 rounded-2xl border border-gray-100 shadow-sm"
          >
            <h4
              class="text-lg font-semibold text-gray-800 mb-6 border-b pb-4 flex items-center"
            >
              <i data-lucide="edit" class="w-5 h-5 mr-2 text-blue-500"></i>
              Thông tin cơ bản
            </h4>
            <form
              onsubmit="
                event.preventDefault();
                submitUpdateFullProfile();
              "
              class="space-y-6"
            >
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1"
                    >Họ và Tên</label
                  >
                  <input
                    type="text"
                    id="full-profile-name"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all bg-white"
                    value="Admin User"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1"
                    >Số điện thoại</label
                  >
                  <input
                    type="tel"
                    id="full-profile-phone"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all bg-white"
                    placeholder="0987654321"
                  />
                </div>
                <div class="md:col-span-2">
                  <label class="block text-sm font-medium text-gray-700 mb-1"
                    >Địa chỉ Email (Cố định)</label
                  >
                  <input
                    type="email"
                    id="full-profile-email"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl bg-gray-50 outline-none text-gray-500 cursor-not-allowed"
                    value="admin@example.com"
                    readonly
                  />
                </div>
              </div>
              <div class="flex justify-end pt-4">
                <button
                  type="submit"
                  class="px-8 py-3 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 shadow-md transition-all flex items-center"
                >
                  <i data-lucide="save" class="w-5 h-5 mr-2"></i> Lưu thay đổi
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div
        id="device-detail-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm"
      >
        <div class="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl">
          <h3 class="text-lg font-bold mb-4">Thông tin thiết bị</h3>

          <div class="space-y-2 text-gray-700">
            <p><strong>Tên:</strong> <span id="detail-name"></span></p>
            <p><strong>Phòng:</strong> <span id="detail-room"></span></p>
            <p><strong>Công suất:</strong> <span id="detail-power"></span> W</p>
            <p>
              <strong>Giờ sử dụng:</strong> <span id="detail-hours"></span> giờ
            </p>
            <p><strong>Trạng thái:</strong> <span id="detail-status"></span></p>
          </div>

          <button
            onclick="closeDeviceDetail()"
            class="mt-5 w-full py-2 bg-blue-600 text-white rounded-xl"
          >
            Đóng
          </button>
        </div>
      </div>

      <div
        id="stats-detail-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4"
      >
        <div
          class="bg-white w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden"
        >
          <div
            class="p-6 border-b border-gray-100 flex justify-between items-center"
          >
            <h3 id="stats-title" class="font-bold text-xl text-gray-800">
              Chi tiết
            </h3>
            <button
              onclick="closeStatsModal()"
              class="text-gray-400 hover:text-gray-800"
            >
              <i data-lucide="x" class="w-6 h-6"></i>
            </button>
          </div>
          <div class="p-6">
            <div id="stats-content" class="space-y-4"></div>
            <button
              onclick="closeStatsModal()"
              class="mt-8 w-full py-3 bg-gray-100 text-gray-600 rounded-2xl font-bold hover:bg-gray-200 transition-colors"
            >
              Đã hiểu
            </button>
          </div>
        </div>
      </div>

      <div
        id="room-stats-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4"
      >
        <div
          class="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden"
        >
          <div
            class="p-5 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-lg text-gray-800 flex items-center">
              <i data-lucide="home" class="w-5 h-5 mr-2 text-green-500"></i>
              Hiệu suất theo phòng
            </h3>
            <button
              onclick="closeRoomModal()"
              class="text-gray-400 hover:text-gray-800"
            >
              <i data-lucide="x" class="w-6 h-6"></i>
            </button>
          </div>
          <div class="p-6">
            <canvas id="roomBarChart" style="max-height: 300px"></canvas>

            <div id="room-details" class="mt-6 space-y-3"></div>
          </div>
          <div class="p-4 border-t border-gray-50 flex justify-center">
            <button
              onclick="closeRoomModal()"
              class="text-sm font-bold text-gray-400 hover:text-gray-600"
            >
              ĐÓNG
            </button>
          </div>
        </div>
      </div>

      <div
        id="chart-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm px-4"
      >
        <div
          class="bg-white w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden"
        >
          <div
            class="p-6 border-b border-gray-100 flex justify-between items-center"
          >
            <h3 class="font-bold text-xl text-gray-800 flex items-center">
              <i data-lucide="pie-chart" class="w-5 h-5 mr-2 text-blue-500"></i>
              Thống kê tiêu thụ hôm nay
            </h3>
            <button
              onclick="closeChartModal()"
              class="text-gray-400 hover:text-gray-800"
            >
              <i data-lucide="x" class="w-6 h-6"></i>
            </button>
          </div>
          <div class="p-8 flex flex-col items-center">
            <div class="w-full max-w-[300px]">
              <canvas id="energyPieChart"></canvas>
            </div>
            <div id="chart-legend" class="mt-6 w-full space-y-2"></div>
          </div>
          <div class="p-4 bg-gray-50 text-center">
            <button
              onclick="closeChartModal()"
              class="text-blue-600 font-bold hover:underline"
            >
              Đóng thống kê
            </button>
          </div>
        </div>
      </div>

      <button
        id="fab-add-device"
        onclick="openAddModal()"
        class="absolute bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg shadow-blue-500/40 hover:scale-105 transition-transform z-40"
      >
        <i data-lucide="plus" class="w-7 h-7"></i>
      </button>

      <div
        id="add-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
      >
        <div
          class="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden transform transition-all"
        >
          <div
            class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-lg text-gray-800">Thêm thiết bị mới</h3>
            <button
              onclick="closeAddModal()"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm"
            >
              <i data-lucide="x" class="w-5 h-5"></i>
            </button>
          </div>
          <form
            onsubmit="
              event.preventDefault();
              submitAddDevice();
            "
            class="p-6 space-y-4"
          >
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Tên thiết bị</label
              >
              <input
                type="text"
                id="new-device-name"
                required
                placeholder="VD: Quạt máy"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Phòng</label
                >
                <select
                  id="new-device-room"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option>Phòng khách</option>
                  <option>Phòng ngủ</option>
                  <option>Bếp</option>
                  <option>Phòng tắm</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Loại</label
                >
                <select
                  id="new-device-type"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option value="light">Chiếu sáng</option>
                  <option value="ac">Điều hoà/Quạt</option>
                  <option value="tv">Giải trí</option>
                  <option value="fridge">Tủ lạnh</option>
                </select>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Công suất (Watt)</label
              >
              <input
                type="number"
                id="new-device-power"
                required
                min="1"
                placeholder="VD: 55"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Thời gian dùng (giờ)</label
              >
              <input
                type="number"
                id="new-device-hours"
                required
                min="1"
                placeholder="VD: 1"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div class="pt-4 flex space-x-3">
              <button
                type="button"
                onclick="closeAddModal()"
                class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 shadow-md"
              >
                Lưu thiết bị
              </button>
            </div>
          </form>
        </div>
      </div>

      <div
        id="quick-profile-modal"
        class="hidden fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
      >
        <div
          class="bg-white w-full max-w-sm rounded-2xl shadow-xl overflow-hidden transform transition-all"
        >
          <div
            class="px-5 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-gray-800">Tài khoản Nhanh</h3>
            <button
              type="button"
              onclick="closeQuickProfileModal()"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm cursor-pointer"
            >
              <i data-lucide="x" class="w-5 h-5"></i>
            </button>
          </div>

          <div class="p-6">
            <div class="flex items-center space-x-4 mb-6">
              <div
                class="w-14 h-14 rounded-full bg-gradient-to-tr from-blue-500 to-purple-500 text-white flex items-center justify-center text-lg font-bold shadow-sm"
                id="modal-avatar"
              >
                AD
              </div>
              <div>
                <h4
                  class="font-semibold text-gray-800 text-base leading-tight"
                  id="modal-display-name"
                >
                  Admin User
                </h4>
                <p class="text-xs text-gray-500 mt-1" id="modal-display-email">
                  admin@example.com
                </p>
              </div>
            </div>

            <hr class="border-gray-100 mb-6" />

            <form
              onsubmit="
                event.preventDefault();
                submitChangePassword();
              "
              class="space-y-4 mb-6"
            >
              <h4 class="font-medium text-gray-800 text-sm flex items-center">
                <i data-lucide="lock" class="w-4 h-4 mr-1.5 text-blue-500"></i>
                Đổi mật khẩu
              </h4>
              <div>
                <div style="position: relative">
                  <input
                    type="password"
                    id="quick-old-password"
                    required
                    placeholder="Mật khẩu hiện tại"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm transition-all"
                  />
                  <i
                    class="fa-solid fa-eye toggle-password"
                    onclick="togglePassword('quick-old-password', this)"
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
              <div>
                <div style="position: relative">
                  <input
                    type="password"
                    id="quick-new-password"
                    required
                    placeholder="Mật khẩu mới"
                    class="w-full px-4 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm transition-all"
                  />
                  <i
                    class="fa-solid fa-eye toggle-password"
                    onclick="togglePassword('quick-new-password', this)"
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
              <button
                type="submit"
                class="w-full py-2.5 bg-slate-800 text-white rounded-xl text-sm font-medium hover:bg-slate-900 shadow-md transition-all"
              >
                Xác nhận đổi mật khẩu
              </button>
            </form>

            <button
              type="button"
              onclick="goToProfilePage()"
              class="w-full py-2.5 border border-blue-200 text-blue-600 bg-blue-50 rounded-xl text-sm font-medium hover:bg-blue-100 transition-colors flex items-center justify-center"
            >
              <i data-lucide="external-link" class="w-4 h-4 mr-2"></i> Mở trang
              Hồ sơ chi tiết
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>
