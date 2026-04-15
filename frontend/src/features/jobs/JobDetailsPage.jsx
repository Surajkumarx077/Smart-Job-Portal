import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { jobsApi } from '../../api/jobs.api';
import { applicationsApi } from '../../api/applications.api';
import { useAuth } from '../auth/AuthContext';
import './Jobs.css';
import { MapPin, Building, Clock, ArrowLeft } from 'lucide-react';

export default function JobDetailsPage() {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [applying, setApplying] = useState(false);
  const [showApplyMenu, setShowApplyMenu] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const data = await jobsApi.getJobDetails(id);
        setJob(data);
      } catch (err) {
        setError('Failed to load job details. It might have been removed.');
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  const handleApply = async () => {
    setApplying(true);
    try {
      await applicationsApi.applyForJob({ jobId: id, coverLetter });
      alert('Application successful!');
      setShowApplyMenu(false);
    } catch (err) {
      alert('Failed to apply. You might have already applied.');
    } finally {
      setApplying(false);
    }
  };

  if (loading) return <div className="page-loader">Loading details...</div>;
  if (error || !job) return <div className="page-error">{error || 'Job not found'}</div>;

  return (
    <div className="jobs-container">
      <Link to="/" className="btn btn-secondary btn-sm" style={{ marginBottom: '2rem' }}>
        <ArrowLeft size={16} /> Back to Jobs
      </Link>
      
      <div className="job-detail-card glass-panel">
        <div className="job-detail-header">
          <div>
            <h1 className="text-gradient" style={{ marginBottom: '1rem' }}>{job.title}</h1>
            <div className="job-meta">
              <span className="meta-item"><Building size={18}/> {job.company}</span>
              <span className="meta-item"><MapPin size={18}/> {job.location}</span>
              <span className="meta-item"><Clock size={18}/> {job.jobType.replace('_', ' ')}</span>
            </div>
            <p className="text-muted" style={{ marginTop: '0.5rem', fontSize: '0.9rem' }}>
              Posted by {job.recruiterName} on {new Date(job.createdAt).toLocaleDateString()}
            </p>
          </div>
          
          <div>
            {isAuthenticated && user?.role === 'STUDENT' ? (
                showApplyMenu ? (
                   <div style={{ background: 'rgba(0,0,0,0.3)', padding: '1rem', borderRadius: 'var(--border-radius-md)' }}>
                      <label style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)', display: 'block', marginBottom: '0.5rem' }}>Cover Letter (Optional)</label>
                      <textarea className="form-control" rows="3" value={coverLetter} onChange={e => setCoverLetter(e.target.value)} style={{ marginBottom: '1rem' }}></textarea>
                      <button className="btn btn-primary btn-sm" onClick={handleApply} disabled={applying}>{applying ? 'Applying...' : 'Submit Application'}</button>
                      <button className="btn btn-secondary btn-sm" onClick={() => setShowApplyMenu(false)} style={{ marginLeft: '0.5rem' }}>Cancel</button>
                   </div>
                ) : (
                   <button className="btn btn-primary" onClick={() => setShowApplyMenu(true)}>Apply Now</button>
                )
            ) : isAuthenticated ? (
                 <span className="text-muted" style={{ fontSize: '0.85rem' }}>Log in as Student to apply</span>
            ) : (
                <Link to="/login" className="btn btn-primary">Login to Apply</Link>
            )}
          </div>
        </div>

        <div className="job-detail-content">
          <h3 style={{ marginBottom: '1rem' }}>Job Description</h3>
          {job.description}
        </div>
      </div>
    </div>
  );
}
