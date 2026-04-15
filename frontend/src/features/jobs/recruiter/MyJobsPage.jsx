import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { jobsApi } from '../../../api/jobs.api';
import { Briefcase, Edit, Users, Star, Plus } from 'lucide-react';

export default function MyJobsPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    try {
      const data = await jobsApi.getMyJobs();
      setJobs(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="page-loader">Loading your jobs...</div>;

  return (
    <div className="page-container">
      <div className="jobs-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
         <div>
             <h1 className="text-gradient">My Postings</h1>
             <p className="text-muted">Manage the jobs you have created.</p>
         </div>
         <Link to="/recruiter/jobs/new" className="btn btn-primary"><Plus size={18}/> Create New Job</Link>
      </div>

      {jobs.length === 0 ? (
        <div className="empty-state glass-panel">You haven't posted any jobs yet.</div>
      ) : (
        <div className="jobs-grid">
           {jobs.map(job => (
             <div key={job.id} className="job-card glass-panel">
               <h3 className="job-title">{job.title}</h3>
               <p className="text-muted" style={{ marginBottom: '1rem' }}>{job.location} • {job.jobType.replace('_', ' ')}</p>
               <div className="job-actions" style={{ justifyContent: 'flex-start', gap: '0.5rem', flexWrap: 'wrap' }}>
                  <Link to={`/recruiter/jobs/${job.id}/edit`} className="btn btn-secondary btn-sm" style={{ padding: '0.4rem 0.8rem' }}><Edit size={14}/> Edit</Link>
                  <Link to={`/recruiter/jobs/${job.id}/applications`} className="btn btn-secondary btn-sm" style={{ padding: '0.4rem 0.8rem' }}><Users size={14}/> Apps</Link>
                  <Link to={`/recruiter/jobs/${job.id}/ranking`} className="btn btn-secondary btn-sm" style={{ color: 'var(--color-primary)', borderColor: 'var(--color-primary)', padding: '0.4rem 0.8rem' }}><Star size={14}/> Rank</Link>
               </div>
             </div>
           ))}
        </div>
      )}
    </div>
  );
}
