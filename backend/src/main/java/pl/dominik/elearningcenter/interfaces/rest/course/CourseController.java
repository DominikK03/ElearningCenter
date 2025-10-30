package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import pl.dominik.elearningcenter.application.course.command.CreateCourseCommandHandler;
import pl.dominik.elearningcenter.application.course.command.UpdateCourseCommandHandler;
import pl.dominik.elearningcenter.application.course.command.DeleteCourseCommandHandler;
import pl.dominik.elearningcenter.application.course.command.PublishCourseCommandHandler;
import pl.dominik.elearningcenter.application.course.command.UnpublishCourseCommandHandler;
import pl.dominik.elearningcenter.application.course.query.GetCourseDetailsQueryHandler;
import pl.dominik.elearningcenter.application.course.query.GetAllCoursesQueryHandler;
import pl.dominik.elearningcenter.application.course.query.GetPublishedCourseQueryHandler;
import pl.dominik.elearningcenter.application.course.query.GetCoursesByInstructorQueryHandler;
import pl.dominik.elearningcenter.application.course.query.GetAllCategoriesQueryHandler;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.application.course.dto.PagedPublicCoursesDTO;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.domain.course.CourseLevel;
import pl.dominik.elearningcenter.application.course.command.CreateCourseCommand;
import pl.dominik.elearningcenter.application.course.command.UpdateCourseCommand;
import pl.dominik.elearningcenter.application.course.command.DeleteCourseCommand;
import pl.dominik.elearningcenter.application.course.command.PublishCourseCommand;
import pl.dominik.elearningcenter.application.course.command.UnpublishCourseCommand;
import pl.dominik.elearningcenter.application.course.query.GetAllCoursesQuery;
import pl.dominik.elearningcenter.application.course.query.GetPublishedCoursesQuery;
import pl.dominik.elearningcenter.application.course.query.GetCoursesByInstructorQuery;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.CreateCourseRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateCourseRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PagedCoursesResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PublicCourseDetailsResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PagedPublicCoursesResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PublishCourseResponse;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CreateCourseCommandHandler createCourseCommandHandler;
    private final UpdateCourseCommandHandler updateCourseCommandHandler;
    private final DeleteCourseCommandHandler deleteCourseCommandHandler;
    private final PublishCourseCommandHandler publishCourseCommandHandler;
    private final UnpublishCourseCommandHandler unpublishCourseCommandHandler;
    private final GetCourseDetailsQueryHandler getCourseDetailsQueryHandler;
    private final GetAllCoursesQueryHandler getAllCoursesQueryHandler;
    private final GetPublishedCourseQueryHandler getPublishedCourseQueryHandler;
    private final GetCoursesByInstructorQueryHandler getCoursesByInstructorQueryHandler;
    private final GetAllCategoriesQueryHandler getAllCategoriesQueryHandler;

    public CourseController(
            CreateCourseCommandHandler createCourseCommandHandler,
            UpdateCourseCommandHandler updateCourseCommandHandler,
            DeleteCourseCommandHandler deleteCourseCommandHandler,
            PublishCourseCommandHandler publishCourseCommandHandler,
            UnpublishCourseCommandHandler unpublishCourseCommandHandler,
            GetCourseDetailsQueryHandler getCourseDetailsQueryHandler,
            GetAllCoursesQueryHandler getAllCoursesQueryHandler,
            GetPublishedCourseQueryHandler getPublishedCourseQueryHandler,
            GetCoursesByInstructorQueryHandler getCoursesByInstructorQueryHandler,
            GetAllCategoriesQueryHandler getAllCategoriesQueryHandler
    ) {
        this.createCourseCommandHandler = createCourseCommandHandler;
        this.updateCourseCommandHandler = updateCourseCommandHandler;
        this.deleteCourseCommandHandler = deleteCourseCommandHandler;
        this.publishCourseCommandHandler = publishCourseCommandHandler;
        this.unpublishCourseCommandHandler = unpublishCourseCommandHandler;
        this.getCourseDetailsQueryHandler = getCourseDetailsQueryHandler;
        this.getAllCoursesQueryHandler = getAllCoursesQueryHandler;
        this.getPublishedCourseQueryHandler = getPublishedCourseQueryHandler;
        this.getCoursesByInstructorQueryHandler = getCoursesByInstructorQueryHandler;
        this.getAllCategoriesQueryHandler = getAllCategoriesQueryHandler;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> createCourse(
            @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateCourseCommand command = new CreateCourseCommand(
                request.title(),
                request.description(),
                request.price(),
                request.currency(),
                userDetails.getUserId(),
                request.category(),
                request.level()
        );

        Long courseId = createCourseCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(courseId, "Course"));
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateCourseCommand command = new UpdateCourseCommand(
                id,
                request.title(),
                request.description(),
                request.price(),
                request.currency(),
                request.category(),
                request.level(),
                userDetails.getUserId()
        );

        updateCourseCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Course updated successfully"));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<PublishCourseResponse> publishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PublishCourseCommand command = new PublishCourseCommand(id, userDetails.getUserId());
        CourseDTO courseDTO = publishCourseCommandHandler.handle(command);
        PublishCourseResponse response = PublishCourseResponse.from(courseDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> unpublishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UnpublishCourseCommand command = new UnpublishCourseCommand(id, currentUser.getUserId());
        unpublishCourseCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Course unpublished successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicCourseDetailsResponse> getCourseDetails(@PathVariable Long id) {
        return getCourseDetailsQueryHandler.handle(id)
                .map(PublicCourseDetailsResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<AckResponse> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteCourseCommand command = new DeleteCourseCommand(
                id,
                userDetails.getUserId()
        );

        deleteCourseCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("Course deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<PagedCoursesResponse> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllCoursesQuery command = new GetAllCoursesQuery(page, size);
        PagedCoursesDTO pagedCourses = getAllCoursesQueryHandler.handle(command);
        PagedCoursesResponse response = PagedCoursesResponse.from(pagedCourses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published")
    public ResponseEntity<PagedPublicCoursesResponse> getPublishedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseLevel level
    ){
        GetPublishedCoursesQuery command = new GetPublishedCoursesQuery(page, size, category, level);
        PagedPublicCoursesDTO dto = getPublishedCourseQueryHandler.handle(command);
        return ResponseEntity.ok(PagedPublicCoursesResponse.from(dto));
    }


    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<PagedCoursesResponse> getCoursesByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetCoursesByInstructorQuery command = new GetCoursesByInstructorQuery(
                instructorId,
                page,
                size
        );
        PagedCoursesDTO dto = getCoursesByInstructorQueryHandler.handle(command);
        return ResponseEntity.ok(PagedCoursesResponse.from(dto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = getAllCategoriesQueryHandler.handle();
        return ResponseEntity.ok(categories);
    }

}
