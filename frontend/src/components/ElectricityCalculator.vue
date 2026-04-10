<template>
  <div class="calculator-container">
    <div class="card input-card">
      <h2>🔌 Dự toán tiêu thụ điện</h2>

      <div class="form-group">
        <label>Thiết bị mẫu:</label>
        <select v-model="selectedSample" @change="applySample">
          <option :value="null">-- Tùy chỉnh thông số --</option>
          <option v-for="s in samples" :key="s.name" :value="s">
            {{ s.name }}
          </option>
        </select>
      </div>

      <div class="grid-inputs">
        <div class="form-group">
          <label>Công suất (W):</label>
          <input type="number" v-model="power" min="1" />
        </div>
        <div class="form-group">
          <label>Số lượng:</label>
          <input type="number" v-model="quantity" min="1" />
        </div>
      </div>

      <div class="form-group">
        <label>Thời gian dùng (Giờ/Ngày): {{ hours }}h</label>
        <input
          type="range"
          v-model="hours"
          min="0.5"
          max="24"
          step="0.5"
          class="slider"
        />
      </div>

      <button @click="getEstimate" class="btn-calc" :disabled="loading">
        <span v-if="!loading">🚀 TÍNH TOÁN NGAY</span>
        <span v-else>Đang tính...</span>
      </button>
    </div>

    <div class="card result-card">
      <h3>Bảng dự toán chi tiết</h3>
      <div class="kwh-badge">
        Tiêu thụ tháng: <span>{{ results.kwhMonthly || 0 }} kWh</span>
      </div>

      <div class="res-list">
        <div class="res-item main-res">
          <div class="res-label"><span class="dot h"></span> Mỗi Giờ</div>
          <strong>{{ formatPrice(results.hourly) }}</strong>
        </div>

        <div class="res-item main-res clickable" @click="fetchDetails('today')">
          <div class="res-label">
            <span class="dot d"></span> Mỗi Ngày
            <small>⚡ Click xem biểu đồ 24h</small>
          </div>
          <strong>{{ formatPrice(results.daily) }}</strong>
        </div>

        <div
          class="res-item highlight clickable"
          @click="fetchDetails('month')"
        >
          <div class="res-label">
            <span class="dot m"></span> Mỗi Tháng (30 ngày)
            <small>📅 Click xem biểu đồ tháng</small>
          </div>
          <div class="price-stack">
            <strong>{{ formatPrice(results.monthly) }}</strong>
            <div class="progress-bar">
              <div class="fill" :style="{ width: '65%' }"></div>
            </div>
          </div>
        </div>

        <div class="res-item main-res">
          <div class="res-label"><span class="dot y"></span> Mỗi Năm</div>
          <strong>{{ formatPrice(results.yearly) }}</strong>
        </div>
      </div>
      <div class="note">
        * Dữ liệu chi tiết được giả lập dựa trên công suất đầu vào.
      </div>
    </div>

    <div
      v-if="modal.show"
      class="modal-overlay"
      @click.self="modal.show = false"
    >
      <div class="modal-content">
        <div class="modal-header">
          <h3>📊 {{ modal.title }}</h3>
          <button @click="modal.show = false" class="close-btn">&times;</button>
        </div>

        <div class="chart-container">
          <div
            v-for="item in modal.data"
            :key="item.label"
            class="chart-bar-item"
          >
            <div class="bar-wrapper">
              <div
                class="bar-fill"
                :style="{ height: (item.value / modal.max) * 100 + '%' }"
              >
                <span class="bar-tooltip">{{ formatPrice(item.value) }}</span>
              </div>
            </div>
            <span class="bar-label">{{ item.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";

const power = ref(100);
const quantity = ref(1);
const hours = ref(8);
const loading = ref(false);
const selectedSample = ref(null);

const samples = [
  { name: "Máy lạnh (9000 BTU)", p: 900 },
  { name: "Tủ lạnh Inverter", p: 150 },
  { name: "Máy giặt cửa ngang", p: 500 },
  { name: "Tivi LED 55 inch", p: 120 },
  { name: "Nồi cơm điện", p: 700 },
  { name: "Máy tính (PC)", p: 300 },
];

const results = reactive({
  hourly: 0,
  daily: 0,
  monthly: 0,
  yearly: 0,
  kwhMonthly: 0,
});
const modal = reactive({ show: false, title: "", data: [], max: 1 });

const applySample = () => {
  if (selectedSample.value) power.value = selectedSample.value.p;
};

// Hàm lấy kết quả tổng quan
const getEstimate = async () => {
  loading.value = true;
  try {
    const url = `http://localhost:8080/api/v1/devices/estimate?power=${power.value}&quantity=${quantity.value}&hours=${hours.value}`;
    const response = await fetch(url);
    const data = await response.json();
    Object.assign(results, data);
  } catch (e) {
    alert("Lỗi kết nối Backend!");
  } finally {
    loading.value = false;
  }
};

// Hàm lấy dữ liệu chi tiết cho Biểu đồ (Drill-down)
const fetchDetails = async (type) => {
  modal.show = true;
  modal.title =
    type === "today"
      ? "Chi tiết tiêu thụ theo khung giờ"
      : "Chi tiết tiêu thụ theo ngày";
  const endpoint = type === "today" ? "hourly" : "monthly";

  try {
    const url = `http://localhost:8080/api/v1/devices/estimate/${endpoint}?power=${power.value}&quantity=${quantity.value}&hours=${hours.value}`;
    const response = await fetch(url);
    const data = await response.json();
    modal.data = data;
    // Tìm giá trị lớn nhất để tính toán tỷ lệ chiều cao cột
    modal.max = Math.max(...data.map((d) => d.value), 1);
  } catch (e) {
    console.error("Lỗi lấy dữ liệu biểu đồ:", e);
  }
};

const formatPrice = (val) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(val);
};
</script>

<style scoped>
/* GIAO DIỆN CHÍNH TO RÕ */
.calculator-container {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 30px;
  padding: 30px;
  max-width: 1300px;
  margin: 0 auto;
}
.card {
  background: #ffffff;
  padding: 40px;
  border-radius: 28px;
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
}
h2,
h3 {
  font-size: 1.8rem !important;
  color: #1a1a1a;
  font-weight: 800;
  margin-bottom: 25px;
}

/* Clickable Items */
.clickable {
  cursor: pointer;
  transition: 0.3s;
}
.clickable:hover {
  background: #f0f7ff !important;
  transform: translateY(-3px);
}
.res-label small {
  display: block;
  color: #3a86ff;
  font-size: 11px;
  margin-top: 4px;
  font-weight: 600;
}

/* MODAL & CHART */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}
.modal-content {
  background: white;
  padding: 40px;
  border-radius: 30px;
  width: 95%;
  max-width: 1000px;
  box-shadow: 0 30px 100px rgba(0, 0, 0, 0.4);
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}
.close-btn {
  background: none;
  border: none;
  font-size: 35px;
  cursor: pointer;
  color: #ccc;
}

