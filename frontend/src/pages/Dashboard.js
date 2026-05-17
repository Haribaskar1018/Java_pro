import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { expenseApi } from '../services/api';
import { TrendingUp, TrendingDown, DollarSign, CheckCircle } from 'lucide-react';

const CATEGORY_ICONS = {
  food: '🍔', travel: '✈️', utilities: '💡', entertainment: '🎬',
  shopping: '🛒', health: '💊', other: '📝'
};

export default function Dashboard() {
  const { user } = useAuth();
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    expenseApi.getSettlementSummary()
      .then(r => setSummary(r.data))
      .catch(() => setSummary({ totalOwed: 0, totalOwing: 0, netBalance: 0, owes: {}, isOwed: {} }))
      .finally(() => setLoading(false));
  }, []);

  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : user?.username?.slice(0, 2).toUpperCase();

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Hey, {user?.fullName?.split(' ')[0] || user?.username} 👋</h1>
          <p className="page-subtitle">Here's your financial snapshot</p>
        </div>
        <div className="avatar" style={{ width: 48, height: 48, fontSize: 18 }}>{initials}</div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-muted)' }}>Loading summary...</div>
      ) : (
        <>
          <div className="stat-grid">
            <div className="stat-card" style={{ '--accent-color': 'linear-gradient(90deg,#6366f1,#818cf8)' }}>
              <TrendingUp size={20} color="var(--primary)" style={{ marginBottom: 8 }} />
              <div className="stat-label">You are owed</div>
              <div className="stat-value" style={{ color: 'var(--success)' }}>
                ₹{Number(summary?.totalOwed || 0).toFixed(2)}
              </div>
              <div className="stat-sub">From {Object.keys(summary?.isOwed || {}).length} people</div>
            </div>

            <div className="stat-card" style={{ '--accent-color': 'linear-gradient(90deg,#ef4444,#f87171)' }}>
              <TrendingDown size={20} color="var(--danger)" style={{ marginBottom: 8 }} />
              <div className="stat-label">You owe</div>
              <div className="stat-value" style={{ color: 'var(--danger)' }}>
                ₹{Number(summary?.totalOwing || 0).toFixed(2)}
              </div>
              <div className="stat-sub">To {Object.keys(summary?.owes || {}).length} people</div>
            </div>

            <div className="stat-card" style={{ '--accent-color': 'linear-gradient(90deg,#06b6d4,#22d3ee)' }}>
              <DollarSign size={20} color="var(--accent)" style={{ marginBottom: 8 }} />
              <div className="stat-label">Net Balance</div>
              <div className="stat-value" style={{ color: Number(summary?.netBalance) >= 0 ? 'var(--success)' : 'var(--danger)' }}>
                ₹{Number(summary?.netBalance || 0).toFixed(2)}
              </div>
              <div className="stat-sub">{Number(summary?.netBalance) >= 0 ? 'You\'re in the green' : 'You need to pay up'}</div>
            </div>

            <div className="stat-card" style={{ '--accent-color': 'linear-gradient(90deg,#10b981,#34d399)' }}>
              <CheckCircle size={20} color="var(--success)" style={{ marginBottom: 8 }} />
              <div className="stat-label">UPI ID</div>
              <div className="stat-value" style={{ fontSize: 16, marginTop: 6 }}>{user?.upiId || 'Not set'}</div>
              <div className="stat-sub">Your payment handle</div>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 20 }}>
            {/* People who owe you */}
            <div className="card">
              <h3 style={{ marginBottom: 16, color: 'var(--success)', fontSize: 15 }}>
                💰 People who owe you
              </h3>
              {Object.keys(summary?.isOwed || {}).length === 0 ? (
                <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>All settled up! 🎉</p>
              ) : (
                Object.entries(summary.isOwed).map(([username, amount]) => (
                  <div key={username} className="expense-item">
                    <div className="avatar" style={{ width: 34, height: 34, fontSize: 12 }}>
                      {username.slice(0,2).toUpperCase()}
                    </div>
                    <div className="expense-info">
                      <div className="expense-title">{username}</div>
                    </div>
                    <div className="expense-amount" style={{ color: 'var(--success)' }}>
                      +₹{Number(amount).toFixed(2)}
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* You owe */}
            <div className="card">
              <h3 style={{ marginBottom: 16, color: 'var(--danger)', fontSize: 15 }}>
                🧾 You owe
              </h3>
              {Object.keys(summary?.owes || {}).length === 0 ? (
                <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>You're all clear! 🎉</p>
              ) : (
                Object.entries(summary.owes).map(([username, amount]) => (
                  <div key={username} className="expense-item">
                    <div className="avatar" style={{ width: 34, height: 34, fontSize: 12 }}>
                      {username.slice(0,2).toUpperCase()}
                    </div>
                    <div className="expense-info">
                      <div className="expense-title">{username}</div>
                    </div>
                    <div className="expense-amount" style={{ color: 'var(--danger)' }}>
                      -₹{Number(amount).toFixed(2)}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
