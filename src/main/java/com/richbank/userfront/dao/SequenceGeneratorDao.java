package com.richbank.userfront.dao;

import com.richbank.userfront.domain.SequenceGenerator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceGeneratorDao extends CrudRepository<SequenceGenerator, Long> {
    SequenceGenerator findByName(String name);
}
