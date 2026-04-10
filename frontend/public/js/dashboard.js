window.initDashboard = function initDashboard() {
  window.renderArticles();
  window.renderDevices();
  if (typeof window.loadGlobalEnergyOverview === 'function') {
    window.loadGlobalEnergyOverview();
  }
  if (typeof window.loadUserProfile === 'function') {
    window.loadUserProfile();
  }
  if (window.switchDashboardTab) {
    window.switchDashboardTab('overview');
  }
};

function formatCurrencyVnd(value) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value || 0);
}

function formatNumber(value) {
  return Number(value || 0).toFixed(2);
}

function toVietnameseDisplayName(name) {
  if (!name) return 'Không rõ';

  const normalized = String(name)
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim();

  const dictionary = {
    'phong khach': 'Phòng khách',
    'phong ngu': 'Phòng ngủ',
    'bep': 'Bếp',
    'phong tam': 'Phòng tắm',
    'dieu hoa': 'Điều hòa',
    'tu lanh': 'Tủ lạnh',
    'may giat': 'Máy giặt'
  };

  return dictionary[normalized] || name;
}

window.loadGlobalEnergyOverview = async function loadGlobalEnergyOverview() {
  const token = localStorage.getItem('token');
  const loading = document.getElementById('global-energy-loading');
  const empty = document.getElementById('global-energy-empty');

  if (!token) return;

  if (loading) loading.classList.remove('hidden');
  if (empty) empty.classList.add('hidden');

  try {
    const today = new Date();
    const currentFrom = new Date(today.getFullYear(), today.getMonth(), 1);
    const currentTo = today;

    const prevMonthFirst = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const daysInPrevMonth = new Date(today.getFullYear(), today.getMonth(), 0).getDate();
    const prevMonthLastComparedDay = Math.min(today.getDate(), daysInPrevMonth);
    const prevMonthTo = new Date(today.getFullYear(), today.getMonth() - 1, prevMonthLastComparedDay);

    const currentFromStr = currentFrom.toISOString().split('T')[0];
    const currentToStr = currentTo.toISOString().split('T')[0];
    const prevFromStr = prevMonthFirst.toISOString().split('T')[0];
    const prevToStr = prevMonthTo.toISOString().split('T')[0];

    const [currentResponse, prevResponse] = await Promise.all([
      fetch(`${window.App.API_BASE}/energy/analytics/daily?fromDate=${currentFromStr}&toDate=${currentToStr}&groupBy=room`, {
        headers: { Authorization: `Bearer ${token}` }
      }),
      fetch(`${window.App.API_BASE}/energy/analytics/daily?fromDate=${prevFromStr}&toDate=${prevToStr}&groupBy=room`, {
        headers: { Authorization: `Bearer ${token}` }
      })
    ]);

    const currentResult = typeof window.parseApiResponse === 'function'
      ? await window.parseApiResponse(currentResponse)
      : await currentResponse.json();

    if (!currentResponse.ok) {
      return;
    }

    const prevResult = typeof window.parseApiResponse === 'function'
      ? await window.parseApiResponse(prevResponse)
      : await prevResponse.json();

    const data = currentResult?.data || {};
    const previousMonthData = prevResponse.ok ? (prevResult?.data || {}) : {};
    const series = Array.isArray(data.dailySeries) ? data.dailySeries : [];
    const hasData = series.some((item) => Number(item.kwh || 0) > 0);

    const totalKwh = document.getElementById('global-total-kwh');
    const totalCost = document.getElementById('global-total-cost');
    const delta = document.getElementById('global-delta');

    if (totalKwh) totalKwh.innerText = formatNumber(data.totalKwh);
    if (totalCost) totalCost.innerText = formatCurrencyVnd(data.totalEstimatedCost);

    const currentTotal = Number(data.totalKwh || 0);
    const previousTotal = Number(previousMonthData.totalKwh || 0);
    const deltaKwh = currentTotal - previousTotal;
    const deltaPercent = previousTotal > 0
      ? `${((deltaKwh / previousTotal) * 100).toFixed(2)}%`
      : (currentTotal > 0 ? '100.00%' : '0.00%');
    if (delta) {
      const symbol = deltaKwh > 0 ? '▲' : deltaKwh < 0 ? '▼' : '•';
      delta.innerText = `${symbol} ${formatNumber(deltaKwh)} kWh (${deltaPercent})`;
    }

    if (!hasData) {
      if (empty) empty.classList.remove('hidden');
      if (window.App.globalEnergyChart) {
        window.App.globalEnergyChart.destroy();
        window.App.globalEnergyChart = null;
      }
      return;
    }

    const canvas = document.getElementById('global-energy-chart');
    if (!canvas) return;

    if (window.App.globalEnergyChart) {
      window.App.globalEnergyChart.destroy();
    }

    const labels = series.map((item) => new Date(item.date).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' }));
    const values = series.map((item) => Number(item.kwh || 0));
    const ctx = canvas.getContext('2d');
    window.App.globalEnergyChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          data: values,
          borderColor: '#2563eb',
          backgroundColor: 'rgba(37, 99, 235, 0.15)',
          fill: true,
          tension: 0.35,
          pointRadius: 3
        }]
      },
      options: {
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } },
        maintainAspectRatio: false
      }
    });
  } catch (error) {
    console.error(error);
  } finally {
    if (loading) loading.classList.add('hidden');
  }
};

