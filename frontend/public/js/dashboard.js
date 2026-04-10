window.initDashboard = function initDashboard() {
  window.renderArticles();
  window.renderDevices();
  if (typeof window.loadUserProfile === 'function') {
    window.loadUserProfile();
  }
  if (window.switchDashboardTab) {
    window.switchDashboardTab('overview');
  }
};

window.openDeviceDetail = function openDeviceDetail(id) {
  const device = window.App.devices.find((d) => d.id === id);
  if (!device) return;

  document.getElementById('detail-name').innerText = device.name;
  document.getElementById('detail-room').innerText = device.room;
  document.getElementById('detail-power').innerText = device.powerW;
  document.getElementById('detail-hours').innerText = device.hours;
  document.getElementById('detail-status').innerText = device.isOn ? 'Đang bật' : 'Đã tắt';

  document.getElementById('device-detail-modal').classList.remove('hidden');
};

window.closeDeviceDetail = function closeDeviceDetail() {
  document.getElementById('device-detail-modal').classList.add('hidden');
};

window.renderArticles = function renderArticles() {
  const container = document.getElementById('articles-container');
  container.innerHTML = window.App.articles.map((art) => `
    <div class="bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm hover:shadow-md transition-shadow group cursor-pointer">
      <div class="h-40 overflow-hidden relative">
        <img src="${art.img}" class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500">
        <div class="absolute top-3 left-3 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full text-xs font-semibold text-gray-700">${art.category}</div>
      </div>
      <div class="p-4">
        <h3 class="font-bold text-gray-800 mb-2 line-clamp-2 group-hover:text-blue-600 transition-colors">${art.title}</h3>
        <div class="flex items-center text-sm text-gray-500">
          <i data-lucide="book-open" class="w-3.5 h-3.5 mr-1.5"></i> ${art.time}
        </div>
      </div>
    </div>
  `).join('');

  if (window.lucide) {
    window.lucide.createIcons();
  }
};

window.renderDevices = function renderDevices() {
  const container = document.getElementById('devices-container');
  container.innerHTML = window.App.devices.map((device) => {
    let iconName = 'power';
    if (device.type === 'light') iconName = 'lightbulb';
    else if (device.type === 'ac') iconName = 'wind';
    else if (device.type === 'tv') iconName = 'tv';
    else if (device.type === 'fridge') iconName = 'thermometer';

    const bgClass = device.isOn
      ? 'bg-white border-blue-100 shadow-md shadow-blue-50/50'
      : 'bg-gray-50 border-gray-100 shadow-sm';

    const iconBgClass = device.isOn
      ? 'bg-blue-50 text-blue-500'
      : 'bg-white text-gray-400 border border-gray-100';

    return `
      <div onclick="openDeviceDetail(${device.id})"
           class="relative p-5 rounded-2xl border transition-all duration-200 ${bgClass} cursor-pointer hover:scale-[1.02]">

        ${window.App.isEditMode ? `
          <button onclick="event.stopPropagation(); deleteDevice(${device.id})"
              class="absolute -top-2 -right-2 w-7 h-7 bg-red-100 text-red-600 rounded-full flex items-center justify-center hover:bg-red-500 hover:text-white shadow-sm z-10">
              <i data-lucide="trash-2" class="w-3.5 h-3.5"></i>
          </button>
        ` : ''}

        <div class="flex justify-between items-start mb-4">
          <div class="p-3 rounded-full ${iconBgClass}">
            <i data-lucide="${iconName}" class="w-6 h-6"></i>
          </div>

          <label class="flex items-center cursor-pointer">
            <div class="relative">
              <input type="checkbox"
                     onclick="event.stopPropagation()"
                     class="sr-only toggle-checkbox"
                     onchange="toggleDevice(${device.id})"
                     ${device.isOn ? 'checked' : ''}>
              <div class="toggle-label block w-12 h-7 rounded-full bg-gray-300 transition-colors duration-300">
                <div class="toggle-circle absolute left-1 top-1 bg-white w-5 h-5 rounded-full transition-transform duration-300"></div>
              </div>
            </div>
          </label>
        </div>

        <div>
          <h3 class="font-semibold text-base mb-1 ${device.isOn ? 'text-gray-800' : 'text-gray-500'}">${device.name}</h3>
          <div class="flex justify-between items-center text-sm">
            <span class="text-gray-400">${device.room}</span>
            <span class="font-medium ${device.isOn ? 'text-amber-500' : 'text-gray-400'}">${device.isOn ? device.powerW + 'W' : 'Tắt'}</span>
          </div>
        </div>
      </div>
    `;
  }).join('');

  if (window.lucide) {
    window.lucide.createIcons();
  }
  window.updateStats();
};

window.toggleDevice = function toggleDevice(id) {
  const device = window.App.devices.find((d) => d.id === id);
  if (device) device.isOn = !device.isOn;
  window.renderDevices();
};

