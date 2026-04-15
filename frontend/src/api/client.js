import axios from 'axios';
import { getToken, removeToken } from '../utils/token';

export const apiClient = axios.create({
  baseURL: '/api', // proxied explicitly by Vite to http://localhost:8081
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token && !config.headers.Authorization) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response.data, // return data directly
  (error) => {
    if (error.response?.status === 401) {
      removeToken();
      // Only redirect if not already on login page to avoid loops
      if (window.location.pathname !== '/login') {
         window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
