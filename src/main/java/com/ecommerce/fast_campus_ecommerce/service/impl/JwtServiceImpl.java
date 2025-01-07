package com.ecommerce.fast_campus_ecommerce.service.impl;

import com.ecommerce.fast_campus_ecommerce.common.DateUtil;
import com.ecommerce.fast_campus_ecommerce.config.JwtSecretConfig;
import com.ecommerce.fast_campus_ecommerce.model.UserInfo;
import com.ecommerce.fast_campus_ecommerce.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final JwtSecretConfig jwtSecretConfig;
    private final SecretKey secretKey;

    @Override
    public String generateToken(UserInfo userInfo) {
        LocalDateTime now = LocalDateTime.now().plus(jwtSecretConfig.getJwtExpiration());
        Date expirationDate = DateUtil.convertLocalDateTimeToDate(now);
        return Jwts.builder()
                .setSubject(userInfo.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();
            parser.parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
