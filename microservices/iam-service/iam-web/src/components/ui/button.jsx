import React from 'react';

const variantStyles = {
  default: 'bg-zinc-900 text-zinc-50 hover:bg-zinc-900/90 shadow-xs',
  destructive: 'bg-rose-600 text-white hover:bg-rose-600/90 shadow-xs',
  outline: 'border border-zinc-200 bg-white hover:bg-zinc-100 hover:text-zinc-900 text-zinc-800',
  secondary: 'bg-zinc-100 text-zinc-900 hover:bg-zinc-100/80',
  ghost: 'hover:bg-zinc-100 hover:text-zinc-900 text-zinc-700',
  link: 'text-zinc-900 underline-offset-4 hover:underline',
  primary: 'bg-indigo-600 text-white hover:bg-indigo-700 shadow-xs',
};

const sizeStyles = {
  default: 'h-9 px-4 py-2 text-sm',
  sm: 'h-8 rounded-lg px-3 text-xs',
  lg: 'h-10 rounded-xl px-6 text-base',
  icon: 'h-8 w-8 p-0',
};

export function Button({
  className = '',
  variant = 'default',
  size = 'default',
  type = 'button',
  children,
  disabled = false,
  ...props
}) {
  return (
    <button
      type={type}
      disabled={disabled}
      className={`inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-lg font-medium transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-zinc-950 disabled:pointer-events-none disabled:opacity-50 cursor-pointer ${
        variantStyles[variant] || variantStyles.default
      } ${sizeStyles[size] || sizeStyles.default} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
