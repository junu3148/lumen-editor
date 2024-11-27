# 생성형 AI를 활용한 영상 편집 플랫폼 Editor

<img width="1920" alt="제목 없음" src="https://github.com/junu3148/Admin/assets/134668162/7d041a41-9691-42d0-844b-b7f1f204a728">

<h1>관리자 API 문서</h1>

생성형 AI 기반 영상 편집 플랫폼의 에디터(Editor)를 개발한 프로젝트입니다.
Redis와 쿠키 기반 인증을 통해 사용자 환경을 강화했습니다.

시작하기
이 API를 사용하기 위해서는 필요한 인증 토큰과 권한이 있어야 합니다. 이 엔드포인트들은 사용자만을 위한 것이므로 접근 권한이 제한되어 있습니다.

## 사용 기술

Open JDK 17, Spring boot, Spring Security, Gradle, Spring Data JPA, MySql, Redis, Restful API, GitHub, SMTP API

## 인증

### 이메일 중복체크
- **엔드포인트**: `POST /auth/send-auth-code`
- **설명**: 가입시 이메일 중복 체크

### 인증번호 확인
- **엔드포인트**: `POST /auth/verify`
- **설명**: 메일로 발송된 인증 번호 확인

### 회원가입
- **엔드포인트**: `POST /auth/signup`
- **설명**: 인증번호 확인 후 회원가입 진행

- ### 로그인
- **엔드포인트**: `POST /auth/login`
- **설명**: 로그인 성공시 쿠키에 accessToken 발급

## 토큰 검증

### 액세스 토큰 검증
- **엔드포인트**: `POST /auth/access-token`
- **설명**: 액세스 토큰의 유효성을 검증합니다.

## 로그아웃

### 관리자 로그아웃
- **엔드포인트**: `POST /auth/logout`
- **설명**: 시스템에서 관리자를 로그아웃시킵니다.

## 유저

### 유저 정보
- **엔드포인트**: `GET /main/user`
- **설명**: 유저 세부 정보 조회

- ### 유저 정보 수정
- **엔드포인트**: `PATCH /main/user/details`
- **설명**: 선택적 유저 정보 수정 (프로필, 공개여부)

- ### 유저 정보 수정
- **엔드포인트**: `PATCH /main/user/password`
- **설명**: 유저 비밀번호 수정

- ### 유저 탈퇴
- **엔드포인트**: `PATCH /main/user/delete`
- **설명**: 유저 활동 상태 변

