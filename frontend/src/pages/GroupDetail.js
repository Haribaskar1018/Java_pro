import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { groupApi, expenseApi, qrApi } from '../services/api';
import { Plus, QrCode, X, CheckCircle } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

const CATEGORIES = ['food', 'travel', 'utilities', 'entertainment', 'shopping', 'health', 'other'];
const CATEGORY_ICONS = { food:'🍔', travel:'✈️', utilities:'💡', entertainment:'🎬', shopping:'🛒', health:'💊', other:'📝' };

export default function GroupDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [group, setGroup] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showExpModal, setShowExpModal] = useState(false);
  const [showQrModal, setShowQrModal] = useState(false);
  const [qrData, setQrData] = useState(null);
  const [form, setForm] = useState({ title: '', description: '', totalAmount: '', splitType: 'EQUAL', category: 'food', memberIds: [], customShares: {} });

  useEffect(() => {
    Promise.all([groupApi.getById(id), expenseApi.getByGroup(id)])
      .then(([g, e]) => { setGroup(g.data); setExpenses(e.data); })
      .catch(() => toast.error('Failed to load group'))
      .finally(() => setLoading(false));
  }, [id]);

  const createExpense = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        totalAmount: parseFloat(form.totalAmount),
        groupId: parseInt(id),
        memberIds: form.memberIds.length > 0 ? form.memberIds : (group?.members || []).map(m => m.id),
      };
      const r = await expenseApi.create(payload);
      setExpenses(prev => [r.data, ...prev]);
      setShowExpModal(false);
      setForm({ title: '', description: '', totalAmount: '', splitType: 'EQUAL', category: 'food', memberIds: [], customShares: {} });
      toast.success('Expense added & split!');
    } catch { toast.error('Failed to add expense'); }
  };

  const generateQr = async (member) => {
    if (!member.upiId) { toast.error('This member has no UPI ID set'); return; }
    try {
      const r = await qrApi.generateUpiQr(member.upiId, member.fullName || member.username, 0, 'SplitWise Pro payment');
      setQrData({ ...r.data, member });
      setShowQrModal(true);
    } catch { toast.error('QR generation failed'); }
  };

  const settleShare = async (shareId) => {
    try {
      await expenseApi.settleShare(shareId);
      toast.success('Marked as settled!');
      const r = await expenseApi.getByGroup(id);
      setExpenses(r.data);
    } catch { toast.error('Failed to settle'); }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: 80, color: 'var(--text-muted)' }}>Loading group...</div>;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">{group?.name}</h1>
          <p className="page-subtitle">{group?.description || 'No description'} · {group?.members?.length} members</p>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button className="btn btn-primary" onClick={() => setShowExpModal(true)}>
            <Plus size={16} /> Add Expense
          </button>
        </div>
      </div>

      {/* Members */}
      <div className="card" style={{ marginBottom: 24 }}>
        <h3 style={{ marginBottom: 16, fontSize: 15 }}>👥 Members</h3>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10 }}>
          {(group?.members || []).map(m => (
            <div key={m.id} style={{ display: 'flex', alignItems: 'center', gap: 8, background: 'rgba(99,102,241,0.08)', borderRadius: 12, padding: '8px 14px' }}>
              <div className="avatar" style={{ width: 30, height: 30, fontSize: 11 }}>{(m.username || '?').slice(0,2).toUpperCase()}</div>
              <div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>{m.fullName || m.username}</div>
                {m.upiId && <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{m.upiId}</div>}
              </div>
              {m.upiId && (
                <button className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: 11 }} onClick={() => generateQr(m)}>
                  <QrCode size={12} /> QR
                </button>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Expenses */}
      <div className="card">
        <h3 style={{ marginBottom: 16, fontSize: 15 }}>🧾 Expenses ({expenses.length})</h3>
        {expenses.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
            <p>No expenses yet. Add your first one!</p>
          </div>
        ) : (
          expenses.map(exp => (
            <div key={exp.id} style={{ marginBottom: 16, paddingBottom: 16, borderBottom: '1px solid var(--border)' }}>
              <div className="expense-item" style={{ borderBottom: 'none', paddingBottom: 8 }}>
                <div className="expense-icon" style={{ background: 'rgba(99,102,241,0.15)' }}>
                  {CATEGORY_ICONS[exp.category] || '📝'}
                </div>
                <div className="expense-info">
                  <div className="expense-title">{exp.title}</div>
                  <div className="expense-meta">
                    Paid by <strong style={{ color: 'var(--primary-light)' }}>{exp.paidBy?.username}</strong>
                    · {exp.splitType} split
                  </div>
                </div>
                <div>
                  <div className="expense-amount" style={{ fontSize: 18 }}>₹{Number(exp.totalAmount).toFixed(2)}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)', textAlign: 'right' }}>{exp.category}</div>
                </div>
              </div>
              {/* Shares breakdown */}
              {(exp.shares || []).length > 0 && (
                <div style={{ paddingLeft: 56, display: 'flex', flexDirection: 'column', gap: 6 }}>
                  {exp.shares.map(s => (
                    <div key={s.id} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <span style={{ fontSize: 12, color: 'var(--text-muted)', flex: 1 }}>{s.user?.username}</span>
                      <span style={{ fontSize: 13, fontWeight: 600 }}>₹{Number(s.shareAmount).toFixed(2)}</span>
                      {s.settled ? (
                        <span className="badge badge-success">Settled</span>
                      ) : (
                        <button className="btn btn-success" style={{ padding: '3px 8px', fontSize: 11 }} onClick={() => settleShare(s.id)}>
                          <CheckCircle size={11} /> Settle
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))
        )}
      </div>

      {/* Add Expense Modal */}
      {showExpModal && (
        <div className="modal-overlay" onClick={() => setShowExpModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Add Expense</h3>
              <button className="close-btn" onClick={() => setShowExpModal(false)}><X size={20} /></button>
            </div>
            <form onSubmit={createExpense}>
              <div className="form-group">
                <label className="form-label">Title *</label>
                <input className="form-input" value={form.title} onChange={e => setForm({...form, title: e.target.value})} placeholder="Dinner at Saravana Bhavan" required />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label">Amount (₹) *</label>
                  <input className="form-input" type="number" step="0.01" value={form.totalAmount} onChange={e => setForm({...form, totalAmount: e.target.value})} placeholder="0.00" required />
                </div>
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select className="form-input" value={form.category} onChange={e => setForm({...form, category: e.target.value})}>
                    {CATEGORIES.map(c => <option key={c} value={c}>{CATEGORY_ICONS[c]} {c}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Split Type</label>
                <div style={{ display: 'flex', gap: 8 }}>
                  {['EQUAL', 'PERCENTAGE', 'EXACT'].map(t => (
                    <button key={t} type="button"
                      className={`tab ${form.splitType === t ? 'active' : ''}`}
                      onClick={() => setForm({...form, splitType: t})}>
                      {t}
                    </button>
                  ))}
                </div>
              </div>
              {form.splitType !== 'EQUAL' && (
                <div className="form-group">
                  <label className="form-label">Custom amounts per member ({form.splitType === 'PERCENTAGE' ? '%' : '₹'})</label>
                  {(group?.members || []).map(m => (
                    <div key={m.id} style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
                      <span style={{ fontSize: 13, flex: 1 }}>{m.username}</span>
                      <input className="form-input" type="number" step="0.01" style={{ width: 120 }}
                        onChange={e => setForm(f => ({ ...f, customShares: { ...f.customShares, [m.id]: parseFloat(e.target.value) } }))}
                        placeholder="0" />
                    </div>
                  ))}
                </div>
              )}
              <button className="btn btn-primary btn-full" type="submit">Add & Split Expense</button>
            </form>
          </div>
        </div>
      )}

      {/* QR Modal */}
      {showQrModal && qrData && (
        <div className="modal-overlay" onClick={() => setShowQrModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()} style={{ textAlign: 'center' }}>
            <div className="modal-header">
              <h3 className="modal-title">Pay via UPI QR</h3>
              <button className="close-btn" onClick={() => setShowQrModal(false)}><X size={20} /></button>
            </div>
            <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>
              Paying <strong style={{ color: 'var(--text)' }}>{qrData.member?.fullName || qrData.member?.username}</strong>
              <br /><span style={{ color: 'var(--accent)', fontSize: 13 }}>{qrData.upiId}</span>
            </p>
            <div className="qr-container">
              <img src={qrData.qrImage} alt="UPI QR Code" className="qr-img" />
            </div>
            <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 16 }}>
              Scan with any UPI app · PhonePe, GPay, Paytm
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
