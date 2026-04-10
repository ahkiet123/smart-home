function resolveApiBase() {
  if (window.App && window.App.API_BASE) {
    return window.App.API_BASE;
  }
  if (window.API_BASE) {
    return window.API_BASE;
  }
  return (window.location.port === '5173' || window.location.port === '4173')
    ? 'http://localhost:8080/api/v1'
    : '/api/v1';
}

function setLoadingSafe(button, isLoading, loadingText, defaultText) {
  if (typeof window.setButtonLoading === 'function') {
    window.setButtonLoading(button, isLoading, loadingText, defaultText);
    return;
  }

  if (!button) return;
  button.disabled = isLoading;
  button.style.opacity = isLoading ? '0.8' : '1';
  button.style.cursor = isLoading ? 'not-allowed' : 'pointer';
  button.textContent = isLoading ? loadingText : defaultText;
}

async function parseApiResponseSafe(response) {
  if (typeof window.parseApiResponse === 'function') {
    return window.parseApiResponse(response);
  }

  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return response.json();
  }
  const text = await response.text();
  return { message: text };
}

window.switchDashboardTab = function switchDashboardTab(tabId) {
  const overviewContent = document.getElementById('overview-content');
  const profileContent = document.getElementById('profile-page-content');
  if (!overviewContent || !profileContent) return;

  overviewContent.classList.add('hidden');
  profileContent.classList.add('hidden');

  const selectedContent = document.getElementById(tabId + '-content');
  if (selectedContent) {
    selectedContent.classList.remove('hidden');
  }

  const fabAddButton = document.getElementById('fab-add-device');
  if (fabAddButton) {
    if (tabId === 'profile-page') {
      fabAddButton.classList.add('hidden');
    } else {
      fabAddButton.classList.remove('hidden');
    }
  }

  const overviewTab = document.getElementById('tab-overview');
  const profileTab = document.getElementById('tab-profile-page');

  if (overviewTab) {
    overviewTab.className = 'flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer';
  }
  if (profileTab) {
    profileTab.className = 'flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer';
  }

  if (tabId === 'overview' && overviewTab) {
    overviewTab.className = 'flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer';
  } else if (tabId === 'profile-page' && profileTab) {
    profileTab.className = 'flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer';
  }
};

window.openQuickProfileModal = function openQuickProfileModal() {
  const modal = document.getElementById('quick-profile-modal');
  if (modal) {
    modal.classList.remove('hidden');
  }
};

window.closeQuickProfileModal = function closeQuickProfileModal() {
  const modal = document.getElementById('quick-profile-modal');
  if (modal) {
    modal.classList.add('hidden');
  }

  const oldInput = document.getElementById('quick-old-password');
  const newInput = document.getElementById('quick-new-password');
  if (oldInput) oldInput.value = '';
  if (newInput) newInput.value = '';
};

window.goToProfilePage = function goToProfilePage() {
  window.closeQuickProfileModal();
  window.switchDashboardTab('profile-page');
};

window.submitChangePassword = async function submitChangePassword() {
  const oldPassword = document.getElementById('quick-old-password')?.value;
  const newPassword = document.getElementById('quick-new-password')?.value;
  const submitBtn = document.querySelector('#quick-profile-modal form button[type="submit"]');
  const token = localStorage.getItem('token');
  const apiBase = resolveApiBase();

  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.closeQuickProfileModal();
    window.doLogout();
    return;
  }

  if (!oldPassword || !newPassword) {
    alert('Vui lòng điền đầy đủ mật khẩu cũ và mật khẩu mới.');
    return;
  }

  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;
  if (!passwordRegex.test(newPassword)) {
    alert('Mật khẩu mới phải có chữ hoa, chữ thường, số, ký tự đặc biệt và tối thiểu 6 ký tự');
    return;
  }

  setLoadingSafe(submitBtn, true, 'Đang đổi mật khẩu...', 'Xác nhận đổi mật khẩu');

  try {
    const response = await fetch(`${apiBase}/auth/change-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ oldPassword, newPassword })
    });

    const result = await parseApiResponseSafe(response);
    if (!response.ok) {
      alert(result.message || 'Đổi mật khẩu thất bại');
      return;
    }

    alert('Đổi mật khẩu thành công! Vui lòng đăng nhập lại.');
    window.closeQuickProfileModal();
    window.doLogout();
  } catch (error) {
    console.error(error);
    alert('Lỗi kết nối server');
  } finally {
    setLoadingSafe(submitBtn, false, 'Đang đổi mật khẩu...', 'Xác nhận đổi mật khẩu');
  }
};

window.submitUpdateFullProfile = function submitUpdateFullProfile() {
  const name = document.getElementById('full-profile-name')?.value || 'Admin User';

  const pageDisplayName = document.getElementById('page-display-name');
  const modalDisplayName = document.getElementById('modal-display-name');
  if (pageDisplayName) pageDisplayName.innerText = name;
  if (modalDisplayName) modalDisplayName.innerText = name;

  let initials = 'AD';
  if (name.trim()) {
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      initials = (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    } else {
      initials = name.substring(0, 2).toUpperCase();
    }
  }

  const pageAvatar = document.getElementById('page-avatar');
  const modalAvatar = document.getElementById('modal-avatar');
  const headerAvatar = document.getElementById('header-avatar');
  if (pageAvatar) pageAvatar.innerText = initials;
  if (modalAvatar) modalAvatar.innerText = initials;
  if (headerAvatar) headerAvatar.innerText = initials;

  alert('Thông tin hồ sơ cá nhân đã được lưu!');
};
