import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { rankingApi } from '../../api/ranking.api';
import { ArrowLeft, User, FileText, CheckCircle, Percent } from 'lucide-react';

export default function RankedCandidatesPage() {
  const { id } = useParams();
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRanking = async () => {
      try {
        const data = await rankingApi.getRankedCandidates(id);
        setCandidates(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchRanking();
  }, [id]);

  if (loading) return <div className="page-loader">Analyzing and ranking candidates via AI...</div>;

  return (
    <div className="page-container">
      <Link to="/recruiter/jobs" className="btn btn-secondary btn-sm" style={{ marginBottom: '2rem' }}>
        <ArrowLeft size={16} /> Back to My Jobs
      </Link>
      <h1 className="text-gradient">AI Candidate Ranking</h1>
      <p className="text-muted" style={{ marginBottom: '2rem' }}>Top candidates matched to the job description based on resume extraction.</p>

      {candidates.length === 0 ? (
        <div className="empty-state glass-panel">No ranked candidates available. They may not have uploaded a resume.</div>
      ) : (
        <div className="applications-list">
          {candidates.map((cand, index) => (
            <div key={cand.applicationId} className="app-card glass-panel" style={{ borderLeft: `4px solid ${index === 0 ? 'var(--color-success)' : 'var(--color-primary)'}`, flexDirection: 'column', alignItems: 'flex-start', gap: '1rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                  <h3 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                     <span style={{ background: 'var(--color-primary)', color: 'white', padding: '0.2rem 0.6rem', borderRadius: '1rem', fontSize: '0.8rem', marginRight: '0.5rem' }}>#{index + 1}</span>
                     <User size={18}/> {cand.applicantName}
                  </h3>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(138, 43, 226, 0.1)', padding: '0.5rem 1rem', borderRadius: 'var(--border-radius-full)' }}>
                     <Percent size={18} className="text-primary"/>
                     <span style={{ fontWeight: 700, fontSize: '1.2rem', color: 'var(--color-primary)' }}>{cand.matchScore}</span>
                  </div>
              </div>
              
              <div style={{ padding: '1rem', background: 'rgba(16, 185, 129, 0.05)', borderRadius: 'var(--border-radius-md)', width: '100%' }}>
                  <h5 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-success)', marginBottom: '0.5rem' }}><CheckCircle size={16}/> Matched Skills</h5>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                     {cand.matchedSkills?.map((skill, i) => (
                         <span key={i} style={{ background: 'rgba(0,0,0,0.3)', padding: '0.3rem 0.8rem', borderRadius: 'var(--border-radius-sm)', fontSize: '0.85rem' }}>{skill}</span>
                     ))}
                  </div>
              </div>

              <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
                <a href={cand.resumeUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                  <FileText size={16}/> View Resume
                </a>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
