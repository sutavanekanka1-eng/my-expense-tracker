package org.example.services;
import io.jsonwebtoken.security.Keys;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
@Service

public class JwtService {
	// Set via the JWT_SECRET env var in deployment - falls back to this dev-only
	// default so local runs still work without extra setup. Must be at least
	// 32 characters (256 bits) for the HS256 signing algorithm below.
	@Value("${JWT_SECRET:mySuperSecretKeyThatIsAtLeast32CharactersLong!}")
	private String SECRET;
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	public<T>T extractClaim(String token,Function<Claims,T>claimsResolver){
		final Claims claims=extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	public Date extractExpiration(String token) {
		return (Date) extractClaim(token, Claims::getExpiration);
	} 
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date(System.currentTimeMillis()));
	}
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	private String generateToken(Map<String, Object> claims, String username) {
	    return Jwts.builder()
	            .setClaims(claims)
	            .setSubject(username)
	            .setIssuedAt(new Date(System.currentTimeMillis()))
	            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
	            .signWith(getSignKey())
	            .compact();
	}
	
	private Claims extractAllClaims(String token) {
	    return Jwts
	    		.parser()
	            .setSigningKey(getSignKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	}
	private SecretKey getSignKey() {
		byte[] keyBytes=SECRET.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
		
		// TODO Auto-generated method stub
	}
	public String generateToken(String username) {
	    return generateToken(new HashMap<>(), username);
	}

}
