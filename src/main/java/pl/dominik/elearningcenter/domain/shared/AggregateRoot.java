package pl.dominik.elearningcenter.domain.shared;

import java.util.Objects;

public abstract class AggregateRoot<ID> {
    protected ID id;

    protected AggregateRoot() {}
    protected AggregateRoot(ID id){
        this.id = id;
    }
    public ID getId(){
        return id;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(id, that.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}
