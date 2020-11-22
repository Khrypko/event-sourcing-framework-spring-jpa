package org.khrypkom.eventsourcing.infrastructure.store;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaEventRepository extends CrudRepository<EventEntity, EventEntity.EventId> {

    List<EventEntity> findEventsByAggregateId(String aggregateId);

    EventEntity findEventEntityByEventId(String eventId);

}
