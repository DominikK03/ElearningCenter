package pl.dominik.elearningcenter.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.dominik.elearningcenter.domain.course.Course;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.domain.course.CourseRepository;
import pl.dominik.elearningcenter.domain.course.Lesson;
import pl.dominik.elearningcenter.domain.course.Section;
import pl.dominik.elearningcenter.domain.shared.valueobject.Email;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;
import pl.dominik.elearningcenter.domain.shared.valueobject.Password;
import pl.dominik.elearningcenter.domain.shared.valueobject.Username;
import pl.dominik.elearningcenter.domain.user.User;
import pl.dominik.elearningcenter.domain.user.UserRepository;
import pl.dominik.elearningcenter.domain.user.UserRole;
import pl.dominik.elearningcenter.infrastructure.security.PasswordHashingService;

import java.math.BigDecimal;

@Configuration
@Profile("!test")
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            CourseRepository courseRepository,
            PasswordHashingService passwordHashingService) {

        return args -> {
            if (userRepository.existsByUsername(new Username("admin"))) {
                return;
            }

            Password adminPassword = passwordHashingService.hashPassword("admin123");
            User admin = User.register(
                    new Username("admin"),
                    new Email("admin@elearning.com"),
                    adminPassword,
                    UserRole.ADMIN
            );
            admin.generateVerificationToken("test-admin-token", 24);
            admin.verifyEmail("test-admin-token");
            admin.addBalance(Money.pln(BigDecimal.valueOf(10000.00)));
            admin = userRepository.save(admin);

            Password instructorPassword = passwordHashingService.hashPassword("instructor123");
            User instructor = User.register(
                    new Username("instructor"),
                    new Email("instructor@elearning.com"),
                    instructorPassword,
                    UserRole.INSTRUCTOR
            );
            instructor.generateVerificationToken("test-instructor-token", 24);
            instructor.verifyEmail("test-instructor-token");
            instructor.addBalance(Money.pln(BigDecimal.valueOf(500.00)));
            instructor = userRepository.save(instructor);

            Password studentPassword = passwordHashingService.hashPassword("student123");
            User student = User.register(
                    new Username("student"),
                    new Email("student@elearning.com"),
                    studentPassword,
                    UserRole.STUDENT
            );
            student.generateVerificationToken("test-student-token", 24);
            student.verifyEmail("test-student-token");
            student.addBalance(Money.pln(BigDecimal.valueOf(1000.00)));
            student = userRepository.save(student);

            Course course1 = Course.create(
                    "Spring Boot for Beginners",
                    "Learn Spring Boot from scratch. This comprehensive course covers everything you need to know to build modern web applications with Spring Boot. Perfect for beginners!",
                    299.99,
                    "PLN",
                    instructor.getId(),
                    "Programming",
                    CourseLevel.BEGINNER
            );
            course1.updateThumbnail("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800");

            Section section1_1 = new Section("Introduction to Spring Boot", 0);
            Lesson lesson1_1_1 = new Lesson("What is Spring Boot?", "Introduction to Spring Boot framework and its benefits.", 0);
            lesson1_1_1.setDurationMinutes(15);
            section1_1.addLesson(lesson1_1_1);

            Lesson lesson1_1_2 = new Lesson("Setting up Development Environment", "How to install Java, Maven, and IDE for Spring Boot development.", 1);
            lesson1_1_2.setVideoUrl("https://www.youtube.com/watch?v=example1");
            lesson1_1_2.setDurationMinutes(20);
            section1_1.addLesson(lesson1_1_2);

            Lesson lesson1_1_3 = new Lesson("Creating Your First Spring Boot Application", "Step by step guide to create a Hello World application.", 2);
            lesson1_1_3.setVideoUrl("https://www.youtube.com/watch?v=example2");
            lesson1_1_3.setDurationMinutes(25);
            section1_1.addLesson(lesson1_1_3);
            course1.addSection(section1_1);

            Section section1_2 = new Section("Spring Boot Fundamentals", 1);
            Lesson lesson1_2_1 = new Lesson("Dependency Injection", "Understanding Spring's core concept of Dependency Injection.", 0);
            lesson1_2_1.setVideoUrl("https://www.youtube.com/watch?v=example3");
            lesson1_2_1.setDurationMinutes(30);
            section1_2.addLesson(lesson1_2_1);

            Lesson lesson1_2_2 = new Lesson("Spring Boot Auto-configuration", "How auto-configuration works in Spring Boot.", 1);
            lesson1_2_2.setDurationMinutes(25);
            section1_2.addLesson(lesson1_2_2);

            Lesson lesson1_2_3 = new Lesson("Working with Application Properties", "Configuration management in Spring Boot applications.", 2);
            lesson1_2_3.setVideoUrl("https://www.youtube.com/watch?v=example4");
            lesson1_2_3.setDurationMinutes(20);
            section1_2.addLesson(lesson1_2_3);
            course1.addSection(section1_2);

            course1.publish();
            course1 = courseRepository.save(course1);

            Course course2 = Course.create(
                    "Advanced React Patterns",
                    "Master advanced React patterns and best practices. Learn hooks, context API, performance optimization, and modern state management.",
                    399.99,
                    "PLN",
                    instructor.getId(),
                    "Web Development",
                    CourseLevel.ADVANCED
            );
            course2.updateThumbnail("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=800");

            Section section2_1 = new Section("React Hooks Deep Dive", 0);
            Lesson lesson2_1_1 = new Lesson("useState and useEffect", "Master the fundamentals of React hooks.", 0);
            lesson2_1_1.setVideoUrl("https://www.youtube.com/watch?v=example5");
            lesson2_1_1.setDurationMinutes(35);
            section2_1.addLesson(lesson2_1_1);

            Lesson lesson2_1_2 = new Lesson("Custom Hooks", "Create your own reusable hooks.", 1);
            lesson2_1_2.setVideoUrl("https://www.youtube.com/watch?v=example6");
            lesson2_1_2.setDurationMinutes(40);
            section2_1.addLesson(lesson2_1_2);

            Lesson lesson2_1_3 = new Lesson("useContext and useReducer", "Advanced state management patterns.", 2);
            lesson2_1_3.setDurationMinutes(45);
            section2_1.addLesson(lesson2_1_3);
            course2.addSection(section2_1);

            Section section2_2 = new Section("Performance Optimization", 1);
            Lesson lesson2_2_1 = new Lesson("React.memo and useMemo", "Prevent unnecessary re-renders.", 0);
            lesson2_2_1.setVideoUrl("https://www.youtube.com/watch?v=example7");
            lesson2_2_1.setDurationMinutes(30);
            section2_2.addLesson(lesson2_2_1);

            Lesson lesson2_2_2 = new Lesson("Code Splitting and Lazy Loading", "Optimize your bundle size.", 1);
            lesson2_2_2.setDurationMinutes(35);
            section2_2.addLesson(lesson2_2_2);
            course2.addSection(section2_2);

            course2.publish();
            course2 = courseRepository.save(course2);

            Course course3 = Course.create(
                    "Python for Data Science",
                    "Complete guide to data science with Python. Learn NumPy, Pandas, Matplotlib, and machine learning basics.",
                    449.99,
                    "PLN",
                    instructor.getId(),
                    "Data Science",
                    CourseLevel.INTERMEDIATE
            );
            course3.updateThumbnail("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800");

            Section section3_1 = new Section("Python Basics for Data Science", 0);
            Lesson lesson3_1_1 = new Lesson("Python Fundamentals", "Quick review of Python syntax.", 0);
            lesson3_1_1.setVideoUrl("https://www.youtube.com/watch?v=example8");
            lesson3_1_1.setDurationMinutes(25);
            section3_1.addLesson(lesson3_1_1);

            Lesson lesson3_1_2 = new Lesson("NumPy Essentials", "Working with arrays and numerical operations.", 1);
            lesson3_1_2.setVideoUrl("https://www.youtube.com/watch?v=example9");
            lesson3_1_2.setDurationMinutes(40);
            section3_1.addLesson(lesson3_1_2);
            course3.addSection(section3_1);

            course3.publish();
            course3 = courseRepository.save(course3);
            Course course4 = Course.create(
                    "Machine Learning Fundamentals",
                    "Introduction to machine learning concepts, algorithms, and practical applications. Work in progress!",
                    499.99,
                    "PLN",
                    instructor.getId(),
                    "Machine Learning",
                    CourseLevel.INTERMEDIATE
            );
            course4.updateThumbnail("https://images.unsplash.com/photo-1555949963-aa79dcee981c?w=800");

            Section section4_1 = new Section("Introduction to Machine Learning", 0);
            Lesson lesson4_1_1 = new Lesson("What is Machine Learning?", "Overview of ML concepts and types.", 0);
            lesson4_1_1.setDurationMinutes(20);
            section4_1.addLesson(lesson4_1_1);
            course4.addSection(section4_1);

            course4 = courseRepository.save(course4);
        };
    }
}
