import { Wallet, ArrowUpRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import Button from '../../components/ui/Button';
import { useAuth } from '../../hooks/useAuth';

export default function BalancePage() {
  const { user } = useAuth();
  const navigate = useNavigate();

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
            This balance updates automatically after course purchases, instructor payouts, and
            admin top-ups.
          </p>
        </div>

        <div className="bg-gray-900 border border-gray-800 rounded-2xl p-6 space-y-4">
          <p className="text-lg font-semibold text-gray-100">Request Top-up</p>
          <p className="text-sm text-gray-400">
            Need more funds? Use the button below to jump to the profile section where you can add
            balance after contacting an admin.
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
              A detailed list of top-ups and course payments will appear here soon.
            </p>
          </div>
          <span className="text-xs px-3 py-1 rounded-full bg-purple-500/10 text-purple-300 border border-purple-500/30">
            Coming Soon
          </span>
        </div>

        <div className="p-6 border border-dashed border-gray-700 rounded-xl text-center">
          <p className="text-gray-300 font-medium mb-2">No transactions yet</p>
          <p className="text-sm text-gray-500">
            We&apos;re working on showcasing your recent top-ups and course purchases. Stay tuned!
          </p>
        </div>
      </div>
    </div>
  );
}
