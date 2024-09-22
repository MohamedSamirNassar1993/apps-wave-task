package com.appswave.model.dto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements UserDetails {

    private static final long serialVersionUID = 1L;
    private long id;
    @ApiModelProperty(notes = "email ",name="email",required=true)
    private String email;
    @ApiModelProperty(notes = "password ",name="password",required=true)
    private String password;
    @ApiModelProperty(notes = "fullName ",name="fullName",required=true)
    private String fullName;
    @ApiModelProperty(notes = "birthDate ",name="birthDate",required=true)
    private LocalDate birthDate;
    @ApiModelProperty(notes = "role",name="role",required=true)
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }
}