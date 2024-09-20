package com.appswave.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.appswave.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Setter
@Getter
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String fullName;

  private String email;

  @JsonIgnore
  private String password;

  private String role;

  private LocalDate birthDate;

  public UserDetailsImpl(Long id, String fullName, String email, String password, String role, LocalDate birthDate) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.role = role;
    this.birthDate = birthDate;
  }

  public static UserDetailsImpl build(User user) {
    return new UserDetailsImpl(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(), user.getRole().getName().name(), user.getBirthDate());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority(role));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
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
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
