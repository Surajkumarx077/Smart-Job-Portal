import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { jobsApi } from '../../../api/jobs.api';
import { ArrowLeft } from 'lucide-react';

export default function JobFormPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    company: '',
    location: '',
    jobType: 'FULL_TIME'
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isEdit) {
      jobsApi.getJobDetails(id).then(data => {
        setFormData({
           title: data.title,
           description: data.description,
           company: data.company,
           location: data.location,
           jobType: data.jobType
        });
      });
    }
  }, [id, isEdit]);

  const handleChange = (e) => setFormData({...formData, [e.target.name]: e.target.value});

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (isEdit) {
        await jobsApi.updateJob(id, formData);
      } else {
        await jobsApi.createJob(formData);
      }
      navigate('/recruiter/jobs');
    } catch (err) {
      alert('Error saving job. Please check inputs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-container" style={{ maxWidth: '600px' }}>
      <Link to="/recruiter/jobs" className="btn btn-secondary btn-sm" style={{ marginBottom: '2rem' }}>
        <ArrowLeft size={16} /> Back to My Jobs
      </Link>
      <h1 className="text-gradient" style={{ marginBottom: '2rem' }}>{isEdit ? 'Edit Job' : 'Create Job'}</h1>
      <form onSubmit={handleSubmit} className="glass-panel" style={{ padding: '2rem' }}>
         <div className="form-group">
            <label className="form-label">Job Title</label>
            <input type="text" name="title" className="form-control" value={formData.title} onChange={handleChange} required />
         </div>
         <div className="form-group">
            <label className="form-label">Company Name</label>
            <input type="text" name="company" className="form-control" value={formData.company} onChange={handleChange} required />
         </div>
         <div className="form-group">
            <label className="form-label">Location</label>
            <input type="text" name="location" className="form-control" value={formData.location} onChange={handleChange} required />
         </div>
         <div className="form-group">
            <label className="form-label">Job Type</label>
            <select name="jobType" className="form-control form-select" value={formData.jobType} onChange={handleChange}>
               <option value="FULL_TIME">Full Time</option>
               <option value="PART_TIME">Part Time</option>
               <option value="CONTRACT">Contract</option>
               <option value="INTERNSHIP">Internship</option>
            </select>
         </div>
         <div className="form-group">
            <label className="form-label">Description</label>
            <textarea name="description" className="form-control" value={formData.description} onChange={handleChange} required rows="5" />
         </div>
         <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
            {loading ? 'Saving...' : 'Save Job'}
         </button>
      </form>
    </div>
  );
}
