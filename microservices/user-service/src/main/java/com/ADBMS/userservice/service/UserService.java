package com.ADBMS.userservice.service;

import com.ADBMS.userservice.dto.UserRequest;
import com.ADBMS.userservice.dto.UserResponse;
import com.ADBMS.userservice.model.User;
import com.ADBMS.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserRequest userRequest){
        User user = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .contact(userRequest.getContact())
                .password(userRequest.getPassword())
                .build();
        userRepository.save(user);
        log.info("User {} is saved", user.getId());
    }

    public List<UserResponse> getAllUsers(){
        List<User> users = userRepository.findAll();

        return users.stream().map(this::mapToUserResponse).toList();
    }

    private UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .contact(user.getContact())
                .password(user.getPassword())
                .build();
    }
}