window.toggleEditMode = function toggleEditMode() {
  window.App.isEditMode = !window.App.isEditMode;
  const btn = document.getElementById('btn-edit-mode');
  if (window.App.isEditMode) {
    btn.classList.add('bg-blue-100', 'text-blue-700');
    btn.querySelector('span').innerText = 'Hoàn tất';
  } else {
    btn.classList.remove('bg-blue-100', 'text-blue-700');
    btn.querySelector('span').innerText = 'Quản lý';
  }
  window.renderDevices();
};

window.deleteDevice = function deleteDevice(id) {
  window.App.devices = window.App.devices.filter((d) => d.id !== id);
  window.renderDevices();
};

window.updateStats = function updateStats() {
  const formatMoney = (n) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(n);

  let totalPowerW = 0;
  let totalKwh = 0;
  let activeCount = 0;

  window.App.devices.forEach((d) => {
    const power = Number(d.powerW) || 0;
    const hours = Number(d.hours) || 0;
    if (d.isOn) {
      totalPowerW += power;
      activeCount++;
      totalKwh += (power * hours) / 1000;
    }
  });

  const todayCost = totalKwh * window.App.ELECTRICITY_RATE;
  const monthlyCost = todayCost * 30;

  const powerElement = document.getElementById('stat-power');
  if (powerElement) powerElement.innerText = (totalPowerW / 1000).toFixed(2);

  const activeDevElement = document.getElementById('stat-active-devices');
  if (activeDevElement) activeDevElement.innerText = `${activeCount} thiết bị`;

  const kwhElement = document.getElementById('stat-kwh');
  if (kwhElement) kwhElement.innerText = totalKwh.toFixed(2);

  const costTodayElement = document.getElementById('stat-cost-today');
  if (costTodayElement) costTodayElement.innerText = formatMoney(todayCost);

  const costMonthElement = document.getElementById('stat-cost-month');
  if (costMonthElement) costMonthElement.innerText = formatMoney(monthlyCost);
};

window.openStatsModal = function openStatsModal(title, contentHtml) {
  document.getElementById('stats-title').innerText = title;
  document.getElementById('stats-content').innerHTML = contentHtml;
  document.getElementById('stats-detail-modal').classList.remove('hidden');
};

window.closeStatsModal = function closeStatsModal() {
  document.getElementById('stats-detail-modal').classList.add('hidden');
};

window.showPowerDetail = function showPowerDetail() {
  const activeDevicesList = window.App.devices.filter((d) => d.isOn);
  let html = '<p class="text-gray-500 mb-4">Danh sách các thiết bị đang tiêu thụ điện thực tế:</p>';

  if (activeDevicesList.length === 0) {
    html += '<div class="p-4 bg-gray-50 rounded-xl text-center text-gray-400">Không có thiết bị nào đang bật</div>';
  } else {
    activeDevicesList.forEach((d) => {
      html += `
        <div class="flex justify-between items-center p-3 bg-amber-50 rounded-xl border border-amber-100">
          <span class="font-medium text-gray-700">${d.name}</span>
          <span class="font-bold text-amber-600">${(d.powerW / 1000).toFixed(2)} kW</span>
        </div>`;
    });
  }

  window.openStatsModal('Phân bổ công suất', html);
};

window.showTodayDetail = function showTodayDetail() {
  const activeDevicesList = window.App.devices.filter((d) => d.isOn);
  let html = '<p class="text-gray-500 mb-4">Số tiền tạm tính dựa trên số giờ sử dụng hôm nay:</p>';

  activeDevicesList.forEach((d) => {
    const cost = (d.powerW * d.hours / 1000) * window.App.ELECTRICITY_RATE;
    html += `
      <div class="flex justify-between items-center p-3 bg-blue-50 rounded-xl border border-blue-100">
        <div>
          <div class="font-medium text-gray-700">${d.name}</div>
          <div class="text-xs text-blue-400">${d.hours} giờ sử dụng</div>
        </div>
        <span class="font-bold text-blue-600">${new Intl.NumberFormat('vi-VN').format(cost)} ₫</span>
      </div>`;
  });

  window.openStatsModal('Chi tiết chi phí hôm nay', html);
};

window.showMonthDetail = function showMonthDetail() {
  const totalToday = window.App.devices
    .filter((d) => d.isOn)
    .reduce((sum, d) => sum + ((d.powerW * d.hours / 1000) * window.App.ELECTRICITY_RATE), 0);
  const totalMonth = totalToday * 30;

  const html = `
    <div class="p-4 bg-slate-800 text-white rounded-2xl mb-4 text-center">
      <div class="text-slate-400 text-sm">Dự báo hóa đơn tháng này</div>
      <div class="text-3xl font-bold">${new Intl.NumberFormat('vi-VN').format(totalMonth)} ₫</div>
    </div>
    <div class="space-y-3">
      <div class="flex justify-between text-sm"><span class="text-gray-500">Trung bình/ngày:</span> <span class="font-bold">${new Intl.NumberFormat('vi-VN').format(totalToday)} ₫</span></div>
      <div class="flex justify-between text-sm"><span class="text-gray-500">Đơn giá điện:</span> <span class="font-bold text-green-600">2.500 ₫ / kWh</span></div>
      <div class="flex justify-between text-sm"><span class="text-gray-500">Chu kỳ tính:</span> <span class="font-bold">30 ngày</span></div>
    </div>`;

  window.openStatsModal('Dự báo tài chính', html);
};

