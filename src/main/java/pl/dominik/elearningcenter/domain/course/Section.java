package pl.dominik.elearningcenter.domain.course;

import jakarta.persistence.*;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    protected Section(){}

    public Section(String title, Integer orderIndex){
        this.title = title;
        this.orderIndex = orderIndex;
    }

    void setCourse(Course course){
        this.course = course;
    }

    public void updateTitle(String newTitle){
        if(newTitle == null || newTitle.isBlank()){
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        this.title = newTitle;
    }
    public void updateOrderIndex(Integer newOrderIndex){
        if (newOrderIndex == null || newOrderIndex < 0){
            throw new IllegalArgumentException("Order index must be >= 0");
        }
        this.orderIndex = newOrderIndex;
    }

    public void addLesson(Lesson lesson){
        lessons.add(lesson);
        lesson.setSection(this);
    }

    public Lesson findLesson(Long lessonId){
        return lessons.stream()
                .filter(l -> l.getId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Lesson not found: " + lessonId));
    }

    public void removeLesson(Long lessonId){
        lessons.removeIf(l -> l.getId().equals(lessonId));
    }

    public boolean hasLessons(){
        return !lessons.isEmpty();
    }

    public int getLessonsCount(){
        return lessons.size();
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Course getCourse() {
        return course;
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