function setEnergyButtonActive(activeId, buttonIds) {
  buttonIds.forEach((id) => {
    const button = document.getElementById(id);
    if (!button) return;

    if (id === activeId) {
      button.className = 'px-3 py-2 rounded-lg text-sm font-medium bg-blue-50 text-blue-700';
    } else {
      button.className = 'px-3 py-2 rounded-lg text-sm font-medium bg-gray-100 text-gray-600';
    }
  });
}

function setDailyEnergyUiState({ loading = false, error = '', empty = false }) {
  const loadingBox = document.getElementById('daily-loading-state');
  const errorBox = document.getElementById('daily-error-state');
  const emptyBox = document.getElementById('daily-empty-state');
  const chartSection = document.getElementById('daily-chart-section');

  if (loadingBox) loadingBox.classList.toggle('hidden', !loading);

  if (errorBox) {
    errorBox.textContent = error;
    errorBox.classList.toggle('hidden', !error);
  }

  if (emptyBox) emptyBox.classList.toggle('hidden', !empty);
  if (chartSection) chartSection.classList.toggle('hidden', empty || Boolean(error));
}

function updateDailySummary(data) {
  const totalKwh = document.getElementById('daily-total-kwh');
  const totalCost = document.getElementById('daily-total-cost');
  const dayDelta = document.getElementById('daily-day-delta');

  if (totalKwh) totalKwh.innerText = formatNumber(data.totalKwh);
  if (totalCost) totalCost.innerText = formatCurrencyVnd(data.totalEstimatedCost);

  const deltaKwh = Number(data.lastDayDeltaKwh || 0);
  const deltaPercent = data.lastDayDeltaPercent;

  if (dayDelta) {
    const arrow = deltaKwh > 0 ? '▲' : deltaKwh < 0 ? '▼' : '•';
    const percentText = typeof deltaPercent === 'number' ? `${deltaPercent.toFixed(2)}%` : 'N/A';
    dayDelta.innerText = `${arrow} ${formatNumber(deltaKwh)} kWh (${percentText})`;

    if (deltaKwh > 0) {
      dayDelta.className = 'text-lg font-bold text-red-600';
    } else if (deltaKwh < 0) {
      dayDelta.className = 'text-lg font-bold text-emerald-600';
    } else {
      dayDelta.className = 'text-lg font-bold text-slate-800';
    }
  }
}

