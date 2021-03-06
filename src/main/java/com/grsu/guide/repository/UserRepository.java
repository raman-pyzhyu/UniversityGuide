package com.grsu.guide.repository;

import com.grsu.guide.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    User findByUserName(String namePage);
    User findUserById(Long id);

}