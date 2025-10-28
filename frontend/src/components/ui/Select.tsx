import { forwardRef, type SelectHTMLAttributes } from 'react';

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
    label?: string;
    error?: string;
    options: { value: string; label: string }[];
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
    ({ label, error, options, className = '', ...props}, ref) => {
        return (
            <div className="w-full">
                {label && (
                    <label className="block text-sm font-semibold text-gray-200 mb-2">
                        {label}
                        {props.required && <span className="text-pink-500 ml-1">*</span>}
                    </label>
                )}
                <select
                    ref={ref}
                    className={`
                    w-full px-4 py-3 bg-gray-800 border rounded-xl shadow-sm
                    text-gray-100
                    focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent
                    disabled:bg-gray-900 disabled:cursor-not-allowed
                    transition-all duration-200
                    ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-700'}
                    ${className}
                    `}
                    {...props}
                >
                    {options.map((option) => (
                        <option key={option.value} value={option.value} className="bg-gray-800">
                            {option.label}
                        </option>
                    ))}
                </select>
                {error && (
                    <p className="mt-2 text-sm text-red-400">{error}</p>
                )}
            </div>
        );
    }
);

Select.displayName = 'Select';
export default Select;