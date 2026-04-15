import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { jobsApi } from '../../api/jobs.api';
import './Jobs.css';
import { MapPin, Building, Clock } from 'lucide-react';

export default function PublicJobsPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchJobs = async () => {
      try {
        const data = await jobsApi.getPublicJobs();
        setJobs(data);
      } catch (err) {
        setError('Failed to load jobs. The server might be down.');
      } finally {
        setLoading(false);
      }
    };
    fetchJobs();
  }, []);

  if (loading) return <div className="page-loader">Loading jobs...</div>;
  if (error) return <div className="page-error">{error}</div>;

  return (
    <div className="jobs-container">
      <div className="jobs-header">
         <h1 className="text-gradient">Explore Opportunities</h1>
         <p style={{ color: 'var(--color-text-muted)' }}>Find your dream job among our top postings.</p>
      </div>
      
      {jobs.length === 0 ? (
        <div className="empty-state glass-panel">No jobs available right now.</div>
      ) : (
        <div className="jobs-grid">
          {jobs.map(job => (
            <div key={job.id} className="job-card glass-panel">
              <h3 className="job-title">{job.title}</h3>
              <div className="job-meta">
                <span className="meta-item"><Building size={16}/> {job.company}</span>
                <span className="meta-item"><MapPin size={16}/> {job.location}</span>
                <span className="meta-item"><Clock size={16}/> {job.jobType.replace('_', ' ')}</span>
              </div>
              <p className="job-desc-preview">{job.description?.substring(0, 100)}...</p>
              <div className="job-actions">
                <Link to={`/jobs/${job.id}`} className="btn btn-secondary btn-sm">View Details</Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
