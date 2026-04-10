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

window.ProfileState = {
  currentProfile: null
};

function getProfileInitials(fullName) {
  const name = (fullName || '').trim();
  if (!name) return 'AD';

  const parts = name.split(/\s+/);
  if (parts.length >= 2) {
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  return name.slice(0, 2).toUpperCase();
}

function applyProfileToUi(profile) {
  const fullName = (profile?.fullName || '').trim() || 'Admin User';
  const email = profile?.email || localStorage.getItem('userEmail') || '';
  const phone = profile?.phone || '';
  const initials = getProfileInitials(fullName);

  const fullNameInput = document.getElementById('full-profile-name');
  const phoneInput = document.getElementById('full-profile-phone');
  const emailInput = document.getElementById('full-profile-email');
  const pageDisplayName = document.getElementById('page-display-name');
  const modalDisplayName = document.getElementById('modal-display-name');
  const pageDisplayEmail = document.getElementById('page-display-email');
  const modalDisplayEmail = document.getElementById('modal-display-email');
  const pageAvatar = document.getElementById('page-avatar');
  const modalAvatar = document.getElementById('modal-avatar');
  const headerAvatar = document.getElementById('header-avatar');

  if (fullNameInput) fullNameInput.value = fullName;
  if (phoneInput) phoneInput.value = phone;
  if (emailInput) emailInput.value = email;
  if (pageDisplayName) pageDisplayName.innerText = fullName;
  if (modalDisplayName) modalDisplayName.innerText = fullName;
  if (pageDisplayEmail) pageDisplayEmail.innerText = email;
  if (modalDisplayEmail) modalDisplayEmail.innerText = email;
  if (pageAvatar) pageAvatar.innerText = initials;
  if (modalAvatar) modalAvatar.innerText = initials;
  if (headerAvatar) headerAvatar.innerText = initials;
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
  const energyContent = document.getElementById('energy-page-content');
  const profileContent = document.getElementById('profile-page-content');
  if (!overviewContent || !profileContent || !energyContent) return;

  overviewContent.classList.add('hidden');
  energyContent.classList.add('hidden');
  profileContent.classList.add('hidden');

  const selectedContent = document.getElementById(tabId + '-content');
  if (selectedContent) {
    selectedContent.classList.remove('hidden');
  }

  const fabAddButton = document.getElementById('fab-add-device');
  if (fabAddButton) {
    if (tabId === 'profile-page' || tabId === 'energy-page') {
      fabAddButton.classList.add('hidden');
    } else {
      fabAddButton.classList.remove('hidden');
    }
  }

  const overviewTab = document.getElementById('tab-overview');
  const energyTab = document.getElementById('tab-energy-page');
  const profileTab = document.getElementById('tab-profile-page');

  if (overviewTab) {
    overviewTab.className = 'flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer';
  }
  if (energyTab) {
    energyTab.className = 'flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer';
  }
  if (profileTab) {
    profileTab.className = 'flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 rounded-xl font-medium transition-colors cursor-pointer';
  }

  if (tabId === 'overview' && overviewTab) {
    overviewTab.className = 'flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer';
    if (typeof window.loadGlobalEnergyOverview === 'function') {
      window.loadGlobalEnergyOverview();
    }
  } else if (tabId === 'energy-page' && energyTab) {
    energyTab.className = 'flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer';
  } else if (tabId === 'profile-page' && profileTab) {
    profileTab.className = 'flex items-center px-4 py-3 text-blue-700 bg-blue-50 rounded-xl font-medium transition-colors cursor-pointer';
  }

  if (tabId === 'energy-page' && typeof window.showEnergyChart === 'function') {
    window.showEnergyChart();
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

window.loadUserProfile = async function loadUserProfile() {
  const token = localStorage.getItem('token');
  const apiBase = resolveApiBase();

  if (!token) {
    applyProfileToUi({
      fullName: 'Admin User',
      email: localStorage.getItem('userEmail') || '',
      phone: ''
    });
    return;
  }

  try {
    const response = await fetch(`${apiBase}/users/profile`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

    const result = await parseApiResponseSafe(response);
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        window.doLogout();
        return;
      }
      throw new Error(result?.message || 'Không thể tải thông tin hồ sơ');
    }

    const profile = result?.data || {};
    window.ProfileState.currentProfile = profile;

    if (profile.email) {
      localStorage.setItem('userEmail', profile.email);
    }

    applyProfileToUi(profile);
  } catch (error) {
    console.error(error);
    const fallbackEmail = localStorage.getItem('userEmail') || '';
    applyProfileToUi({ fullName: 'Admin User', email: fallbackEmail, phone: '' });
    alert('Không tải được hồ sơ người dùng từ server.');
  }
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

window.submitUpdateFullProfile = async function submitUpdateFullProfile() {
  const fullName = (document.getElementById('full-profile-name')?.value || '').trim();
  const phone = (document.getElementById('full-profile-phone')?.value || '').trim();
  const submitBtn = document.querySelector('#profile-page-content form button[type="submit"]');
  const token = localStorage.getItem('token');
  const apiBase = resolveApiBase();

  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.doLogout();
    return;
  }

  if (!fullName) {
    alert('Họ và tên không được để trống.');
    return;
  }

  const vnPhoneRegex = /^(0|\+84)(3|5|7|8|9)\d{8}$/;
  if (phone && !vnPhoneRegex.test(phone)) {
    alert('Số điện thoại không hợp lệ (định dạng VN).');
    return;
  }

  const cached = window.ProfileState.currentProfile || {};
  const cachedFullName = (cached.fullName || '').trim();
  const cachedPhone = (cached.phone || '').trim();

  if (cachedFullName === fullName && cachedPhone === phone) {
    alert('Thông tin chưa thay đổi.');
    return;
  }

  const payload = {
    fullName,
    phone,
    address: cached.address || '',
    city: cached.city || '',
    country: cached.country || ''
  };

  setLoadingSafe(submitBtn, true, 'Đang lưu thay đổi...', 'Lưu thay đổi');

  try {
    const response = await fetch(`${apiBase}/users/profile`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    const result = await parseApiResponseSafe(response);
    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
        window.doLogout();
        return;
      }
      alert(result?.message || 'Cập nhật hồ sơ thất bại');
      return;
    }

    window.ProfileState.currentProfile = {
      ...cached,
      ...payload,
      email: cached.email || localStorage.getItem('userEmail') || ''
    };
    applyProfileToUi(window.ProfileState.currentProfile);

    alert('Đã cập nhật hồ sơ thành công.');
  } catch (error) {
    console.error(error);
    alert('Lỗi kết nối server khi cập nhật hồ sơ.');
  } finally {
    setLoadingSafe(submitBtn, false, 'Đang lưu thay đổi...', 'Lưu thay đổi');
  }
};
