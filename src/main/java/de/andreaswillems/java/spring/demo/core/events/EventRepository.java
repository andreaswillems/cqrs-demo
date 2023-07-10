package de.andreaswillems.java.spring.demo.core.events;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<BaseEvent, Long> {
}
