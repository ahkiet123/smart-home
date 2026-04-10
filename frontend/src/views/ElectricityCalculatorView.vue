<template>
  <div class="calculator-page-wrapper">
    <header class="page-header container-max flex-shrink-0">
      <div class="header-left">
        <button @click="router.push('/home')" class="btn-back-luxury">
          <i data-lucide="arrow-left" class="w-4 h-4"></i>
          <span>Dashboard</span>
        </button>
      </div>
      <div class="header-right">
        <h1 class="main-title">Dự toán tiêu thụ</h1>
        <div class="date-badge-modern">
          <span class="pulse-dot"></span>
          <p>{{ currentDate }}</p>
        </div>
      </div>
    </header>

    <main class="calculator-main-content container-max">
      <div class="layout-column column-left">
        <div class="card-luxury flex-col-container">
          <div class="fixed-top-section">
            <div class="section-header-flex mb-6">
              <h2 class="section-heading">
                <i data-lucide="database" class="w-4 h-4 text-blue-500"></i>
                Kho thiết bị
              </h2>
              <button @click="showAddForm = !showAddForm" class="btn-action-glass" :class="{ 'is-active': showAddForm }">
                {{ showAddForm ? 'Đóng form' : '+ Thêm mới' }}
              </button>
            </div>

            <transition name="slide-fade">
              <div v-if="showAddForm" class="add-form-premium">
                <div class="grid grid-cols-2 gap-4">
                  <div class="col-span-2">
                    <label class="label-tiny">Tên thiết bị</label>
                    <input v-model="newStoreDevice.name" placeholder="VD: Máy sấy tóc..." class="input-premium" />
                  </div>
                  <div>
                    <label class="label-tiny">Watts (W)</label>
                    <input v-model.number="newStoreDevice.powerW" type="number" placeholder="1500" class="input-premium" />
                  </div>
                  <div>
                    <label class="label-tiny">Khu vực</label>
                    <select v-model="newStoreDevice.room" class="input-premium">
                      <option>Phòng khách</option>
                      <option>Phòng ngủ</option>
                      <option>Bếp</option>
                      <option>Phòng tắm</option>
                    </select>
                  </div>
                  <button @click="saveToStorage" class="btn-save-premium col-span-2 shadow-blue">Lưu hệ thống</button>
                </div>
              </div>
            </transition>

            <div class="selector-container mb-6">
              <label class="label-main">Thêm thiết bị vào bảng tính:</label>
              <div class="flex gap-3 mt-2">
                <select v-model="selectedSampleId" class="select-premium flex-1">
                  <option :value="null">-- Chọn từ kho lưu trữ --</option>
                  <option v-for="s in samples" :key="s.id" :value="s.id">
                    {{ s.name }} ({{ s.powerW }}W)
                  </option>
                </select>
                <button @click="addDeviceToCalculator" class="btn-plus-premium">
                  <i data-lucide="plus" class="w-6 h-6"></i>
                </button>
              </div>
            </div>
          </div>

          <div class="scroll-wrapper-inner">
            <p class="scroll-title mb-4">Đang tính toán ({{ calcList.length }})</p>

            <div v-if="calcList.length === 0" class="empty-state-card">
              <i data-lucide="shopping-cart" class="w-10 h-10 opacity-10 mb-2"></i>
              <p>Chưa có thiết bị nào</p>
            </div>

            <div class="space-y-4 pr-2 pb-6">
              <div v-for="(item, index) in calcList" :key="index" class="device-card-premium">
                <div class="flex justify-between items-start mb-4">
                  <div class="info-group">
                    <h4 class="device-name-luxury">{{ item.name }}</h4>
                    <div class="badge-row mt-2">
                      <span class="badge-tag blue">{{ item.room }}</span>
                      <span class="badge-tag slate">{{ item.powerW }}W</span>
                    </div>
                  </div>
                  <button @click="removeDevice(index)" class="btn-remove-luxury">
                    <i data-lucide="trash-2" class="w-4 h-4"></i>
                  </button>
                </div>

                <div class="adjustment-row-premium">
                  <div class="slider-wrapper">
                    <div class="flex justify-between items-center mb-1">
                      <span class="label-tiny">Dùng: <b class="text-blue-600">{{ item.hours }}H</b></span>
                    </div>
                    <input type="range" v-model="item.hours" min="0.5" max="24" step="0.5" class="slider-premium" />
                  </div>
                  <div class="qty-wrapper">
                    <span class="label-tiny block mb-1">SL:</span>
                    <input v-model.number="item.quantity" type="number" min="1" class="input-qty-premium" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="layout-column column-right">
        <div class="card-luxury result-section-fixed shadow-2xl">
          <div class="relative z-10 flex flex-col h-full">
            <h3 class="result-heading-luxury">
              <i data-lucide="receipt-text" class="text-blue-600"></i>
              Hóa đơn dự tính
            </h3>

            <div class="flex-1 space-y-6">
              <div class="price-display-box shadow-blue">
                <p class="month-tag">Tháng {{ currentMonth }} / 2026</p>
                <h2 class="main-price-text">{{ formatPrice(totalCostMonth) }}</h2>

                <div class="mini-stats-row">
                  <div class="stat-item">
                    <p class="label">Tiêu thụ</p>
                    <p class="val">{{ totalKwhMonth.toFixed(1) }} <span>kWh</span></p>
                  </div>
                  <div class="v-divider"></div>
                  <div class="stat-item">
                    <p class="label">Đơn giá TB</p>
                    <p class="val">2,500 <span>đ</span></p>
                  </div>
                </div>
              </div>

              <button @click="openChart" class="btn-forecast-luxury group">
                <div class="flex flex-col text-left">
                  <span class="label-tiny opacity-50 uppercase">Xem phân bổ điện năng 24h</span>
                  <span class="forecast-price">{{ formatPrice(totalCostDay) }} / ngày</span>
                </div>
                <div class="icon-circle shadow-sm group-hover:bg-blue-600 group-hover:text-white transition-all">
                  <i data-lucide="bar-chart-3" class="w-5 h-5"></i>
                </div>
              </button>
            </div>

            <footer class="footer-note-premium border-t pt-4">
              <p>* Dựa trên công suất hoạt động dự kiến</p>
            </footer>
          </div>
        </div>
      </div>
    </main>

    <div v-if="modal.show" class="modal-overlay-glass" @click.self="modal.show = false">
      <div class="modal-content-luxury animate-in zoom-in">
        <button @click="modal.show = false" class="btn-close-modal-luxury">&times;</button>

        <div class="modal-header-luxury mb-4">
          <h3 class="modal-title-luxury">{{ modal.title }}</h3>
          <p class="modal-sub-luxury">Phân bổ chi phí dự tính từng khung giờ trong ngày</p>
        </div>

        <div class="chart-viewport-premium">
          <div v-for="item in modal.data" :key="item.label" class="chart-column-group">
            <div class="bar-track-premium bg-slate-50">
              <div class="bar-pillar-premium" :style="{ height: (item.value / modal.max) * 80 + '%' }">
                <span class="bar-price-tag shadow-xl">{{ formatPrice(item.value) }}</span>
              </div>
            </div>
            <span class="bar-label-time">{{ item.label }}</span>
          </div>
        </div>

        <div class="mt-8 flex justify-center">
          <button @click="modal.show = false" class="btn-close-final-luxury">Xác nhận</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const PRICE_PER_KWH = 2500;

