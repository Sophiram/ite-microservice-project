import axios from 'axios';

const resolveBaseUrl = () => {
    if (import.meta.env.VITE_API_BASE_URL) return import.meta.env.VITE_API_BASE_URL;
    if (typeof window !== 'undefined') {
        if (window.location.port === '9000' || window.location.port === '8080') {
            return '';
        }
    }
    return 'http://localhost:9000';
};

const api = axios.create({
    baseURL: resolveBaseUrl(),
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Auto-attach JWT token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;