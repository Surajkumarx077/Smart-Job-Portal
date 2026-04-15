import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../features/auth/AuthContext';
import { Briefcase, LogOut, User } from 'lucide-react';
import './TopNav.css';

export default function TopNav() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="top-nav glass-panel">
      <div className="nav-container">
        <Link to="/" className="nav-brand">
          <Briefcase className="brand-icon" />
          <span className="brand-text text-gradient">SmartJob</span>
        </Link>
        <div className="nav-links">
          <Link to="/" className="nav-link">Jobs</Link>
          
          {isAuthenticated && user?.role === 'STUDENT' && (
            <>
              <Link to="/student/resume" className="nav-link">My Resume</Link>
              <Link to="/student/applications" className="nav-link">Applications</Link>
            </>
          )}

          {isAuthenticated && user?.role === 'RECRUITER' && (
            <>
              <Link to="/recruiter/jobs" className="nav-link">My Postings</Link>
              <Link to="/recruiter/jobs/new" className="nav-link">Create Job</Link>
            </>
          )}

          {isAuthenticated && user?.role === 'ADMIN' && (
            <>
              <Link to="/admin/dashboard" className="nav-link">Dashboard</Link>
              <Link to="/admin/users" className="nav-link">Users</Link>
            </>
          )}
        </div>
        <div className="nav-actions">
          {isAuthenticated ? (
            <div className="user-menu">
              <span className="user-name"><User size={18}/> {user?.fullName}</span>
              <button onClick={handleLogout} className="btn btn-secondary btn-sm">
                <LogOut size={16} /> Logout
              </button>
            </div>
          ) : (
            <>
              <Link to="/login" className="btn btn-secondary">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
