# SpringBoot 에서 사용하는 공통 코드를 사용하는 방법

## 공통 유틸 종류 

### yml 설정에 관해서 
- mysql 방식
```
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/{database}?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: {이름}
    password: {비밀번호}
```
- h2-console 방식
```
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb   # 메모리 모드 (애플리케이션 종료 시 데이터 삭제)
    username: sa
    password: 

  h2:
    console:
      enabled: true   # h2-console 켜기
      path: /h2-console  # 접속 경로 (http://localhost:8080/h2-console)
```

- JPA 설정 
```
spring:
  jpa:
    defer-datasource-initialization: true
    show-sql: true # DB에 수행하는 모든 쿼리문을 콘솔에 출력
    hibernate:
      format_sql: true # SQL 쿼리를 보기 좋게 줄바꿈하여 포맷팅
      default_batch_fetch_size: 10 # 조인 페치 와 같이 한방 쿼리를 내보냄 
      ddl-auto: update # 개발 초기에 설정 -> update 기존에 있던 데이터는 가만히 두고 변경되는 데이터만 추가 
      generate-ddl: true # jpa 스키마 자동 생성 기능 
```

- 이미지 업로드에 로컬 파일에 저장
```
server:
  servlet:
    tomcat:
      max-part-count: 20
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB
    encoding:
      charset: utf-8
      force: true
  port: 8080

upload:
  root-dir: ./uploads/
  member-dir: member-images/
  community-dir: community-images/

```

---

## 이미지 업로드 Multipart 방식 사용법
- yml 에 추가한 코드를 `@ConfigurationProperties` 설정 실제 경로
```
@Configuration
@ConfigurationProperties(prefix = "upload")
@Data
public class UploadProperties {
    private String memberDir;
    private String communityDir;
    private String rootDir;
}

```

- 다중 이미지 업로드 Multipart[] 배열일 경우 
- 단일 이미지 업로드 Multipart

---

### Exception 예외 처리 방식 설정 , CSR , SSR 방식 두가지 있음 
- 400,401,403,404,500
- 400 : 잘못된 파라미터 값 전송 또는 JSON 형식이 틀림 ,요청 헤더 부족
- 401 : 인증을 하지 않은 요청 거부 , JWT , Session 누락
- 403 : 접근 권한에 관해서 거부 (자신의 게시물에만 접근 가능)
- 404 : 없는 URL 접근 , 삭제된 게시물 접근 , 잘못된 경로 접근
- 500 : 서버내부에서 알 수 없는 오류가 발생한 경우
  **- SQL 에러 , 서버 내부문제**
- **`GlobalException 설정`** -> 에러 발생시 어떤식으로 보여줄지 설정

---

### Session 방식 로그인 전역 model로 설정 하는 방법
(로그인 시 전역 Controller 등록해서 어디서나 model로 가져올 수 있음)
- session 파일 안에 있음 
---

### 비밀번호 암호화 하기 
- 의존성 추가 : `	implementation 'org.springframework.security:spring-security-crypto'`
- MVC 파일 생성후 `@Configuration` 추가 -> @Bean 객체로 암호화 파일 등록
```
@Configuration
public class passConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

---

### JWT 토큰 방식
- jjwt 의존성 라이브러리 사용 코드 jjwt 파일 안에 있음
  ```
  	// 1. jjwt-api: Jwts, Claims 등 핵심 API
	implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
	// 2. jjwt-impl: 실제 구현체
	runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
	// 3. jjwt-jackson: JSON 파서
	runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'
  ```

- auth0 의존성 라이브러리 사용 코드 auto0_jwt 안에 있음
  `implementation group: 'com.auth0', name: 'java-jwt', version: '4.4.0'`
---

### 로그인 인터셉터 설정 
- 아래와 같이 WebMvc 안에서 적어주기 addInterceptor 안에 추가해주기 인터셉터 
```
@RequiredArgsConstructor
@Configuration // IoC 처리 (싱글톤 패턴 관리)
public class WebMvcConfig implements WebMvcConfigurer {
    // DI 처리(생성자 의존 주입)
    private final AuthInterceptor authInterceptor;
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");

