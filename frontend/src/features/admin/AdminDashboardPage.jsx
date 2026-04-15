import React, { useEffect, useState } from 'react';
import { adminApi } from '../../api/admin.api';
import { Users, Briefcase, FileText } from 'lucide-react';

export default function AdminDashboardPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.getStats()
      .then(setStats)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-loader">Loading stats...</div>;

  return (
    <div className="page-container">
      <h1 className="text-gradient" style={{ marginBottom: '2rem' }}>Admin Dashboard</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem', marginBottom: '2rem' }}>
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center' }}>
           <Users size={32} style={{ color: 'var(--color-info)', marginBottom: '1rem' }}/>
           <h2>{stats?.totalUsers || 0}</h2>
           <p className="text-muted">Total Users</p>
        </div>
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center' }}>
           <Briefcase size={32} style={{ color: 'var(--color-warning)', marginBottom: '1rem' }}/>
           <h2>{stats?.totalJobs || 0}</h2>
           <p className="text-muted">Total Jobs</p>
        </div>
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center' }}>
           <FileText size={32} style={{ color: 'var(--color-success)', marginBottom: '1rem' }}/>
           <h2>{stats?.totalApplications || 0}</h2>
           <p className="text-muted">Total Applications</p>
        </div>
      </div>

      <div className="glass-panel" style={{ padding: '2rem' }}>
         <h3>Users by Role</h3>
         <div style={{ marginTop: '1rem', display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
            <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px', flex: 1, minWidth: '150px', textAlign: 'center' }}>
               <h4 style={{ color: 'var(--color-text)' }}>Students</h4>
               <p style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats?.usersByRole?.STUDENT || 0}</p>
            </div>
            <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px', flex: 1, minWidth: '150px', textAlign: 'center' }}>
               <h4 style={{ color: 'var(--color-text)' }}>Recruiters</h4>
               <p style={{ fontSize: '1.8rem', fontWeight: 'bold', color: 'var(--color-primary)' }}>{stats?.usersByRole?.RECRUITER || 0}</p>
            </div>
            <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: '8px', flex: 1, minWidth: '150px', textAlign: 'center' }}>
               <h4 style={{ color: 'var(--color-text)' }}>Admins</h4>
               <p style={{ fontSize: '1.8rem', fontWeight: 'bold', color: 'var(--color-error)' }}>{stats?.usersByRole?.ADMIN || 0}</p>
            </div>
         </div>
      </div>
    </div>
  );
}
