import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Filter, ArrowLeft } from 'lucide-react';
import { getPublishedCourses, getAllCategories } from '../../services/courseService';
import type { PublicCourse, CourseLevel } from '../../types/api';
import CourseCard from '../../components/course/CourseCard';
import { CourseCardSkeleton } from '../../components/ui/Skeleton';
import Pagination from '../../components/ui/Pagination';
import Select from '../../components/ui/Select';
import Input from '../../components/ui/Input';
import Button from '../../components/ui/Button';
import Autocomplete from '../../components/ui/Autocomplete';
import { useAuth } from '../../contexts/AuthContext';

export default function CoursesPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const [courses, setCourses] = useState<PublicCourse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 9;

  const [categoryFilter, setCategoryFilter] = useState('');
  const [levelFilter, setLevelFilter] = useState<CourseLevel | ''>('');
  const [searchQuery, setSearchQuery] = useState('');
  const [availableCategories, setAvailableCategories] = useState<string[]>([]);

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchCourses();
  }, [currentPage, categoryFilter, levelFilter]);

  const fetchCategories = async () => {
    try {
      const categories = await getAllCategories();
      setAvailableCategories(categories);
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const fetchCourses = async () => {
    try {
      setLoading(true);
      setError(null);

      const params: {
        page: number;
        size: number;
        category?: string;
        level?: CourseLevel;
      } = {
        page: currentPage - 1,
        size: pageSize,
      };

      if (categoryFilter) {
        params.category = categoryFilter;
      }

      if (levelFilter) {
        params.level = levelFilter;
      }

      const response = await getPublishedCourses(params);

      setCourses(response.courses || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (err: any) {
      console.error('Error fetching courses:', err);
      setError(err.response?.data?.message || 'Failed to load courses');
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleCategoryChange = (value: string) => {
    setCategoryFilter(value);
    setCurrentPage(1);
  };

  const handleLevelChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setLevelFilter(e.target.value as CourseLevel | '');
    setCurrentPage(1);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  const handleClearFilters = () => {
    setCategoryFilter('');
    setLevelFilter('');
    setSearchQuery('');
    setCurrentPage(1);
  };

  const filteredCourses = searchQuery
    ? courses.filter(
        (course) =>
          course.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          course.description.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : courses;

  return (
    <div className="min-h-screen bg-gray-950">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-4xl font-bold text-gray-100">
              Browse Courses
            </h1>
            {isAuthenticated && (
              <Button
                variant="secondary"
                onClick={() => navigate('/dashboard')}
                className="px-4 py-2"
              >
                <ArrowLeft className="w-4 h-4" />
                Back to Dashboard
              </Button>
            )}
          </div>
          <p className="text-lg text-gray-400">
            Discover our collection of {totalElements} published courses
          </p>
        </div>

        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6 mb-8">
          <div className="flex items-center gap-2 mb-4">
            <Filter className="w-5 h-5 text-purple-400" />
            <h2 className="text-lg font-semibold text-gray-100">Filters</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-500" />
              <Input
                type="text"
                placeholder="Search courses..."
                value={searchQuery}
                onChange={handleSearchChange}
                className="pl-10"
              />
            </div>

            <Autocomplete
              value={categoryFilter}
              onChange={handleCategoryChange}
              options={availableCategories}
              placeholder="All Categories"
            />

            <Select
              value={levelFilter}
              onChange={handleLevelChange}
              options={[
                { value: '', label: 'All Levels' },
                { value: 'BEGINNER', label: 'Beginner' },
                { value: 'INTERMEDIATE', label: 'Intermediate' },
                { value: 'ADVANCED', label: 'Advanced' },
              ]}
            />
          </div>

          {(categoryFilter || levelFilter || searchQuery) && (
            <button
              onClick={handleClearFilters}
              className="mt-4 text-sm text-purple-400 hover:text-purple-300 transition-colors"
            >
              Clear all filters
            </button>
          )}
        </div>

        {error && (
          <div className="bg-red-900/30 border border-red-700 text-red-400 px-6 py-4 rounded-xl mb-8">
            <p className="font-medium">Error loading courses</p>
            <p className="text-sm mt-1">{error}</p>
          </div>
        )}

        {loading && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {Array.from({ length: pageSize }).map((_, index) => (
              <CourseCardSkeleton key={index} />
            ))}
          </div>
        )}

        {!loading && filteredCourses.length === 0 && (
          <div className="text-center py-16">
            <div className="w-24 h-24 mx-auto mb-6 bg-gray-800 rounded-full flex items-center justify-center">
              <Search className="w-12 h-12 text-gray-600" />
            </div>
            <h3 className="text-2xl font-bold text-gray-300 mb-2">No courses found</h3>
            <p className="text-gray-500 mb-6">
              Try adjusting your filters or search query
            </p>
            {(categoryFilter || levelFilter || searchQuery) && (
              <button
                onClick={handleClearFilters}
                className="text-purple-400 hover:text-purple-300 transition-colors"
              >
                Clear all filters
              </button>
            )}
          </div>
        )}

        {!loading && filteredCourses.length > 0 && (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredCourses.map((course) => (
                <CourseCard key={course.id} course={course} />
              ))}
            </div>

            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />

            <div className="text-center mt-6 text-sm text-gray-500">
              Showing {((currentPage - 1) * pageSize) + 1} - {Math.min(currentPage * pageSize, totalElements)} of {totalElements} courses
            </div>
          </>
        )}
      </div>
    </div>
  );
}
