import {useEffect} from 'react';
import {useForm} from 'react-hook-form';
import {zodResolver} from '@hookform/resolvers/zod';
import {z} from 'zod';
import {Link, useNavigate} from 'react-router-dom';
import {toast} from 'react-hot-toast';
import {LogIn} from 'lucide-react';

import {useAuth} from '../../hooks/useAuth';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';

const loginSchema = z.object({
    email: z.string()
        .min(1, "Email is required")
        .email("Invalid email address"),
    password: z.string()
        .min(1, "Password is required")
        .min(8, "Password must be at least 8 characters"),
});

type LoginFormData = z.infer<typeof loginSchema>;


export default function LoginPage() {
    const navigate = useNavigate();
    const {login, isAuthenticated, error, clearError} = useAuth();

    const {
        register,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
    });

    useEffect(() => {
        if (isAuthenticated) {
            navigate('/dashboard');
        }
    }, [isAuthenticated, navigate]);

    useEffect(() => {
        if (error) {
            toast.error(error.message || 'Login failed');
            clearError();
        }
    }, [error, clearError]);

    const onSubmit = async (data: LoginFormData) => {
        try {
            await login(data);
            toast.success("Login successful!");
        } catch (err) {
            console.error('Login error: ', err);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-950 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div className="text-center">
                    <div className="flex justify-center">
                        <div className="bg-gradient-to-br from-purple-600 to-pink-600 p-4 rounded-2xl shadow-lg">
                            <LogIn className="h-10 w-10 text-white"/>
                        </div>
                    </div>
                    <h2 className="mt-8 text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
                        Sign in to your account
                    </h2>
                    <p className="mt-3 text-sm text-gray-400">
                        Or{' '}
                        <Link to="/register" className="font-semibold text-purple-400 hover:text-purple-300 transition-colors">
                            create a new account
                        </Link>
                    </p>
                </div>

                <form className="mt-8 space-y-6" onSubmit={handleSubmit(onSubmit)}>
                    <div className="space-y-4">
                        <Input
                            label="Email"
                            type="email"
                            autoComplete="email"
                            placeholder="john@example.com"
                            required
                            error={errors.email?.message}
                            {...register('email')}
                        />

                        <Input
                            label="Password"
                            type="password"
                            autoComplete="current-password"
                            required
                            error={errors.password?.message}
                            {...register('password')}
                        />
                    </div>

                    <div className="flex items-center justify-between">
                        <div className="flex items-center">
                            <input
                                id="remember-me"
                                name="remember-me"
                                type="checkbox"
                                className="h-4 w-4 text-purple-600 focus:ring-purple-500 border-gray-600 rounded bg-gray-800"
                            />
                            <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-300">
                                Remember me
                            </label>
                        </div>

                        <div className="text-sm">
                            <Link
                                to="/request-password-reset"
                                className="font-semibold text-purple-400 hover:text-purple-300 transition-colors"
                            >
                                Forgot your password?
                            </Link>
                        </div>
                    </div>

                    <Button
                        type="submit"
                        variant="primary"
                        isLoading={isSubmitting}
                        className="w-full"
                    >
                        {isSubmitting ? 'Signing in...' : 'Sign in'}
                    </Button>
                </form>
            </div>
        </div>
    );
}