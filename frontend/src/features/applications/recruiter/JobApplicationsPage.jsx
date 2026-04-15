import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { applicationsApi } from '../../../api/applications.api';
import { ArrowLeft, User, FileText, Mail, CalendarClock, Link as LinkIcon, CheckCircle2 } from 'lucide-react';
import '../StudentViews.css';

export default function JobApplicationsPage() {
  const { id } = useParams();
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeFormId, setActiveFormId] = useState(null);
  const [submittingId, setSubmittingId] = useState(null);
  const [formData, setFormData] = useState({
    interviewScheduledAt: '',
    onlineMeetingLink: '',
    interviewNotes: '',
  });

  const fetchApps = async () => {
    try {
      const data = await applicationsApi.getJobApplications(id);
      setApps(data);
    } catch (err) {
      setError('Failed to load applications for this job.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApps();
  }, [id]);

  const openScheduleForm = (applicationId) => {
    setActiveFormId(applicationId);
    setFormData({
      interviewScheduledAt: '',
      onlineMeetingLink: '',
      interviewNotes: '',
    });
    setError(null);
  };

  const submitInterviewSchedule = async (applicationId) => {
    if (!formData.interviewScheduledAt || !formData.onlineMeetingLink) {
      setError('Interview date/time and meeting link are required.');
      return;
    }

    setSubmittingId(applicationId);
    setError(null);
    try {
      await applicationsApi.scheduleInterview(applicationId, {
        interviewScheduledAt: new Date(formData.interviewScheduledAt).toISOString(),
        onlineMeetingLink: formData.onlineMeetingLink,
        interviewNotes: formData.interviewNotes,
      });
      setActiveFormId(null);
      await fetchApps();
    } catch (err) {
      const msg = err?.response?.data?.error || 'Failed to schedule interview.';
      setError(msg);
    } finally {
      setSubmittingId(null);
    }
  };

  if (loading) return <div className="page-loader">Loading applications...</div>;

  return (
    <div className="page-container">
      <Link to="/recruiter/jobs" className="btn btn-secondary btn-sm" style={{ marginBottom: '2rem' }}>
        <ArrowLeft size={16} /> Back to My Jobs
      </Link>
      <h1 className="text-gradient">Job Applications</h1>
      <p className="text-muted" style={{ marginBottom: '2rem' }}>Candidates who applied for this role.</p>
      {error && <div className="page-error" style={{ marginBottom: '1rem' }}>{error}</div>}

      {apps.length === 0 ? (
        <div className="empty-state glass-panel">No one has applied to this job yet.</div>
      ) : (
        <div className="applications-list">
          {apps.map(app => (
            <div key={app.id} className="app-card glass-panel" style={{ flexDirection: 'column', alignItems: 'flex-start', gap: '1rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                  <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                     <User size={18} className="text-primary"/> {app.applicantName}
                  </h3>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span className="status-badge status-applied">
                      {app.status || 'APPLIED'}
                    </span>
                    <span className="text-muted" style={{ fontSize: '0.9rem' }}>{new Date(app.createdAt).toLocaleDateString()}</span>
                  </div>
              </div>
              <p style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-info)' }}>
                 <Mail size={16}/> {app.applicantEmail}
              </p>
              {app.coverLetter && (
                <div style={{ background: 'rgba(0,0,0,0.2)', padding: '1rem', borderRadius: 'var(--border-radius-sm)', width: '100%', fontStyle: 'italic', fontSize: '0.95rem' }}>
                   "{app.coverLetter}"
                </div>
              )}
              {app.resumeUrl && (
                  <a href={app.resumeUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm" style={{ marginTop: '0.5rem' }}>
                    <FileText size={16}/> View Resume
                  </a>
              )}

              {app.interviewScheduledAt && (
                <div className="interview-card" style={{ width: '100%' }}>
                  <p><CalendarClock size={16} /> Interview: {new Date(app.interviewScheduledAt).toLocaleString()}</p>
                  <p>
                    <LinkIcon size={16} /> Meeting:
                    {' '}
                    <a href={app.onlineMeetingLink} target="_blank" rel="noreferrer">Join meeting</a>
                  </p>
                  {app.interviewNotes && <p><CheckCircle2 size={16} /> Notes: {app.interviewNotes}</p>}
                </div>
              )}

              <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.25rem' }}>
                <button
                  type="button"
                  className="btn btn-primary btn-sm"
                  onClick={() => openScheduleForm(app.id)}
                >
                  {app.interviewScheduledAt ? 'Reschedule Interview' : 'Shortlist & Schedule Interview'}
                </button>
              </div>

              {activeFormId === app.id && (
                <div className="interview-form" style={{ width: '100%' }}>
                  <div className="form-group">
                    <label className="form-label">Interview Date & Time</label>
                    <input
                      type="datetime-local"
                      className="form-control"
                      value={formData.interviewScheduledAt}
                      onChange={(e) => setFormData((prev) => ({ ...prev, interviewScheduledAt: e.target.value }))}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Online Meeting Link</label>
                    <input
                      type="url"
                      className="form-control"
                      placeholder="https://meet.google.com/..."
                      value={formData.onlineMeetingLink}
                      onChange={(e) => setFormData((prev) => ({ ...prev, onlineMeetingLink: e.target.value }))}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Notes (Optional)</label>
                    <textarea
                      className="form-control"
                      rows="3"
                      placeholder="Interview agenda, instructions, etc."
                      value={formData.interviewNotes}
                      onChange={(e) => setFormData((prev) => ({ ...prev, interviewNotes: e.target.value }))}
                    />
                  </div>
                  <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <button
                      type="button"
                      className="btn btn-primary btn-sm"
                      onClick={() => submitInterviewSchedule(app.id)}
                      disabled={submittingId === app.id}
                    >
                      {submittingId === app.id ? 'Saving...' : 'Save Interview Schedule'}
                    </button>
                    <button
                      type="button"
                      className="btn btn-secondary btn-sm"
                      onClick={() => setActiveFormId(null)}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
