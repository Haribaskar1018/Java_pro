import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '', fullName: '', upiId: '', phoneNumber: '' });
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await register(form);
      toast.success('Account created!');
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <div className="auth-logo-icon">💸</div>
          <h1>Split<span>Wise</span> Pro</h1>
        </div>
        <h2 className="auth-title">Create account</h2>
        <p className="auth-sub">Start splitting bills the smart way</p>

        <form onSubmit={submit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input className="form-input" name="fullName" value={form.fullName} onChange={handle} placeholder="Hari Kumar" />
            </div>
            <div className="form-group">
              <label className="form-label">Username *</label>
              <input className="form-input" name="username" value={form.username} onChange={handle} placeholder="hari123" required />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Email *</label>
            <input className="form-input" type="email" name="email" value={form.email} onChange={handle} placeholder="hari@email.com" required />
          </div>
          <div className="form-group">
            <label className="form-label">Password *</label>
            <input className="form-input" type="password" name="password" value={form.password} onChange={handle} placeholder="Min 6 characters" required />
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div className="form-group">
              <label className="form-label">UPI ID</label>
              <input className="form-input" name="upiId" value={form.upiId} onChange={handle} placeholder="hari@upi" />
            </div>
            <div className="form-group">
              <label className="form-label">Phone</label>
              <input className="form-input" name="phoneNumber" value={form.phoneNumber} onChange={handle} placeholder="9876543210" />
            </div>
          </div>
          <button className="btn btn-primary btn-full" type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create Account'}
          </button>
        </form>
        <p className="auth-link">Already have an account? <Link to="/login">Sign in →</Link></p>
      </div>
    </div>
  );
}
