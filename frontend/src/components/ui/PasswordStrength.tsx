interface PasswordStrengthProps {
    password: string;
}

export default function PasswordStrength({ password }: PasswordStrengthProps)
{
    const calculateStrength = (pwd: string): number => {
        let strength = 0;

        if (pwd.length >= 8) strength++;
        if (pwd.length >= 12) strength++;
        if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++;
        if (/\d/.test(pwd)) strength++;
        if (/[^a-zA-Z\d]/.test(pwd)) strength++;

        return strength;
    };

    const strength = calculateStrength(password);

    const getStrengthLabel = () => {
        if (strength === 0) return '';
        if (strength <= 2) return 'Weak';
        if (strength <= 3) return 'Medium';
        return 'Strong';
    };

    const getStrengthColor = () => {
        if (strength <= 2) return 'bg-gradient-to-r from-red-500 to-orange-500';
        if (strength <= 3) return 'bg-gradient-to-r from-yellow-500 to-orange-500';
        return 'bg-gradient-to-r from-green-500 to-emerald-500';
    };

    if (!password) return null;

    return (
        <div className="mt-2">
            <div className="flex gap-1.5">
                {[1, 2, 3, 4, 5].map((level) => (
                    <div
                        key={level}
                        className={`h-1.5 flex-1 rounded-full transition-all duration-300 ${
                            level <= strength ? getStrengthColor() : 'bg-gray-700'
                        }`}
                    />
                ))}
            </div>
            <p className="text-xs mt-2 text-gray-400 font-medium">
                Password strength: <span className="text-gray-200">{getStrengthLabel()}</span>
            </p>
        </div>
    );
}