package com.appswave.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appswave.exception.TokenRefreshException;
import com.appswave.model.entity.RefreshToken;
import com.appswave.repository.RefreshTokenRepository;
import com.appswave.repository.UserRepository;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationDateInMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredRefreshTokens() {
        Instant now = Instant.now();
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByExpiryDateBefore(now);

        if (!refreshTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(refreshTokens);
            System.out.println("Deleted expired refresh tokens items: " + refreshTokens.size());
        } else {
            System.out.println("No expired refresh tokens items to delete.");
        }
    }
}