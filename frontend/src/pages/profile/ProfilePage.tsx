import { useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { User, Mail, Wallet, Edit2, Save, X, Lock, Plus } from 'lucide-react';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import PasswordStrength from '../../components/ui/PasswordStrength';
import * as userService from '../../services/userService';

const editProfileSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
});

const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, 'Current password is required'),
  newPassword: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

const addBalanceSchema = z.object({
  amount: z.string().min(1, 'Amount is required').refine((val) => !isNaN(Number(val)) && Number(val) > 0, {
    message: 'Amount must be a positive number',
  }),
});

type EditProfileFormData = z.infer<typeof editProfileSchema>;
type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;
type AddBalanceFormData = z.infer<typeof addBalanceSchema>;

export default function ProfilePage() {
  const { user, checkAuth } = useAuth();
  const [editMode, setEditMode] = useState(false);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [showAddBalanceForm, setShowAddBalanceForm] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const {
    register: registerProfile,
    handleSubmit: handleSubmitProfile,
    formState: { errors: profileErrors, isSubmitting: isSubmittingProfile },
    reset: resetProfile,
  } = useForm<EditProfileFormData>({
    resolver: zodResolver(editProfileSchema),
    defaultValues: {
      username: user?.username || '',
      email: user?.email || '',
    },
  });

  const {
    register: registerPassword,
    handleSubmit: handleSubmitPassword,
    formState: { errors: passwordErrors, isSubmitting: isSubmittingPassword },
    watch: watchPassword,
    reset: resetPassword,
  } = useForm<ChangePasswordFormData>({
    resolver: zodResolver(changePasswordSchema),
  });

  const newPassword = watchPassword('newPassword');

  const {
    register: registerBalance,
    handleSubmit: handleSubmitBalance,
    formState: { errors: balanceErrors, isSubmitting: isSubmittingBalance },
    reset: resetBalance,
  } = useForm<AddBalanceFormData>({
    resolver: zodResolver(addBalanceSchema),
  });

  const onSubmitProfile = async (data: EditProfileFormData) => {
    if (!user) return;

    try {
      setError(null);
      setSuccessMessage(null);

      await userService.updateProfile(user.id, data);
      await checkAuth();

      setSuccessMessage('Profile updated successfully!');
      setEditMode(false);

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      const errorMessage = (err as { response?: { data?: { message?: string } } }).response?.data?.message || 'Failed to update profile';
      setError(errorMessage);
    }
  };

  const onSubmitPassword = async (data: ChangePasswordFormData) => {
    if (!user) return;

    try {
      setError(null);
      setSuccessMessage(null);

      await userService.changePassword(user.id, {
        oldPassword: data.currentPassword,
        newPassword: data.newPassword,
      });

      setSuccessMessage('Password changed successfully!');
      resetPassword();
      setShowPasswordForm(false);

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      const errorMessage = (err as { response?: { data?: { message?: string } } }).response?.data?.message || 'Failed to change password';
      setError(errorMessage);
    }
  };

  const onSubmitBalance = async (data: AddBalanceFormData) => {
    if (!user) return;

    try {
      setError(null);
      setSuccessMessage(null);

      await userService.addBalance(user.id, { amount: Number(data.amount) });
      await checkAuth();

      setSuccessMessage(`Successfully added ${Number(data.amount).toFixed(2)} PLN to your balance!`);
      resetBalance();
      setShowAddBalanceForm(false);

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      const errorMessage = (err as { response?: { data?: { message?: string } } }).response?.data?.message || 'Failed to add balance';
      setError(errorMessage);
    }
  };

  const handleCancelEdit = () => {
    resetProfile({
      username: user?.username || '',
      email: user?.email || '',
    });
    setEditMode(false);
  };

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-gray-400">Loading profile...</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent mb-8">
        My Profile
      </h1>

      {successMessage && (
        <div className="mb-6 bg-green-600/20 border border-green-500 rounded-xl p-4 backdrop-blur-sm">
          <p className="text-green-400 font-semibold">{successMessage}</p>
        </div>
      )}

      {error && (
        <div className="mb-6 bg-red-600/20 border border-red-500 rounded-xl p-4 backdrop-blur-sm">
          <p className="text-red-400 font-semibold">{error}</p>
        </div>
      )}

      <div className="space-y-6">
        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-200">Profile Information</h2>
            {!editMode && (
              <Button
                variant="secondary"
                onClick={() => setEditMode(true)}
                className="gap-2"
              >
                <Edit2 className="h-4 w-4" />
                Edit Profile
              </Button>
            )}
          </div>

          {editMode ? (
            <form onSubmit={handleSubmitProfile(onSubmitProfile)} className="space-y-4">
              <Input
                label="Username"
                {...registerProfile('username')}
                error={profileErrors.username?.message}
              />

              <Input
                label="Email"
                type="email"
                {...registerProfile('email')}
                error={profileErrors.email?.message}
              />

              <div className="flex gap-3 pt-4">
                <Button
                  type="submit"
                  variant="primary"
                  disabled={isSubmittingProfile}
                  className="gap-2"
                >
                  <Save className="h-4 w-4" />
                  Save Changes
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={handleCancelEdit}
                  disabled={isSubmittingProfile}
                  className="gap-2"
                >
                  <X className="h-4 w-4" />
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <div className="space-y-4">
              <div className="flex items-center gap-4 p-4 bg-gray-900/50 rounded-xl">
                <User className="h-5 w-5 text-purple-400" />
                <div>
                  <p className="text-sm text-gray-400">Username</p>
                  <p className="text-lg font-semibold text-gray-200">{user.username}</p>
                </div>
              </div>

              <div className="flex items-center gap-4 p-4 bg-gray-900/50 rounded-xl">
                <Mail className="h-5 w-5 text-purple-400" />
                <div>
                  <p className="text-sm text-gray-400">Email</p>
                  <p className="text-lg font-semibold text-gray-200">{user.email}</p>
                </div>
              </div>

              <div className="flex items-center gap-4 p-4 bg-gray-900/50 rounded-xl">
                <User className="h-5 w-5 text-purple-400" />
                <div>
                  <p className="text-sm text-gray-400">Role</p>
                  <p className="text-lg font-semibold text-gray-200">{user.role}</p>
                </div>
              </div>

              <div className="flex items-center gap-4 p-4 bg-gray-900/50 rounded-xl">
                <Wallet className="h-5 w-5 text-green-400" />
                <div>
                  <p className="text-sm text-gray-400">Account Balance (PLN)</p>
                  <p className="text-lg font-semibold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent">
                    PLN {user.balance.toFixed(2)}
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-200">Password & Security</h2>
            {!showPasswordForm && (
              <Button
                variant="secondary"
                onClick={() => setShowPasswordForm(true)}
                className="gap-2"
              >
                <Lock className="h-4 w-4" />
                Change Password
              </Button>
            )}
          </div>

          {showPasswordForm ? (
            <form onSubmit={handleSubmitPassword(onSubmitPassword)} className="space-y-4">
              <Input
                label="Current Password"
                type="password"
                {...registerPassword('currentPassword')}
                error={passwordErrors.currentPassword?.message}
              />

              <Input
                label="New Password"
                type="password"
                {...registerPassword('newPassword')}
                error={passwordErrors.newPassword?.message}
              />

              {newPassword && <PasswordStrength password={newPassword} />}

              <Input
                label="Confirm New Password"
                type="password"
                {...registerPassword('confirmPassword')}
                error={passwordErrors.confirmPassword?.message}
              />

              <div className="flex gap-3 pt-4">
                <Button
                  type="submit"
                  variant="primary"
                  disabled={isSubmittingPassword}
                  className="gap-2"
                >
                  <Save className="h-4 w-4" />
                  Update Password
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => {
                    resetPassword();
                    setShowPasswordForm(false);
                  }}
                  disabled={isSubmittingPassword}
                  className="gap-2"
                >
                  <X className="h-4 w-4" />
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <div className="p-4 bg-gray-900/50 rounded-xl">
              <p className="text-gray-400">
                Keep your account secure by using a strong password and changing it regularly.
              </p>
            </div>
          )}
        </div>

        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-200">Balance Management</h2>
            {!showAddBalanceForm && (
              <Button
                variant="secondary"
                onClick={() => setShowAddBalanceForm(true)}
                className="gap-2"
              >
                <Plus className="h-4 w-4" />
                Add Balance
              </Button>
            )}
          </div>

          {showAddBalanceForm ? (
            <form onSubmit={handleSubmitBalance(onSubmitBalance)} className="space-y-4">
              <Input
                label="Amount to Add (PLN)"
                type="number"
                step="0.01"
                placeholder="0.00"
                {...registerBalance('amount')}
                error={balanceErrors.amount?.message}
              />

              <div className="flex gap-3 pt-4">
                <Button
                  type="submit"
                  variant="primary"
                  disabled={isSubmittingBalance}
                  className="gap-2"
                >
                  <Plus className="h-4 w-4" />
                  Add Balance
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => {
                    resetBalance();
                    setShowAddBalanceForm(false);
                  }}
                  disabled={isSubmittingBalance}
                  className="gap-2"
                >
                  <X className="h-4 w-4" />
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <div className="p-4 bg-gray-900/50 rounded-xl">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-400">Current Balance (PLN)</p>
                  <p className="text-3xl font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent mt-2">
                    PLN {user.balance.toFixed(2)}
                  </p>
                </div>
                <Wallet className="h-12 w-12 text-green-400 opacity-50" />
              </div>
            </div>
          )}
        </div>

        <div className="bg-gray-800/50 backdrop-blur-sm border border-gray-700 rounded-2xl p-6 shadow-xl">
          <h2 className="text-2xl font-bold text-gray-200 mb-6">Account Details</h2>
          <div className="space-y-4">
            <div className="flex items-center justify-between p-4 bg-gray-900/50 rounded-xl">
              <div>
                <p className="text-sm text-gray-400">Account Created</p>
                <p className="text-lg font-semibold text-gray-200">
                  {new Date(user.createdAt).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                  })}
                </p>
              </div>
            </div>

            <div className="flex items-center justify-between p-4 bg-gray-900/50 rounded-xl">
              <div>
                <p className="text-sm text-gray-400">Account Status</p>
                <p className="text-lg font-semibold text-green-400">
                  {user.enabled ? 'Active' : 'Disabled'}
                </p>
              </div>
            </div>

            <div className="flex items-center justify-between p-4 bg-gray-900/50 rounded-xl">
              <div>
                <p className="text-sm text-gray-400">Email Verification</p>
                <p className="text-lg font-semibold text-green-400">
                  {user.emailVerified ? 'Verified' : 'Not Verified'}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
