import React, { useState, useEffect, useRef } from 'react';
import { Calendar as CalendarIcon, ChevronLeft, ChevronRight, X, ChevronDown } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export default function ModernDatePicker({
  value,
  onChange,
  name = 'birthdate',
  placeholder,
  disabled = false,
  maxDate,
  minDate,
}) {
  const { locale } = useLanguage();
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef(null);

  // Month names
  const MONTHS_EN = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  const MONTHS_KM = [
    'មករា', 'កុម្ភៈ', 'មីនា', 'មេសា', 'ឧសភា', 'មិថុនា',
    'កក្កដា', 'សីហា', 'កញ្ញា', 'តុលា', 'វិច្ឆិកា', 'ធ្នូ'
  ];
  const MONTHS = locale === 'km' ? MONTHS_KM : MONTHS_EN;

  const DAYS_EN = ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'];
  const DAYS_KM = ['ច', 'អ', 'ព', 'ព្រ', 'សុ', 'ស', 'អា'];
  const DAYS = locale === 'km' ? DAYS_KM : DAYS_EN;

  // Parse initial selected date
  const parseDate = (val) => {
    if (!val) return null;
    const parts = val.split('-');
    if (parts.length === 3) {
      const y = parseInt(parts[0], 10);
      const m = parseInt(parts[1], 10) - 1;
      const d = parseInt(parts[2], 10);
      if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
        return new Date(y, m, d);
      }
    }
    const d = new Date(val);
    return isNaN(d.getTime()) ? null : d;
  };

  const selectedDate = parseDate(value);

  const [currentYear, setCurrentYear] = useState(() => {
    return selectedDate ? selectedDate.getFullYear() : new Date().getFullYear();
  });
  const [currentMonth, setCurrentMonth] = useState(() => {
    return selectedDate ? selectedDate.getMonth() : new Date().getMonth();
  });

  // Sync viewed year/month when value changes externally
  useEffect(() => {
    if (selectedDate) {
      setCurrentYear(selectedDate.getFullYear());
      setCurrentMonth(selectedDate.getMonth());
    }
  }, [value]);

  // Close on outside click
  useEffect(() => {
    function handleClickOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    }
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handlePrevMonth = (e) => {
    e.stopPropagation();
    if (currentMonth === 0) {
      setCurrentMonth(11);
      setCurrentYear((y) => y - 1);
    } else {
      setCurrentMonth((m) => m - 1);
    }
  };

  const handleNextMonth = (e) => {
    e.stopPropagation();
    if (currentMonth === 11) {
      setCurrentMonth(0);
      setCurrentYear((y) => y + 1);
    } else {
      setCurrentMonth((m) => m + 1);
    }
  };

  const handleSelectDay = (day) => {
    const formattedMonth = String(currentMonth + 1).padStart(2, '0');
    const formattedDay = String(day).padStart(2, '0');
    const dateStr = `${currentYear}-${formattedMonth}-${formattedDay}`;

    if (onChange) {
      onChange({
        target: {
          name,
          value: dateStr,
        },
      });
    }
    setIsOpen(false);
  };

  const handleClear = (e) => {
    e.stopPropagation();
    if (onChange) {
      onChange({
        target: {
          name,
          value: '',
        },
      });
    }
  };

  const handleSelectToday = (e) => {
    e.stopPropagation();
    const today = new Date();
    const formattedMonth = String(today.getMonth() + 1).padStart(2, '0');
    const formattedDay = String(today.getDate()).padStart(2, '0');
    const dateStr = `${today.getFullYear()}-${formattedMonth}-${formattedDay}`;

    setCurrentYear(today.getFullYear());
    setCurrentMonth(today.getMonth());

    if (onChange) {
      onChange({
        target: {
          name,
          value: dateStr,
        },
      });
    }
    setIsOpen(false);
  };

  // Generate days grid
  const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
  const firstDayOfWeek = (new Date(currentYear, currentMonth, 1).getDay() + 6) % 7; // Monday = 0

  const daysInPrevMonth = new Date(currentYear, currentMonth, 0).getDate();
  const prevMonthPadding = [];
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    prevMonthPadding.push(daysInPrevMonth - i);
  }

  const currentMonthDays = [];
  for (let i = 1; i <= daysInMonth; i++) {
    currentMonthDays.push(i);
  }

  const totalGridCells = prevMonthPadding.length + currentMonthDays.length;
  const nextMonthPadding = [];
  const remainingCells = (7 - (totalGridCells % 7)) % 7;
  for (let i = 1; i <= remainingCells; i++) {
    nextMonthPadding.push(i);
  }

  // Generate Year options (current down to 1920)
  const currentActualYear = new Date().getFullYear();
  const years = [];
  for (let y = currentActualYear; y >= 1920; y--) {
    years.push(y);
  }

  // Format display text
  const formatDisplay = () => {
    if (!selectedDate) return '';
    const d = selectedDate.getDate();
    const m = MONTHS[selectedDate.getMonth()];
    const y = selectedDate.getFullYear();
    return `${d} ${m} ${y}`;
  };

  // Calculate age if birthdate
  const getAge = () => {
    if (!selectedDate) return null;
    const today = new Date();
    let age = today.getFullYear() - selectedDate.getFullYear();
    const m = today.getMonth() - selectedDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < selectedDate.getDate())) {
      age--;
    }
    return age >= 0 ? age : null;
  };

  const age = getAge();
  const defaultPlaceholder = locale === 'km' ? 'ជ្រើសរើសថ្ងៃខែឆ្នាំកំណើត' : 'Select Date of Birth';

  return (
    <div className="relative w-full" ref={containerRef}>
      {/* Trigger Button */}
      <button
        type="button"
        disabled={disabled}
        onClick={() => !disabled && setIsOpen(!isOpen)}
        className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-xl border transition shadow-2xs text-left cursor-pointer ${
          isOpen
            ? 'border-indigo-500 ring-2 ring-indigo-500/20 bg-white dark:bg-slate-800'
            : 'border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 hover:border-slate-300 dark:hover:border-slate-600'
        } ${disabled ? 'opacity-50 cursor-not-allowed bg-slate-100 dark:bg-slate-850' : ''}`}
      >
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-8 h-8 rounded-lg bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center shrink-0">
            <CalendarIcon size={16} />
          </div>

          <div className="truncate">
            {value ? (
              <div className="flex items-center gap-2">
                <span className="text-sm font-semibold text-slate-900 dark:text-white">
                  {formatDisplay()}
                </span>
                {age !== null && (
                  <span className="hidden sm:inline-block px-2 py-0.5 text-[11px] font-bold bg-indigo-100 dark:bg-indigo-950 text-indigo-700 dark:text-indigo-300 rounded-md">
                    {age} {locale === 'km' ? 'ឆ្នាំ' : 'yrs'}
                  </span>
                )}
              </div>
            ) : (
              <span className="text-sm text-slate-400 dark:text-slate-500">
                {placeholder || defaultPlaceholder}
              </span>
            )}
          </div>
        </div>

        <div className="flex items-center gap-1.5 shrink-0 ml-2">
          {value && !disabled && (
            <span
              onClick={handleClear}
              className="p-1 rounded-md text-slate-400 hover:text-rose-500 hover:bg-slate-100 dark:hover:bg-slate-700 transition"
              title={locale === 'km' ? 'សម្អាត' : 'Clear'}
            >
              <X size={14} />
            </span>
          )}
          <ChevronDown
            size={16}
            className={`text-slate-400 transition-transform duration-200 ${
              isOpen ? 'rotate-180 text-indigo-600 dark:text-indigo-400' : ''
            }`}
          />
        </div>
      </button>

      {/* Popover Calendar Dropdown */}
      {isOpen && (
        <div className="absolute left-0 sm:left-auto sm:right-0 mt-2 z-50 w-72 sm:w-80 bg-white dark:bg-slate-900 rounded-2xl shadow-2xl border border-slate-200/90 dark:border-slate-800 p-4 transition-all animate-in fade-in zoom-in-95 backdrop-blur-md">
          {/* Header Controls: Month Selector, Year Selector, Arrows */}
          <div className="flex items-center justify-between mb-3.5 gap-2">
            <div className="flex items-center gap-1.5">
              {/* Month Selector */}
              <select
                value={currentMonth}
                onChange={(e) => setCurrentMonth(parseInt(e.target.value, 10))}
                className="text-xs sm:text-sm font-bold bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-100 border-none rounded-lg px-2.5 py-1.5 outline-none cursor-pointer hover:bg-slate-200 dark:hover:bg-slate-700 transition"
              >
                {MONTHS.map((m, idx) => (
                  <option key={m} value={idx}>
                    {m}
                  </option>
                ))}
              </select>

              {/* Year Selector */}
              <select
                value={currentYear}
                onChange={(e) => setCurrentYear(parseInt(e.target.value, 10))}
                className="text-xs sm:text-sm font-bold bg-slate-100 dark:bg-slate-800 text-slate-800 dark:text-slate-100 border-none rounded-lg px-2.5 py-1.5 outline-none cursor-pointer hover:bg-slate-200 dark:hover:bg-slate-700 transition"
              >
                {years.map((y) => (
                  <option key={y} value={y}>
                    {y}
                  </option>
                ))}
              </select>
            </div>

            {/* Prev/Next Month Arrows */}
            <div className="flex items-center gap-1">
              <button
                type="button"
                onClick={handlePrevMonth}
                className="p-1.5 rounded-lg text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
                title={locale === 'km' ? 'ខែមុន' : 'Previous month'}
              >
                <ChevronLeft size={16} />
              </button>
              <button
                type="button"
                onClick={handleNextMonth}
                className="p-1.5 rounded-lg text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 transition cursor-pointer"
                title={locale === 'km' ? 'ខែបន្ទាប់' : 'Next month'}
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>

          {/* Day of Week Labels */}
          <div className="grid grid-cols-7 mb-2 text-center">
            {DAYS.map((d) => (
              <span
                key={d}
                className="text-[11px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider py-1"
              >
                {d}
              </span>
            ))}
          </div>

          {/* Days Grid */}
          <div className="grid grid-cols-7 gap-1 text-center">
            {/* Prev month days (disabled/muted) */}
            {prevMonthPadding.map((d, i) => (
              <div
                key={`prev-${i}`}
                className="h-8 flex items-center justify-center text-xs text-slate-300 dark:text-slate-600 select-none pointer-events-none"
              >
                {d}
              </div>
            ))}

            {/* Current month days */}
            {currentMonthDays.map((d) => {
              const isSelected =
                selectedDate &&
                selectedDate.getDate() === d &&
                selectedDate.getMonth() === currentMonth &&
                selectedDate.getFullYear() === currentYear;

              const isToday =
                new Date().getDate() === d &&
                new Date().getMonth() === currentMonth &&
                new Date().getFullYear() === currentYear;

              return (
                <button
                  type="button"
                  key={`cur-${d}`}
                  onClick={() => handleSelectDay(d)}
                  className={`h-8 w-8 mx-auto flex items-center justify-center text-xs rounded-xl transition cursor-pointer ${
                    isSelected
                      ? 'bg-indigo-600 text-white font-bold shadow-md shadow-indigo-600/30'
                      : isToday
                      ? 'border border-indigo-400 dark:border-indigo-500 text-indigo-600 dark:text-indigo-400 font-bold hover:bg-indigo-50 dark:hover:bg-indigo-950/40'
                      : 'text-slate-700 dark:text-slate-200 hover:bg-indigo-50 dark:hover:bg-indigo-950/40 hover:text-indigo-600 dark:hover:text-indigo-400 font-medium'
                  }`}
                >
                  {d}
                </button>
              );
            })}

            {/* Next month days (disabled/muted) */}
            {nextMonthPadding.map((d, i) => (
              <div
                key={`next-${i}`}
                className="h-8 flex items-center justify-center text-xs text-slate-300 dark:text-slate-600 select-none pointer-events-none"
              >
                {d}
              </div>
            ))}
          </div>

          {/* Quick Footer Action Bar */}
          <div className="flex items-center justify-between mt-3 pt-3 border-t border-slate-100 dark:border-slate-800 text-xs">
            <button
              type="button"
              onClick={handleClear}
              className="text-slate-500 hover:text-rose-600 dark:text-slate-400 dark:hover:text-rose-400 font-medium cursor-pointer transition"
            >
              {locale === 'km' ? 'សម្អាត' : 'Clear'}
            </button>

            <button
              type="button"
              onClick={handleSelectToday}
              className="text-indigo-600 hover:text-indigo-700 dark:text-indigo-400 font-semibold cursor-pointer transition"
            >
              {locale === 'km' ? 'ថ្ងៃនេះ' : 'Today'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
