package org.khrypkom.eventsourcing.infrastructure.store;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "company_events")
@IdClass(EventEntity.EventId.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @Column(name = "aggregate_id")
    private String aggregateId;

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "class_name")
    private String className;

    @Type(type = "jsonb")
    @Column(name = "event_data")
    private String event;

    @Getter
    @Setter
    public static class EventId implements Serializable {
        private String aggregateId;
        private String eventId;
    }

}
