package pl.dominik.elearningcenter.domain.course;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private MaterialType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    protected Material(){}

    public Material(String title, String fileUrl, MaterialType fileType){
        this.title = title;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    void setLesson(Lesson lesson){
        this.lesson = lesson;
    }
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public MaterialType getFileType() {
        return fileType;
    }

    public Lesson getLesson() {
        return lesson;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(id, material.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
