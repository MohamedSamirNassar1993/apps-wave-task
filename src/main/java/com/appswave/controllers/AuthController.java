package com.appswave.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.appswave.exception.TokenRefreshException;
import com.appswave.repository.RoleRepository;
import com.appswave.repository.UserRepository;
import com.appswave.jwt.JwtUtils;
import com.appswave.service.RefreshTokenService;
import com.appswave.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appswave.model.entity.RoleEnum;
import com.appswave.model.entity.RefreshToken;
import com.appswave.model.entity.Role;
import com.appswave.model.entity.User;
import com.appswave.model.payload.request.LoginRequest;
import com.appswave.model.payload.request.SignupRequest;
import com.appswave.model.payload.response.UserInfoResponse;
import com.appswave.model.payload.response.MessageResponse;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
            .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
            .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getRole(), userDetails.getBirthDate()));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User();
    user.setFullName(signUpRequest.getFullName());
    user.setPassword(signUpRequest.getPassword());
    user.setEmail(signUpRequest.getEmail());
    user.setBirthDate(signUpRequest.getBirthDate());
    Role userRole = roleRepository.findByName(RoleEnum.NORMAL).get();
    user.setRole(userRole);
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!principle.toString().equals("anonymousUser")) {
      Long userId = ((UserDetailsImpl) principle).getId();
      refreshTokenService.deleteByUserId(userId);
    }
    
    ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
    ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(HttpServletRequest request) {
    String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
    
    if ((refreshToken != null) && (!refreshToken.isEmpty())) {
      return refreshTokenService.findByToken(refreshToken)
          .map(refreshTokenService::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(user -> {
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully!"));
          })
          .orElseThrow(() -> new TokenRefreshException(refreshToken,
              "Refresh token is not in database!"));
    }
    
    return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
  }
}