window.openAddModal = function openAddModal() {
  document.getElementById('add-modal').classList.remove('hidden');
};

window.closeAddModal = function closeAddModal() {
  document.getElementById('add-modal').classList.add('hidden');
};

window.submitAddDevice = function submitAddDevice() {
  const name = document.getElementById('new-device-name').value;
  const room = document.getElementById('new-device-room').value;
  const type = document.getElementById('new-device-type').value;
  const powerW = document.getElementById('new-device-power').value;
  const hours = document.getElementById('new-device-hours').value;

  if (name && powerW) {
    window.App.devices.push({
      id: Date.now(),
      name,
      room,
      type,
      isOn: false,
      powerW: Number(powerW),
      hours: Number(hours) || 0
    });

    window.closeAddModal();
    document.getElementById('new-device-name').value = '';
    document.getElementById('new-device-power').value = '';
    document.getElementById('new-device-hours').value = '';
    window.renderDevices();
  }
};

window.showEnergyChart = function showEnergyChart() {
  const modal = document.getElementById('chart-modal');
  modal.classList.remove('hidden');

  const activeDevices = window.App.devices.filter((d) => d.isOn && d.powerW > 0);
  if (activeDevices.length === 0) {
    document.getElementById('chart-legend').innerHTML = '<p class="text-center text-gray-400">Chưa có dữ liệu. Hãy bật thiết bị để xem thống kê!</p>';
    return;
  }

  if (window.App.myChart) {
    window.App.myChart.destroy();
  }

  const ctx = document.getElementById('energyPieChart').getContext('2d');
  window.App.myChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: activeDevices.map((d) => d.name),
      datasets: [{
        data: activeDevices.map((d) => (d.powerW * d.hours / 1000).toFixed(2)),
        backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'],
        borderWidth: 0,
        hoverOffset: 10
      }]
    },
    options: {
      plugins: { legend: { display: false } },
      cutout: '70%'
    }
  });

  let legendHtml = '';
  const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];
  activeDevices.forEach((d, index) => {
    const kwh = (d.powerW * d.hours / 1000).toFixed(2);
    legendHtml += `
      <div class="flex justify-between items-center text-sm">
        <div class="flex items-center">
          <span class="w-3 h-3 rounded-full mr-2" style="background-color: ${colors[index % colors.length]}"></span>
          <span class="text-gray-600">${d.name}</span>
        </div>
        <span class="font-bold text-gray-800">${kwh} kWh</span>
      </div>`;
  });
  document.getElementById('chart-legend').innerHTML = legendHtml;
};

window.closeChartModal = function closeChartModal() {
  document.getElementById('chart-modal').classList.add('hidden');
};

window.showRoomStats = function showRoomStats() {
  const modal = document.getElementById('room-stats-modal');
  modal.classList.remove('hidden');

  const roomData = {};
  window.App.devices.forEach((d) => {
    if (!roomData[d.room]) roomData[d.room] = 0;
    if (d.isOn) {
      roomData[d.room] += (d.powerW * d.hours / 1000) * window.App.ELECTRICITY_RATE;
    }
  });

  const labels = Object.keys(roomData);
  const values = Object.values(roomData);
  const ctx = document.getElementById('roomBarChart').getContext('2d');
  if (window.App.roomChart) {
    window.App.roomChart.destroy();
  }

  window.App.roomChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        label: 'Tiền điện (VND)',
        data: values,
        backgroundColor: '#10b981',
        borderRadius: 8,
        barThickness: 20
      }]
    },
    options: {
      indexAxis: 'y',
      plugins: { legend: { display: false } },
      scales: {
        x: { beginAtZero: true, grid: { display: false } },
        y: { grid: { display: false } }
      }
    }
  });

  let detailHtml = '';
  labels.forEach((room, index) => {
    detailHtml += `
      <div class="flex justify-between items-center p-3 bg-gray-50 rounded-xl">
        <span class="text-sm font-medium text-gray-600">${room}</span>
        <span class="font-bold text-gray-800">${new Intl.NumberFormat('vi-VN').format(values[index])} ₫</span>
      </div>`;
  });
  document.getElementById('room-details').innerHTML = detailHtml;
};

window.closeRoomModal = function closeRoomModal() {
  document.getElementById('room-stats-modal').classList.add('hidden');
};
