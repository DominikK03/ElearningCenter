package pl.dominik.elearningcenter.interfaces.rest.course;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.*;
import pl.dominik.elearningcenter.application.course.command.AddSectionCommand;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.command.CreateCourseCommand;
import pl.dominik.elearningcenter.interfaces.rest.course.request.AddSectionRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.response.CourseResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.request.CreateCourseRequest;

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
    private final DeleteLessonUseCase deleteLessonUseCase

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
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CreateCourseRequest request) {
        CreateCourseCommand command = new CreateCourseCommand(
                request.title(),
                request.description(),
                request.price(),
                request.currency(),
                request.instructorId(),
                request.category(),
                request.level()
        );

        CourseDTO courseDTO = createCourseUseCase.execute(command);

        CourseResponse response = CourseResponse.from(courseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CourseResponse> publishCourse(@PathVariable Long id) {

        CourseDTO courseDTO = publishCourseUseCase.execute(id);
        CourseResponse response = CourseResponse.from(courseDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/sections")
    public ResponseEntity<CourseResponse> addSection(@PathVariable Long courseId, @RequestBody AddSectionRequest request){
        AddSectionCommand command = new AddSectionCommand(
                courseId,
                request.title(),
                request.orderIndex()
        );

        CourseDTO courseDTO = addSectionUseCase.execute(command);
        CourseResponse response = CourseResponse.from(courseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseDetails(@PathVariable Long id){
        CourseDTO courseDTO = getCourseDetailsUseCase.execute(id);
        CourseResponse response = CourseResponse.from(courseDTO);

        return ResponseEntity.ok(response);
    }
}