.chart-container {
  display: flex;
  align-items: flex-end;
  height: 350px;
  gap: 10px;
  padding: 20px 0;
  overflow-x: auto;
  border-bottom: 2px solid #eee;
}
.chart-bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 45px;
}
.bar-wrapper {
  width: 100%;
  height: 250px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}
.bar-fill {
  width: 70%;
  background: #3a86ff;
  border-radius: 10px 10px 0 0;
  position: relative;
  transition: height 0.8s ease-out;
}
.bar-fill:hover {
  background: #fb5607;
}
.bar-tooltip {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  background: #222;
  color: #fff;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 11px;
  opacity: 0;
  transition: 0.3s;
  pointer-events: none;
}
.bar-fill:hover .bar-tooltip {
  opacity: 1;
  top: -50px;
}
.bar-label {
  font-size: 11px;
  margin-top: 15px;
  font-weight: 700;
  color: #888;
}

/* Styles giữ nguyên từ bản trước */
.grid-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.form-group {
  margin-bottom: 25px;
  text-align: left;
}
label {
  display: block;
  font-size: 14px;
  font-weight: 700;
  color: #555;
  text-transform: uppercase;
  margin-bottom: 10px;
}
input,
select {
  width: 100%;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 15px;
  font-size: 16px;
  font-weight: 600;
}
.slider {
  width: 100%;
  height: 8px;
  background: #eee;
  border-radius: 10px;
  appearance: none;
}
.btn-calc {
  width: 100%;
  background: linear-gradient(135deg, #3a86ff, #0056d2);
  color: white;
  border: none;
  padding: 20px;
  border-radius: 18px;
  font-weight: 800;
  font-size: 18px;
  cursor: pointer;
  transition: 0.3s;
}
.res-item strong {
  font-size: 1.6rem;
  color: #222;
  font-weight: 800;
}
.highlight strong {
  font-size: 2.2rem;
  color: #3a86ff;
}
.kwh-badge {
  background: #eef5ff;
  color: #3a86ff;
  padding: 12px 25px;
  border-radius: 50px;
  font-weight: 800;
  margin-bottom: 30px;
  display: inline-block;
}
@media (max-width: 900px) {
  .calculator-container {
    grid-template-columns: 1fr;
  }
}
</style>
