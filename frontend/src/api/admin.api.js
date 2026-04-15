import { apiClient } from './client';

export const adminApi = {
  getStats: () => apiClient.get('/admin/stats'),
  getUsers: (page = 0, size = 20) => apiClient.get(`/admin/users?page=${page}&size=${size}`),
  getJobs: () => apiClient.get('/admin/jobs'),
  getApplications: () => apiClient.get('/admin/applications'),
};
