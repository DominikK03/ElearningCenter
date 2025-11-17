import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import Button from '../../components/ui/Button';
import Modal from '../../components/ui/Modal';
import { getAllUsers } from '../../services/userService';
import { adjustUserBalance } from '../../services/adminService';
import type { User } from '../../types/api';

export default function AdminUsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isAdjustModalOpen, setIsAdjustModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [adjustType, setAdjustType] = useState<'CREDIT' | 'DEBIT'>('CREDIT');
  const [amount, setAmount] = useState('');
  const [reason, setReason] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);
  const [search, setSearch] = useState('');

  const fetchUsers = async () => {
    try {
      setIsLoading(true);
      const response = await getAllUsers({ page: 0, size: 50 });
      setUsers(response.users);
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to load users';
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const openAdjustModal = (user: User) => {
    setSelectedUser(user);
    setAmount('');
    setReason('');
    setAdjustType('CREDIT');
    setIsAdjustModalOpen(true);
  };

  const handleAdjustBalance = async () => {
    if (!selectedUser || !amount) return;
    try {
      setIsProcessing(true);
      await adjustUserBalance(selectedUser.id, {
        amount: parseFloat(amount),
        type: adjustType,
        reason: reason || undefined,
      });
      toast.success('Balance updated');
      setIsAdjustModalOpen(false);
      setSelectedUser(null);
      fetchUsers();
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to adjust balance';
      toast.error(message);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="space-y-8">
      <div>
        <p className="text-sm uppercase tracking-wider text-purple-400 mb-2">Administration</p>
        <h1 className="text-4xl font-bold text-gray-100">Manage Users</h1>
        <p className="text-gray-400 mt-2">
          Adjust balances, verify activity, and support students or instructors directly.
        </p>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-4">
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by username or email..."
          className="w-full bg-gray-950 border border-gray-800 rounded-xl px-4 py-2 text-gray-100 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-500"
        />
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl overflow-hidden">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 p-4 border-b border-gray-800 text-sm text-gray-400 uppercase tracking-wider">
          <div>User</div>
          <div>Email</div>
          <div>Role</div>
          <div>Balance</div>
          <div className="text-right">Actions</div>
        </div>

        {isLoading ? (
          <div className="p-6 text-gray-400">Loading users...</div>
        ) : users.length === 0 ? (
          <div className="p-6 text-gray-400">No users found.</div>
        ) : (
          users
            .filter((user) => {
              if (!search.trim()) return true;
              const term = search.toLowerCase();
              return (
                user.username.toLowerCase().includes(term) ||
                user.email.toLowerCase().includes(term)
              );
            })
            .map((user) => (
            <div
              key={user.id}
              className="grid grid-cols-1 md:grid-cols-5 gap-4 p-4 border-b border-gray-800 text-sm"
            >
              <div>
                <p className="font-semibold text-gray-100">{user.username}</p>
                <p className="text-gray-500 text-xs">ID: {user.id}</p>
              </div>
              <div className="text-gray-300">{user.email}</div>
              <div className="text-gray-300">{user.role}</div>
              <div className="text-gray-100">PLN {user.balance.toFixed(2)}</div>
              <div className="flex justify-end">
                <Button variant="secondary" onClick={() => openAdjustModal(user)}>
                  Adjust Balance
                </Button>
              </div>
            </div>
          ))
        )}
      </div>

      <Modal
        isOpen={isAdjustModalOpen}
        onClose={() => setIsAdjustModalOpen(false)}
        title={`Adjust Balance for ${selectedUser?.username ?? ''}`}
      >
        <div className="space-y-4">
          <div>
            <label className="text-sm text-gray-400">Adjustment Type</label>
            <select
              value={adjustType}
              onChange={(e) => setAdjustType(e.target.value as 'CREDIT' | 'DEBIT')}
              className="w-full mt-2 bg-gray-900 border border-gray-700 rounded-lg px-4 py-2 text-gray-100"
            >
              <option value="CREDIT">Credit (Add)</option>
              <option value="DEBIT">Debit (Subtract)</option>
            </select>
          </div>
          <div>
            <label className="text-sm text-gray-400">Amount (PLN)</label>
            <input
              type="number"
              min="0"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              className="w-full mt-2 bg-gray-900 border border-gray-700 rounded-lg px-4 py-2 text-gray-100"
              placeholder="0.00"
            />
          </div>
          <div>
            <label className="text-sm text-gray-400">Reason (optional)</label>
            <input
              type="text"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              className="w-full mt-2 bg-gray-900 border border-gray-700 rounded-lg px-4 py-2 text-gray-100"
              placeholder="Manual adjustment note"
            />
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <Button variant="secondary" onClick={() => setIsAdjustModalOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="primary"
              onClick={handleAdjustBalance}
              disabled={!amount || isProcessing}
            >
              {isProcessing ? 'Updating...' : 'Apply'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
