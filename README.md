## Spring Security 6.0 (SpringBoot 3.x.x을 곁들인)

- 해당 프로젝트는 스프링 시큐리티의 기본적 이해를 돕기 위해 만든 프로젝트입니다.
- 소스코드 중간중간 주석이 있습니다.

### 기술스택
- Java 17(corretto-17)
- SpringBoot 3.1.2
- Spring Security 6.1.2
- Spring Data Jpa
- Maria DB 10.11.2
  - 특이사항: jdbc 드라이버 "mysql-connector-j"로 변경
- 간단한 폼 로그인 구현위해 Mustache 사용 
  - 인덱스, 가입, 로그인 페이지로 구성(classpath:/templates/)
- lombok 라이브러리 추가
- spring-boot-starter-oauth2-client 라이브러리 추가

### 패키지 구조
- auth
  - PrincipalDetails: 시큐리티의 UserDetails를 상속받아 구현한 인증 객체
  - PrincipalDetailsService: UserDetailsService를 상속받아 구현한 서비스 loadUserByUsername()를 오버라이딩하여 우리 회원 객체로 wrapping
- controller
  - ViewController
- entity
  - User
- repository
  - UserRepository(JpaRepository 상속)

### Oauth2.0
- 구글
  - https://console.cloud.google.com/ 개발자 센터 접속
  - 프로젝트 생성 후 OAuth 2.0 클라이언트 Id 생성
  - 승인된 리디렉션 URI: http://localhost:8080/login/oauth2/code/google path 고정
  - 클라이언트ID, 보안 비밀번호 yml 설정 추가

### JWT
- JSON 객체를 사용해서 토큰 자체에 정보들을 저장하고 있는 Web Token
- 기존 세션 방식의 처리는 서버가 메모리 혹은 별도의 세션을 저장할 공간이 필요했음(하드디스크, DB)
  - 서버의 부담
  - auto scale이 되어 서버 인스턴스가 늘어났을 때, 메모리를 서로 공유하지 못하기 때문에 세션을 공유할 수 없음.
  - 중간에 서버가 죽으면? 메모리에 있던 정보들 다 날라감. 별도 저장소를 구성하기엔 비용이 듦
  - 이러한 이유 등으로 인해 jwt 웹 표준(RFC 7519) 등장.