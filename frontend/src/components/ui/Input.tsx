import {forwardRef, type InputHTMLAttributes} from "react";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
    ({label, error, className = '', ...props}, ref) => {
        return (
            <div className="w-full">
                {label && (
                    <label className="block text-sm font-semibold text-gray-200 mb-2">
                        {label}
                        {props.required && <span className="text-pink-500 ml-1">*</span>}
                    </label>
                )}
                <input
                    ref={ref}
                    className={`
              w-full px-4 py-3 bg-gray-800 border rounded-xl shadow-sm
              text-gray-100 placeholder-gray-500
              focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent
              disabled:bg-gray-900 disabled:cursor-not-allowed
              transition-all duration-200
              ${error ? 'border-red-500 focus:ring-red-500' : 'border-gray-700'}
              ${className}
            `}
            {...props}
                />
                {error && (
                    <p className="mt-2 text-sm text-red-400">{error}</p>
                )}
            </div>
        );
    }
);

Input.displayName = 'Input';
export default Input;