function renderDailyEnergyChart(dailySeries) {
  const chartCanvas = document.getElementById('daily-energy-chart');
  if (!chartCanvas) return;

  const labels = dailySeries.map((point) => {
    const date = new Date(point.date);
    return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
  });
  const values = dailySeries.map((point) => Number(point.kwh || 0));

  if (window.App.dailyEnergyChart) {
    window.App.dailyEnergyChart.destroy();
  }

  const ctx = chartCanvas.getContext('2d');
  window.App.dailyEnergyChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'kWh / ngày',
        data: values,
        borderColor: '#2563eb',
        backgroundColor: 'rgba(37, 99, 235, 0.15)',
        fill: true,
        tension: 0.35,
        pointRadius: 3,
        pointHoverRadius: 5
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: { display: true, text: 'kWh' }
        }
      }
    }
  });
}

function renderDailyBreakdown(data) {
  const title = document.getElementById('daily-breakdown-title');
  const list = document.getElementById('daily-breakdown-list');
  if (!list || !title) return;

  const isDevice = data.breakdownType === 'device';
  title.innerText = isDevice ? 'Top thiết bị tiêu thụ' : 'Top phòng tiêu thụ';

  const breakdown = Array.isArray(data.breakdown) ? data.breakdown : [];
  if (breakdown.length === 0) {
    list.innerHTML = '<div class="p-3 rounded-xl bg-gray-50 text-gray-500 text-sm">Không có dữ liệu breakdown trong khoảng thời gian này.</div>';
    return;
  }

  list.innerHTML = breakdown.map((item, index) => `
    <div class="flex items-center justify-between p-3 rounded-xl border border-gray-100 bg-gray-50">
      <div>
        <div class="text-sm font-semibold text-gray-800">#${index + 1} ${item.name || 'N/A'}</div>
        <div class="text-xs text-gray-500">${formatNumber(item.kwh)} kWh</div>
      </div>
      <div class="text-sm font-bold text-blue-700">${formatCurrencyVnd(item.estimatedCost)}</div>
    </div>
  `).join('');
}

