import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { authApi } from '../../api/auth.api';
import './Auth.css';
import { Mail, Lock, AlertCircle, User, Briefcase } from 'lucide-react';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    role: 'STUDENT'
  });
  const [errorObj, setErrorObj] = useState(null);
  const [globalError, setGlobalError] = useState(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleObjChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorObj(null);
    setGlobalError(null);
    setLoading(true);
    try {
      const response = await authApi.register(formData);
      login(response); // log them in automatically
      navigate('/');
    } catch (err) {
      if (err.response && err.response.data) {
        if (err.response.data.error) {
           setGlobalError(err.response.data.error);
        } else {
           setErrorObj(err.response.data); // Validation errors map
        }
      } else {
        setGlobalError('Network error or server is unreachable.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card glass-panel flex-row">
        <h2 className="auth-title text-gradient">Create Account</h2>
        <p className="auth-subtitle">Join SmartJob Portal today</p>
        
        {globalError && (
          <div className="auth-error">
            <AlertCircle size={18} />
            <span>{globalError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <div className="input-with-icon">
              <User className="input-icon" size={18} />
              <input 
                type="text" 
                name="fullName"
                className="form-control" 
                required 
                value={formData.fullName}
                onChange={handleObjChange}
                placeholder="John Doe"
              />
            </div>
            {errorObj?.fullName && <span className="field-error">{errorObj.fullName}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div className="input-with-icon">
              <Mail className="input-icon" size={18} />
              <input 
                type="email" 
                name="email"
                className="form-control" 
                required 
                value={formData.email}
                onChange={handleObjChange}
                placeholder="you@example.com"
              />
            </div>
            {errorObj?.email && <span className="field-error">{errorObj.email}</span>}
          </div>
          
          <div className="form-group">
            <label className="form-label">Password</label>
            <div className="input-with-icon">
              <Lock className="input-icon" size={18} />
              <input 
                type="password" 
                name="password"
                className="form-control" 
                required 
                value={formData.password}
                onChange={handleObjChange}
                placeholder="••••••••"
              />
            </div>
            {errorObj?.password && <span className="field-error">{errorObj.password}</span>}
          </div>

          <div className="form-group">
            <label className="form-label">Account Role</label>
            <div className="input-with-icon">
              <Briefcase className="input-icon" size={18} />
              <select 
                className="form-control form-select" 
                name="role" 
                value={formData.role} 
                onChange={handleObjChange}
              >
                <option value="STUDENT">Student / Job Seeker</option>
                <option value="RECRUITER">Recruiter / Employer</option>
              </select>
            </div>
            {errorObj?.role && <span className="field-error">{errorObj.role}</span>}
          </div>

          <button type="submit" className="btn btn-primary auth-submit" disabled={loading}>
            {loading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>

        <p className="auth-switch">
          Already have an account? <Link to="/login">Sign in here</Link>
        </p>
      </div>
    </div>
  );
}
