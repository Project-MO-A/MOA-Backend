<div align="center">
  <img width="600" alt="MOA pannel" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/3e79e073-490e-43e9-8664-8a007582f7a0">
</div>


<div align="center"><h1></h1></div>

> **🧑🏻‍💻 모든 스터디를 한 눈에! 스터디 정보를 모아 볼 수 있는 서비스, MO:A**
#### 주요 기능
- 프로젝트, 스터디 모집글 작성 및 관리 기능
- 프로젝트 포지션별 참여 신청 및 관리 기능
- 팀원 인기도 평가, 참여도 관리 기능
- 위치 기반 장소 추천, 시간 조율 기능

&nbsp;

### Wiki 📑
👉 [Wiki Page](https://github.com/Project-MO-A/Community/wiki)

## 팀원👨‍💻👩‍💻

|Backend|Backend|Frontend|Frontend|
|:---:|:---:|:---:|:---:|
|![image](https://avatars.githubusercontent.com/u/75611167?v=4)|![image](https://avatars.githubusercontent.com/u/99247279?v=4)|![image](https://avatars.githubusercontent.com/u/90231153?v=4)|![image](https://avatars.githubusercontent.com/u/74188167?v=4)|
|[주홍석](https://github.com/Juhongseok)|[이기우](https://github.com/KAispread)|[조은서](https://github.com/Eunseo-jo)|[박신비](https://github.com/shinbi-park)|

&nbsp;

<h2>🌠 Overview</h2>
<div align="center">
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/727f959d-f943-4894-9d3a-449970a83c70">
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/5cc16554-aa37-4d49-ada1-8da9b89e7cab">
  &nbsp;
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/fed10d7f-4040-4085-8c8f-7fe4ca34667b">
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/1706470f-a163-40fb-8d6c-7bc08af68617">
  &nbsp;
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/48ca4d01-40d3-4091-a408-88454627a3b8">
  <img width="400" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/d4c51db6-cf1b-4faf-bce6-c4707014e272">
</div>

&nbsp;
<h2>🛠 Stack</h2>
<div align="center">
  <img width="700" alt="Stacks" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/24fd4663-cc8d-478e-af6d-24d6ec7a0bcc">
</div>

&nbsp;
<h2>📜 Infra</h2>
<div align="center">
  <img width="700" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/b6a73f49-6c6e-4887-8682-d7ab13538d6f">
</div>

&nbsp;
<h2>📋 ERD</h2>
<div align="center">
  <img width="700" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/ad793e8a-3c19-4fc4-bc0e-9f3f4d65cbbf">
</div>

&nbsp;
<h2>✍🏻 Collaboration</h2>
<div align="center">
  
  **🧑🏻‍💻 Discussions를 통한 커뮤니케이션 및 Issue Tracking**
  <img width="700" alt="ERD" src="https://github.com/Project-MO-A/MOA-Backend/assets/99247279/555813c3-0fbc-4879-bc05-feeed4425785">
</div>



<h2>⛔️ Custom Exception</h2>

|분류|코드|메시지|설명|
|:---:|:---:|:---:|:---:|
|A|0001|비밀번호를 다시 입력해 주세요|비밀번호가 일치하지 않을 경우|
| |0002|잘못된 요청입니다|로그인 요청 시 POST or JSON 형식이 아닐 경우|
| |0003|토큰이 유효하지 않습니다|JWT에 있는 email이 다르거나 기간이 만료되었을 경우|
| |0004|email과 password는 필수 값입니다|로그인 시 값이 하나라도 없을 경우|
|S|0001|잘못된 상태 코드입니다|상태 코드의 값이 존재하지 않을 경우|
| |0002|참여중인 멤버를 추방하려면 상태코드를 'KICK' 으로 변경해주세요.||
|U|0001|해당 이메일을 가진 유저를 찾을 수 없습니다||
| |0002|중복된 이메일입니다|회원가입 시 검증|
| |0003|비밀번호가 일치하지 않습니다|비밀번호 변경 시 검증|
|N|0001|존재하지 않는 공지사항입니다|공지사항 수정, 삭제|
| |0002|해당 모집글에 속해있지 않습니다|모집글 투표, 수정|
|AM|0001|||
|R|0001|해당 아이디를 가진 모집글을 찾을 수 없습니다||
| |0002|모집글에 일치하는 신청분야가 없습니다|참여 신청|
| |0003|신청 분야 정원이 다 찼습니다|admin 참여 신청 수락|
| |0004|승인된 신청 분야 멤버가 0명입니다|admin 참여 신청 거절|
| |0005|해당 신청 분야를 찾을 수 없습니다||
| |0006|주어진 Id와 일치하는 모집글이 없거나 잘못된 신청 분야입니다.||
|P|0001|신청자를 찾을 수 없습니다||
| |0002|리더는 포지션을 변경할 수 없습니다.|리더 포지션 멤버가 신청 상태를 변경하려 했을 때 발생|
| |0003|해당 모집글에 이미 신청했습니다.|모집글에 포지션 관계 없이 2회 이상 신청할 때 발생|
|T|0001|유효하지 않은 시간입니다||
|RQ|0001|유효하지 않은 요청 데이터입니다||
|C|0001|유효하지 않은 카운트 입니다||
