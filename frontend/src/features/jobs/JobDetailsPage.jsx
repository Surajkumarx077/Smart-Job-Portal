import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { jobsApi } from '../../api/jobs.api';
import { applicationsApi } from '../../api/applications.api';
import { resumesApi } from '../../api/resumes.api';
import { useAuth } from '../auth/AuthContext';
import './Jobs.css';
import { MapPin, Building, Clock, ArrowLeft, UploadCloud, FileText } from 'lucide-react';

export default function JobDetailsPage() {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [applying, setApplying] = useState(false);
  const [showApplyMenu, setShowApplyMenu] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [resumeUrl, setResumeUrl] = useState('');
  const [resumeFile, setResumeFile] = useState(null);
  const [resumeLoading, setResumeLoading] = useState(false);
  const [applyError, setApplyError] = useState(null);
  const { user, isAuthenticated } = useAuth();

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const data = await jobsApi.getJobDetails(id);
        setJob(data);
      } catch {
        setError('Failed to load job details. It might have been removed.');
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  useEffect(() => {
    if (isAuthenticated && user?.role === 'STUDENT') {
      resumesApi.getResume()
        .then((data) => setResumeUrl(data?.resumeUrl || ''))
        .catch(() => setResumeUrl(''));
    }
  }, [isAuthenticated, user?.role]);

  const uploadResume = async () => {
    if (!resumeFile) {
      setApplyError('Select a PDF resume before applying.');
      return false;
    }
    setResumeLoading(true);
    setApplyError(null);
    try {
      const result = await resumesApi.uploadResume(resumeFile);
      setResumeUrl(result?.resumeUrl || 'uploaded');
      setResumeFile(null);
      return true;
    } catch (err) {
      setApplyError(err.response?.data?.error || 'Resume upload failed. Please select a valid PDF.');
      return false;
    } finally {
      setResumeLoading(false);
    }
  };

  const handleApply = async () => {
    setApplying(true);
    setApplyError(null);
    try {
      if (!resumeUrl && !(await uploadResume())) {
        return;
      }
      await applicationsApi.applyForJob({ jobId: id, coverLetter });
      alert('Application successful!');
      setShowApplyMenu(false);
    } catch (err) {
      setApplyError(err.response?.data?.error || 'Failed to submit your application.');
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
                      {!resumeUrl && (
                        <div className="resume-apply-upload">
                          <label style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)', display: 'block', marginBottom: '0.5rem' }}>
                            Resume PDF (required)
                          </label>
                          <div className="file-drop-area" style={{ padding: '1rem', marginBottom: '0.75rem' }}>
                            <UploadCloud size={24} className="upload-icon" />
                            <p style={{ margin: 0 }}>{resumeFile ? resumeFile.name : 'Choose your resume PDF'}</p>
                            <input
                              type="file"
                              accept="application/pdf,.pdf"
                              className="file-input"
                              onChange={(e) => setResumeFile(e.target.files?.[0] || null)}
                            />
                          </div>
                          <p className="text-muted" style={{ fontSize: '0.8rem', marginBottom: '1rem' }}>
                            Uploading here also updates your profile resume and extracts screening insights.
                          </p>
                        </div>
                      )}
                      {resumeUrl && <p className="text-success" style={{ fontSize: '0.85rem' }}><FileText size={15} /> Resume ready</p>}
                      <label style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)', display: 'block', marginBottom: '0.5rem' }}>Cover Letter (Optional)</label>
                      <textarea className="form-control" rows="3" value={coverLetter} onChange={e => setCoverLetter(e.target.value)} style={{ marginBottom: '1rem' }}></textarea>
                      {applyError && <div className="page-error" style={{ marginBottom: '1rem' }}>{applyError}</div>}
                      <button className="btn btn-primary btn-sm" onClick={handleApply} disabled={applying || resumeLoading}>{applying || resumeLoading ? 'Processing...' : 'Submit Application'}</button>
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
