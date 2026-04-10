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
        :class="
          device.isOn
            ? 'bg-white border-blue-100 shadow-md shadow-blue-50/50'
            : 'bg-gray-50 border-gray-100 shadow-sm'
        "
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

        <!-- ICON + TOGGLE -->
        <div class="flex justify-between items-start mb-4">
          <div
            class="p-3 rounded-full"
            :class="
              device.isOn
                ? 'bg-blue-50 text-blue-500'
                : 'bg-white text-gray-400 border border-gray-100'
            "
          >
            <i :data-lucide="getIcon(device.type)" class="w-6 h-6"></i>
          </div>

          <label class="flex items-center cursor-pointer" @click.stop>
            <div class="relative">
              <input
                type="checkbox"
                class="sr-only toggle-checkbox"
                :checked="device.isOn"
                @change="toggleDevice(device)"
              />
              <div
                class="toggle-label block w-12 h-7 rounded-full transition-colors duration-300"
                :class="device.isOn ? 'bg-blue-500' : 'bg-gray-300'"
              >
                <div
                  class="toggle-circle absolute left-1 top-1 bg-white w-5 h-5 rounded-full transition-transform duration-300"
                  :class="device.isOn ? 'translate-x-5' : 'translate-x-0'"
                ></div>
              </div>
            </div>
          </label>
        </div>

        <!-- NAME + ROOM + POWER -->
        <div>
          <h3
            class="font-semibold text-base mb-1"
            :class="device.isOn ? 'text-gray-800' : 'text-gray-500'"
          >
            {{ device.deviceName }}
          </h3>
          <div class="flex justify-between items-center text-sm">
            <span class="text-gray-400">{{ device.roomName }}</span>
            <span
              class="font-medium"
              :class="device.isOn ? 'text-amber-500' : 'text-gray-400'"
            >
              {{ device.isOn ? device.powerRating + "W" : "Tắt" }}
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
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm"
        @click.self="closeDeviceDetail"
      >
        <div class="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl">
          <h3 class="text-lg font-bold mb-4">Thông tin thiết bị</h3>
          <div class="space-y-2 text-gray-700">
            <p><strong>Tên:</strong> {{ selectedDevice?.deviceName }}</p>
            <p><strong>Phòng:</strong> {{ selectedDevice?.roomName }}</p>
            <p>
              <strong>Công suất:</strong> {{ selectedDevice?.powerRating }} W
            </p>
            <p>
              <strong>Giờ sử dụng: </strong>
              {{ selectedDevice?.hours ? selectedDevice.hours.toFixed(4) : 0 }}
              giờ
            </p>
            <p>
              <strong>Trạng thái: </strong>
              <span
                :class="
                  selectedDevice?.isOn
                    ? 'text-green-600 font-medium'
                    : 'text-gray-400'
                "
              >
                {{ selectedDevice?.isOn ? "Đang bật" : "Đã tắt" }}
              </span>
            </p>
            <p>
              <strong>Tiêu thụ hôm nay: </strong>
              <span class="font-medium text-blue-600">
                {{
                  selectedDevice?.kwhToday
                    ? selectedDevice.kwhToday.toFixed(4)
                    : 0
                }}
              </span>
            </p>
            <p>
              <strong>Chi phí ước tính: </strong>
              <span class="font-medium text-amber-600">
                {{ formatMoney(selectedDevice?.costToday || 0) }}
              </span>
            </p>
          </div>
          <button
            @click="closeDeviceDetail"
            class="mt-5 w-full py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors"
          >
            Đóng
          </button>
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
        @click.self="closeAddModal"
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
              @click="closeAddModal"
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
import { ref, computed, watch, nextTick, onMounted } from "vue";

const devices = ref([]);
const selectedDevice = ref(null);

const editingDeviceId = ref(null);

const rooms = ref([]);
const deviceTypes = ref([]);

const loadingId = ref(null);

const showDetailModal = ref(false);
const showAddModal = ref(false);
const isEditMode = ref(false);
const showEditModal = ref(false);

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

function toggleEditMode() {
  isEditMode.value = !isEditMode.value;
}

// Render lại icons
async function refreshIcons() {
  await nextTick();
  if (typeof lucide !== "undefined") lucide.createIcons();
}

// Re-render lucide icons sau mỗi thay đổi DOM
watch([devices, isEditMode], refreshIcons, { deep: true });

onMounted(refreshIcons);

// Lấy danh sách thiết bị
async function loadDevices() {
  const token = localStorage.getItem("token");

  const res = await fetch("http://localhost:8080/api/v1/energy", {
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
    isOn: d.isOn,
    brand: d.brand,
    modelNumber: d.modelNumber,
  }));
}

onMounted(loadDevices);

// Lấy danh sách phòng và loại thiết bị (hiển thị trong form thêm/sửa)
onMounted(async () => {
  const r = await fetch("http://localhost:8080/api/v1/rooms");
  rooms.value = await r.json();

  const t = await fetch("http://localhost:8080/api/v1/device-types");
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

    const res = await fetch(`http://localhost:8080/api/v1/energy/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await res.json();
    console.log("Device detail:", data);

    selectedDevice.value = data;
    showDetailModal.value = true;
  } catch (err) {
    console.error(err);
    alert("Không lấy được chi tiết thiết bị");
  }
}

function closeDeviceDetail() {
  showDetailModal.value = false;
  selectedDeviceId.value = null;
}

// Thêm thiết bị mới
const newDevice = ref({
  name: "",
  room: null,
  type: null,
  powerW: null,
  hours: null,
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
    console.log("TOKEN:", token);

    const res = await fetch("http://localhost:8080/api/v1/energy", {
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
        hours: Number(newDevice.value.hours) || 0,
      }),
    });

    if (!res.ok) {
      const text = await res.text();
      console.error("Backend error: ", text);
      throw new Error("Thêm thiết bị thất bại");
    }

    const data = await res.json();
    console.log("Created: ", data);

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
    room: "Phòng khách",
    type: "light",
    powerW: null,
    hours: null,
  };
}

// Bật/tắt thiết bị
async function toggleDevice(device) {
  try {
    loadingId.value = device.id;

    const token = localStorage.getItem("token");

    const url = device.isOn
      ? `http://localhost:8080/api/v1/energy/${device.id}/off`
      : `http://localhost:8080/api/v1/energy/${device.id}/on`;

    const res = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(text);
    }

    // Update lại UI
    device.isOn = !device.isOn;

    console.log("Toggle success:", device.id);
  } catch (err) {
    console.error("Toggle error:", err);
    alert("Lỗi bật/tắt thiết bị");
  } finally {
    loadingId.value = null;
  }
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

    const res = await fetch(
      `http://localhost:8080/api/v1/energy/${editingDeviceId.value}`,
      {
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
      },
    );

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

    const res = await fetch(`http://localhost:8080/api/v1/energy/${id}`, {
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

    console.log("Deleted:", id);
  } catch (err) {
    console.error(err);
    alert("Xóa thiết bị thất bại");
  }
}
</script>
