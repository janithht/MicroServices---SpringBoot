package com.ADBMS.userservice.service;

import com.ADBMS.userservice.dto.UserCreateDTO;
import com.ADBMS.userservice.dto.UserResponseDTO;
import com.ADBMS.userservice.dto.UserUpdateDTO;
import com.ADBMS.userservice.exception.UserNotFoundException;
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

    public User createUser(UserCreateDTO userRequest){
        User user = User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .contact(userRequest.getContact())
                .build();
        User createdUser = userRepository.save(user);
        log.info("User {} is saved", createdUser.getId());
        return createdUser;
    }

    public UserResponseDTO getUserByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User is not found with ID " + userId)
        );

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setUserID(user.getId());
        userResponse.setUsername(user.getName());
        userResponse.setContact(user.getContact());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }

    public UserResponseDTO updateByUserName(String username, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findByName(username);

        if (existingUser == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }

        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setName(userUpdateDTO.getName());
        existingUser.setContact(userUpdateDTO.getContact());

        User updatedUser = userRepository.save(existingUser);

        UserResponseDTO userResponseDTO = mapUserToResponseDTO(updatedUser);

        return userResponseDTO;
    }

    private UserResponseDTO mapUserToResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserID(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setUsername(user.getName());
        userResponseDTO.setContact(user.getContact());
        return userResponseDTO;
    }

    public String deleteUserByName(String username) {

        User existingUser = userRepository.findByName(username);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with username " + username + " not found");
        }
        userRepository.deleteUserByName(username);
        return "User deleted successfully";
    }
}
