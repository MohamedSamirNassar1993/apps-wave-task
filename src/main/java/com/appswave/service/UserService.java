package com.appswave.service;

import com.appswave.exception.UserException;
import com.appswave.model.entity.User;
import com.appswave.model.enums.RoleEnum;
import com.appswave.model.payload.request.CreateUserRequest;
import com.appswave.model.payload.response.UserDtoResponse;
import com.appswave.repository.RoleRepository;
import com.appswave.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public UserDtoResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(true, "Error: Email is already in use!", request);
        }

        User user = new User();
        user.setRole(roleRepository.findByName(RoleEnum.valueOf(request.getRole().toUpperCase())));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setBirthDate(request.getBirthDate());
        userRepository.save(user);

        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(user.getId());
        userDtoResponse.setBirthDate(user.getBirthDate());
        userDtoResponse.setFullName(user.getFullName());
        userDtoResponse.setPassword(user.getPassword());
        userDtoResponse.setEmail(user.getEmail());
        return userDtoResponse;
    }


    public UserDtoResponse findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException(true, "Error: There is no User for this Id!!", id));
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(user.getId());
        userDtoResponse.setBirthDate(user.getBirthDate());
        userDtoResponse.setFullName(user.getFullName());
        userDtoResponse.setPassword(user.getPassword());
        userDtoResponse.setEmail(user.getEmail());
        return userDtoResponse;
    }

    public List<UserDtoResponse> findAllUsers() {
        List<User> usersList = userRepository.findAll();
        List<UserDtoResponse> users = new ArrayList<>();
        for(User user : usersList){
            UserDtoResponse userDtoResponse = new UserDtoResponse();
            userDtoResponse.setId(user.getId());
            userDtoResponse.setBirthDate(user.getBirthDate());
            userDtoResponse.setFullName(user.getFullName());
            userDtoResponse.setPassword(user.getPassword());
            userDtoResponse.setEmail(user.getEmail());
            users.add(userDtoResponse);
        }
        return users;
    }

    public UserDtoResponse updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException(true, "Error: There is no User for this Id!!", id));
        if(request.getEmail().equals(user.getEmail())){
            user.setRole(roleRepository.findByName(RoleEnum.valueOf(request.getRole().toUpperCase())));
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setBirthDate(request.getBirthDate());
            userRepository.save(user);
        }else{
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserException(true, "Error: Email is already in use!", request);
            }
            user.setRole(roleRepository.findByName(RoleEnum.valueOf(request.getRole().toUpperCase())));
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setBirthDate(request.getBirthDate());
            userRepository.save(user);
        }
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(user.getId());
        userDtoResponse.setBirthDate(user.getBirthDate());
        userDtoResponse.setFullName(user.getFullName());
        userDtoResponse.setPassword(user.getPassword());
        userDtoResponse.setEmail(user.getEmail());
        return userDtoResponse;
    }

    public UserDtoResponse deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserException(true, "Error: There is no User for this Id!!", id));
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(user.getId());
        userDtoResponse.setBirthDate(user.getBirthDate());
        userDtoResponse.setFullName(user.getFullName());
        userDtoResponse.setPassword(user.getPassword());
        userDtoResponse.setEmail(user.getEmail());
        userRepository.delete(user);
        return userDtoResponse;
    }
}