const dateObj = new Date();
const currentDate = dateObj.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
const currentMonth = dateObj.getMonth() + 1;

const samples = ref([
  { id: 101, name: 'Đèn chùm phòng khách', room: 'Phòng khách', powerW: 100, hours: 5 },
  { id: 102, name: 'Smart TV 55 inch', room: 'Phòng khách', powerW: 150, hours: 4 },
  { id: 103, name: 'Máy lạnh (Living)', room: 'Phòng khách', powerW: 1200, hours: 3 },
  { id: 401, name: 'Tủ lạnh Inverter', room: 'Bếp', powerW: 150, hours: 24 },
  { id: 501, name: 'Bình nóng lạnh', room: 'Phòng tắm', powerW: 2500, hours: 1 }
]);

const showAddForm = ref(false);
const newStoreDevice = reactive({ name: '', powerW: null, room: 'Phòng khách' });

const saveToStorage = () => {
  if (!newStoreDevice.name || !newStoreDevice.powerW) return alert("Vui lòng nhập đủ thông tin!");
  samples.value.push({ ...newStoreDevice, id: Date.now(), hours: 1 });
  newStoreDevice.name = ''; newStoreDevice.powerW = null;
  showAddForm.value = false;
};

const selectedSampleId = ref(null);
const calcList = reactive([]);

const addDeviceToCalculator = () => {
  const sample = samples.value.find(s => s.id === selectedSampleId.value);
  if (sample) {
    calcList.push({ ...sample, quantity: 1 });
    selectedSampleId.value = null;
    setTimeout(() => window.lucide?.createIcons(), 50);
  }
};
const removeDevice = (index) => calcList.splice(index, 1);

