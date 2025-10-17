package pl.dominik.elearningcenter.interfaces.rest.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.command.CreateCourseUseCase;
import pl.dominik.elearningcenter.application.course.command.UpdateCourseUseCase;
import pl.dominik.elearningcenter.application.course.command.DeleteCourseUseCase;
import pl.dominik.elearningcenter.application.course.command.PublishCourseUseCase;
import pl.dominik.elearningcenter.application.course.command.UnpublishCourseUseCase;
import pl.dominik.elearningcenter.application.course.query.GetCourseDetailsUseCase;
import pl.dominik.elearningcenter.application.course.query.GetAllCoursesUseCase;
import pl.dominik.elearningcenter.application.course.query.GetPublishedCourseUseCase;
import pl.dominik.elearningcenter.application.course.query.GetCoursesByInstructorUseCase;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.input.CreateCourseInput;
import pl.dominik.elearningcenter.application.course.input.UpdateCourseInput;
import pl.dominik.elearningcenter.application.course.input.DeleteCourseInput;
import pl.dominik.elearningcenter.application.course.input.UnpublishCourseInput;
import pl.dominik.elearningcenter.application.course.input.GetAllCoursesInput;
import pl.dominik.elearningcenter.application.course.input.GetPublishedCoursesInput;
import pl.dominik.elearningcenter.application.course.input.GetCoursesByInstructorInput;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.CreateCourseRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.request.UpdateCourseRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.response.CourseResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PagedCoursesResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.response.PublishCourseResponse;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CreateCourseUseCase createCourseUseCase;
    private final UpdateCourseUseCase updateCourseUseCase;
    private final DeleteCourseUseCase deleteCourseUseCase;
    private final PublishCourseUseCase publishCourseUseCase;
    private final UnpublishCourseUseCase unpublishCourseUseCase;
    private final GetCourseDetailsUseCase getCourseDetailsUseCase;
    private final GetAllCoursesUseCase getAllCoursesUseCase;
    private final GetPublishedCourseUseCase getPublishedCourseUseCase;
    private final GetCoursesByInstructorUseCase getCoursesByInstructorUseCase;

    public CourseController(
            CreateCourseUseCase createCourseUseCase,
            UpdateCourseUseCase updateCourseUseCase,
            DeleteCourseUseCase deleteCourseUseCase,
            PublishCourseUseCase publishCourseUseCase,
            UnpublishCourseUseCase unpublishCourseUseCase,
            GetCourseDetailsUseCase getCourseDetailsUseCase,
            GetAllCoursesUseCase getAllCoursesUseCase,
            GetPublishedCourseUseCase getPublishedCourseUseCase,
            GetCoursesByInstructorUseCase getCoursesByInstructorUseCase
    ) {
        this.createCourseUseCase = createCourseUseCase;
        this.updateCourseUseCase = updateCourseUseCase;
        this.deleteCourseUseCase = deleteCourseUseCase;
        this.publishCourseUseCase = publishCourseUseCase;
        this.unpublishCourseUseCase = unpublishCourseUseCase;
        this.getCourseDetailsUseCase = getCourseDetailsUseCase;
        this.getAllCoursesUseCase = getAllCoursesUseCase;
        this.getPublishedCourseUseCase = getPublishedCourseUseCase;
        this.getCoursesByInstructorUseCase = getCoursesByInstructorUseCase;
    }

    @PostMapping
    public ResponseEntity<AckResponse> createCourse(
            @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateCourseInput command = new CreateCourseInput(
                request.title(),
                request.description(),
                request.price(),
                request.currency(),
                userDetails.getUserId(),
                request.category(),
                request.level()
        );

        Long courseId = createCourseUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(courseId, "Course"));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<AckResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateCourseInput command = new UpdateCourseInput(
                id,
                request.title(),
                request.description(),
                request.price(),
                request.currency(),
                request.category(),
                request.level(),
                userDetails.getUserId()
        );

        updateCourseUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Course updated successfully"));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<PublishCourseResponse> publishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDTO courseDTO = publishCourseUseCase.execute(id, userDetails.getUserId());
        PublishCourseResponse response = PublishCourseResponse.from(courseDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<AckResponse> unpublishCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UnpublishCourseInput command = new UnpublishCourseInput(id, currentUser.getUserId());
        unpublishCourseUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Course unpublished successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseDetails(@PathVariable Long id) {
        CourseDTO courseDTO = getCourseDetailsUseCase.execute(id);
        CourseResponse response = CourseResponse.from(courseDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AckResponse> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteCourseInput command = new DeleteCourseInput(
                id,
                userDetails.getUserId()
        );

        deleteCourseUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Course deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<PagedCoursesResponse> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetAllCoursesInput command = new GetAllCoursesInput(page, size);
        PagedCoursesDTO pagedCourses = getAllCoursesUseCase.execute(command);
        PagedCoursesResponse response = PagedCoursesResponse.from(pagedCourses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/published")
    public ResponseEntity<PagedCoursesResponse> getPublishedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        GetPublishedCoursesInput commmand = new GetPublishedCoursesInput(page, size);
        PagedCoursesDTO dto = getPublishedCourseUseCase.execute(commmand);
        return ResponseEntity.ok(PagedCoursesResponse.from(dto));
    }


    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<PagedCoursesResponse> getCoursesByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GetCoursesByInstructorInput command = new GetCoursesByInstructorInput(
                instructorId,
                page,
                size
        );
        PagedCoursesDTO dto = getCoursesByInstructorUseCase.execute(command);
        return ResponseEntity.ok(PagedCoursesResponse.from(dto));
    }



}
