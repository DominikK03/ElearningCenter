package pl.dominik.elearningcenter.domain.enrollment.valueobject;

import jakarta.persistence.Embeddable;
import pl.dominik.elearningcenter.domain.shared.exception.DomainException;

import java.util.Objects;

@Embeddable
public class Progress {

    private Integer percentage;

    protected Progress() {
    }

    public Progress(Integer percentage) {
        if (percentage == null) {
            throw new DomainException("Progress percentage cannot be null");
        }
        if (percentage < 0 || percentage > 100) {
            throw new DomainException("Progress must be between 0 and 100, got: " + percentage);
        }
        this.percentage = percentage;
    }

    public static Progress of(int percentage) {
        return new Progress(percentage);
    }

    public static Progress zero() {
        return new Progress(0);
    }

    public static Progress completed() {
        return new Progress(100);
    }

    public boolean isCompleted() {
        return percentage == 100;
    }

    public Progress increase(int amount) {
        int newPercentage = Math.min(100, this.percentage + amount);
        return new Progress(newPercentage);
    }

    public Integer getPercentage() {
        return percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Progress progress = (Progress) o;
        return Objects.equals(percentage, progress.percentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage);
    }

    @Override
    public String toString() {
        return percentage + "%";
    }
}
