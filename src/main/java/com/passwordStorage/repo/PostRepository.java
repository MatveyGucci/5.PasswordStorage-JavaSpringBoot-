package com.passwordStorage.repo;

import com.passwordStorage.models.Storage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Storage,Long> {
}
