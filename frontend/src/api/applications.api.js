import { apiClient } from './client';

export const applicationsApi = {
  applyForJob: (data) => apiClient.post('/applications', data),
  getMyApplications: () => apiClient.get('/applications/my'),
  getJobApplications: (jobId) => apiClient.get(`/applications/job/${jobId}`),
  scheduleInterview: (applicationId, data) => apiClient.put(`/applications/${applicationId}/interview`, data),
};
