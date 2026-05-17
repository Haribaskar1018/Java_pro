import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Auto logout on 401
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ─── Auth ────────────────────────────────────────────────────────
export const authApi = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
};

// ─── Groups ──────────────────────────────────────────────────────
export const groupApi = {
  create: (data) => api.post('/api/groups', data),
  getAll: () => api.get('/api/groups'),
  getById: (id) => api.get(`/api/groups/${id}`),
  searchUsers: (query) => api.get(`/api/groups/users/search?query=${query}`),
};

// ─── Expenses ────────────────────────────────────────────────────
export const expenseApi = {
  create: (data) => api.post('/api/expenses', data),
  getByGroup: (groupId) => api.get(`/api/expenses/group/${groupId}`),
  getSettlementSummary: () => api.get('/api/expenses/settlement-summary'),
  settleShare: (shareId) => api.patch(`/api/expenses/settle/${shareId}`),
};

// ─── QR ──────────────────────────────────────────────────────────
export const qrApi = {
  generateUpiQr: (upiId, name, amount, note) =>
    api.get(`/api/qr/upi?upiId=${upiId}&name=${encodeURIComponent(name)}&amount=${amount}&note=${encodeURIComponent(note || '')}`),
};

export default api;
