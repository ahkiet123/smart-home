<script setup>
// 1. Import Component mới mà bạn vừa tạo trong thư mục components
import ElectricityCalculator from './components/ElectricityCalculator.vue'
</script>

<template>
  <div class="app-container">
    <header class="dashboard-header">
      <h1>☘️ Smart Home Energy Dashboard</h1>
      <p>Quản lý và dự toán tiêu thụ điện năng thông minh</p>
    </header>

    <main>
      <ElectricityCalculator />
    </main>

    <footer class="app-footer">
      <p>&copy; 2026 - Đồ án Phát triển phần mềm - Nhóm SmartHome</p>
    </footer>
  </div>
</template>

<style>
/* Style cơ bản để giao diện trông chuyên nghiệp hơn */
#app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  text-align: center;
}

.dashboard-header {
  margin-bottom: 3rem;
}

.dashboard-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.dashboard-header p {
  color: #7f8c8d;
  font-size: 1.1rem;
}

.app-footer {
  margin-top: 4rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
  color: #bdc3c7;
  font-size: 0.9rem;
}
</style>