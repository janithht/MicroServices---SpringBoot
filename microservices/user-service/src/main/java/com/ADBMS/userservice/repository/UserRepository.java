package com.ADBMS.userservice.repository;

import com.ADBMS.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);

    User deleteUserByName(String name);


}
