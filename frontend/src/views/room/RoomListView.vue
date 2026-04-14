<script setup>
import { computed, nextTick, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";

const API_BASE =
  window.location.port === "5173" || window.location.port === "4173"
    ? "http://localhost:8080/api/v1"
    : "/api/v1";

const router = useRouter();

const rooms = ref([]);
const loading = ref(false);
const error = ref("");
const searchQuery = ref("");
const sortBy = ref("kwh");
const sortOrder = ref("desc");
const showAddModal = ref(false);
const submitting = ref(false);

const roomForm = ref({
  roomName: "",
  roomType: "",
  area: "",
});

function parseApiResponse(response) {
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }
  return response.text().then((text) => ({ message: text }));
}

function authHeaders(extra = {}) {
  const token = localStorage.getItem("token");
  return {
    Authorization: `Bearer ${token}`,
    ...extra,
  };
}

function formatMoney(value) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(Number(value || 0));
}

function formatNumber(value, digits = 2) {
  return new Intl.NumberFormat("vi-VN", {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  }).format(Number(value || 0));
}

function formatArea(area) {
  if (area === null || area === undefined || area === "") return "Chưa có";
  return `${new Intl.NumberFormat("vi-VN", {
    maximumFractionDigits: 1,
  }).format(Number(area))} m²`;
}

function refreshIcons() {
  nextTick(() => {
    if (window.lucide) {
      window.lucide.createIcons();
    }
  });
}

function doLogout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userEmail");
  router.push("/login");
}

function openAddModal() {
  showAddModal.value = true;
}

function closeAddModal() {
  showAddModal.value = false;
  roomForm.value = {
    roomName: "",
    roomType: "",
    area: "",
  };
}

async function loadRooms() {
  loading.value = true;
  error.value = "";

  try {
    const params = new URLSearchParams({
      sortBy: sortBy.value,
      order: sortOrder.value,
    });

    if (searchQuery.value.trim()) {
      params.set("search", searchQuery.value.trim());
    }

    const response = await fetch(
      `${API_BASE}/rooms/stats?${params.toString()}`,
      {
        headers: authHeaders(),
      },
    );

    const result = await parseApiResponse(response);

    if (!response.ok) {
      throw new Error(result.message || "Không tải được danh sách phòng");
    }

    rooms.value = Array.isArray(result) ? result : [];
  } catch (err) {
    console.error(err);
    error.value = err.message || "Không tải được danh sách phòng";
    rooms.value = [];
  } finally {
    loading.value = false;
  }
}

async function submitRoom() {
  if (!roomForm.value.roomName.trim()) {
    alert("Vui lòng nhập tên phòng.");
    return;
  }

  submitting.value = true;
  try {
    const response = await fetch(`${API_BASE}/rooms`, {
      method: "POST",
      headers: authHeaders({
        "Content-Type": "application/json",
      }),
      body: JSON.stringify({
        roomName: roomForm.value.roomName.trim(),
        roomType: roomForm.value.roomType.trim(),
        area: roomForm.value.area === "" ? null : Number(roomForm.value.area),
      }),
    });

    const result = await parseApiResponse(response);

    if (!response.ok) {
      throw new Error(result.message || "Thêm phòng thất bại");
    }

    closeAddModal();
    await loadRooms();
    alert("Đã thêm phòng mới.");
  } catch (err) {
    console.error(err);
    alert(err.message || "Thêm phòng thất bại");
  } finally {
    submitting.value = false;
  }
}

function openRoomDetail(room) {
  router.push(`/rooms/${room.id}`);
}

const emptyStateTitle = computed(() => {
  if (loading.value) return "Đang tải dữ liệu...";
  if (error.value) return "Không tải được dữ liệu";
  return "Chưa có phòng nào";
});

const emptyStateMessage = computed(() => {
  if (loading.value) return "Vui lòng chờ trong giây lát.";
  if (error.value) return error.value;
  return "Thêm phòng đầu tiên để theo dõi tiêu thụ điện theo từng khu vực.";
});

watch([searchQuery, sortBy, sortOrder], () => {
  loadRooms();
});