        registry.addInterceptor(loginInterceptor)
```
---

### WebMvc CorsMappings 설정 하기
- Cors 설정하기 , methods 각각 설정 
```
@Configuration
@RequiredArgsConstructor
public class MVConfig implements WebMvcConfigurer {


    private final JwtInterceptor jwtInterceptor;

    // 비밀번호 암호화 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/**");
    }
}

```

---

### 페이징 하는 법(SSR 방식) -> 이는 코드는 없음 
- Repository 에서 해당 페이징 갯수랑 키워드 값을 받아옴 
```
@Query("select c from Community c " +
            "where (:keyword is null or c.title LIKE %:keyword% OR c.content LIKE %:keyword%)")
    Page<Community> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
```
- Service 에서 해당 페이지의 키워드 및 사이즈 값을 받아준다 
```
// 게시물 페이징 및 검색 기능
    public Page<CommunityResponseDTO.ListDTO> getList(String keyWord,int page,int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Community> community = communityRepository.findByKeyword(keyWord, pageable);
        return community.map(CommunityResponseDTO.ListDTO::new);
    }
```
- Controller 임의로 페이지 값 과 키워드 값을 설정 , @RequestParam 형식으로 받아줌 
```
@GetMapping("/")
    public String communityList(Model model,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value="page", defaultValue="0") int page){

        Page<CommunityResponseDTO.ListDTO> dtoList = communityService.getList(keyword,page,5);
        model.addAttribute("communityPage", dtoList);
        model.addAttribute("communityList",dtoList.getContent());
        model.addAttribute("keyword", keyword);
        return "index";
    }
```
---

### Response ApiUtil 설정 (CSR 방식일 경우)
- 에러 및 성공 했을 때 해당 객체만 던저주는 것과 해당 객체와 메세지를던져주는것 두가지 버전 임

```
public class ApiUtil<T> {

    // 성공 응답을 생성하는 정적 메서드
    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response, null);
    }

    // 실패 응답을 생성하는 정적 메서드 (메시지와 상태 코드만 포함)
    public static <T> ApiResult<T> fail(String errorMessage, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status.value()));
    }

    // 실패 응답을 생성하는 정적 메서드 (메시지, 상태 코드, 에러 코드 포함)
    public static <T> ApiResult<T> fail(String errorMessage, HttpStatus status, String errorCode) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status.value(), errorCode));
    }

    // 실패 응답을 생성하는 정적 메서드 (유효성 검사 오류 포함)
    public static <T> ApiResult<T> fail(String errorMessage, HttpStatus status, String errorCode, Map<String, String> validationErrors) {
        return new ApiResult<>(false, null, new ApiError(errorMessage, status.value(), errorCode, validationErrors));
    }

    @Data
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final ApiError error;

        private ApiResult(boolean success, T response, ApiError error) {
            this.success = success;
            this.response = response;
            this.error = error;
        }
    }

    @Data
    public static class ApiError {
        private final String message;
        private final int status;
        @JsonInclude(JsonInclude.Include.NON_NULL) // code가 null이면 JSON에서 제외
        private final String code;
        @JsonInclude(JsonInclude.Include.NON_NULL) // validationErrors가 null이면 JSON에서 제외
        private final Map<String, String> validationErrors;

        // 기존 생성자
        private ApiError(String message, int status) {
            this(message, status, null, null);
        }

        // 에러 코드를 받는 새로운 생성자
        private ApiError(String message, int status, String code) {
            this(message, status, code, null);
        }

        // 유효성 검사 오류를 포함하는 최종 생성자
        private ApiError(String message, int status, String code, Map<String, String> validationErrors) {
            this.message = message;
            this.status = status;
            this.code = code;
            this.validationErrors = validationErrors;
        }
    }

}
```
- 아래와 같이 사용
```
@PutMapping("/{issueId}")
    public ResponseEntity<CommonResponseDto<IssueResponse.FindById>> updateIssue(@PathVariable("issueId") Long issuedId,
                                                                @RequestBody IssueRequest.Update update){
        Issue issue = issueService.updateIssue(issuedId,update);
        return ResponseEntity.ok(CommonResponseDto.success(new IssueResponse.FindById(issue),"성공적으로 변경되었습니다"));
    }
