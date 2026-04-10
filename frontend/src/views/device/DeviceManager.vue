<template>
  <section>
    <!-- HEADER -->
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-bold text-gray-800">Thiết bị trong nhà</h2>
      <div class="flex items-center space-x-3">
        <button
          @click="toggleEditMode"
          class="text-sm font-medium flex items-center px-3 py-1.5 rounded-lg transition-colors"
          :class="
            isEditMode
              ? 'bg-blue-100 text-blue-700'
              : 'text-gray-600 hover:bg-gray-100'
          "
        >
          <i data-lucide="edit-2" class="w-4 h-4 mr-1.5"></i>
          <span>{{ isEditMode ? "Hoàn tất" : "Quản lý" }}</span>
        </button>
      </div>
    </div>

    <!-- DEVICE GRID -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div
        v-for="device in devices"
        :key="device.id"
        @click="openDeviceDetail(device.id)"
        class="relative p-5 rounded-2xl border transition-all duration-200 cursor-pointer hover:scale-[1.02]"
        :class="'bg-white border-blue-100 shadow-md shadow-blue-50/50'"
      >
        <!-- EDIT BUTTON -->
        <button
          v-if="isEditMode"
          @click.stop="openEditModal(device)"
          class="absolute -top-2 left-2 w-7 h-7 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center hover:bg-blue-500 hover:text-white shadow-sm z-10"
        >
          <i data-lucide="edit-2" class="w-3.5 h-3.5"></i>
        </button>
        <!-- DELETE BUTTON (edit mode) -->
        <button
          v-if="isEditMode"
          @click.stop="deleteDevice(device.id)"
          class="absolute -top-2 -right-2 w-7 h-7 bg-red-100 text-red-600 rounded-full flex items-center justify-center hover:bg-red-500 hover:text-white shadow-sm z-10"
        >
          <i data-lucide="trash-2" class="w-3.5 h-3.5"></i>
        </button>

        <!-- ICON + ACTION -->
        <div class="flex justify-between items-start mb-4">
          <div class="p-3 rounded-full" :class="'bg-blue-50 text-blue-500'">
            <i :data-lucide="getIcon(device.type)" class="w-6 h-6"></i>
          </div>

          <button
            @click.stop="openUsageModal(device)"
            class="px-3 py-1.5 text-xs font-semibold rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 transition-colors"
          >
            Ghi nhận
          </button>
        </div>

        <!-- NAME + ROOM + POWER -->
        <div>
          <h3 class="font-semibold text-base mb-1" :class="'text-gray-800'">
            {{ device.deviceName }}
          </h3>
          <div class="flex justify-between items-center text-sm">
            <span class="text-gray-400">{{ device.roomName }}</span>
            <span class="font-medium text-amber-500">
              {{ device.powerRating }}W
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- FAB: Thêm thiết bị -->
    <button
      @click="openAddModal"
      class="fixed bottom-6 right-6 lg:bottom-8 lg:right-8 w-14 h-14 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-full flex items-center justify-center shadow-lg shadow-blue-500/40 hover:scale-105 transition-transform z-40"
    >
      <i data-lucide="plus" class="w-7 h-7"></i>
    </button>

    <!-- Modal: Chi tiết thiết bị -->
    <Teleport to="body">
      <div
        v-if="showDetailModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/45 backdrop-blur-sm px-4"
        @click.self="closeDeviceDetail"
      >
        <div
          class="bg-white w-full max-w-xl rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-5 py-4 border-b border-gray-100 flex items-center justify-between"
          >
            <h3 class="text-lg font-bold text-gray-800">Thông tin thiết bị</h3>
            <button
              @click="closeDeviceDetail"
              class="text-gray-400 hover:text-gray-600 bg-gray-50 p-1.5 rounded-full"
            >
              <i data-lucide="x" class="w-4 h-4"></i>
            </button>
          </div>

          <div class="p-5 space-y-4">
            <div class="grid grid-cols-2 gap-2.5 text-sm">
              <div
                class="col-span-2 rounded-xl border border-gray-100 bg-gray-50 px-3 py-2"
              >
                <p class="text-gray-400 text-xs">Tên thiết bị</p>
                <p class="font-semibold text-gray-800">
                  {{ selectedDevice?.deviceName || "-" }}
                </p>
              </div>
              <div class="rounded-xl border border-gray-100 px-3 py-2">
                <p class="text-gray-400 text-xs">Phòng</p>
                <p class="font-medium text-gray-700">
                  {{ selectedDevice?.roomName || "-" }}
                </p>
              </div>
              <div class="rounded-xl border border-gray-100 px-3 py-2">
                <p class="text-gray-400 text-xs">Công suất</p>
                <p class="font-medium text-gray-700">
                  {{ selectedDevice?.powerRating || 0 }} W
                </p>
              </div>
              <div
                class="rounded-xl border border-blue-100 bg-blue-50/60 px-3 py-2"
              >
                <p class="text-blue-500 text-xs">Giờ sử dụng</p>
                <p class="font-semibold text-blue-700">
                  {{
                    selectedDevice?.hours ? selectedDevice.hours.toFixed(4) : 0
                  }}
                  giờ
                </p>
              </div>
              <div
                class="rounded-xl border border-amber-100 bg-amber-50/60 px-3 py-2"
              >
                <p class="text-amber-500 text-xs">Chi phí hôm nay</p>
                <p class="font-semibold text-amber-700">
                  {{ formatMoney(selectedDevice?.costToday || 0) }}
                </p>
              </div>
            </div>

            <div>
              <p class="font-semibold text-gray-800 mb-2">Lịch sử ghi nhận</p>
              <p class="text-xs text-gray-400 mb-2">
                Tiêu thụ hôm nay:
                <span class="text-blue-600 font-semibold">
                  {{
                    selectedDevice?.kwhToday
                      ? selectedDevice.kwhToday.toFixed(4)
                      : 0
                  }}
                  kWh
                </span>
              </p>

              <div
                v-if="selectedDevice?.usageLogs?.length"
                class="max-h-52 overflow-y-auto rounded-xl border border-gray-100"
              >
                <table class="w-full text-sm">
                  <thead
                    class="bg-gray-50 text-gray-500 text-xs uppercase sticky top-0"
                  >
                    <tr>
                      <th class="text-left px-3 py-2">Ngày</th>
                      <th class="text-right px-3 py-2">Giờ dùng</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="log in selectedDevice.usageLogs"
                      :key="log.id"
                      class="border-t border-gray-100 hover:bg-gray-50/70"
                    >
                      <td class="px-3 py-2">{{ formatDate(log.usageDate) }}</td>
                      <td class="px-3 py-2 text-right font-medium">
                        {{ log.hoursUsed ?? 0 }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div
                v-else
                class="text-sm text-gray-400 rounded-xl border border-dashed border-gray-200 p-3"
              >
                Chưa có dữ liệu ghi nhận thời gian sử dụng.
              </div>
            </div>

            <button
              @click="closeDeviceDetail"
              class="w-full py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Modal ghi nhận sử dụng -->
    <Teleport to="body">
      <div
        v-if="showUsageModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
        @click.self="closeUsageModal"
      >
        <div
          class="bg-white w-full max-w-sm rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-5 py-3.5 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-base text-gray-800">Ghi nhận sử dụng</h3>
            <button
              @click="closeUsageModal"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm"
            >
              <i data-lucide="x" class="w-4 h-4"></i>
            </button>
          </div>

          <form @submit.prevent="submitUsageLog" class="p-5 space-y-3.5">
            <div>
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Tên thiết bị</label
              >
              <div
                class="w-full px-3 py-2 border border-gray-200 rounded-xl bg-gray-50 text-gray-700 font-medium"
              >
                {{ usageForm.deviceName }}
              </div>
            </div>

            <div>
              <label class="block text-xs font-semibold text-gray-500 mb-1"
                >Số giờ sử dụng</label
              >
              <div class="relative">
                <input
                  v-model.number="usageForm.hoursUsed"
                  type="number"
                  min="0"
                  step="0.01"
                  required
                  placeholder="Ví dụ: 2.5"
                  class="w-full px-3 py-2.5 pr-12 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
                <span
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-400"
                >
                  giờ
                </span>
              </div>
              <p class="text-xs text-gray-400 mt-1">
                Nhập đúng số giờ theo thực tế sử dụng.
              </p>
            </div>

            <div class="pt-1 flex space-x-2.5">
              <button
                type="button"
                @click="closeUsageModal"
                class="flex-1 px-3 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="flex-1 px-3 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 shadow-md"
              >
                Lưu ghi nhận
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- Modal thêm thiết bị -->
    <Teleport to="body">
      <div
        v-if="showAddModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
        @click.self="closeAddModal"
      >
        <div
          class="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-lg text-gray-800">Thêm thiết bị mới</h3>
            <button
              @click="closeAddModal"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm"
            >
              <i data-lucide="x" class="w-5 h-5"></i>
            </button>
          </div>

          <form @submit.prevent="submitAddDevice" class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Tên thiết bị</label
              >
              <input
                v-model="newDevice.name"
                type="text"
                required
                placeholder="Nhập tên thiết bị"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Phòng</label
                >
                <select
                  v-model="newDevice.room"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option v-for="room in rooms" :key="room.id" :value="room.id">
                    {{ room.roomName }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Loại</label
                >
                <select
                  v-model="newDevice.type"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option
                    v-for="type in deviceTypes"
                    :key="type.id"
                    :value="type.id"
                  >
                    {{ type.typeName }}
                  </option>
                </select>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Thương hiệu</label
                >
                <input
                  v-model="newDevice.brand"
                  type="text"
                  required
                  placeholder="Nhập thương hiệu"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Mã sản phẩm</label
                >
                <input
                  v-model="newDevice.modelNumber"
                  type="text"
                  required
                  placeholder="Nhập mã sản phẩm"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Công suất (Watt)</label
              >
              <input
                v-model.number="newDevice.powerW"
                type="number"
                required
                min="1"
                placeholder="Nhập công suất"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div class="pt-4 flex space-x-3">
              <button
                type="button"
                @click="closeAddModal"
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
    </Teleport>

    <!-- Modal chỉnh sửa thiết bị -->
    <Teleport to="body">
      <div
        v-if="showEditModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
        @click.self="showEditModal = false"
      >
        <div
          class="bg-white w-full max-w-md rounded-2xl shadow-xl overflow-hidden"
        >
          <div
            class="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50/50"
          >
            <h3 class="font-bold text-lg text-gray-800">
              Chỉnh sửa thiết bị mới
            </h3>
            <button
              @click="showEditModal = false"
              class="text-gray-400 hover:text-gray-600 bg-white p-1 rounded-full shadow-sm"
            >
              <i data-lucide="x" class="w-5 h-5"></i>
            </button>
          </div>

          <form @submit.prevent="submitUpdateDevice" class="p-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Tên thiết bị</label
              >
              <input
                v-model="editDevice.name"
                type="text"
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
                  v-model="editDevice.room"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option v-for="room in rooms" :key="room.id" :value="room.id">
                    {{ room.roomName }}
                  </option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Loại</label
                >
                <select
                  v-model="editDevice.type"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                >
                  <option
                    v-for="type in deviceTypes"
                    :key="type.id"
                    :value="type.id"
                  >
                    {{ type.typeName }}
                  </option>
                </select>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Thương hiệu</label
                >
                <input
                  v-model="editDevice.brand"
                  type="text"
                  required
                  placeholder="VD: Samsung"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Mã sản phẩm</label
                >
                <input
                  v-model="editDevice.modelNumber"
                  type="text"
                  required
                  placeholder="VD: Quạt máy"
                  class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Công suất (Watt)</label
              >
              <input
                v-model.number="editDevice.powerW"
                type="number"
                required
                min="1"
                placeholder="VD: 55"
                class="w-full px-4 py-2 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>

            <div class="pt-4 flex space-x-3">
              <button
                type="button"
                @click="showEditModal = false"
                class="flex-1 px-4 py-2.5 border border-gray-200 text-gray-700 rounded-xl font-medium hover:bg-gray-50"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="flex-1 px-4 py-2.5 bg-blue-600 text-white rounded-xl font-medium hover:bg-blue-700 shadow-md"
              >
                Cập nhật
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from "vue";
import API_BASE from "../../services/api";

const devices = ref([]);
const selectedDevice = ref(null);

const editingDeviceId = ref(null);
const usageDeviceId = ref(null);

const rooms = ref([]);
const deviceTypes = ref([]);

const showDetailModal = ref(false);
const showAddModal = ref(false);
const isEditMode = ref(false);
const showEditModal = ref(false);
const showUsageModal = ref(false);

const usageForm = ref({
  deviceName: "",
  hoursUsed: null,
});

function formatMoney(n) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(n);
}

function getIcon(type) {
  const map = {
    light: "lightbulb",
    ac: "wind",
    tv: "tv",
    fridge: "thermometer",
  };
  return map[type] ?? "power";
}

function formatDate(date) {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("vi-VN");
}

// Format datetime cho bảng lịch sử trong modal chi tiết.
function formatDateTime(dateTime) {
  if (!dateTime) return "-";
  return new Date(dateTime).toLocaleString("vi-VN", {
    hour12: false,
  });
}

function toggleEditMode() {
  isEditMode.value = !isEditMode.value;
}

// Render lại icons
async function refreshIcons() {
  await nextTick();
  if (typeof lucide !== "undefined") lucide.createIcons();
}

// Re-render lucide icons sau mỗi thay đổi DOM
watch(
  [devices, isEditMode, showUsageModal, showAddModal, showEditModal],
  refreshIcons,
  {
    deep: true,
  },
);

onMounted(refreshIcons);

// Lấy danh sách thiết bị
async function loadDevices() {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_BASE}/energy`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  const data = await res.json();

  devices.value = data.map((d) => ({
    id: d.id,
    deviceName: d.deviceName,
    roomName: d.roomName,
    roomId: d.roomId,
    deviceTypeId: d.deviceTypeId,
    powerRating: d.powerRating,
    brand: d.brand,
    modelNumber: d.modelNumber,
  }));
}

onMounted(loadDevices);

// Lấy danh sách phòng và loại thiết bị (hiển thị trong form thêm/sửa)
onMounted(async () => {
  const r = await fetch(`${API_BASE}/rooms`);
  rooms.value = await r.json();

  const t = await fetch(`${API_BASE}/device-types`);
  deviceTypes.value = await t.json();

  if (rooms.value.length > 0) {
    newDevice.value.room = rooms.value[0].id;
  }

  if (deviceTypes.value.length > 0) {
    newDevice.value.type = deviceTypes.value[0].id;
  }
});

// Mở modal chi tiết thiết bị
async function openDeviceDetail(id) {
  try {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE}/energy/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await res.json();
    selectedDevice.value = data;
    showDetailModal.value = true;
  } catch (err) {
    console.error(err);
    alert("Không lấy được chi tiết thiết bị");
  }
}

function closeDeviceDetail() {
  showDetailModal.value = false;
  selectedDevice.value = null;
}

// Chuẩn hóa chuỗi datetime-local về định dạng backend LocalDateTime.
function normalizeDateTime(value) {
  if (!value) return null;
  return value.length === 16 ? `${value}:00` : value;
}

// Mở form ghi nhận sử dụng và gắn thiết bị đang thao tác.
function openUsageModal(device) {
  usageDeviceId.value = device.id;
  usageForm.value = {
    deviceName: device.deviceName,
    hoursUsed: null,
  };
  showUsageModal.value = true;
}

// Đóng modal ghi nhận sử dụng.
function closeUsageModal() {
  showUsageModal.value = false;
}

// Validate dữ liệu và gửi API tạo usage log cho thiết bị.
async function submitUsageLog() {
  if (!usageDeviceId.value) return;

  const hoursUsed =
    usageForm.value.hoursUsed !== null && usageForm.value.hoursUsed !== "";

  if (!hoursUsed) {
    alert("Vui lòng nhập số giờ sử dụng.");
    return;
  }

  try {
    const token = localStorage.getItem("token");

    const payload = {
      hoursUsed: Number(usageForm.value.hoursUsed),
    };

    const res = await fetch(
      `${API_BASE}/energy/${usageDeviceId.value}/usage-logs`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      },
    );

    if (!res.ok) {
      const errData = await res.json().catch(() => null);
      throw new Error(errData?.message || "Lưu ghi nhận thất bại");
    }

    closeUsageModal();
    alert("Đã lưu ghi nhận sử dụng.");

    if (selectedDevice.value?.id === usageDeviceId.value) {
      await openDeviceDetail(usageDeviceId.value);
    }
  } catch (err) {
    console.error(err);
    alert(err.message || "Lưu ghi nhận thất bại");
  }
}

// Thêm thiết bị mới
const newDevice = ref({
  name: "",
  room: null,
  type: null,
  powerW: null,
  brand: "",
  modelNumber: "",
});

function openAddModal() {
  showAddModal.value = true;
}

function closeAddModal() {
  showAddModal.value = false;
  resetNewDevice();
}

async function submitAddDevice() {
  if (!newDevice.value.name || !newDevice.value.powerW) return;

  try {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE}/energy`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        deviceName: newDevice.value.name,
        powerRating: Number(newDevice.value.powerW),
        roomId: newDevice.value.room,
        deviceTypeId: newDevice.value.type,
        brand: newDevice.value.brand,
        modelNumber: newDevice.value.modelNumber,
      }),
    });

    if (!res.ok) {
      const text = await res.text();
      console.error("Backend error: ", text);
      throw new Error("Thêm thiết bị thất bại");
    }
    await res.json();

    await loadDevices();
    closeAddModal();
  } catch (err) {
    console.error(err);
    alert("Thêm thiết bị lỗi!");
  }
}

