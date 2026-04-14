<template>
  <router-view />
</template>

<style>
/* RESET TOÀN BỘ ĐỂ KHÔNG BỊ NHỎ NHƯ CŨ */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

#app {
  max-width: 100% !important; /* Xóa bỏ giới hạn 1200px cũ */
  width: 100%;
  margin: 0;
  padding: 0;
}

.full-page-wrapper {
  background-color: #f4f7fe;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-header {
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  color: white;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.main-header h1 {
  font-size: 3rem; /* Cho tiêu đề to hẳn lên */
  letter-spacing: 2px;
  margin-bottom: 10px;
}

.main-body {
  flex: 1;
  padding: 50px 20px;
  display: flex;
  justify-content: center;
}

.container-expanded {
  width: 100%;
  max-width: 1400px; /* Nới rộng khung hiển thị */
}

.main-footer {
  padding: 30px;
  background: #fff;
  border-top: 1px solid #eee;
  color: #888;
  text-align: center;
  font-weight: 500;
}

/* Đảm bảo font chữ Inter hoặc Sans-serif cho sạch */
body {
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
</style>
