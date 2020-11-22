package org.khrypkom.eventsourcing.infrastructure.task;

import lombok.AllArgsConstructor;
import org.khrypkom.eventsourcing.Id;
import org.khrypkom.eventsourcing.event.task.Task;
import org.khrypkom.eventsourcing.event.task.TaskRepository;
import org.khrypkom.eventsourcing.infrastructure.store.SpringDataJpaEventStore;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JpaTaskRepository implements TaskRepository {
    private final SpringDataTaskRepository taskRepository;
    private final SpringDataJpaEventStore eventStore;

    @Override
    public void scheduleTasks(Collection<Task> tasks) {
        List<TaskEntity> taskEntities = tasks.stream().map(this::toEntity).collect(Collectors.toList());
        taskRepository.saveAll(taskEntities);
    }

    private TaskEntity toEntity(Task task) {
        return TaskEntity.builder()
                .taskId(task.getId().getValue())
                .eventId(task.getEvent().getId().getValue())
                .createDate(task.getCreateDate())
                .status(task.getStatus())
                .executorId(task.getExecutorId() == null ? null : task.getExecutorId().getValue())
                .completeDate(task.getCompleteDate())
                .build();
    }

    @Override
    public List<Task> lockAndFetchOutstandingTasks(Id executorId) {
        //Probably need to be done by some db query to ensure atomicity... will do like this for now
        List<TaskEntity> tasks = taskRepository.findIdleTasks();
        List<TaskEntity> lockedTasks = tasks.stream()
                .map(taskEntity -> lockTasks(taskEntity, executorId))
                .collect(Collectors.toList());
        taskRepository.saveAll(lockedTasks);
        return lockedTasks.stream().map(this::toTask).collect(Collectors.toList());
    }

    @Override
    public void updateTaskStatus(Collection<Task> tasks) {
        taskRepository.saveAll(tasks.stream().map(this::toEntity).collect(Collectors.toList()));
    }

    private Task toTask(TaskEntity taskEntity) {
        return Task.builder()
                .id(new Id(taskEntity.getTaskId()))
                .event(eventStore.getEventById(taskEntity.getEventId()))
                .status(taskEntity.getStatus())
                .createDate(taskEntity.getCreateDate())
                .completeDate(taskEntity.getCompleteDate())
                .executorId(new Id(taskEntity.getExecutorId()))
                .build();
    }

    private TaskEntity lockTasks(TaskEntity taskEntity, Id executorId) {
        taskEntity.setStatus(Task.Status.LOCKED);
        taskEntity.setExecutorId(executorId.getValue());
        return taskEntity;
    }
}
