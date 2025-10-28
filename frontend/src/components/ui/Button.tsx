import type {ButtonHTMLAttributes, ReactNode} from "react";
import {Loader2} from 'lucide-react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
    isLoading?: boolean;
    children: ReactNode;
}

export default function Button({
    variant = 'primary',
    isLoading = false,
    disabled,
    className = '',
    children,
    ...props
}: ButtonProps) {
    const baseStyles = `px-6 py-3 rounded-xl font-semibold transition-all duration-200
    focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-900
    disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2
    shadow-lg hover:shadow-xl transform hover:scale-[1.02] active:scale-[0.98]`;

    const variants = {
        primary: 'bg-gradient-to-r from-purple-600 to-pink-600 text-white hover:from-purple-700 hover:to-pink-700 focus:ring-purple-500',
        secondary: 'bg-gray-700 text-gray-100 hover:bg-gray-600 focus:ring-gray-500 border border-gray-600',
        danger: 'bg-gradient-to-r from-red-600 to-orange-600 text-white hover:from-red-700 hover:to-orange-700 focus:ring-red-500',
        ghost: 'bg-transparent text-gray-300 hover:bg-gray-800 focus:ring-gray-600 border border-gray-700',
    };

    return (
        <button
            disabled={disabled || isLoading}
            className={`${baseStyles} ${variants[variant]} ${className}`}
            {...props}
            >
            {isLoading && <Loader2 className="w-4 h-4 animate-spin" />}
            {children}
        </button>
    );
}