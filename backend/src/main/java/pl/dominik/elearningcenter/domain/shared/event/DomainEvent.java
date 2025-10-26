package pl.dominik.elearningcenter.domain.shared.event;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events.
 * Domain events represent something that happened in the domain that domain experts care about.
 */
public interface DomainEvent {

    /**
     * Returns the timestamp when this event occurred.
     */
    LocalDateTime occurredOn();
}
