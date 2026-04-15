import { apiClient } from './client';

export const jobsApi = {
  getPublicJobs: () => apiClient.get('/jobs/public'),
  getJobDetails: (id) => apiClient.get(`/jobs/${id}`),
  
  // Recruiter specific
  getMyJobs: () => apiClient.get('/jobs/my'),
  createJob: (data) => apiClient.post('/jobs', data),
  updateJob: (id, data) => apiClient.put(`/jobs/${id}`, data),
  deleteJob: (id) => apiClient.delete(`/jobs/${id}`),
};
