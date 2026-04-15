import { apiClient } from './client';

export const resumesApi = {
  uploadResume: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return apiClient.post('/resumes/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  getResume: () => apiClient.get('/resumes/me'),
  getParsedResume: () => apiClient.get('/resumes/parsed'),
};
