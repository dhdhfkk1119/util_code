package com.puzzlix.solid_task._global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret}") String key,
                            @Value("${jwt.expiration-in-ms}") long validityInMilliseconds){

        // 1. 주고받은 비밀 키 문자열을 Base64 값을 디코딩 하여 byte 배열로 변환합니다
        byte[] keyBytes = Decoders.BASE64.decode(key);

        // 2. 알고리즘을 사용할 SecretKey 객체를 생성한다
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = validityInMilliseconds;

    }

    public String createToken(String email){
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /*
    * 토큰에서 사용자 이메일을 추출하는 기능
    * */
    public Claims parseClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도, 만료 정보를 확인하기 위해 클레임 자체를 변환함
            return e.getClaims();
        }
    }

    /*전체 토큰에서 사용자 이메일
    * @param token
    * */
    public String getSubject(String token){
        return parseClaims(token).getSubject();
    }

    /*
    * 토킨의 전체 유효성 검증
    * @param token */
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            // 토큰이 만료되었더라도, 만료 정보를 확인하기 위해 클레임 자체를 변환함
            log.error("잘못된 JWT 서명 입니다",e);
        } catch (ExpiredJwtException e){
            log.error("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e){
            log.error("지원되지 않는 JWT 토큰입니다");
        } catch (Exception e){
            log.error("JWT 토큰이 잘못되었습니다");
        }
        return false;
    }
}
