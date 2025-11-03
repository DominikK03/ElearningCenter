package pl.dominik.elearningcenter.domain.course;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;
import pl.dominik.elearningcenter.domain.course.exception.CourseNotPublishedException;
import pl.dominik.elearningcenter.domain.course.exception.SectionNotFoundException;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseDescription;
import pl.dominik.elearningcenter.domain.course.valueobject.CourseTitle;
import pl.dominik.elearningcenter.domain.quiz.Quiz;
import pl.dominik.elearningcenter.domain.shared.AggregateRoot;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;
import pl.dominik.elearningcenter.domain.shared.valueobject.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_course_published", columnList = "published"),
        @Index(name = "idx_course_category", columnList = "category"),
        @Index(name = "idx_course_level", columnList = "level"),
        @Index(name = "idx_course_instructor", columnList = "instructor_id"),
        @Index(name = "idx_course_published_category", columnList = "published, category"),
        @Index(name = "idx_course_published_level", columnList = "published, level"),
        @Index(name = "idx_course_published_category_level", columnList = "published, category, level")
})
public class Course extends AggregateRoot<Long> {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false, length = 200))
    private CourseTitle title;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "description", columnDefinition = "TEXT"))
    private CourseDescription description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price_amount", nullable = false, precision = 10, scale = 2)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "price_currency", nullable = false, length = 3))
    })
    private Money price;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(nullable = false, length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel level;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Section> sections = new ArrayList<>();

    @OneToOne(mappedBy = "course", fetch = FetchType.EAGER)
    private Quiz quiz;

    @Transient
    private Long quizId;

    @Formula("(SELECT COUNT(*) FROM sections s WHERE s.course_id = id)")
    private int sectionsCount;

    @Formula("(SELECT COALESCE(SUM((SELECT COUNT(*) FROM lessons l WHERE l.section_id = s.id)), 0) FROM sections s WHERE s.course_id = id)")
    private int totalLessonsCount;

    protected Course() {
        super();
    }

    private Course(CourseTitle title, CourseDescription description, Money price, String category, CourseLevel level, Long instructorId){
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.level = level;
        this.instructorId = instructorId;
        this.createdAt = LocalDateTime.now();
        this.published = false;
    }

    public static Course create(CourseTitle title, CourseDescription description, Money price, String category, CourseLevel level, Long instructorId){
        return new Course(title, description, price, category, level, instructorId);
    }

    public static Course create(String title, String description, double priceAmount, String priceCurrency,  Long instructorId, String category, CourseLevel level){
        return new Course(
                new CourseTitle(title),
                new CourseDescription(description),
                Money.of(java.math.BigDecimal.valueOf(priceAmount), priceCurrency),
                category,
                level,
                instructorId
        );
    }

    public void updateTitle(CourseTitle newTitle){
        if (newTitle == null){
            throw new IllegalArgumentException("Title cannot be null");
        }
        this.title = newTitle;
    }
    public void updateDescription(CourseDescription newDescription){
        if (newDescription == null){
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.description = newDescription;
    }
    public void updateCategory(String newCategory){
        if (newCategory == null || newCategory.isBlank()){
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = newCategory;
    }
    public void updateLevel(CourseLevel newLevel) {
        if (newLevel == null) {
            throw new IllegalArgumentException("Level cannot be null");
        }
        this.level = newLevel;
    }

    public void publish(){
        if(!canBePublished()){
            throw new CourseNotPublishedException("Course must have at least one section with lessons to be published");
        }
        this.published = true;
    }

    public void unpublish(){
        this.published = false;
    }

    public void ensureOwnedBy(Long userId){
        if(!isOwnedBy(userId)){
            throw new DomainException("Only course owner can perform this action");
        }
    }

    public void addSection(Section section){
        sections.add(section);
        section.setCourse(this);
    }
    public Section findSection(Long sectionId){
        return sections.stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new SectionNotFoundException("Section not found: " + sectionId));
    }

    public void removeSection(Long sectionId){
        Section section = findSection(sectionId);
        sections.remove(section);
    }

    public void reorderSection(Long sectionId, int newOrderIndex) {
        Section section = findSection(sectionId);
        int currentIndex = section.getOrderIndex();

        if (currentIndex == newOrderIndex) {
            return;
        }

        sections.remove(section);

        if (newOrderIndex < 0) {
            newOrderIndex = 0;
        }
        if (newOrderIndex > sections.size()) {
            newOrderIndex = sections.size();
        }

        sections.add(newOrderIndex, section);

        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).updateOrderIndex(i);
        }
    }

    public void updatePrice(Money newPrice){
        if (newPrice == null){
            throw new IllegalArgumentException("Price cannot be null");
        }
        this.price = newPrice;
    }

    public void updateThumbnail(String thumbnailUrl){
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean canBePublished(){
        return !sections.isEmpty() && sections.stream().anyMatch(Section::hasLessons);
    }

    public boolean isOwnedBy(Long instructorId){
        return this.instructorId.equals(instructorId);
    }

    public int getSectionsCount(){
        return sectionsCount;
    }

    public int getTotalLessonsCount(){
        return totalLessonsCount;
    }

    public CourseTitle getTitle(){
        return title;
    }
    public CourseDescription getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getCategory() {
        return category;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public boolean isPublished() {
        return published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public Long getQuizId() {
        if (quizId != null) {
            return quizId;
        }
        return quiz != null ? quiz.getId() : null;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public pl.dominik.elearningcenter.domain.quiz.Quiz getQuiz() {
        return quiz;
    }
}
