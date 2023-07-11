package de.andreaswillems.java.spring.demo.core.events;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findByAggregateIdOrderByTimestampAsc(UUID aggregateId);

    @Query("SELECT DISTINCT e.aggregateId FROM Event e")
    Collection<UUID> findDistinctAggregateIds();
}
