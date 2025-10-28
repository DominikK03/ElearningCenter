import {useEffect, useState} from 'react';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {Link, useNavigate} from 'react-router-dom';
import {toast} from 'react-hot-toast';
import {UserPlus, CheckCircle} from 'lucide-react';

import {useAuth} from '../../hooks/useAuth';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';
import Select from '../../components/ui/Select';
import PasswordStrength from '../../components/ui/PasswordStrength';
import {UserRole} from '../../types/api';

const registerSchema = z.object({
    username: z.string()
        .min(3, 'Username must be at least 3 characters')
        .max(50, 'Username must not exceed 50 characters')
        .regex(/^[a-zA-Z0-9_]+$/, 'Username can only contain letters, numbers,and underscores'), email: z.string()
        .min(1, 'Email is required')
        .email('Invalid email address'),
    password: z.string()
        .min(8, 'Password must be at least 8 characters')
        .regex(/[a-z]/, 'Password must contain at least one lowercase letter')
        .regex(/[A-Z]/, 'Password must contain at least one uppercase letter')
        .regex(/\d/, 'Password must contain at least one number'),
    confirmPassword: z.string()
        .min(1, 'Please confirm your password'),
    role: z.enum(['STUDENT', 'INSTRUCTOR'] as const),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export default function RegisterPage() {
    const navigate = useNavigate();
    const {register: registerUser, isAuthenticated, error, clearError} =
        useAuth();
    const [registrationSuccess, setRegistrationSuccess] = useState(false);

    const {
        register,
        handleSubmit,
        watch,
        formState: {errors, isSubmitting},
    } = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            role: 'STUDENT',
        },
    });

    const passwordValue = watch('password');

    useEffect(() => {
        if (isAuthenticated) {
            navigate('/dashboard');
        }
    }, [isAuthenticated, navigate]);

    useEffect(() => {
        if (error) {
            toast.error(error.message || 'Registration failed');
            clearError();
        }
    }, [error, clearError]);

    const onSubmit = async (data: RegisterFormData) => {
        try {
            await registerUser({
                username: data.username,
                email: data.email,
                password: data.password,
                role: data.role as UserRole,
            });

            setRegistrationSuccess(true);
            toast.success('Registration successful! Check your email for verification.');
        } catch (err) {
            console.error('Registration error:', err);
        }
    };

    if (registrationSuccess) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4">
                <div className="max-w-md w-full text-center space-y-6">
                    <div className="flex justify-center">
                        <div className="bg-gradient-to-br from-green-600 to-emerald-600 p-5 rounded-3xl shadow-2xl">
                            <CheckCircle className="h-16 w-16 text-white"/>
                        </div>
                    </div>
                    <h2 className="text-4xl font-bold bg-gradient-to-r from-green-400 to-emerald-400 bg-clip-text text-transparent">
                        Registration Successful!
                    </h2>
                    <div className="bg-gray-800/50 border border-purple-500/30 rounded-2xl p-5 backdrop-blur-sm">
                        <p className="text-sm text-gray-300">
                            We've sent a verification link to your email address.
                            Please check your inbox (and spam folder) and verify your email before logging in.
                        </p>
                    </div>
                    <div className="bg-gray-800/50 border border-yellow-500/30 rounded-2xl p-5 backdrop-blur-sm">
                        <p className="text-sm text-gray-300">
                            <strong className="text-yellow-400">Important:</strong> You must verify your email before you can log in.
                            The verification link will expire in 24 hours.
                        </p>
                    </div>
                    <div className="space-y-3">
                        <p className="text-gray-400 text-sm">
                            Didn't receive the email?{' '}
                            <Link
                                to="/resend-verification"
                                className="font-semibold text-purple-400 hover:text-purple-300 transition-colors"
                            >
                                Resend verification email
                            </Link>
                        </p>
                        <Link to="/login">
                            <Button variant="primary" className="w-full">
                                Go to Login
                            </Button>
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div className="text-center">
                    <div className="flex justify-center">
                        <div className="bg-gradient-to-br from-purple-600 to-pink-600 p-4 rounded-2xl shadow-lg">
                            <UserPlus className="h-10 w-10 text-white"/>
                        </div>
                    </div>
                    <h2 className="mt-8 text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
                        Create your account
                    </h2>
                    <p className="mt-3 text-sm text-gray-400">
                        Already have an account?{' '}
                        <Link to="/login" className="font-semibold text-purple-400 hover:text-purple-300 transition-colors">
                            Sign in
                        </Link>
                    </p>
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
                    <div className="space-y-4">
                        <Input
                            label="Username"
                            type="text"
                            autoComplete="username"
                            placeholder="john_doe"
                            required
                            error={errors.username?.message}
                            {...register('username')}
                        />

                        <Input
                            label="Email"
                            type="email"
                            autoComplete="email"
                            placeholder="john@example.com"
                            required
                            error={errors.email?.message}
                            {...register('email')}
                        />

                        <div>
                            <Input
                                label="Password"
                                type="password"
                                autoComplete="new-password"
                                placeholder="••••••••"
                                required
                                error={errors.password?.message}
                                {...register('password')}
                            />
                            <PasswordStrength password={passwordValue || ''}/>
                        </div>

                        <Input
                            label="Confirm Password"
                            type="password"
                            autoComplete="new-password"
                            placeholder="••••••••"
                            required
                            error={errors.confirmPassword?.message}
                            {...register('confirmPassword')}
                        />

                        <Select
                            label="I want to register as"
                            required
                            error={errors.role?.message}
                            options={[
                                {value: 'STUDENT', label: '🎓 Student - I want to learn'},
                                {
                                    value: 'INSTRUCTOR', label: '👨‍🏫 Instructor - I want to teach' },
                            ]}
                            {...register('role')}
                                />
                                </div>

                                <div className="bg-gray-50 border border-gray-200 rounded-lg p-4">
                                <p className="text-xs text-gray-600">
                                By creating an account, you agree to our Terms of Service and
                                Privacy Policy.
                                You will receive a verification email after registration.
                                </p>
                                </div>

                                <Button
                                type="submit"
                                variant="primary"
                                isLoading={isSubmitting}
                            className="w-full"
                        >
                            {isSubmitting ? 'Creating account...' : 'Create account'}
                        </Button>
                </form>
            </div>
        </div>
    );
}