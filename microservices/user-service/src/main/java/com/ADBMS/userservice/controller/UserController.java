package com.ADBMS.userservice.controller;

import com.ADBMS.userservice.dto.UserCreate;
import com.ADBMS.userservice.dto.UserResponse;
import com.ADBMS.userservice.model.User;
import com.ADBMS.userservice.service.UserService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserCreate userCreate){

        return userService.createUser(userCreate);
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserByUsername(@PathVariable String username){
        return userService.getUserByUsername(username);
    }



   /* @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }*/
}
