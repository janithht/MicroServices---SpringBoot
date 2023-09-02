package com.ADBMS.userservice.service;

import com.ADBMS.userservice.dto.UserCreate;
import com.ADBMS.userservice.dto.UserResponse;
import com.ADBMS.userservice.model.User;
import com.ADBMS.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User createUser(UserCreate userRequest){
        User user = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .contact(userRequest.getContact())
                .build();
        User createdUser = userRepository.save(user);
        log.info("User {} is saved", createdUser.getId());
        return createdUser;
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByName(username);

        if(user == null){
            throw new IllegalArgumentException("User not Found");

        }

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setContact(user.getContact());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }

    /*public List<UserResponse> getAllUsers(){
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
    }*/
}
