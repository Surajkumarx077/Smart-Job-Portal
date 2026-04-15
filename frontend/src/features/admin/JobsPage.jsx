import React, { useEffect, useState } from 'react';
import { adminApi } from '../../api/admin.api';

export default function JobsPage() {
  const [jobs, setJobs] = useState([]);

  useEffect(() => {
    adminApi.getJobs().then(setJobs);
  }, []);

  return (
    <div className="page-container">
      <h1 className="text-gradient" style={{ marginBottom: '2rem' }}>All Jobs</h1>
      <div className="glass-panel" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
           <thead>
              <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                 <th style={{ padding: '1rem' }}>ID</th>
                 <th style={{ padding: '1rem' }}>Title</th>
                 <th style={{ padding: '1rem' }}>Company</th>
                 <th style={{ padding: '1rem' }}>Recruiter</th>
                 <th style={{ padding: '1rem' }}>Status</th>
              </tr>
           </thead>
           <tbody>
              {jobs.map(j => (
                 <tr key={j.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '1rem' }}>{j.id}</td>
                    <td style={{ padding: '1rem', fontWeight: 500 }}>{j.title}</td>
                    <td style={{ padding: '1rem' }}>{j.company}</td>
                    <td style={{ padding: '1rem' }}>{j.recruiterName}</td>
                    <td style={{ padding: '1rem' }}><span className="status-badge status-applied">{j.active ? 'Active' : 'Closed'}</span></td>
                 </tr>
              ))}
           </tbody>
        </table>
      </div>
    </div>
  );
}
