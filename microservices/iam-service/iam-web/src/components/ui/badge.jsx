import React from 'react';

const badgeVariants = {
  default: 'border-transparent bg-zinc-900 text-zinc-50 shadow-xs hover:bg-zinc-900/80',
  secondary: 'border-transparent bg-zinc-100 text-zinc-900 hover:bg-zinc-100/80',
  destructive: 'border-transparent bg-rose-50 text-rose-700 border border-rose-200',
  outline: 'text-zinc-950 border border-zinc-200',
  success: 'border-transparent bg-emerald-50 text-emerald-700 border border-emerald-200',
  warning: 'border-transparent bg-amber-50 text-amber-800 border border-amber-200',
  indigo: 'border-transparent bg-indigo-50 text-indigo-700 border border-indigo-200',
  purple: 'border-transparent bg-purple-50 text-purple-700 border border-purple-200',
};

export function Badge({ className = '', variant = 'default', children, ...props }) {
  return (
    <div
      className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-zinc-950 focus:ring-offset-2 ${
        badgeVariants[variant] || badgeVariants.default
      } ${className}`}
      {...props}
    >
      {children}
    </div>
  );
}