```
---

### 시간 Format 설정
- 의존성 추가 : `implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.10'`
- LocalDateTime , Timestamp 두개다 포맷 가능
```
    public static String localDateTimeFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        return dateTime.format(formatter);
    }

    public static String dateTimeFormat(Timestamp endDate) {
        if (endDate == null) {
            return "";
        }

        LocalDateTime localDateTime = endDate.toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }
```
### JJWT 토큰 방식 토큰 만들기
```
package com.nationwide.nationwide_server.core.jwt;

import com.nationwide.nationwide_server.member.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtTokenProvider {
    private SecretKey key = Jwts.SIG.HS256.key().build(); // 대칭키 생성
    private final long accessExpirationMs; // 일반 토큰 유효 시간 1시간
    private final long refreshExpirationMs; // 새 토큰 유효 시간 70일


    public JwtTokenProvider(
            @Value("${jwt.access-expiration-ms}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs
    ) {
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    // Access Token 생성
    public String createAccessToken(Member member) {
        log.debug("Access Token 생성 시작 - 사용자: {}", member.getLoginId());
        return createToken(member, accessExpirationMs);
    }

    // Refresh Token 생성
    public String createRefreshToken(Member member) {
        log.debug("Refresh Token 생성 시작 - 사용자: {}", member.getLoginId());
        return createToken(member, refreshExpirationMs);
    }

    // 토큰 생성 공통 로직
    private String createToken(Member member, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("name", member.getName());
        claims.put("profileImage", member.getProfileImage());

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(member.getLoginId())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();

        log.info("토큰 생성 완료 - 사용자: {}, 만료시간: {}", member.getLoginId(), expiry);
        return token;
    }

    // 토큰 검증 하기
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 생성한 토큰 추출
    public Member getClaimsMember(String token) {
        log.debug("토큰에서 회원 정보 추출 시작");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Claims에서 개별 필드를 꺼내서 Member 객체 생성
            Member member = new Member();
            member.setId(claims.get("id", Long.class));
            member.setLoginId(claims.getSubject());
            member.setName(claims.get("name", String.class));
            member.setProfileImage(claims.get("profileImage", String.class));

            log.debug("회원 정보 추출 완료 - ID: {}", member.getId());
            return member;

        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰에서 정보 추출 시도");
            // 만료된 토큰이라도 Claims는 추출 가능
            Claims claims = e.getClaims();

            Member member = new Member();
            member.setId(claims.get("id", Long.class));
            member.setLoginId(claims.getSubject());
            member.setName(claims.get("name", String.class));
            member.setProfileImage(claims.get("profileImage", String.class));

            return member;

        } catch (Exception e) {
            log.error("토큰에서 회원 정보 추출 실패", e);
            return null;
        }
    }

    // 유저 loginId 가져오기
    public String getLoginId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception e) {
            log.error("LoginId 추출 실패", e);
            return null;
        }
    }

}
```
- JwtInterceptor 
```
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = resolveToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)){

            Member member = jwtTokenProvider.getClaimsMember(token);

            SessionUser sessionUser = new SessionUser(
                    member.getId(),
                    member.getLoginId(),
                    member.getName(),
                    member.getProfileImage()
            );

            request.setAttribute("sessionUser", sessionUser);
            return true;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다");
        return false;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 토큰 검증 및 "Bearer " (공백 한칸을 잘라내자)
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```
