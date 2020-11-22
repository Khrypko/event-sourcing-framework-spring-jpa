package org.khrypkom.eventsourcing.infrastructure.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.khrypkom.eventsourcing.Id;
import org.khrypkom.eventsourcing.event.Event;
import org.khrypkom.eventsourcing.event.store.TaskBasedEventStore;
import org.khrypkom.eventsourcing.event.task.scheduler.TaskScheduler;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SpringDataJpaEventStore extends TaskBasedEventStore {

    private final ObjectMapper objectMapper;
    private final JpaEventRepository eventRepository;

    public SpringDataJpaEventStore(TaskScheduler scheduler, ObjectMapper objectMapper, JpaEventRepository eventRepository) {
        super(scheduler);
        this.objectMapper = objectMapper;
        this.eventRepository = eventRepository;
    }

    @Override
    public Collection<Event> getEvents(Id aggregateId) {
        return eventRepository.findEventsByAggregateId(aggregateId.getValue()).stream()
                .map(eventEntity -> deserializeEvent(eventEntity, getClassFromEntity(eventEntity)))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Event deserializeEvent(EventEntity eventEntity, Class clazz) {
        return (Event) objectMapper.readValue(eventEntity.getEvent(), clazz);
    }

    @SneakyThrows
    private Class getClassFromEntity(EventEntity eventEntity) {
        return Class.forName(eventEntity.getClassName());
    }

    @Override
    protected void doSaveEvents(Collection<Event> events) {
        List<EventEntity> eventEntities = events.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        eventRepository.saveAll(eventEntities);
    }

    private EventEntity toEntity(Event event) {
        return EventEntity.builder()
                .aggregateId(event.getAggregateId().getValue())
                .className(event.getClass().getName())
                .timestamp(event.getTimestamp())
                .eventId(event.getId().getValue())
                .event(serializeEvent(event))
                .build();
    }

    @SneakyThrows
    private String serializeEvent(Event event) {
        return objectMapper.writeValueAsString(event);
    }

    public Event getEventById(String eventId) {
        EventEntity eventEntity = eventRepository.findEventEntityByEventId(eventId);
        return deserializeEvent(eventEntity, getClassFromEntity(eventEntity));
    }
}
