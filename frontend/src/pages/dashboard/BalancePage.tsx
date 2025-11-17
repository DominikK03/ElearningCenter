import { useEffect, useState } from 'react';
import { Wallet, ArrowUpRight, Loader2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import { useAuth } from '../../hooks/useAuth';
import * as userService from '../../services/userService';
import type { WalletTransaction } from '../../types/api';

export default function BalancePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [transactions, setTransactions] = useState<WalletTransaction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTransactions = async () => {
      if (!user) return;
      try {
        setIsLoading(true);
        setError(null);
        const response = await userService.getUserTransactions(user.id, { size: 10 });
        setTransactions(response.transactions);
      } catch (err: any) {
        const message = err.response?.data?.message || 'Failed to load transactions';
        setError(message);
      } finally {
        setIsLoading(false);
      }
    };
    fetchTransactions();
  }, [user]);

  const renderTransactions = () => {
    if (isLoading) {
      return (
        <div className="flex items-center justify-center py-10 text-gray-400">
          <Loader2 className="h-5 w-5 animate-spin mr-2" />
          Loading transactions...
        </div>
      );
    }

    if (error) {
      return (
        <div className="p-4 bg-red-900/20 border border-red-700/40 rounded-xl text-red-300">
          {error}
        </div>
      );
    }

    if (transactions.length === 0) {
      return (
        <div className="p-6 border border-dashed border-gray-700 rounded-xl text-center text-gray-400">
          No transactions to display yet.
        </div>
      );
    }

    return (
      <div className="space-y-4">
        {transactions.map((tx) => {
          const isCredit = tx.type === 'CREDIT';
          const amountClass = isCredit ? 'text-green-400' : 'text-red-400';
          const sign = isCredit ? '+' : '-';
          return (
            <div
              key={tx.id}
              className="flex items-center justify-between p-4 bg-gray-900/60 border border-gray-800 rounded-xl"
            >
              <div>
                <p className="text-gray-100 font-medium">{tx.description}</p>
                <p className="text-sm text-gray-500">
                  {new Date(tx.createdAt).toLocaleString()}
                </p>
              </div>
              <div className="text-right">
                <p className={`text-lg font-semibold ${amountClass}`}>
                  {sign} {tx.amount.toFixed(2)} {tx.currency}
                </p>
                <p className="text-xs text-gray-500 uppercase tracking-wide">{tx.type}</p>
              </div>
            </div>
          );
        })}
      </div>
    );
  };

  if (!user) {
    return (
      <div className="min-h-[50vh] flex items-center justify-center">
        <div className="text-center text-gray-400">Loading balance...</div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
        <div>
          <p className="text-sm uppercase tracking-wider text-purple-400 mb-2">Wallet Overview</p>
          <h1 className="text-3xl font-bold text-gray-100">My Balance</h1>
          <p className="text-gray-400 mt-2">
            Track your current funds and review upcoming transaction history.
          </p>
        </div>
        <Button
          variant="primary"
          onClick={() => navigate('/profile')}
          className="inline-flex items-center gap-2"
        >
          <ArrowUpRight className="h-4 w-4" />
          Manage Balance
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-gray-900 border border-gray-800 rounded-2xl p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <p className="text-sm text-gray-400">Current Balance</p>
              <p className="text-4xl font-bold text-gray-100 mt-2">
                PLN {user.balance.toFixed(2)}
              </p>
            </div>
            <div className="p-4 bg-green-500/10 rounded-2xl border border-green-500/20">
              <Wallet className="h-8 w-8 text-green-400" />
            </div>
          </div>
          <p className="text-sm text-gray-400">
            This balance updates automatically after every action comming to your account.
          </p>
        </div>

        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 space-y-4">
          <p className="text-lg font-semibold text-gray-100">Need more funds?</p>
          <p className="text-sm text-gray-400">
            Use the button below to jump to the profile section where you can add
            balance.
          </p>
          <Button variant="secondary" onClick={() => navigate('/profile')}>
            Go to Profile
          </Button>
        </div>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <p className="text-lg font-semibold text-gray-100">Transaction History</p>
            <p className="text-sm text-gray-400">
              Latest balance top-ups, purchases, and payouts.
            </p>
          </div>
        </div>

        {renderTransactions()}
      </div>
    </div>
  );
}
