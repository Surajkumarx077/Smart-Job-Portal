import React, { useEffect, useState } from 'react';
import { adminApi } from '../../api/admin.api';

export default function UsersPage() {
  const [data, setData] = useState({ content: [], totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers(0);
  }, []);

  const fetchUsers = async (page) => {
    setLoading(true);
    try {
       const result = await adminApi.getUsers(page);
       setData(result);
    } finally {
       setLoading(false);
    }
  };

  if (loading && data.content.length === 0) return <div className="page-loader">Loading users...</div>;

  return (
    <div className="page-container">
      <h1 className="text-gradient" style={{ marginBottom: '2rem' }}>Platform Users</h1>
      <div className="glass-panel" style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
           <thead>
              <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                 <th style={{ padding: '1rem' }}>ID</th>
                 <th style={{ padding: '1rem' }}>Name</th>
                 <th style={{ padding: '1rem' }}>Email</th>
                 <th style={{ padding: '1rem' }}>Role</th>
                 <th style={{ padding: '1rem' }}>Joined</th>
              </tr>
           </thead>
           <tbody>
              {data.content.map(u => (
                 <tr key={u.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '1rem' }}>{u.id}</td>
                    <td style={{ padding: '1rem' }}>{u.fullName}</td>
                    <td style={{ padding: '1rem', color: 'var(--color-info)' }}>{u.email}</td>
                    <td style={{ padding: '1rem' }}><span className="status-badge">{u.role}</span></td>
                    <td style={{ padding: '1rem' }}>{new Date(u.createdAt).toLocaleDateString()}</td>
                 </tr>
              ))}
           </tbody>
        </table>
      </div>
      <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '2rem' }}>
          <button className="btn btn-secondary btn-sm" disabled={data.number === 0} onClick={() => fetchUsers(data.number - 1)}>Previous</button>
          <span style={{ lineHeight: '32px' }}>Page {data.number + 1} of {data.totalPages}</span>
          <button className="btn btn-secondary btn-sm" disabled={data.number + 1 >= data.totalPages} onClick={() => fetchUsers(data.number + 1)}>Next</button>
      </div>
    </div>
  );
}
