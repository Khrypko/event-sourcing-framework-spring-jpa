package org.khrypkom.eventsourcing.infrastructure.task;

import lombok.*;
import org.khrypkom.eventsourcing.event.task.Task;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "company_event_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    @Column(name = "task_id")
    private String taskId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "complete_date")
    private Instant completeDate;

    @Column(name = "executor_id")
    private String executorId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Task.Status status;

}
