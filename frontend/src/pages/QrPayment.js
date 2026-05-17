import React, { useState } from 'react';
import { qrApi } from '../services/api';
import { QrCode, Download } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function QrPayment() {
  const { user } = useAuth();
  const [form, setForm] = useState({ upiId: user?.upiId || '', name: user?.fullName || user?.username || '', amount: '', note: '' });
  const [qrImage, setQrImage] = useState(null);
  const [upiUri, setUpiUri] = useState('');
  const [loading, setLoading] = useState(false);

  const generate = async (e) => {
    e.preventDefault();
    if (!form.upiId) { toast.error('UPI ID is required'); return; }
    setLoading(true);
    try {
      const r = await qrApi.generateUpiQr(form.upiId, form.name, form.amount || 0, form.note);
      setQrImage(r.data.qrImage);
      setUpiUri(r.data.upiUri);
      toast.success('QR Code generated!');
    } catch { toast.error('QR generation failed. Make sure backend is running.'); }
    finally { setLoading(false); }
  };

  const downloadQr = () => {
    const link = document.createElement('a');
    link.download = `upi-qr-${form.upiId}.png`;
    link.href = qrImage;
    link.click();
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">UPI QR Generator</h1>
          <p className="page-subtitle">Generate payment QR codes for instant UPI collection</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24, alignItems: 'start' }}>
        {/* Form */}
        <div className="card">
          <h3 style={{ marginBottom: 20, fontSize: 16 }}>⚙️ Configure QR Code</h3>
          <form onSubmit={generate}>
            <div className="form-group">
              <label className="form-label">UPI ID *</label>
              <input className="form-input" value={form.upiId} onChange={e => setForm({...form, upiId: e.target.value})}
                placeholder="yourname@upi or 9876543210@paytm" required />
            </div>
            <div className="form-group">
              <label className="form-label">Payee Name *</label>
              <input className="form-input" value={form.name} onChange={e => setForm({...form, name: e.target.value})}
                placeholder="Full name" required />
            </div>
            <div className="form-group">
              <label className="form-label">Amount (₹) — Optional</label>
              <input className="form-input" type="number" step="0.01" value={form.amount}
                onChange={e => setForm({...form, amount: e.target.value})} placeholder="Leave blank for open amount" />
            </div>
            <div className="form-group">
              <label className="form-label">Payment Note</label>
              <input className="form-input" value={form.note} onChange={e => setForm({...form, note: e.target.value})}
                placeholder="Dinner split, Rent, etc." />
            </div>
            <button className="btn btn-primary btn-full" type="submit" disabled={loading}>
              <QrCode size={16} /> {loading ? 'Generating...' : 'Generate QR Code'}
            </button>
          </form>

          {upiUri && (
            <div style={{ marginTop: 20, padding: 14, background: 'rgba(6,182,212,0.08)', borderRadius: 10, border: '1px solid rgba(6,182,212,0.2)' }}>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>UPI Deep Link</div>
              <div style={{ fontSize: 11, wordBreak: 'break-all', color: 'var(--accent)' }}>{upiUri}</div>
            </div>
          )}
        </div>

        {/* QR Display */}
        <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 20, minHeight: 400 }}>
          <h3 style={{ fontSize: 16, alignSelf: 'flex-start' }}>📱 QR Code Preview</h3>

          {qrImage ? (
            <>
              <img src={qrImage} alt="UPI QR" className="qr-img" />
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontWeight: 700, fontSize: 16 }}>{form.name}</div>
                <div style={{ color: 'var(--accent)', fontSize: 13 }}>{form.upiId}</div>
                {form.amount && <div style={{ color: 'var(--success)', fontWeight: 700, fontSize: 18, marginTop: 6 }}>₹{Number(form.amount).toFixed(2)}</div>}
                {form.note && <div style={{ color: 'var(--text-muted)', fontSize: 12, marginTop: 4 }}>{form.note}</div>}
              </div>
              <button className="btn btn-secondary" onClick={downloadQr}>
                <Download size={16} /> Download QR
              </button>
              <p style={{ fontSize: 11, color: 'var(--text-muted)', textAlign: 'center' }}>
                Works with PhonePe · Google Pay · Paytm · BHIM · Any UPI App
              </p>
            </>
          ) : (
            <div className="qr-placeholder" style={{ flex: 1 }}>
              <QrCode size={48} color="var(--text-muted)" />
              <p>Your QR code will appear here</p>
              <p style={{ fontSize: 12 }}>Fill the form and click Generate</p>
            </div>
          )}
        </div>
      </div>

      {/* Info cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16, marginTop: 24 }}>
        {[
          { icon: '⚡', title: 'Instant Payment', desc: 'Share QR with anyone for instant UPI payment collection' },
          { icon: '🔒', title: 'Secure', desc: 'Uses official UPI deep-link format. No payment data stored' },
          { icon: '📲', title: 'All UPI Apps', desc: 'Works with PhonePe, Google Pay, Paytm, BHIM and all UPI apps' },
        ].map((item, i) => (
          <div key={i} className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 32, marginBottom: 8 }}>{item.icon}</div>
            <h4 style={{ marginBottom: 6, fontSize: 14 }}>{item.title}</h4>
            <p style={{ fontSize: 12, color: 'var(--text-muted)', lineHeight: 1.6 }}>{item.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
