window.setButtonLoading = function setButtonLoading(button, isLoading, loadingText, defaultText) {
  if (!button) return;
  button.disabled = isLoading;
  button.style.opacity = isLoading ? '0.8' : '1';
  button.style.cursor = isLoading ? 'not-allowed' : 'pointer';
  button.textContent = isLoading ? loadingText : defaultText;
};

window.parseApiResponse = async function parseApiResponse(response) {
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return await response.json();
  }

  const text = await response.text();
  return { message: text };
};

window.switchAuthForm = function switchAuthForm(formId) {
  const forms = document.querySelectorAll('.auth-form');
  forms.forEach((form) => form.classList.remove('active'));
  const targetForm = document.getElementById(formId);
  if (targetForm) {
    targetForm.classList.add('active');
  }
};

window.openDashboard = function openDashboard() {
  const authView = document.getElementById('auth-view');
  const dashboard = document.getElementById('dashboard-view');
  if (!authView || !dashboard) return;

  authView.style.display = 'none';
  dashboard.style.display = 'flex';
  setTimeout(() => dashboard.classList.add('active'), 50);
  if (window.initDashboard) {
    window.initDashboard();
  }
};

window.doLogin = async function doLogin() {
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-password').value;
  const submitBtn = document.querySelector('#form-login .btn-auth');

  window.setButtonLoading(submitBtn, true, 'Đang đăng nhập...', 'Đăng nhập');

  try {
    const response = await fetch(`${window.App.API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });

    const result = await window.parseApiResponse(response);
    if (!response.ok) {
      alert(result.message || 'Đăng nhập thất bại');
      return;
    }

    const token = result?.data?.token;
    if (!token) {
      alert('Đăng nhập thất bại: Không nhận được token từ máy chủ');
      return;
    }

    localStorage.setItem('token', token);
    localStorage.setItem('userEmail', email);

    const fullProfileEmail = document.getElementById('full-profile-email');
    const pageDisplayEmail = document.getElementById('page-display-email');
    const modalDisplayEmail = document.getElementById('modal-display-email');
    if (fullProfileEmail) fullProfileEmail.value = email;
    if (pageDisplayEmail) pageDisplayEmail.innerText = email;
    if (modalDisplayEmail) modalDisplayEmail.innerText = email;

    window.openDashboard();
  } catch (error) {
    alert('Lỗi kết nối tới máy chủ');
    console.error(error);
  } finally {
    window.setButtonLoading(submitBtn, false, 'Đang đăng nhập...', 'Đăng nhập');
  }
};

window.doRegister = async function doRegister() {
  const fullName = document.getElementById('register-fullname').value;
  const email = document.getElementById('register-email').value;
  const password = document.getElementById('register-password').value;
  const confirmPassword = document.getElementById('register-confirm').value;

  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;
  if (!passwordRegex.test(password)) {
    alert('Mật khẩu phải có chữ hoa, chữ thường, số, ký tự đặc biệt');
    return;
  }

  if (password !== confirmPassword) {
    alert('Mật khẩu xác nhận không khớp!');
    return;
  }

  try {
    const response = await fetch(`${window.App.API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ fullName, email, password })
    });

    if (!response.ok && response.status !== 201) {
      const text = await response.text();
      if (text.includes('Email address already in use')) {
        alert('Email đã tồn tại rồi!');
      } else {
        alert('Đăng ký thất bại: ' + text);
      }
      return;
    }

    alert('Đăng ký thành công! Vui lòng đăng nhập.');
    window.switchAuthForm('login-form');
  } catch (error) {
    console.error(error);
    alert('Lỗi kết nối server');
  }
};

window.doForgotPassword = async function doForgotPassword() {
  const email = document.getElementById('forgot-email').value;
  localStorage.setItem('resetEmail', email);

  try {
    const response = await fetch(`${window.App.API_BASE}/auth/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email })
    });

    const result = await response.json();

    if (response.ok) {
      alert('OTP đã gửi về email!');
      window.switchAuthForm('otp-form');
    } else {
      alert('Lỗi: ' + (result.message || 'Không gửi được OTP'));
    }
  } catch (error) {
    alert('Lỗi server');
    console.error(error);
  }
};

window.doVerifyOTP = async function doVerifyOTP() {
  const email = localStorage.getItem('resetEmail');
  const otp = document.getElementById('otp-code').value;

  try {
    const response = await fetch(`${window.App.API_BASE}/auth/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, otp })
    });

    if (response.ok) {
      alert('OTP đúng!');
      window.switchAuthForm('reset-password-form');
    } else {
      alert('OTP sai hoặc hết hạn');
    }
  } catch (error) {
    alert('Lỗi server');
    console.error(error);
  }
};

window.doResetPassword = async function doResetPassword() {
  const email = localStorage.getItem('resetEmail');
  const password = document.getElementById('reset-password').value;
  const confirm = document.getElementById('reset-confirm-password').value;
  const otp = document.getElementById('otp-code').value;
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{6,}$/;

  if (!passwordRegex.test(password)) {
    alert('Mật khẩu phải có chữ hoa, chữ thường, số, ký tự đặc biệt');
    return;
  }

  if (password !== confirm) {
    alert('Mật khẩu không khớp');
    return;
  }

  try {
    const response = await fetch(`${window.App.API_BASE}/auth/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email,
        otp,
        newPassword: password
      })
    });

    const result = await window.parseApiResponse(response);
    if (response.ok) {
      alert('Đổi mật khẩu thành công!');
      window.switchAuthForm('login-form');
    } else {
      alert(result.message || 'Mật khẩu mới không được trùng mật khẩu cũ');
    }
  } catch (error) {
    alert('Lỗi server');
    console.error(error);
  }
};

window.doLogout = function doLogout() {
  localStorage.removeItem('token');
  localStorage.removeItem('userEmail');
  const dashboard = document.getElementById('dashboard-view');
  const authView = document.getElementById('auth-view');
  if (!dashboard || !authView) return;

  dashboard.classList.remove('active');
  setTimeout(() => {
    dashboard.style.display = 'none';
    authView.style.display = 'flex';
    window.switchAuthForm('login-form');
    if (window.switchDashboardTab) {
      window.switchDashboardTab('overview');
    }
  }, 500);
};
