window.App = {
  API_BASE: (window.location.port === '5173' || window.location.port === '4173')
    ? 'http://localhost:8080/api/v1'
    : '/api/v1',
  ELECTRICITY_RATE: 2500,
  isEditMode: false,
  myChart: null,
  roomChart: null,
  devices: [
    { id: 101, name: 'Đèn chùm phòng khách', room: 'Phòng khách', type: 'light', powerW: 100, hours: 5, isOn: true },
    { id: 102, name: 'Smart TV 55 inch', room: 'Phòng khách', type: 'tv', powerW: 150, hours: 4, isOn: false },
    { id: 103, name: 'Máy lạnh (Living)', room: 'Phòng khách', type: 'ac', powerW: 1200, hours: 3, isOn: true },
    { id: 301, name: 'Đèn trần PN2', room: 'Phòng ngủ', type: 'light', powerW: 40, hours: 2, isOn: true },
    { id: 302, name: 'Quạt treo tường', room: 'Phòng ngủ', type: 'ac', powerW: 60, hours: 6, isOn: true },
    { id: 401, name: 'Tủ lạnh Inverter', room: 'Bếp', type: 'fridge', powerW: 150, hours: 24, isOn: true },
    { id: 402, name: 'Lò vi sóng', room: 'Bếp', type: 'tv', powerW: 1000, hours: 0.5, isOn: false },
    { id: 501, name: 'Bình nóng lạnh', room: 'Phòng tắm', type: 'ac', powerW: 2500, hours: 1, isOn: false },
    { id: 502, name: 'Đèn gương', room: 'Phòng tắm', type: 'light', powerW: 15, hours: 1, isOn: true }
  ],
  articles: [
    { title: '5 mẹo thiết lập máy lạnh tiết kiệm điện', category: 'Mẹo vặt', time: '5 phút', img: 'https://images.unsplash.com/photo-1585338107529-13afc5f02586?w=400&q=80' },
    { title: 'Xu hướng nhà thông minh đáng chú ý', category: 'Công nghệ', time: '8 phút', img: 'https://images.unsplash.com/photo-1558002038-1055907df827?w=400&q=80' },
    { title: 'Cách bảo dưỡng thiết bị điện định kỳ', category: 'Bảo trì', time: '4 phút', img: 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=400&q=80' }
  ]
};

window.togglePassword = function togglePassword(inputId, icon) {
  const input = document.getElementById(inputId);
  if (!input) return;

  if (input.type === 'password') {
    input.type = 'text';
    icon.classList.remove('fa-eye');
    icon.classList.add('fa-eye-slash');
  } else {
    input.type = 'password';
    icon.classList.remove('fa-eye-slash');
    icon.classList.add('fa-eye');
  }
};

window.onclick = function (event) {
  const modalOverlays = [
    'chart-modal',
    'room-stats-modal',
    'stats-detail-modal',
    'device-detail-modal',
    'add-modal',
    'quick-profile-modal'
  ];

  modalOverlays.forEach((id) => {
    const modal = document.getElementById(id);
    if (event.target === modal) {
      modal.classList.add('hidden');
    }
  });
};

if (window.lucide) {
  window.lucide.createIcons();
}
