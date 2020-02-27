# 주택 금융 서비스 API개발 HousingFinance Project

### 개발 환경

- Java 1.8
- Gradle
- SpringBoot
- JPA / hibernate
- lombok
- h2 database

## 설치방법
> IDE(STS, Intellij)
github repository https://github.com/piyoro/HousingFinance.git 을 clone한 후, gradle 프로젝트로 임포트

> command or terminal
git clone https://github.com/piyoro/HousingFinance.git 으로 프로젝트 clone

>querydsl Q클래스 generate 필요
IDE 의 gradle build 태스크를 실행하거나 커맨드창의 프로젝트 경로에서 gradle compileQuerydsl 명령어를 실행하고,
IDE 의 경우 프로젝트를 새로고침합니다.
generate 된 java 소스를 ide에서 자동 컴파일 하기 위해 src/main/generated 경로를 build path에 추가해줍니다.   
※generate 소스는 .gitignore 추가하여 공유되지 않습니다. 

## 실행방법
- 로컬 h2 databse 실행 필요
> IDE(STS, Intellij)
HousingFinanceApplication 를 IDE(STS, Intellij) 에서 Run As - Spring Boot App 으로 실행
> command or terminal
프로젝트 루트 경로에서 gradle bootRun 실행

## DB설계
- TB_BANK 테이블 : 지원 기관 관리
- TB_BANK_SUPP_AMT 테이블 : 년도/월 별 지원기관의 지원금액 관리
- TB_USER 테이블 : 계정 관리 (JWT)

## API 공통
포트 9684
헤더
Content-Type : application/json
인코딩 utf8

http status 응답코드  
```
200 정상
204 데이터 미존재
401 계정미생성/비로그인/토큰인증만료
500 서버오류
```

추가 제약사항(옵션) 적용으로 api 테스트 전,
계정 생성 (/user/signup) -> 테스트 (/user/signin) 단계로 signin 응답값 token을 요청 헤더 Authorization - Bearer Token 타입으로 포함하여 진행해야 합니다.
token 만료시간 : 5분
refresh token 만료시간 : 10분

## POST http://localhost:9684/user/signup
파라미터
|Key|항목명|type|필수|비고|
|:-------|:-------:|:-------:|:-------:|:-------|
|userId|id|string|O||
|pwd|비밀번호|string|O||

응답
http status 200 정상
http status 400 이미 존재하는 Id입니다.

## POST http://localhost:9684/user/signin
파라미터
|Key|항목명|type|필수|비고|
|:-------|:-------:|:-------:|:-------:|:-------|
|userId|id|string|O||
|pwd|비밀번호|string|O||
응답
|Key|항목명|type|비고|
|:-------:|:-------:|:-------:|:-------|
|token|jwt access token|string||
|refreshToken|jwt refreshToken|string|토큰 만료 refresh 토큰|

## GET http://localhost:9684/user/refresh
토큰 만료 후, /user/signin 응답 refreshToken값을 요청 헤더 Authorization - Bearer Token 타입으로 적용.
응답
|Key|항목명|type|비고|
|:-------:|:-------:|:-------:|:-------|
|token|jwt access token|string||
|refreshToken|jwt refreshToken|string|토큰 만료 refresh 토큰|

## POST http://localhost:9684/bank/saveInfo 
csv 데이터 파일에서 각 레코드를 데이터베이스에 API

헤더에 중복된 기관명이 존재할 경우 400 오류 (data rollback)
헤더의 유효 기관명 갯수와 데이터의 금액의 갯수가 상이할 경우 400 오류 (data rollback)

헤더 기관명은 기관코드를 채번하여 db 적재(tb_bank)와 동시에 기관코드 와 기관명을 static 메모리에 적재하였습니다.
데이터 금액의 컬럼순서에 따라 채번된 기관코드 (메모리에 적재된 기관코드를 취득)
를 부여하여 년 / 월 / 지원금액을 db 적재(tb_bank_supp_amt) 하였습니다. 

csv 파일은 /src/main/resources/resource.csv 파일 경로로 고정되어 있습니다.

## GET http://localhost:9684/bank/bankInfo
기관목록 조회 API
tb_bank 테이블에 적재된 기관정보를 모두 조회하였습니다.

## GET http://localhost:9684/bank/sumInfo
년도별 금융기관 지원금액 합계 조회 API

년도/금융기관으로 group by 한 지원금액의 합계 금액을 쿼리하고,
쿼리 결과 list 를 java stream api 로 년도별 grouppingBy 하여 
그룹핑된 데이터를 금융기관:지원금액으로 매핑한 목록을 출력하였습니다.

## GET http://localhost:9684/bank/maxInfo
년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명 조회 API

년도/금융기관으로 group by 한 지원금액의 합계 금액을 조회하여
지원금액 합계금액을 내림차순 정렬하여 첫번째 결과의 기관정보를 출력하였습니다.

## GET http://localhost:9684/bank/avgInfo
전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액 조회 API

외환은행 데이터를 각 년도별 group by 하여 지원금액/12(평균) 한 목록을 조회하여
자바에서 list 의 처음/끝 항목만 추출하여 출력하였습니다.
리스트가 1건일 경우, 최소/최대 정보는 동일하게 출력됩니다.