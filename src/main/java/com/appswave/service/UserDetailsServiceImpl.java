package com.appswave.service;

import java.util.Locale;

import com.appswave.model.dto.UserDTO;
import com.appswave.model.enums.RoleEnum;
import com.appswave.model.entity.User;
import com.appswave.repository.RoleRepository;
import com.appswave.repository.UserRepository;
import com.appswave.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("User does not exist!");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setFullName(user.getFullName());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setRole(user.getRole().getName().name());
        return userDTO;
    }

    public UserDTO registerUser(UserDTO userDTO) throws UserException {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserException(true, "Error: Email is already in use!", userDTO);
        }
        User user = new User();
        user.setRole(roleRepository.findByName(RoleEnum.valueOf(userDTO.getRole().toUpperCase())));
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setBirthDate(userDTO.getBirthDate());
        user = userRepository.save(user);
        userDTO.setId(user.getId());
        return userDTO;
    }

}