function resetNewDevice() {
  newDevice.value = {
    name: "",
    room: rooms.value.length ? rooms.value[0].id : null,
    type: deviceTypes.value.length ? deviceTypes.value[0].id : null,
    powerW: null,
    brand: "",
    modelNumber: "",
  };
}

// Chỉnh sửa thiết bị
const editDevice = ref({
  name: "",
  room: null,
  type: null,
  powerW: null,
  brand: "",
  modelNumber: "",
});

function openEditModal(device) {
  editingDeviceId.value = device.id;

  editDevice.value = {
    name: device.deviceName,
    room: device.roomId || null,
    type: device.deviceTypeId || null,
    powerW: device.powerRating,
    brand: device.brand || "",
    modelNumber: device.modelNumber || "",
  };

  showEditModal.value = true;
}

async function submitUpdateDevice() {
  try {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE}/energy/${editingDeviceId.value}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        deviceName: editDevice.value.name,
        powerRating: Number(editDevice.value.powerW),
        roomId: editDevice.value.room,
        deviceTypeId: editDevice.value.type,
        brand: editDevice.value.brand,
        modelNumber: editDevice.value.modelNumber,
      }),
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text);
    }

    await loadDevices();

    showEditModal.value = false;
  } catch (err) {
    console.error(err);
    alert("Cập nhật thất bại");
  }
}

async function deleteDevice(id) {
  if (!confirm("Bạn có chắc chắn muốn xóa thiết bị này?")) return;

  try {
    const token = localStorage.getItem("token");

    const res = await fetch(`${API_BASE}/energy/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!res.ok) {
      const text = await res.text();
      console.error("Backend error:", text);
      throw new Error("Xóa thất bại");
    }

    devices.value = devices.value.filter((d) => d.id !== id);
  } catch (err) {
    console.error(err);
    alert("Xóa thiết bị thất bại");
  }
}
</script>
