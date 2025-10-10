package pl.dominik.elearningcenter.domain.course.valueobject;


import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public final class CourseTitle {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 200;
    private String value;

    protected CourseTitle(){}

    public CourseTitle(String value){
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("Course Title cannot be null or blank");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH){
            throw new IllegalArgumentException("Course title must be between 3 and 200 characters");
        }
        this.value = value;
    }

    public String getValue(){
        return value;
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseTitle courseTitle = (CourseTitle) o;
        return Objects.equals(value, courseTitle.value);
    }
    @Override
    public int hashCode(){
        return Objects.hash(value);
    }
    @Override
    public String toString(){
        return value;
    }
}
