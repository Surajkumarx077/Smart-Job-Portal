import React, { useEffect, useState } from 'react';
import { resumesApi } from '../../api/resumes.api';
import '../applications/StudentViews.css';
import { UploadCloud, CheckCircle, FileText, Code, GraduationCap, Briefcase } from 'lucide-react';

export default function ResumeUploadPage() {
  const [resumeUrl, setResumeUrl] = useState(null);
  const [parsedData, setParsedData] = useState(null);
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchResumeInfo();
  }, []);

  const fetchResumeInfo = async () => {
    try {
      setFetchLoading(true);
      const resData = await resumesApi.getResume();
      if (resData?.resumeUrl) {
        setResumeUrl(resData.resumeUrl);
        try {
           const parsed = await resumesApi.getParsedResume();
           setParsedData(parsed);
        } catch (e) {
           // Parsing might fail or return 404, ignore and just show the URL
        }
      }
    } catch (err) {
      if (err.response?.status !== 404 && err.response?.status !== 400 && err.response?.status !== 204) {
         setError('Failed to fetch resume status.');
      }
    } finally {
      setFetchLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;
    setLoading(true);
    setError(null);
    try {
      await resumesApi.uploadResume(file);
      await fetchResumeInfo();
      setFile(null);
    } catch (err) {
      setError('Failed to upload resume. Ensure it is a valid PDF.');
    } finally {
      setLoading(false);
    }
  };

  if (fetchLoading) return <div className="page-loader">Loading resume data...</div>;

  return (
    <div className="page-container">
      <h1 className="text-gradient">My Resume</h1>
      <p className="text-muted" style={{ marginBottom: '2rem' }}>Upload your latest PDF resume. Our system automatically parses it to match you with top jobs.</p>

      {error && <div className="page-error" style={{ marginBottom: '2rem' }}>{error}</div>}

      <div className="resume-grid">
        <div className="upload-card glass-panel">
          <h3>Upload New Resume</h3>
          <form onSubmit={handleUpload} className="upload-form">
            <div className="file-drop-area">
              <UploadCloud size={48} className="upload-icon" />
              <p>Drag & drop your PDF here or click to browse</p>
              <input 
                type="file" 
                accept=".pdf" 
                onChange={(e) => setFile(e.target.files[0])} 
                className="file-input"
              />
            </div>
            {file && <p className="file-selected">Selected: {file.name}</p>}
            <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={!file || loading}>
              {loading ? 'Uploading...' : 'Upload Resume'}
            </button>
          </form>
        </div>

        {resumeUrl && (
          <div className="current-resume-card glass-panel">
             <h3><CheckCircle size={20} className="text-success" style={{ verticalAlign: 'middle', marginRight: '0.5rem' }}/> Resume Active</h3>
             <a href={resumeUrl} target="_blank" rel="noreferrer" className="btn btn-secondary" style={{ marginTop: '0.5rem', display: 'inline-flex' }}>
               <FileText size={18} /> View Document
             </a>

             {parsedData && (
                <div className="parsed-data-section">
                  <h4 style={{ marginTop: '2.5rem', marginBottom: '1rem', color: 'var(--color-primary)' }}>AI Extracted Insights</h4>
                  
                  <div className="insight-item">
                    <h5><Code size={16}/> Skills</h5>
                    <p>{parsedData.skills || 'Not detected'}</p>
                  </div>
                  
                  <div className="insight-item">
                    <h5><Briefcase size={16}/> Experience</h5>
                    <p>{parsedData.experienceSummary || 'Not detected'}</p>
                  </div>

                  <div className="insight-item">
                    <h5><GraduationCap size={16}/> Education</h5>
                    <p>{parsedData.education || 'Not detected'}</p>
                  </div>
                </div>
             )}
          </div>
        )}
      </div>
    </div>
  );
}
