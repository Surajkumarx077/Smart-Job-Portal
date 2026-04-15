import React, { useEffect, useState } from 'react';
import { adminApi } from '../../api/admin.api';

export default function ApplicationsPage() {
  const [apps, setApps] = useState([]);

  useEffect(() => {
    adminApi.getApplications().then(setApps);
  }, []);

  return (
    <div className="page-container">
      <h1 className="text-gradient" style={{ marginBottom: '2rem' }}>All Applications</h1>
      <div className="glass-panel" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
           <thead>
              <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                 <th style={{ padding: '1rem' }}>ID</th>
                 <th style={{ padding: '1rem' }}>Applicant</th>
                 <th style={{ padding: '1rem' }}>Job Title</th>
                 <th style={{ padding: '1rem' }}>Date</th>
              </tr>
           </thead>
           <tbody>
              {apps.map(a => (
                 <tr key={a.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '1rem' }}>{a.id}</td>
                    <td style={{ padding: '1rem', color: 'var(--color-primary)' }}>{a.applicantName}</td>
                    <td style={{ padding: '1rem', fontWeight: 500 }}>{a.jobTitle}</td>
                    <td style={{ padding: '1rem' }}>{new Date(a.createdAt).toLocaleDateString()}</td>
                 </tr>
              ))}
           </tbody>
        </table>
      </div>
    </div>
  );
}
