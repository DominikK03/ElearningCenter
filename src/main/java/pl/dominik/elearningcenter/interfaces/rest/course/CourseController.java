package pl.dominik.elearningcenter.interfaces.rest.course;


import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.*;
import pl.dominik.elearningcenter.application.course.dto.PagedCoursesDTO;
import pl.dominik.elearningcenter.application.course.input.*;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.infrastructure.security.CustomUserDetails;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.*;
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
    private final GetCourseDetailsUseCase getCourseDetailsUseCase;
    private final GetAllCoursesUseCase getAllCoursesUseCase;
    private final AddSectionUseCase addSectionUseCase;
    private final UpdateSectionUseCase updateSectionUseCase;
    private final DeleteSectionUseCase deleteSectionUseCase;
    private final AddLessonUseCase addLessonUseCase;
    private final UpdateLessonUseCase updateLessonUseCase;
    private final DeleteLessonUseCase deleteLessonUseCase;

    public CourseController(
            CreateCourseUseCase createCourseUseCase,
            UpdateCourseUseCase updateCourseUseCase,
            DeleteCourseUseCase deleteCourseUseCase,
            PublishCourseUseCase publishCourseUseCase,
            GetCourseDetailsUseCase getCourseDetailsUseCase,
            GetAllCoursesUseCase getAllCoursesUseCase,
            AddSectionUseCase addSectionUseCase,
            UpdateSectionUseCase updateSectionUseCase,
            DeleteSectionUseCase deleteSectionUseCase,
            AddLessonUseCase addLessonUseCase,
            UpdateLessonUseCase updateLessonUseCase,
            DeleteLessonUseCase deleteLessonUseCase
    ) {
        this.createCourseUseCase = createCourseUseCase;
        this.updateCourseUseCase = updateCourseUseCase;
        this.deleteCourseUseCase = deleteCourseUseCase;
        this.publishCourseUseCase = publishCourseUseCase;
        this.getCourseDetailsUseCase = getCourseDetailsUseCase;
        this.getAllCoursesUseCase = getAllCoursesUseCase;
        this.addSectionUseCase = addSectionUseCase;
        this.updateSectionUseCase = updateSectionUseCase;
        this.deleteSectionUseCase = deleteSectionUseCase;
        this.addLessonUseCase = addLessonUseCase;
        this.updateLessonUseCase = updateLessonUseCase;
        this.deleteLessonUseCase = deleteLessonUseCase;
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

    @PostMapping("/{courseId}/sections")

    public ResponseEntity<AckResponse> addSection(
            @PathVariable Long courseId,
            @RequestBody AddSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddSectionInput command = new AddSectionInput(
                courseId,
                request.title(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        Long sectionId = addSectionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(sectionId, "Section"));
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

    @PutMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<AckResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody UpdateSectionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateSectionInput command = new UpdateSectionInput(
                courseId,
                sectionId,
                request.title(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        updateSectionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.updated("Section"));
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<AckResponse> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteSectionInput command = new DeleteSectionInput(
                courseId,
                sectionId,
                userDetails.getUserId()
        );

        deleteSectionUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Section deleted successfully"));
    }

    @PostMapping("/{courseId}/sections/{sectionId}/lessons")
    public ResponseEntity<AckResponse> addLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody AddLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddLessonInput command = new AddLessonInput(
                courseId,
                sectionId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );
        Long lessonId = addLessonUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(AckResponse.created(lessonId, "Lesson"));

    }

    @PutMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}")
    public ResponseEntity<AckResponse> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @RequestBody UpdateLessonRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UpdateLessonInput command = new UpdateLessonInput(
                courseId,
                sectionId,
                lessonId,
                request.title(),
                request.content(),
                request.orderIndex(),
                userDetails.getUserId()
        );

        updateLessonUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.updated("Lesson"));
    }

    @DeleteMapping("/{courseId}/sections/{sectionId}/lessons/{lessonId}")
    public ResponseEntity<AckResponse> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        DeleteLessonInput command = new DeleteLessonInput(
                courseId,
                sectionId,
                lessonId,
                userDetails.getUserId()
        );
        deleteLessonUseCase.execute(command);
        return ResponseEntity.ok(AckResponse.success("Lesson deleted successfully"));
    }


}
