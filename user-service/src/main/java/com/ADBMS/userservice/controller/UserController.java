package com.ADBMS.userservice.controller;

import com.ADBMS.userservice.dto.UserCreateDTO;
import com.ADBMS.userservice.dto.UserResponseDTO;
import com.ADBMS.userservice.dto.UserUpdateDTO;
import com.ADBMS.userservice.model.User;
import com.ADBMS.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserCreateDTO userCreate){
        return userService.createUser(userCreate);
    }

    @GetMapping("/{userID}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO getUserByUserId(@PathVariable(name = "userID") String userID){
        return userService.getUserByUserId(userID);
    }

    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO updateByUserName(@PathVariable String username, @RequestBody UserUpdateDTO userUpdateDTO){
        return userService.updateByUserName(username, userUpdateDTO);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUserByName(@PathVariable String username){
        return userService.deleteUserByName(username);
    }

}
