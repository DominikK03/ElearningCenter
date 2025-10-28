import type {ReactNode} from 'react';
import {Navigate} from 'react-router-dom';
import {useAuth} from '../../hooks/useAuth';
import type {UserRole} from '../../types/api';

interface ProtectedRouteProps {
    children: ReactNode;
    redirectTo?: string;
    allowedRoles?: UserRole[];
}

export default function ProtectedRoute({
                                           children,
                                           redirectTo = '/login',
                                           allowedRoles
                                       }: ProtectedRouteProps) {
    const {isAuthenticated, loading, user} = useAuth();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-950">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500 mx-auto"></div>
                    <p className="mt-4 text-gray-300">Loading...</p>
                </div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to={redirectTo} replace/>;
    }

    if (allowedRoles && user && !allowedRoles.includes(user.role)) {
        return <Navigate to="/forbidden" replace/>;
    }

    return <>{children}</>;
}