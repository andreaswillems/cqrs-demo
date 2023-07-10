package de.andreaswillems.java.spring.demo.core.read.queries;

import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadModelTaskRepository extends CrudRepository<ReadModelTask, Long> {
}
