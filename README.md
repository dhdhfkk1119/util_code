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

### Exception 예외 처리 방식 설정 
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

---

### 비밀번호 암호화 하기 
- 의존성 추가 : `	implementation 'org.springframework.security:spring-security-crypto'`
- MVC 파일 생성후 `@Configuration` 추가 -> @Bean 객체로 암호화 파일 등록
```
@Configuration
public class WebMVConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

---

### JWT 토큰 방식
(JWT 이면서 SessionUser로 전역으로 관리함)

---

### 로그인 인터셉터 설정 

---

### 페이징 하는 법(SSR 방식) -> 이는 코드는 없음 

---

### Response ApiUtil 설정 (CSR 방식일 경우)

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
