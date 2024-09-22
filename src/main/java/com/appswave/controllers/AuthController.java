package com.appswave.controllers;

import com.appswave.advice.CommonResponse;
import com.appswave.model.dto.UserDTO;
import com.appswave.model.payload.response.UserResponse;
import com.appswave.service.TokenBlacklist;
import com.appswave.service.UserDetailsServiceImpl;
import com.appswave.util.JwtUtils;
import com.appswave.service.RefreshTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.appswave.model.enums.RoleEnum;
import com.appswave.model.entity.RefreshToken;
import com.appswave.model.payload.request.LoginRequest;
import com.appswave.model.payload.request.SignupRequest;

import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


@RestController
@RequestMapping("/api/auth/")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    TokenBlacklist tokenBlacklist;

    @PostMapping("/login")
    @Operation(summary = "login user in the System ", responses = {
            @ApiResponse(responseCode = "200", description = "Data Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = "application/json"))
    })
    public CommonResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS");
        }
        UserDetails details = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        UserDTO userDTO = (UserDTO) details;
        String token = jwtUtils.generateToken(details);
        RefreshToken refreshtoken = refreshTokenService.createRefreshToken(userDTO.getId());
        UserResponse userResponse = new UserResponse(loginRequest.getEmail(), token, refreshtoken.getToken());
        return new CommonResponse(200, false, "User authenticated! login success", new Date(), userResponse, null);
    }

    @PostMapping("admin/signup")
    @Operation(summary = "register admin in the System ", responses = {
            @ApiResponse(responseCode = "200", description = "Data Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = "application/json"))
    })
    public CommonResponse registerAdmin(@RequestBody SignupRequest signupRequest) {
        UserDTO userdto = new UserDTO();
        userdto.setFullName(signupRequest.getFullName());
        userdto.setEmail(signupRequest.getEmail());
        userdto.setPassword(signupRequest.getPassword());
        userdto.setBirthDate(signupRequest.getBirthDate());
        userdto.setRole(RoleEnum.ADMIN.name());
        UserDTO userDTO = userDetailsService.registerUser(userdto);
        return new CommonResponse(201, false, "User registration success", new Date(), userDTO, null);
    }

    @PostMapping("normal/signup")
    @Operation(summary = "register normal in the System ", responses = {
            @ApiResponse(responseCode = "200", description = "Data Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = "application/json"))
    })
    public CommonResponse registerNormal(@RequestBody SignupRequest signupRequest) {
        UserDTO userdto = new UserDTO();
        userdto.setFullName(signupRequest.getFullName());
        userdto.setEmail(signupRequest.getEmail());
        userdto.setPassword(signupRequest.getPassword());
        userdto.setBirthDate(signupRequest.getBirthDate());
        userdto.setRole(RoleEnum.NORMAL.name());
        UserDTO userDTO = userDetailsService.registerUser(userdto);
        return new CommonResponse(201, false, "User registration success", new Date(), userDTO, null);
    }

    @PostMapping("content-writer/signup")
    @Operation(summary = "register Content Writer in the System ", responses = {
            @ApiResponse(responseCode = "200", description = "Data Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = "application/json"))
    })
    public CommonResponse registerContentWriter(@RequestBody SignupRequest signupRequest) {
        UserDTO userdto = new UserDTO();
        userdto.setFullName(signupRequest.getFullName());
        userdto.setEmail(signupRequest.getEmail());
        userdto.setPassword(signupRequest.getPassword());
        userdto.setBirthDate(signupRequest.getBirthDate());
        userdto.setRole(RoleEnum.CONTENT_WRITER.name());
        UserDTO userDTO = userDetailsService.registerUser(userdto);
        return new CommonResponse(201, false, "User registration success", new Date(), userDTO, null);
    }

    @GetMapping("/refresh-token")
    public CommonResponse refreshToken(HttpServletRequest request) throws Exception {
        log.info("refreshToken called");
        DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");
        Map<String, Object> expectedMap = new HashMap<>();
        if(claims!=null) expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtUtils.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        log.info(token);
        return new CommonResponse(200, false, "Token re-generated successfully(refresh token)!!", new Date(), token, null);
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        log.info(expectedMap.toString());
        return expectedMap;
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principle.toString().equals("anonymousUser")) {
            UserDTO userDTO = (UserDTO) principle;
            Long userId = userDTO.getId();
            refreshTokenService.deleteByUserId(userId);
            String token = extractTokenFromRequest(request);
            if(token==null) return ResponseEntity.ok().body(new CommonResponse(400, false,"There is no token!!", new Date(),null,null));
            tokenBlacklist.addToBlacklist(token);
            return ResponseEntity.ok().body(new CommonResponse(200, false,"You've been logout!", new Date(),userDTO,null));
        }
        return ResponseEntity.ok().body(new CommonResponse(400, false,"There are no authentications!!", new Date(),null,null));
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) token = bearerToken.substring(7);
        return token;
    }

    @GetMapping("/currentUser")
    @Operation(summary = "get current logged in user in the System", responses = {
            @ApiResponse(responseCode = "200", description = "Data Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Data Not Found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal Error", content = @Content(mediaType = "application/json"))
    })
    public CommonResponse getCurrentUser(Principal authentication) {
        UserDetails details = null;
        if (authentication != null) details = userDetailsService.loadUserByUsername(authentication.getName());
        return new CommonResponse(200, false, "Current user fetched successfully", new Date(), (UserDTO) details, null);
    }
}
