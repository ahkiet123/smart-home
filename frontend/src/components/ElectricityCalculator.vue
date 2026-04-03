<template>
  <div class="calculator-container">
    <div class="card input-card">
      <h2>🔌 Dự toán tiêu thụ điện</h2>

      <div class="form-group">
        <label>Thiết bị mẫu:</label>
        <select v-model="selectedSample" @change="applySample">
          <option :value="null">-- Tùy chỉnh thông số --</option>
          <option v-for="s in samples" :key="s.name" :value="s">{{ s.name }}</option>
        </select>
      </div>

      <div class="grid-inputs">
        <div class="form-group">
          <label>Công suất (W):</label>
          <input type="number" v-model="power" min="1">
        </div>
        <div class="form-group">
          <label>Số lượng:</label>
          <input type="number" v-model="quantity" min="1">
        </div>
      </div>

      <div class="form-group">
        <label>Thời gian dùng (Giờ/Ngày): {{ hours }}h</label>
        <input type="range" v-model="hours" min="0.5" max="24" step="0.5" class="slider">
      </div>

      <button @click="getEstimate" class="btn-calc" :disabled="loading">
        <span v-if="!loading">🚀 TÍNH TOÁN NGAY</span>
        <span v-else>Đang tính...</span>
      </button>
    </div>

    <div class="card result-card">
      <h3>Bảng dự toán chi tiết</h3>
      <div class="kwh-badge">Tiêu thụ tháng: <span>{{ results.kwhMonthly || 0 }} kWh</span></div>

      <div class="res-list">
        <div class="res-item main-res">
          <div class="res-label">
            <span class="dot h"></span> Mỗi Giờ
          </div>
          <strong>{{ formatPrice(results.hourly) }}</strong>
        </div>

        <div class="res-item main-res">
          <div class="res-label">
            <span class="dot d"></span> Mỗi Ngày
          </div>
          <strong>{{ formatPrice(results.daily) }}</strong>
        </div>

        <div class="res-item highlight">
          <div class="res-label">
            <span class="dot m"></span> Mỗi Tháng (30 ngày)
          </div>
          <div class="price-stack">
            <strong>{{ formatPrice(results.monthly) }}</strong>
            <div class="progress-bar"><div class="fill" :style="{width: '65%'}"></div></div>
          </div>
        </div>

        <div class="res-item main-res">
          <div class="res-label">
            <span class="dot y"></span> Mỗi Năm
          </div>
          <strong>{{ formatPrice(results.yearly) }}</strong>
        </div>
      </div>

      <div class="note">
        * Ước tính dựa trên đơn giá bình quân 2.500đ/kWh.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

const power = ref(100);
const quantity = ref(1);
const hours = ref(8);
const loading = ref(false);
const selectedSample = ref(null);

const samples = [
  { name: 'Máy lạnh (9000 BTU)', p: 900 },
  { name: 'Tủ lạnh Inverter', p: 150 },
  { name: 'Máy giặt cửa ngang', p: 500 },
  { name: 'Tivi LED 55 inch', p: 120 },
  { name: 'Nồi cơm điện', p: 700 },
  { name: 'Máy tính (PC)', p: 300 }
];

const results = reactive({ hourly: 0, daily: 0, monthly: 0, yearly: 0, kwhMonthly: 0 });

const applySample = () => {
  if (selectedSample.value) power.value = selectedSample.value.p;
};

const getEstimate = async () => {
  loading.value = true;
  try {
    // PHẢI CÓ /api/v1 vì Backend của bạn đang chạy trên context path này
    const url = `http://localhost:8080/api/v1/devices/estimate?power=${power.value}&quantity=${quantity.value}&hours=${hours.value}`;

    const response = await fetch(url);
    if (!response.ok) throw new Error('Network response was not ok');
    const data = await response.json();
    Object.assign(results, data);
  } catch (e) {
    console.error(e);
    alert("Lỗi: Không thể kết nối với Backend! Kiểm tra lại URL hoặc Server.");
  } finally {
    loading.value = false;
  }
};

const formatPrice = (val) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};
</script>

<style scoped>
/* Giữ nguyên CSS cũ của bạn */
.calculator-container { display: grid; grid-template-columns: 1fr 1.2fr; gap: 25px; padding: 20px; }
.card { background: #ffffff; padding: 30px; border-radius: 24px; box-shadow: 0 10px 40px rgba(0,0,0,0.06); border: 1px solid #f0f0f0; }
h2, h3 { margin-top: 0; color: #1a1a1a; font-weight: 800; }
.grid-inputs { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.form-group { margin-bottom: 20px; text-align: left; }
label { display: block; font-size: 13px; font-weight: 700; margin-bottom: 8px; color: #666; text-transform: uppercase; }
input, select { width: 100%; padding: 12px; border: 2px solid #f0f0f0; border-radius: 12px; font-size: 15px; transition: 0.3s; }
input:focus { border-color: #3A86FF; outline: none; }
.slider { width: 100%; height: 6px; background: #eee; border-radius: 5px; appearance: none; }
.slider::-webkit-slider-thumb { appearance: none; width: 18px; height: 18px; background: #3A86FF; border-radius: 50%; cursor: pointer; }
.btn-calc { width: 100%; background: linear-gradient(135deg, #3A86FF, #0056D2); color: white; border: none; padding: 16px; border-radius: 14px; font-weight: 800; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.btn-calc:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(58, 134, 255, 0.4); }
.kwh-badge { background: #eef5ff; color: #3A86FF; padding: 10px 20px; border-radius: 50px; display: inline-block; font-weight: 700; margin-bottom: 20px; }
.res-item { display: flex; justify-content: space-between; align-items: center; padding: 18px 0; border-bottom: 1px solid #f8f8f8; }
.highlight { background: #fafbff; padding: 20px; border-radius: 15px; border: 1px solid #eef2ff; margin: 10px 0; }
.dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; margin-right: 10px; }
.dot.h { background: #FFBE0B; } .dot.d { background: #FB5607; } .dot.m { background: #3A86FF; } .dot.y { background: #8338EC; }
.price-stack { text-align: right; width: 60%; }
.progress-bar { width: 100%; height: 6px; background: #eee; border-radius: 10px; margin-top: 8px; overflow: hidden; }
.fill { height: 100%; background: #3A86FF; border-radius: 10px; }
.note { font-size: 12px; color: #999; margin-top: 20px; font-style: italic; }
@media (max-width: 768px) { .calculator-container { grid-template-columns: 1fr; } }
</style>