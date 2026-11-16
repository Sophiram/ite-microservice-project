import React from 'react';
import { X } from 'lucide-react';

export function Dialog({ open, onOpenChange, children }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs transition-opacity"
        onClick={() => onOpenChange && onOpenChange(false)}
      />
      {/* Content wrapper */}
      <div className="relative z-50 w-full max-w-lg rounded-3xl border border-slate-200/80 dark:border-slate-800 bg-white dark:bg-slate-900 p-6 sm:p-8 shadow-2xl animate-in fade-in-0 zoom-in-95">
        <button
          onClick={() => onOpenChange && onOpenChange(false)}
          className="absolute right-5 top-5 rounded-xl p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
        >
          <X size={18} />
        </button>
        {children}
      </div>
    </div>
  );
}

export function DialogHeader({ className = '', children, ...props }) {
  return (
    <div className={`flex flex-col space-y-1.5 text-left mb-5 ${className}`} {...props}>
      {children}
    </div>
  );
}

export function DialogTitle({ className = '', children, ...props }) {
  return (
    <h3 className={`text-lg sm:text-xl font-extrabold leading-none tracking-tight text-slate-900 dark:text-white ${className}`} {...props}>
      {children}
    </h3>
  );
}

export function DialogDescription({ className = '', children, ...props }) {
  return (
    <p className={`text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1 ${className}`} {...props}>
      {children}
    </p>
  );
}

export function DialogFooter({ className = '', children, ...props }) {
  return (
    <div className={`flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-3 pt-5 gap-2 sm:gap-0 ${className}`} {...props}>
      {children}
    </div>
  );
}
