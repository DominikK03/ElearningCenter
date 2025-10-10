package pl.dominik.elearningcenter.interfaces.rest.course;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.course.AddSectionUseCase;
import pl.dominik.elearningcenter.application.course.CreateCourseUseCase;
import pl.dominik.elearningcenter.application.course.GetCourseDetailsUseCase;
import pl.dominik.elearningcenter.application.course.PublishCourseUseCase;
import pl.dominik.elearningcenter.application.course.dto.AddSectionCommand;
import pl.dominik.elearningcenter.application.course.dto.CourseDTO;
import pl.dominik.elearningcenter.application.course.dto.CreateCourseCommand;
import pl.dominik.elearningcenter.interfaces.rest.course.dto.AddSectionRequest;
import pl.dominik.elearningcenter.interfaces.rest.course.dto.CourseResponse;
import pl.dominik.elearningcenter.interfaces.rest.course.dto.CreateCourseRequest;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final AddSectionUseCase addSectionUseCase;
    private final CreateCourseUseCase createCourseUseCase;
    private final GetCourseDetailsUseCase getCourseDetailsUseCase;
    private final PublishCourseUseCase publishCourseUseCase;

    public CourseController(AddSectionUseCase addSectionUseCase, CreateCourseUseCase createCourseUseCase, GetCourseDetailsUseCase getCourseDetailsUseCase, PublishCourseUseCase publishCourseUseCase) {
        this.addSectionUseCase = addSectionUseCase;
        this.createCourseUseCase = createCourseUseCase;
        this.getCourseDetailsUseCase = getCourseDetailsUseCase;
        this.publishCourseUseCase = publishCourseUseCase;
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
