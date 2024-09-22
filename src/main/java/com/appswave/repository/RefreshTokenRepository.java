package com.appswave.repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.appswave.model.entity.RefreshToken;
import com.appswave.model.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  List<RefreshToken> findByExpiryDateBefore(Instant instant);
  @Modifying
  int deleteByUser(User user);
}