window.loadDailyEnergyAnalytics = async function loadDailyEnergyAnalytics() {
  const token = localStorage.getItem('token');
  const fromDate = document.getElementById('energy-from-date')?.value;
  const toDate = document.getElementById('energy-to-date')?.value;

  if (!token) {
    setDailyEnergyUiState({ error: 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.' });
    return;
  }

  if (!fromDate || !toDate) {
    setDailyEnergyUiState({ error: 'Vui lòng chọn khoảng ngày hợp lệ.' });
    return;
  }

  setDailyEnergyUiState({ loading: true });

  try {
    const groupBy = window.App.dailyEnergyBreakdownType || 'room';
    const params = new URLSearchParams({ fromDate, toDate, groupBy });

    const response = await fetch(`${window.App.API_BASE}/energy/analytics/daily?${params.toString()}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const result = typeof window.parseApiResponse === 'function'
      ? await window.parseApiResponse(response)
      : await response.json();

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        window.doLogout();
        return;
      }

      const errorMessage = result?.message || result?.error || 'Không tải được dữ liệu điện năng theo ngày.';
      setDailyEnergyUiState({ error: errorMessage });
      return;
    }

    const data = result?.data || {};
    const dailySeries = Array.isArray(data.dailySeries) ? data.dailySeries : [];
    const hasData = dailySeries.some((point) => Number(point.kwh || 0) > 0);

    updateDailySummary(data);
    renderDailyBreakdown(data);

    if (!hasData) {
      setDailyEnergyUiState({ empty: true });
      return;
    }

    renderDailyEnergyChart(dailySeries);
    setDailyEnergyUiState({});
  } catch (error) {
    console.error(error);
    setDailyEnergyUiState({ error: 'Lỗi kết nối server khi tải dữ liệu điện năng.' });
  } finally {
    const loadingBox = document.getElementById('daily-loading-state');
    if (loadingBox) loadingBox.classList.add('hidden');
  }
};

window.setEnergyPresetDays = function setEnergyPresetDays(days) {
  const toDate = new Date();
  const fromDate = new Date();
  fromDate.setDate(toDate.getDate() - (days - 1));

  const fromInput = document.getElementById('energy-from-date');
  const toInput = document.getElementById('energy-to-date');

  if (fromInput) fromInput.value = fromDate.toISOString().split('T')[0];
  if (toInput) toInput.value = toDate.toISOString().split('T')[0];

  window.App.dailyEnergyPresetDays = days;
  setEnergyButtonActive(`energy-preset-${days}`, ['energy-preset-7', 'energy-preset-14', 'energy-preset-30']);
  window.showEnergyChart();
};

window.applyCustomEnergyRange = function applyCustomEnergyRange() {
  const fromDate = document.getElementById('energy-from-date')?.value;
  const toDate = document.getElementById('energy-to-date')?.value;
  if (!fromDate || !toDate) {
    alert('Vui lòng chọn đầy đủ từ ngày và đến ngày.');
    return;
  }

  if (fromDate > toDate) {
    alert('Từ ngày không được lớn hơn đến ngày.');
    return;
  }

  window.App.dailyEnergyPresetDays = null;
  setEnergyButtonActive('', ['energy-preset-7', 'energy-preset-14', 'energy-preset-30']);
  window.showEnergyChart();
};

window.setEnergyBreakdownType = function setEnergyBreakdownType(type) {
  window.App.dailyEnergyBreakdownType = type === 'device' ? 'device' : 'room';
  setEnergyButtonActive(
    window.App.dailyEnergyBreakdownType === 'device' ? 'energy-breakdown-device' : 'energy-breakdown-room',
    ['energy-breakdown-room', 'energy-breakdown-device']
  );
  window.loadDailyEnergyAnalytics();
};

window.seedDailyEnergyData = async function seedDailyEnergyData() {
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.doLogout();
    return;
  }

  const btn = document.getElementById('energy-seed-demo');
  if (btn) {
    btn.disabled = true;
    btn.innerText = 'Đang tạo...';
  }

  try {
    const response = await fetch(`${window.App.API_BASE}/energy/analytics/seed-demo?days=30`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const result = typeof window.parseApiResponse === 'function'
      ? await window.parseApiResponse(response)
      : await response.json();

    if (!response.ok) {
      alert(result?.message || 'Tạo dữ liệu demo thất bại.');
      return;
    }

    const inserted = result?.data?.inserted ?? 0;
    alert(`Đã tạo dữ liệu demo thành công (${inserted} bản ghi mới).`);
    window.showEnergyChart();
    if (typeof window.loadGlobalEnergyOverview === 'function') {
      window.loadGlobalEnergyOverview();
    }
  } catch (error) {
    console.error(error);
    alert('Lỗi kết nối server khi tạo dữ liệu demo.');
  } finally {
    if (btn) {
      btn.disabled = false;
      btn.innerText = 'Tạo data demo';
    }
  }
};

window.initDailyEnergyAnalytics = function initDailyEnergyAnalytics() {
  window.App.dailyEnergyBreakdownType = window.App.dailyEnergyBreakdownType || 'room';
  window.App.dailyEnergyPresetDays = window.App.dailyEnergyPresetDays || 7;

  setEnergyButtonActive('energy-breakdown-room', ['energy-breakdown-room', 'energy-breakdown-device']);
  window.setEnergyPresetDays(window.App.dailyEnergyPresetDays);
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
  const loading = document.getElementById('bar-chart-loading');
  const empty = document.getElementById('bar-chart-empty');
  const roomList = document.getElementById('room-breakdown-list');
  const selectedDateLabel = document.getElementById('room-breakdown-selected-date');

  if (loading) loading.classList.remove('hidden');
  if (empty) empty.classList.add('hidden');
  if (roomList) roomList.innerHTML = '';
  if (selectedDateLabel) selectedDateLabel.innerText = 'Chưa chọn ngày';

  const token = localStorage.getItem('token');
  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.doLogout();
    return;
  }

  const fromInput = document.getElementById('energy-from-date');
  const toInput = document.getElementById('energy-to-date');

  if (fromInput && toInput && (!fromInput.value || !toInput.value)) {
    const defaultDays = window.App.dailyEnergyPresetDays || 7;
    const toDateDefault = new Date();
    const fromDateDefault = new Date();
    fromDateDefault.setDate(toDateDefault.getDate() - (defaultDays - 1));

    fromInput.value = fromDateDefault.toISOString().split('T')[0];
    toInput.value = toDateDefault.toISOString().split('T')[0];
    setEnergyButtonActive(`energy-preset-${defaultDays}`, ['energy-preset-7', 'energy-preset-14', 'energy-preset-30']);
  }

  const fromDate = fromInput?.value;
  const toDate = toInput?.value;
  if (!fromDate || !toDate) {
    alert('Vui lòng chọn khoảng ngày hợp lệ.');
    if (loading) loading.classList.add('hidden');
    return;
  }

  fetch(`${window.App.API_BASE}/energy/analytics/daily?fromDate=${fromDate}&toDate=${toDate}&groupBy=room`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
    .then(async (response) => {
      const result = typeof window.parseApiResponse === 'function'
        ? await window.parseApiResponse(response)
        : await response.json();

      if (!response.ok) {
        throw new Error(result?.message || 'Không tải được biểu đồ điện.');
      }

      return result?.data || {};
    })
    .then((data) => {
      const series = Array.isArray(data.dailySeries) ? data.dailySeries : [];
      const hasData = series.some((item) => Number(item.kwh || 0) > 0);

      if (!hasData) {
        if (empty) empty.classList.remove('hidden');
        if (window.App.energyBarChart) {
          window.App.energyBarChart.destroy();
          window.App.energyBarChart = null;
        }
        return;
      }

      renderSevenDayEnergyBarChart(series);
    })
    .catch((error) => {
      console.error(error);
      alert(error.message || 'Lỗi khi tải biểu đồ điện.');
    })
    .finally(() => {
      if (loading) loading.classList.add('hidden');
    });
};

function renderSevenDayEnergyBarChart(series) {
  const chartCanvas = document.getElementById('energyBarChart');
  if (!chartCanvas) return;

  const labels = series.map((item) => {
    const date = new Date(item.date);
    return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
  });
  const values = series.map((item) => Number(item.kwh || 0));

  if (window.App.energyBarChart) {
    window.App.energyBarChart.destroy();
  }

  window.App.energyBarSeries = series;
  const ctx = chartCanvas.getContext('2d');

  window.App.energyBarChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [{
        label: 'kWh',
        data: values,
        backgroundColor: '#3b82f6',
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (context) => `${Number(context.raw || 0).toFixed(2)} kWh`
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: { display: true, text: 'kWh' }
        }
      },
      onClick: (_, elements) => {
        if (!elements.length) return;
        const index = elements[0].index;
        const point = window.App.energyBarSeries?.[index];
        if (!point?.date) return;
        loadRoomBreakdownByDate(point.date);
      }
    }
  });
}

function loadRoomBreakdownByDate(usageDate) {
  const token = localStorage.getItem('token');
  const loading = document.getElementById('room-breakdown-loading');
  const list = document.getElementById('room-breakdown-list');
  const selectedDateLabel = document.getElementById('room-breakdown-selected-date');

  if (!token) return;

  if (loading) loading.classList.remove('hidden');
  if (list) list.innerHTML = '';

  const date = new Date(usageDate);
  if (selectedDateLabel) {
    selectedDateLabel.innerText = `Ngày ${date.toLocaleDateString('vi-VN')}`;
  }

  fetch(`${window.App.API_BASE}/energy/analytics/day/rooms?usageDate=${usageDate}`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
    .then(async (response) => {
      const result = typeof window.parseApiResponse === 'function'
        ? await window.parseApiResponse(response)
        : await response.json();

      if (!response.ok) {
        throw new Error(result?.message || 'Không tải được thống kê phòng.');
      }

      return result?.data || [];
    })
    .then((rooms) => {
      renderRoomBreakdownList(rooms, usageDate);
    })
    .catch((error) => {
      console.error(error);
      if (list) {
        list.innerHTML = '<div class="p-3 rounded-lg bg-red-50 text-red-600 text-sm">Không tải được thống kê phòng.</div>';
      }
    })
    .finally(() => {
      if (loading) loading.classList.add('hidden');
    });
}

function renderRoomBreakdownList(rooms, usageDate) {
  const list = document.getElementById('room-breakdown-list');
  if (!list) return;

  if (!Array.isArray(rooms) || rooms.length === 0) {
    list.innerHTML = '<div class="p-3 rounded-lg bg-gray-50 text-gray-500 text-sm">Không có dữ liệu theo phòng trong ngày này.</div>';
    return;
  }

  list.innerHTML = rooms.map((room, index) => `
    <button
      onclick="openRoomDeviceModal(${room.roomId}, '${usageDate}', '${toVietnameseDisplayName(room.roomName || '').replace(/'/g, "\\'")}')"
      class="w-full text-left p-3 rounded-xl border border-gray-100 hover:border-blue-300 hover:bg-blue-50 transition-all"
    >
      <div class="flex justify-between items-center">
        <div>
          <div class="text-sm font-semibold text-gray-800">#${index + 1} ${toVietnameseDisplayName(room.roomName || 'Không rõ phòng')}</div>
          <div class="text-xs text-gray-500">${Number(room.kwh || 0).toFixed(2)} kWh</div>
        </div>
        <div class="text-sm font-bold text-blue-700">${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(room.estimatedCost || 0)}</div>
      </div>
    </button>
  `).join('');
}

window.openRoomDeviceModal = function openRoomDeviceModal(roomId, usageDate, roomName) {
  const token = localStorage.getItem('token');
  if (!token) return;

  const modal = document.getElementById('room-device-modal');
  const title = document.getElementById('room-device-title');
  const subtitle = document.getElementById('room-device-subtitle');
  const loading = document.getElementById('room-device-loading');
  const list = document.getElementById('room-device-list');

  if (title) title.innerText = `Thiết bị trong ${toVietnameseDisplayName(roomName)}`;
  if (subtitle) {
    const date = new Date(usageDate);
    subtitle.innerText = `Ngày ${date.toLocaleDateString('vi-VN')} • Sắp xếp giảm dần theo điện năng`;
  }
  if (list) list.innerHTML = '';
  if (loading) loading.classList.remove('hidden');
  if (modal) modal.classList.remove('hidden');

  fetch(`${window.App.API_BASE}/energy/analytics/day/rooms/${roomId}/devices?usageDate=${usageDate}`, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
    .then(async (response) => {
      const result = typeof window.parseApiResponse === 'function'
        ? await window.parseApiResponse(response)
        : await response.json();

      if (!response.ok) {
        throw new Error(result?.message || 'Không tải được danh sách thiết bị.');
      }

      return result?.data || [];
    })
    .then((devices) => {
      if (!list) return;

      if (!Array.isArray(devices) || devices.length === 0) {
        list.innerHTML = '<div class="p-3 rounded-lg bg-gray-50 text-gray-500 text-sm">Không có dữ liệu thiết bị trong phòng này.</div>';
        return;
      }

      list.innerHTML = devices.map((device, index) => `
        <div class="p-3 rounded-xl border border-gray-100 bg-gray-50">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-sm font-semibold text-gray-800">#${index + 1} ${toVietnameseDisplayName(device.deviceName || 'Thiết bị không tên')}</div>
              <div class="text-xs text-gray-500">Công suất: ${Number(device.powerRating || 0).toFixed(0)} W • ${Number(device.kwh || 0).toFixed(2)} kWh</div>
            </div>
            <div class="text-sm font-bold text-blue-700">${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(device.estimatedCost || 0)}</div>
          </div>
        </div>
      `).join('');
    })
    .catch((error) => {
      console.error(error);
      if (list) {
        list.innerHTML = '<div class="p-3 rounded-lg bg-red-50 text-red-600 text-sm">Không tải được dữ liệu thiết bị.</div>';
      }
    })
    .finally(() => {
      if (loading) loading.classList.add('hidden');
    });
};

window.closeRoomDeviceModal = function closeRoomDeviceModal() {
  const modal = document.getElementById('room-device-modal');
  if (modal) {
    modal.classList.add('hidden');
  }
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