const totalKwhDay = computed(() => calcList.reduce((sum, item) => sum + (item.powerW * item.hours * item.quantity / 1000), 0));
const totalCostDay = computed(() => totalKwhDay.value * PRICE_PER_KWH);
const totalCostMonth = computed(() => totalCostDay.value * 30);
const totalKwhMonth = computed(() => totalKwhDay.value * 30);

const modal = reactive({ show: false, title: "", data: [], max: 1 });

const openChart = () => {
  if (calcList.length === 0) return alert("Hãy thêm thiết bị trước!");
  modal.show = true;
  modal.title = "Phân bổ 24 Giờ";
  modal.data = Array.from({length: 24}, (_, i) => {
    let hourlyPower = 0;
    calcList.forEach(item => { if (i < item.hours) hourlyPower += (item.powerW * item.quantity); });
    return { label: `${i}h`, value: (hourlyPower / 1000) * PRICE_PER_KWH };
  });
  modal.max = Math.max(...modal.data.map(d => d.value), 1);
  setTimeout(() => window.lucide?.createIcons(), 100);
};

const formatPrice = (val) => new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND", maximumFractionDigits: 0 }).format(val);
onMounted(() => { if (window.lucide) window.lucide.createIcons(); });
</script>

<style scoped>
/* 1. GLOBAL LAYOUT - FIX BREAKING */
.calculator-page-wrapper {
  background-color: #f8fafc;
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden; /* Ngăn trang tổng bị vỡ */
}

