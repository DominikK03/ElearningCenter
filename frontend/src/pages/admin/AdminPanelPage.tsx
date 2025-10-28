export default function AdminPanelPage() {
  return (
    <div className="min-h-screen bg-gray-950">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent mb-8">
          Admin Panel
        </h1>
        <p className="text-gray-400">This page is only accessible to users with ADMIN role.</p>
      </div>
    </div>
  );
}
