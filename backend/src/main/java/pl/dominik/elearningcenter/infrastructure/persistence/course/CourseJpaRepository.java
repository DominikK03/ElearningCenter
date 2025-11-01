package pl.dominik.elearningcenter.infrastructure.persistence.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dominik.elearningcenter.domain.course.Course;

import java.util.List;
import java.util.Optional;

interface CourseJpaRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);
    List<Course> findByCategory(String category);
    Page<Course> findByPublished(boolean published, Pageable pageable);
    Optional<Course> findByIdAndInstructorId(Long id, Long instructorId);

    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.category IS NOT NULL ORDER BY c.category")
    List<String> findAllDistinctCategories();

    @EntityGraph(attributePaths = {"sections"})
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findWithSectionsAndLessonsById(@Param("id") Long id);
}
