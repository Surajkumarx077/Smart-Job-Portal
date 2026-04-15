import React, { useEffect, useState } from 'react';
import { applicationsApi } from '../../api/applications.api';
import './StudentViews.css';
import { Briefcase, Calendar } from 'lucide-react';
import { CalendarClock, Link as LinkIcon } from 'lucide-react';

export default function MyApplicationsPage() {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchApps = async () => {
      try {
        const data = await applicationsApi.getMyApplications();
        setApps(data);
      } catch (err) {
        setError('Failed to load applications.');
      } finally {
        setLoading(false);
      }
    };
    fetchApps();
  }, []);

  if (loading) return <div className="page-loader">Loading applications...</div>;

  return (
    <div className="page-container">
      <h1 className="text-gradient">My Applications</h1>
      <p className="text-muted" style={{ marginBottom: '2rem' }}>Track the status of roles you have applied for.</p>

      {error && <div className="page-error" style={{ marginBottom: '2rem' }}>{error}</div>}

      {apps.length === 0 && !error ? (
        <div className="empty-state glass-panel">
           You have not applied to any jobs yet.
        </div>
      ) : (
        <div className="applications-list">
          {apps.map(app => (
            <div key={app.id} className="app-card glass-panel flex-row">
              <div className="app-main-info">
                 <h3 className="app-job-title"><Briefcase size={18} className="text-primary"/> {app.jobTitle}</h3>
                 <p className="app-date"><Calendar size={16}/> Applied on: {new Date(app.createdAt).toLocaleDateString()}</p>
                 {app.coverLetter && <p className="text-muted" style={{ marginTop: '0.5rem', fontSize: '0.9rem', fontStyle: 'italic' }}>"{app.coverLetter.substring(0, 80)}..."</p>}
                 {app.interviewScheduledAt && (
                  <div className="interview-card" style={{ marginTop: '0.75rem' }}>
                    <p><CalendarClock size={16} /> Interview: {new Date(app.interviewScheduledAt).toLocaleString()}</p>
                    <p>
                      <LinkIcon size={16} /> Meeting:{' '}
                      <a href={app.onlineMeetingLink} target="_blank" rel="noreferrer">Join meeting</a>
                    </p>
                    {app.interviewNotes && <p>Notes: {app.interviewNotes}</p>}
                  </div>
                 )}
              </div>
              <div className="app-status">
                 <span className={`status-badge ${app.status === 'INTERVIEW_SCHEDULED' ? 'status-interview' : 'status-applied'}`}>
                   {app.status === 'INTERVIEW_SCHEDULED' ? 'Interview Scheduled' : 'Applied'}
                 </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
