<script setup>
import { computed, nextTick, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";

const API_BASE =
  window.location.port === "5173" || window.location.port === "4173"
    ? "http://localhost:8080/api/v1"
    : "/api/v1";

const router = useRouter();

const home = ref(null);
const rooms = ref([]);
const devices = ref([]);

const showRoomModal = ref(false);
const showAddModal = ref(false);
const selectedRoom = ref(null);

const roomForm = ref({
  roomName: "",
  roomType: "",
  area: null,
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

function formatArea(area) {
  if (area === null || area === undefined || area === "") {
    return "Chưa có";
  }

  return `${new Intl.NumberFormat("vi-VN", {
    maximumFractionDigits: 1,
  }).format(Number(area))} m²`;
}

function formatNumber(value) {
  return new Intl.NumberFormat("vi-VN").format(Number(value || 0));
}

function getRoomDevices(roomId) {
  return devices.value.filter((device) => device.roomId === roomId);
}

function openRoomDetail(room) {
  selectedRoom.value = room;
  showRoomModal.value = true;
}

function closeRoomDetail() {
  showRoomModal.value = false;
  selectedRoom.value = null;
}

function openAddModal() {
  showAddModal.value = true;
}

function closeAddModal() {
  showAddModal.value = false;
  roomForm.value = {
    roomName: "",
    roomType: "",
    area: null,
  };
}

async function loadHome() {
  const response = await fetch(`${API_BASE}/homes?page=0&size=1`, {
    headers: authHeaders(),
  });

  const result = await parseApiResponse(response);

  if (!response.ok) {
    throw new Error(result.message || "Không tải được dữ liệu nhà");
  }

  home.value = result?.data?.content?.[0] || null;
}

async function loadRooms() {
  if (!home.value?.id) {
    rooms.value = [];
    return;
  }

  const response = await fetch(
    `${API_BASE}/rooms/by-home?homeId=${home.value.id}`,
    {
      headers: authHeaders(),
    },
  );

  const result = await parseApiResponse(response);

  if (!response.ok) {
    throw new Error(result.message || "Không tải được danh sách phòng");
  }

  rooms.value = Array.isArray(result) ? result : [];
}

async function loadDevices() {
  const response = await fetch(`${API_BASE}/energy`, {
    headers: authHeaders(),
  });

  const result = await response.json();

  devices.value = result.map((device) => ({
    id: device.id,
    deviceName: device.deviceName,
    roomName: device.roomName,
    roomId: device.roomId,
    deviceTypeId: device.deviceTypeId,
    powerRating: device.powerRating,
    brand: device.brand,
    modelNumber: device.modelNumber,
  }));
}

async function loadPageData() {
  const token = localStorage.getItem("token");
  if (!token) {
    router.push("/login");
    return;
  }

  try {
    await loadHome();
    await Promise.all([loadRooms(), loadDevices()]);
  } catch (error) {
    console.error(error);
    alert(error.message || "Không tải được dữ liệu phòng");
  }
}

async function submitRoom() {
  if (!home.value?.id) {
    alert("Không xác định được ngôi nhà hiện tại.");
    return;
  }

  if (!roomForm.value.roomName.trim()) {
    alert("Vui lòng nhập tên phòng.");
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/rooms`, {
      method: "POST",
      headers: authHeaders({
        "Content-Type": "application/json",
      }),
      body: JSON.stringify({
        homeId: home.value.id,
        roomName: roomForm.value.roomName.trim(),
        roomType: roomForm.value.roomType.trim(),
        area:
          roomForm.value.area === null || roomForm.value.area === ""
            ? null
            : Number(roomForm.value.area),
      }),
    });

    const result = await parseApiResponse(response);

    if (!response.ok) {
      throw new Error(result.message || "Thêm phòng thất bại");
    }

    await loadRooms();
    closeAddModal();
    alert("Đã thêm phòng mới.");
  } catch (error) {
    console.error(error);
    alert(error.message || "Thêm phòng thất bại");
  }
}

const selectedRoomDevices = computed(() => {
  if (!selectedRoom.value) return [];
  return getRoomDevices(selectedRoom.value.id);
});

const totalDevices = computed(() => devices.value.length);
const totalArea = computed(() =>
  rooms.value.reduce((sum, room) => sum + (Number(room.area) || 0), 0),
);

watch([rooms, devices, showRoomModal, showAddModal], refreshIcons, {
  deep: true,
});

onMounted(async () => {
  await loadPageData();
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

      <div class="p-4 border-t border-gray-100">
        <button
          type="button"
          @click="doLogout"
          class="w-full flex items-center px-4 py-3 text-red-600 hover:bg-red-50 rounded-xl font-medium transition-colors"
        >
          <i data-lucide="log-out" class="mr-3 w-5 h-5"></i>
          Đăng xuất
        </button>
      </div>
    </aside>

    <main class="flex-1 flex flex-col h-screen overflow-hidden relative">
      <header
        class="h-16 flex items-center justify-between px-6 bg-white/80 backdrop-blur-md border-b border-gray-100 sticky top-0 z-10"
      >
        <div>
          <h1 class="text-xl font-semibold text-gray-800">Quản lý phòng</h1>
          <p class="text-sm text-gray-400">
            {{ home?.homeName || "Ngôi nhà của bạn" }}
          </p>
        </div>
        <button
          @click="openAddModal"
          class="hidden sm:inline-flex items-center px-4 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 shadow-md"
        >
          <i data-lucide="plus" class="w-4 h-4 mr-2"></i>
          Thêm phòng
        </button>
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
            <p class="text-sm text-gray-400 mb-1">Thiết bị trong nhà</p>
            <div class="text-3xl font-bold text-gray-800">
              {{ totalDevices }}
            </div>
          </div>
          <div
            class="bg-white rounded-2xl p-5 border border-blue-100 shadow-sm shadow-blue-50/50"
          >
            <p class="text-sm text-gray-400 mb-1">Tổng diện tích phòng</p>
            <div class="text-3xl font-bold text-gray-800">
              {{ formatNumber(totalArea) }} m²
            </div>
          </div>
        </section>

        <section>
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-bold text-gray-800">Danh sách phòng</h2>
            <span class="text-sm text-gray-500"
              >Nhấn vào phòng để xem thiết bị</span
            >
          </div>

          <div
            v-if="rooms.length"
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
                  {{ getRoomDevices(room.id).length }} thiết bị
                </span>
              </div>

              <h3 class="text-lg font-semibold text-gray-800 mb-1">
                {{ room.roomName }}
              </h3>
              <p class="text-sm text-gray-500 mb-3">
                {{ room.roomType || "Chưa có loại phòng" }}
              </p>

              <div
                class="flex items-center justify-between text-sm text-gray-500"
              >
                <span>{{ formatArea(room.area) }}</span>
                <span class="text-blue-600 font-medium">Xem chi tiết</span>
              </div>

              <div
                v-if="getRoomDevices(room.id).length"
                class="mt-4 flex flex-wrap gap-2"
              >
                <span
                  v-for="device in getRoomDevices(room.id).slice(0, 3)"
                  :key="device.id"
                  class="px-2.5 py-1 rounded-full bg-gray-50 text-gray-600 text-xs"
                >
                  {{ device.deviceName }}
                </span>
              </div>
            </button>
          </div>

          <div
            v-else
            class="bg-white rounded-2xl border border-dashed border-gray-200 p-8 text-center text-gray-400"
          >
            Chưa có phòng nào. Nhấn “Thêm phòng” để tạo phòng đầu tiên.
          </div>
        </section>
      </div>

      <button
        @click="openAddModal"
        class="fixed bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg shadow-blue-500/40 hover:scale-105 transition-transform z-40"
      >
        <i data-lucide="plus" class="w-7 h-7"></i>
      </button>
    </main>

    <Teleport to="body">
      <div
        v-if="showRoomModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/45 backdrop-blur-sm px-4"
        @click.self="closeRoomDetail"
      >
        <div
          class="bg-white w-full max-w-2xl rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-5 py-4 border-b border-gray-100 flex items-center justify-between"
          >
            <div>
              <h3 class="text-lg font-bold text-gray-800">
                {{ selectedRoom?.roomName || "Chi tiết phòng" }}
              </h3>
              <p class="text-sm text-gray-400">
                {{ selectedRoom?.roomType || "Chưa có loại phòng" }}
              </p>
            </div>
            <button
              @click="closeRoomDetail"
              class="text-gray-400 hover:text-gray-600 bg-gray-50 p-1.5 rounded-full"
            >
              <i data-lucide="x" class="w-4 h-4"></i>
            </button>
          </div>

          <div class="p-5 space-y-4">
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div
                class="rounded-xl border border-gray-100 bg-gray-50 px-3 py-2"
              >
                <p class="text-gray-400 text-xs">Diện tích</p>
                <p class="font-semibold text-gray-800">
                  {{ formatArea(selectedRoom?.area) }}
                </p>
              </div>
              <div class="rounded-xl border border-gray-100 px-3 py-2">
                <p class="text-gray-400 text-xs">Số thiết bị</p>
                <p class="font-semibold text-gray-800">
                  {{ selectedRoomDevices.length }}
                </p>
              </div>
            </div>

            <div>
              <p class="font-semibold text-gray-800 mb-2">
                Thiết bị trong phòng
              </p>
              <div
                v-if="selectedRoomDevices.length"
                class="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-80 overflow-auto pr-1"
              >
                <div
                  v-for="device in selectedRoomDevices"
                  :key="device.id"
                  class="rounded-xl border border-gray-100 bg-white p-3 shadow-sm"
                >
                  <div class="flex items-start justify-between gap-3">
                    <div>
                      <p class="font-semibold text-gray-800">
                        {{ device.deviceName }}
                      </p>
                      <p class="text-sm text-gray-500">
                        {{ device.brand || "Không rõ thương hiệu" }}
                      </p>
                    </div>
                    <span
                      class="text-xs font-medium text-amber-600 bg-amber-50 px-2 py-1 rounded-full"
                    >
                      {{ device.powerRating }}W
                    </span>
                  </div>
                </div>
              </div>
              <div
                v-else
                class="rounded-xl border border-dashed border-gray-200 p-4 text-sm text-gray-400"
              >
                Phòng này chưa có thiết bị nào.
              </div>
            </div>

            <button
              @click="closeRoomDetail"
              class="w-full py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </Teleport>

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
                  v-model.number="roomForm.area"
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
              >
                Lưu phòng
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
