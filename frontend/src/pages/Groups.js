import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { groupApi } from '../services/api';
import { Plus, Users, X, Search } from 'lucide-react';
import toast from 'react-hot-toast';

export default function Groups() {
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', memberIds: [] });
  const [userSearch, setUserSearch] = useState('');
  const [searchResults, setSearchResults] = useState([]);

  useEffect(() => {
    groupApi.getAll()
      .then(r => setGroups(r.data))
      .catch(() => toast.error('Failed to load groups'))
      .finally(() => setLoading(false));
  }, []);

  const searchUsers = async (q) => {
    setUserSearch(q);
    if (q.length < 2) { setSearchResults([]); return; }
    try {
      const r = await groupApi.searchUsers(q);
      setSearchResults(r.data);
    } catch {}
  };

  const addMember = (user) => {
    if (!form.memberIds.includes(user.id)) {
      setForm(f => ({ ...f, memberIds: [...f.memberIds, user.id], _members: [...(f._members || []), user] }));
    }
    setUserSearch(''); setSearchResults([]);
  };

  const removeMember = (id) => {
    setForm(f => ({ ...f, memberIds: f.memberIds.filter(m => m !== id), _members: (f._members || []).filter(m => m.id !== id) }));
  };

  const createGroup = async (e) => {
    e.preventDefault();
    try {
      const r = await groupApi.create({ name: form.name, description: form.description, memberIds: form.memberIds });
      setGroups(g => [...g, r.data]);
      setShowModal(false);
      setForm({ name: '', description: '', memberIds: [] });
      toast.success('Group created!');
    } catch { toast.error('Failed to create group'); }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Groups</h1>
          <p className="page-subtitle">Manage your split circles</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          <Plus size={16} /> New Group
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-muted)' }}>Loading...</div>
      ) : groups.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 60 }}>
          <Users size={48} color="var(--text-muted)" style={{ margin: '0 auto 16px' }} />
          <h3 style={{ marginBottom: 8 }}>No groups yet</h3>
          <p style={{ color: 'var(--text-muted)', marginBottom: 20 }}>Create a group to start splitting bills</p>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}><Plus size={16} /> Create Group</button>
        </div>
      ) : (
        <div className="group-grid">
          {groups.map(g => (
            <Link key={g.id} to={`/groups/${g.id}`} className="group-card">
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12 }}>
                <div style={{ width: 44, height: 44, borderRadius: 12, background: 'linear-gradient(135deg,#6366f1,#06b6d4)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>
                  👥
                </div>
                <div>
                  <div className="group-name">{g.name}</div>
                  <div className="group-desc">{g.description || 'No description'}</div>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div className="group-members">
                  {(g.members || []).slice(0, 4).map(m => (
                    <div key={m.id} className="member-avatar">{(m.username || '?').slice(0,1).toUpperCase()}</div>
                  ))}
                </div>
                <span className="badge badge-info">{(g.members || []).length} members</span>
              </div>
            </Link>
          ))}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">Create Group</h3>
              <button className="close-btn" onClick={() => setShowModal(false)}><X size={20} /></button>
            </div>
            <form onSubmit={createGroup}>
              <div className="form-group">
                <label className="form-label">Group Name *</label>
                <input className="form-input" value={form.name} onChange={e => setForm({...form, name: e.target.value})} placeholder="Weekend Trip, Flat mates..." required />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <input className="form-input" value={form.description} onChange={e => setForm({...form, description: e.target.value})} placeholder="Optional" />
              </div>
              <div className="form-group">
                <label className="form-label">Add Members</label>
                <div style={{ position: 'relative' }}>
                  <Search size={14} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-muted)' }} />
                  <input className="form-input" style={{ paddingLeft: 32 }} value={userSearch} onChange={e => searchUsers(e.target.value)} placeholder="Search by username or email" />
                  {searchResults.length > 0 && (
                    <div style={{ position: 'absolute', top: '100%', left: 0, right: 0, background: 'var(--bg-card2)', border: '1px solid var(--border)', borderRadius: 10, zIndex: 10, overflow: 'hidden' }}>
                      {searchResults.map(u => (
                        <div key={u.id} onClick={() => addMember(u)} style={{ padding: '10px 14px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 10, transition: 'background 0.15s' }}
                          onMouseEnter={e => e.currentTarget.style.background = 'rgba(99,102,241,0.1)'}
                          onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                          <div className="avatar" style={{ width: 28, height: 28, fontSize: 11 }}>{u.username.slice(0,2).toUpperCase()}</div>
                          <div>
                            <div style={{ fontSize: 13, fontWeight: 600 }}>{u.username}</div>
                            <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{u.email}</div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                {(form._members || []).length > 0 && (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginTop: 10 }}>
                    {form._members.map(m => (
                      <div key={m.id} style={{ display: 'flex', alignItems: 'center', gap: 6, background: 'rgba(99,102,241,0.15)', borderRadius: 20, padding: '4px 10px 4px 8px' }}>
                        <div className="avatar" style={{ width: 20, height: 20, fontSize: 9 }}>{m.username.slice(0,1).toUpperCase()}</div>
                        <span style={{ fontSize: 12 }}>{m.username}</span>
                        <X size={12} style={{ cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => removeMember(m.id)} />
                      </div>
                    ))}
                  </div>
                )}
              </div>
              <button className="btn btn-primary btn-full" type="submit">Create Group</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
