package com.bsep2024.MarketingAgency.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import com.bsep2024.MarketingAgency.models.User;
import com.bsep2024.MarketingAgency.payload.response.RefreshTokenResponse;
import com.bsep2024.MarketingAgency.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.bsep2024.MarketingAgency.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${bsep2024.app.jwtSecret}")
  private String jwtSecret;

  @Value("${bsep2024.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${bsep2024.app.jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

  @Value("${bsep2024.app.jwtCookieName}")
  private String jwtCookie;

  @Autowired
  private UserRepository userRepository;
  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {

    String authority = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse(null); // This assumes there's always at least one authority

    String jwt = generateTokenFrom(userPrincipal.getId(), authority);
    //access token traje 15min
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(15 * 60).httpOnly(true).build();
    return cookie;
  }

  public ResponseCookie generateRefreshJwtCookie(UserDetailsImpl userPrincipal){
    String authority = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse(null); // This assumes there's always at least one authority

    String jwt = generateRefreshTokenFrom(userPrincipal.getId(), authority);
    //access token traje 1h
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(60 * 60 * 1000).httpOnly(true).build();
    return cookie;
  }

  public Boolean validateToken(String token, UserDetailsImpl userDetails) {
    Long id = getUserIdFromJwtToken(token);
    User user = userRepository.findById(id).get();
    return (user.getEmail().equals(userDetails.getEmail()) && !isTokenExpired(token));
  }

  public Boolean isTokenExpired(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key()).build()
            .parseClaimsJws(token)
            .getBody()
            .get("exp", Long.class) < new Date().getTime();
  }

  public ResponseCookie getCleanJwtCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
    return cookie;
  }

  public Long getUserIdFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody().get("id", Long.class);
  }
  
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }


  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    } catch (JwtException e){
      logger.error("JWT exception: {}", e.getMessage());
    }

    return false;
  }
  
  public String generateTokenFrom(Long id, String role) {
    return Jwts.builder()
              .claim("id", id)
              .claim("role", role)
              .setIssuedAt(new Date())
              .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
              .signWith(key(), SignatureAlgorithm.HS256)
              .compact();
  }
  public String generateRefreshTokenFrom(Long id, String role) {
    return Jwts.builder()
            .claim("id", id)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs)).signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }

  public String generateTokenForPasswordless(Long id) {
    return Jwts.builder()
            .claim("id", id)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + 10 * 60 * 1000))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }
}