.container-max {
  max-width: 1300px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.calculator-main-content {
  display: flex;
  flex: 1;
  gap: 24px;
  padding: 0 24px 24px 24px;
  overflow: hidden; /* Quan trọng để scroll nội bộ hoạt động */
}

/* 2. HEADER */
.page-header { padding: 24px 24px; display: flex; align-items: center; justify-content: space-between; }
.btn-back-luxury {
  display: flex; align-items: center; gap: 8px; padding: 10px 16px;
  background: white; border: 1px solid #e2e8f0; border-radius: 14px;
  color: #64748b; font-size: 13px; font-weight: 700; transition: 0.3s;
}
.btn-back-luxury:hover { transform: translateX(-4px); border-color: #cbd5e1; }
.main-title { font-size: 1.5rem; font-weight: 900; color: #0f172a; }
.date-badge-modern { display: flex; align-items: center; gap: 6px; }
.date-badge-modern p { font-size: 11px; font-weight: 800; color: #3b82f6; text-transform: uppercase; }
.pulse-dot { width: 6px; height: 6px; background: #3b82f6; border-radius: 50%; animation: pulse 2s infinite; }

/* 3. COLUMN CONTROLS */
.layout-column { display: flex; flex-direction: column; height: 100%; }
.column-left { flex: 1.3; min-width: 0; }
.column-right { flex: 0.7; min-width: 360px; }

.card-luxury {
  background: #ffffff; border-radius: 32px; border: 1px solid #edf2f7; padding: 24px; box-sizing: border-box;
}

/* FLEXCONTAINER FOR SCROLLING */
.flex-col-container { display: flex; flex-direction: column; height: 100%; overflow: hidden; }

.fixed-top-section { flex-shrink: 0; }
.scroll-wrapper-inner { flex: 1; overflow-y: auto; padding-right: 8px; margin-top: 10px; }
.scroll-wrapper-inner::-webkit-scrollbar { width: 5px; }
.scroll-wrapper-inner::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

/* UI COMPONENTS */
.section-header-flex { display: flex; justify-content: space-between; align-items: center; }
.section-heading { font-size: 13px; font-weight: 900; text-transform: uppercase; display: flex; align-items: center; gap: 8px; }
.btn-action-glass { font-size: 10px; font-weight: 800; padding: 6px 12px; border-radius: 10px; background: #f1f5f9; color: #64748b; text-transform: uppercase; }
.btn-action-glass.is-active { background: #fee2e2; color: #ef4444; }

.add-form-premium { background: #f8fafc; padding: 20px; border-radius: 24px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
.input-premium { width: 100%; padding: 12px 14px; border: 1px solid #e2e8f0; border-radius: 12px; font-weight: 700; font-size: 13px; outline: none; }
.btn-save-premium { background: #2563eb; color: white; padding: 12px; border-radius: 12px; font-weight: 800; text-transform: uppercase; font-size: 11px; }

.select-premium { padding: 12px; border: 1px solid #e2e8f0; border-radius: 12px; font-weight: 800; font-size: 13px; outline: none; background: #fff; cursor: pointer; }
.btn-plus-premium { padding: 12px; background: #0f172a; color: #fff; border-radius: 12px; transition: 0.2s; }
.btn-plus-premium:hover { background: #2563eb; }

/* DEVICE CARDS */
.device-card-premium { background: #fff; border: 1px solid #f1f5f9; border-radius: 24px; padding: 18px; transition: 0.3s; }
.device-card-premium:hover { border-color: #3b82f6; box-shadow: 0 10px 20px rgba(0,0,0,0.02); }
.device-name-luxury { font-size: 1.1rem; font-weight: 900; color: #1e293b; }
.badge-tag { font-size: 9px; font-weight: 800; padding: 3px 8px; border-radius: 20px; text-transform: uppercase; }
.badge-tag.blue { background: #eff6ff; color: #2563eb; }
.badge-tag.slate { background: #f1f5f9; color: #64748b; }

.adjustment-row-premium { display: grid; grid-template-columns: 1fr 80px; gap: 16px; align-items: center; background: #f8fafc; padding: 12px; border-radius: 16px; margin-top: 12px; }
.input-qty-premium { width: 100%; text-align: center; border: 1px solid #e2e8f0; border-radius: 8px; padding: 6px; font-weight: 900; font-size: 16px; }

/* RESULT BOX */
.result-section-fixed { border: 2.5px solid #eff6ff; position: relative; }
.result-heading-luxury { font-size: 13px; font-weight: 900; text-transform: uppercase; border-bottom: 1px solid #f1f5f9; padding-bottom: 16px; margin-bottom: 24px; display: flex; align-items: center; gap: 8px; }

.price-display-box { background: linear-gradient(135deg, #2563eb, #1d4ed8); padding: 32px; border-radius: 28px; color: #fff; }
.main-price-text { font-size: 2.8rem; font-weight: 900; margin: 12px 0 24px 0; letter-spacing: -1.5px; }

.mini-stats-row { display: flex; align-items: center; gap: 16px; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 20px; }
.stat-item .val { font-size: 1.1rem; font-weight: 900; }
.stat-item .val span { font-size: 10px; opacity: 0.6; }
.v-divider { width: 1px; height: 28px; background: rgba(255,255,255,0.2); }

.btn-forecast-luxury {
  width: 100%; display: flex; justify-content: space-between; align-items: center; padding: 20px;
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 20px; cursor: pointer; transition: 0.2s;
}
.btn-forecast-luxury:hover { border-color: #3b82f6; background: #fff; }
.icon-circle { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 10px; color: #3b82f6; }

.footer-note-premium p { font-size: 9px; font-weight: 800; color: #cbd5e1; text-transform: uppercase; text-align: center; }

/* MODAL & CHART */
.modal-overlay-glass { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.4); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 2000; }
.modal-content-luxury { background: #fff; padding: 40px; border-radius: 40px; width: 90%; max-width: 800px; position: relative; }
.btn-close-modal-luxury { position: absolute; top: 24px; right: 24px; font-size: 1.5rem; color: #cbd5e1; }

.chart-viewport-premium { display: flex; align-items: flex-end; height: 280px; gap: 6px; margin-top: 50px; justify-content: center; }
.chart-column-group { flex: 1; display: flex; flex-direction: column; align-items: center; min-width: 20px; }
.bar-track-premium { width: 100%; height: 220px; display: flex; align-items: flex-end; justify-content: center; border-radius: 4px; position: relative; }

.bar-pillar-premium { width: 60%; background: #3b82f6; border-radius: 4px 4px 2px 2px; position: relative; transition: height 0.6s ease-out; }
.bar-price-tag {
  position: absolute; top: -35px; left: 50%; transform: translateX(-50%);
  background: #0f172a; color: white; padding: 4px 8px; border-radius: 8px;
  font-size: 8px; font-weight: 900; white-space: nowrap;
}

.bar-label-time { font-size: 9px; font-weight: 800; color: #94a3b8; margin-top: 12px; }
.btn-close-final-luxury { background: #0f172a; color: #fff; padding: 12px 32px; border-radius: 12px; font-weight: 800; text-transform: uppercase; font-size: 11px; }

/* ANIMATION */
@keyframes pulse { 0% { opacity: 0.6; } 50% { opacity: 1; } 100% { opacity: 0.6; } }
.slide-fade-enter-active, .slide-fade-leave-active { transition: 0.3s; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(-10px); }

@media (max-width: 1024px) {
  .calculator-main-content { flex-direction: column; overflow-y: auto; height: auto; }
  .calculator-page-wrapper { height: auto; overflow-y: auto; }
  .column-right { min-width: 100%; }
}
</style>