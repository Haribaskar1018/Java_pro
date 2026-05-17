import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LayoutDashboard, Users, QrCode, LogOut } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success('Logged out!');
    navigate('/login');
  };

  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : user?.username?.slice(0, 2).toUpperCase();

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-icon">💸</div>
          <h2>Split<span>Wise</span> Pro</h2>
        </div>

        <nav>
          <NavLink to="/" end className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /> Dashboard
          </NavLink>
          <NavLink to="/groups" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Users size={18} /> Groups
          </NavLink>
          <NavLink to="/qr" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <QrCode size={18} /> UPI QR Pay
          </NavLink>
        </nav>

        <div className="sidebar-bottom">
          <div className="user-badge">
            <div className="avatar">{initials}</div>
            <div>
              <div style={{ fontSize: 13, fontWeight: 600 }}>{user?.fullName || user?.username}</div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{user?.upiId || 'No UPI set'}</div>
            </div>
          </div>
          <button className="btn btn-secondary btn-full" onClick={handleLogout}>
            <LogOut size={16} /> Logout
          </button>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
