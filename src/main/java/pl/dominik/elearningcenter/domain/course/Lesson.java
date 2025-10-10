package pl.dominik.elearningcenter.domain.course;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materials = new ArrayList<>();

    protected Lesson(){}

    public Lesson(String title, String content, Integer orderIndex){
        this.title = title;
        this.content = content;
        this.orderIndex = orderIndex;
    }

    void setSection(Section section){
        this.section = section;
    }

    public void addMaterial(Material material){
        materials.add(material);
        material.setLesson(this);
    }

    public void removeMaterial(Long materialId){
        materials.removeIf(m -> m.getId().equals(materialId));
    }

    public void setVideoUrl(String videoUrl){
        this.videoUrl = videoUrl;
    }
    public void setDurationMinutes(Integer durationMinutes){
        this.durationMinutes = durationMinutes;
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Section getSection() {
        return section;
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
