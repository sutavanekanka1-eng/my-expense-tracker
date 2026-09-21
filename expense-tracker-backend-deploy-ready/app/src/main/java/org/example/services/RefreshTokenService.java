package org.example.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    @Autowired 
    RefreshTokenRepository refreshTokenRepository;

    @Autowired 
    UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        System.out.println("A: Fetching user");

        UserInfo user = userRepository.findByUsername(username);
        System.out.println("B: User fetched: " + user);


        if (user == null) {
            throw new RuntimeException("User not found");
        }
     // 🔥 Check if token already exists
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserInfo(user);

        RefreshToken refreshToken;

        if (existingTokenOpt.isPresent()) {
            // ✅ Update existing token
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7));
            System.out.println("B: Updating existing token");
        } else {
            // ✅ Create new token
            refreshToken = RefreshToken.builder()
                    .userInfo(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                    .build();
            System.out.println("B: Creating new token");
        }
        System.out.println("C: Before save");


        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        System.out.println("D: After save");

        return saved;
        

    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken()+"Refresh token expired. Please login again.");
        }
        return token;
    }
    public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}
    
      
}