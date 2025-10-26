package pl.dominik.elearningcenter.domain.course.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public final class CourseDescription {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 5000;

    private String value;

    protected CourseDescription(){}

    public CourseDescription(String value) {
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Course Description must be between 10 and 5000 characters");
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
        CourseDescription courseDescription = (CourseDescription) o;
        return Objects.equals(value, courseDescription.value);
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
