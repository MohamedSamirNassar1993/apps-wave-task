package com.appswave.repository;

import com.appswave.model.entity.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Long> {

  Optional<BlackListedToken> findByToken(String token);
  List<BlackListedToken> findByExpiryDateBefore(Instant instant);
  @Modifying
  int deleteAByToken(String token);
}
