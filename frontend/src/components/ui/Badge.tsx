import type { HTMLAttributes, ReactNode } from 'react';

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  children: ReactNode;
  variant?: 'default' | 'success' | 'warning' | 'danger' | 'info' | 'purple';
}

export default function Badge({ children, variant = 'default', className = '', ...props }: BadgeProps) {
  const variants = {
    default: 'bg-gray-700 text-gray-300 border-gray-600',
    success: 'bg-green-900/30 text-green-400 border-green-700',
    warning: 'bg-yellow-900/30 text-yellow-400 border-yellow-700',
    danger: 'bg-red-900/30 text-red-400 border-red-700',
    info: 'bg-blue-900/30 text-blue-400 border-blue-700',
    purple: 'bg-purple-900/30 text-purple-400 border-purple-700',
  };

  return (
    <span
      className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium border ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </span>
  );
}
