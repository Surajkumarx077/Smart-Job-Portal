import { apiClient } from './client';

export const rankingApi = {
  getRankedCandidates: (jobId) => apiClient.get(`/ranking/job/${jobId}`)
};
