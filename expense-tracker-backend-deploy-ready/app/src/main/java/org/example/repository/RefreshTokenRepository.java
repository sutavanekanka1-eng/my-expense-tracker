package org.example.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.example.entities.RefreshToken;
import org.example.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserInfo(UserInfo user);
}
