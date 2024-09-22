package com.appswave.service;

import com.appswave.model.entity.BlackListedToken;
import com.appswave.repository.BlackListedTokenRepository;
import com.appswave.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TokenBlacklistImpl implements TokenBlacklist {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    BlackListedTokenRepository blackListedTokenRepository;

    @Override
    public void addToBlacklist(String token) {

        BlackListedToken blackListedToken = new BlackListedToken();
        blackListedToken.setToken(token);
        blackListedToken.setExpiryDate(jwtUtils.extractExpiration(token).toInstant());
        blackListedTokenRepository.save(blackListedToken);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blackListedTokenRepository.findByToken(token).isPresent();
    }

    @Scheduled(fixedRate = 3600000)
    public void deleteBlackListedExpiredTokens() {
        Instant now = Instant.now();
        List<BlackListedToken> blackListedTokens = blackListedTokenRepository.findByExpiryDateBefore(now);

        if (!blackListedTokens.isEmpty()) {
            blackListedTokenRepository.deleteAll(blackListedTokens);
            System.out.println("Deleted black listed expired tokens items: " + blackListedTokens.size());
        } else {
            System.out.println("No black listed expired tokens items to delete.");
        }
    }
}