onMounted(async () => {
  const token = localStorage.getItem("token");
  if (!token) {
    router.push("/login");
    return;
  }

  await loadRooms();
  refreshIcons();
});
</script>

<template>
  <div class="h-screen w-full overflow-hidden flex bg-slate-50 text-gray-700">
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
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="layout-dashboard" class="mr-3 w-5 h-5"></i>
          Tổng quan
        </RouterLink>
        <RouterLink
          to="/rooms"
          class="flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="home" class="mr-3 w-5 h-5"></i>
          Phòng ốc
        </RouterLink>
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="activity" class="mr-3 w-5 h-5"></i>
          Thống kê điện
        </RouterLink>
        <RouterLink
          to="/home"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="user" class="mr-3 w-5 h-5"></i>
          Hồ sơ cá nhân
        </RouterLink>
        <RouterLink
          to="/blog"
          class="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="book-open" class="mr-3 w-5 h-5"></i>
          Blog
        </RouterLink>
      </nav>
    </aside>

    <main class="flex-1 flex flex-col h-screen overflow-hidden relative">
      <header
        class="h-16 flex items-center justify-between px-6 bg-white/80 backdrop-blur-md border-b border-gray-100 sticky top-0 z-10"
      >
        <div>
          <h1 class="text-xl font-semibold text-gray-800">Quản lý phòng</h1>
          <p class="text-sm text-gray-400">
            Theo dõi điện năng theo từng khu vực
          </p>
        </div>
      </header>

      <div class="flex-1 overflow-auto p-4 sm:p-6 lg:p-8 pb-24 space-y-6">
        <section class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div
            class="bg-white rounded-2xl p-5 border border-blue-100 shadow-sm shadow-blue-50/50"
          >
            <p class="text-sm text-gray-400 mb-1">Tổng số phòng</p>
            <div class="text-3xl font-bold text-gray-800">
              {{ rooms.length }}
            </div>
          </div>
          <div
            class="bg-white rounded-2xl p-5 border border-blue-100 shadow-sm shadow-blue-50/50"
          >
            <p class="text-sm text-gray-400 mb-1">Tiêu thụ hiện tại</p>
            <div class="text-3xl font-bold text-gray-800">
              {{
                formatNumber(
                  rooms.reduce(
                    (sum, room) => sum + (Number(room.currentKwh) || 0),
                    0,
                  ),
                )
              }}
              kWh
            </div>
          </div>
          <div
            class="bg-white rounded-2xl p-5 border border-blue-100 shadow-sm shadow-blue-50/50"
          >
            <p class="text-sm text-gray-400 mb-1">Chi phí ước tính</p>
            <div class="text-3xl font-bold text-gray-800">
              {{
                formatMoney(
                  rooms.reduce(
                    (sum, room) => sum + (Number(room.currentCost) || 0),
                    0,
                  ),
                )
              }}
            </div>
          </div>
        </section>

        <section
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 sm:p-5"
        >
          <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div class="md:col-span-1">
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Tìm phòng</label
              >
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Nhập tên phòng..."
                class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
            <div>
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Sắp xếp theo</label
              >
              <select
                v-model="sortBy"
                class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              >
                <option value="kwh">Điện năng</option>
                <option value="cost">Chi phí</option>
                <option value="trend">Xu hướng</option>
                <option value="name">Tên phòng</option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Thứ tự</label
              >
              <select
                v-model="sortOrder"
                class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
              >
                <option value="desc">Giảm dần</option>
                <option value="asc">Tăng dần</option>
              </select>
            </div>
          </div>
        </section>

        <section>
          <div
            v-if="loading"
            class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4"
          >
            <div
              v-for="n in 6"
              :key="n"
              class="bg-white rounded-2xl p-5 border border-gray-100 animate-pulse"
            >
              <div class="h-10 w-10 rounded-xl bg-gray-100 mb-4"></div>
              <div class="h-4 bg-gray-100 rounded w-3/4 mb-2"></div>
              <div class="h-3 bg-gray-100 rounded w-1/2 mb-4"></div>
              <div class="h-10 bg-gray-100 rounded-xl"></div>
            </div>
          </div>

          <div
            v-else-if="rooms.length"
            class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4"
          >
            <button
              v-for="room in rooms"
              :key="room.id"
              @click="openRoomDetail(room)"
              class="text-left bg-white rounded-2xl p-5 border border-gray-100 shadow-md shadow-blue-50/40 hover:shadow-lg hover:border-blue-200 transition-all duration-200 group"
            >
              <div class="flex items-start justify-between gap-3 mb-4">
                <div
                  class="p-3 rounded-xl bg-blue-50 text-blue-600 group-hover:bg-blue-100"
                >
                  <i data-lucide="home" class="w-6 h-6"></i>
                </div>
                <span
                  class="text-xs font-semibold text-blue-600 bg-blue-50 px-2.5 py-1 rounded-full"
                >
                  {{ room.deviceCount }} thiết bị
                </span>
              </div>

              <h3 class="text-lg font-semibold text-gray-800 mb-1">
                {{ room.roomName }}
              </h3>
              <p class="text-sm text-gray-500 mb-4">
                {{ room.roomType || "Chưa có loại phòng" }}
              </p>

              <div class="grid grid-cols-2 gap-2 text-sm mb-4">
                <div class="rounded-xl bg-gray-50 px-3 py-2">
                  <p class="text-gray-400 text-xs">kWh tháng này</p>
                  <p class="font-semibold text-gray-800">
                    {{ formatNumber(room.currentKwh) }}
                  </p>
                </div>
                <div class="rounded-xl bg-gray-50 px-3 py-2">
                  <p class="text-gray-400 text-xs">Chi phí</p>
                  <p class="font-semibold text-gray-800">
                    {{ formatMoney(room.currentCost) }}
                  </p>
                </div>
              </div>

              <div
                class="flex items-center justify-between text-sm text-gray-500"
              >
                <span>{{ formatArea(room.area) }}</span>
              </div>
            </button>
          </div>

          <div
            v-else
            class="bg-white rounded-2xl border border-dashed border-gray-200 p-8 text-center text-gray-400"
          >
            <p class="font-semibold text-gray-700">{{ emptyStateTitle }}</p>
            <p class="mt-2 text-sm">{{ emptyStateMessage }}</p>
          </div>
        </section>

        <button
          @click="openAddModal"
          class="fixed bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg shadow-blue-500/40 hover:scale-105 transition-transform z-40"
        >
          <i data-lucide="plus" class="w-7 h-7"></i>
        </button>
      </div>
    </main>

    <Teleport to="body">
      <div
        v-if="showAddModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/45 backdrop-blur-sm px-4"
        @click.self="closeAddModal"
      >
        <div
          class="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-5 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-lg text-gray-800">Thêm phòng mới</h3>
            <button
              @click="closeAddModal"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm"
            >
              <i data-lucide="x" class="w-4 h-4"></i>
            </button>
          </div>

          <form @submit.prevent="submitRoom" class="p-5 space-y-3.5">
            <div>
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Tên phòng</label
              >
              <input
                v-model="roomForm.roomName"
                type="text"
                required
                placeholder="VD: Phòng khách"
                class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div>
                <label class="block text-xs font-semibold text-gray-500 mb-1"
                  >Loại phòng</label
                >
                <input
                  v-model="roomForm.roomType"
                  type="text"
                  placeholder="VD: Living room"
                  class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
              <div>
                <label class="block text-xs font-semibold text-gray-500 mb-1"
                  >Diện tích</label
                >
                <input
                  v-model="roomForm.area"
                  type="number"
                  min="0"
                  step="0.1"
                  placeholder="m²"
                  class="w-full px-3 py-2.5 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
            </div>

            <div class="flex space-x-2.5 pt-1">
              <button
                type="button"
                @click="closeAddModal"
                class="flex-1 px-3 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="flex-1 px-3 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 shadow-md"
                :disabled="submitting"
              >
                {{ submitting ? "Đang lưu..." : "Lưu phòng" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
