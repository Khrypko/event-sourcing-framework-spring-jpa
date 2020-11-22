package org.khrypkom.eventsourcing.infrastructure.task;

import org.khrypkom.eventsourcing.event.task.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpringDataTaskRepository extends CrudRepository<TaskEntity, String> {

    default List<TaskEntity> findIdleTasks(){
        return findTaskEntitiesByStatusIsAndExecutorIdNull(Task.Status.IDLE);
    }

    List<TaskEntity> findTaskEntitiesByStatusIsAndExecutorIdNull(Task.Status status);
}
