package pl.dominik.elearningcenter.interfaces.rest.common;

public record AckResponse(
        String message,
        Long resourceId
) {
    public static AckResponse created(Long id, String resourceType){
        return new AckResponse(
                resourceType + " created successfully",
                id
        );
    }

    public static AckResponse updated(String resourceType){
        return new AckResponse(
                resourceType + " updated successfully",
                null
        );
    }

    public static AckResponse success(String message) {
        return new AckResponse(message, null);
    }

    public static AckResponse error(String message) {
        return new AckResponse(message, null);
    }

    public static AckResponse of(String message, Long id) {
        return new AckResponse(message, id);
    }
}
