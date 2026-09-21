package org.example.auth;


import java.io.IOException;

import org.example.services.JwtService;
import org.example.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Component
@AllArgsConstructor

public class JwtAuthFilter extends OncePerRequestFilter
{

	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String path = request.getServletPath();
        if (path.startsWith("/auth/v1/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                logger.warn("Rejecting request to " + request.getRequestURI() + ": could not parse JWT (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
            }
        }

        // Wrap the rest too: validateToken() re-parses the token and can throw
        // (e.g. ExpiredJwtException) just as easily as extractUsername() above did.
        // Letting that escape uncaught sends the request into Spring's /error
        // dispatch instead of a clean 401/403, which is confusing to debug.
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtService.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    logger.warn("Rejecting request to " + request.getRequestURI() + ": JWT failed validation for user '" + username + "' (expired, or token subject does not case-sensitively match stored username '" + userDetails.getUsername() + "')");
                }
            } catch (Exception e) {
                logger.warn("Rejecting request to " + request.getRequestURI() + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
