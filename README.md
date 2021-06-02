# MediTime
약 알림 어플리케이션, MediTime<br>
개발기간 : 2021.04 ~ 2020.06
<img src="/readme_img/0.png" width=600 />
<h2>프로젝트 개요</h2>
- 정해진 시간에 약 복용을 필요로 하는 사람들을 위한 전화알림 어플리케이션
> 복용할 약을 종류, 시간대에 맞춰 추가
> 약을 복용해야하는 시간에 전화 형식의 알람을 전송
> 음성 인식 또는 터치를 통해 복약 여부확인
> 약의 복용 여부 주간별로 기록 및 확인
<br><br>
<h2>화면설계, 디자인, 개발일정</h2>
<h3>초안</h3>
<img src="/readme_img/1.png" width=600 />
<h3>디자인(<a href="https://www.figma.com/file/samkPpkufA59y3akwKmjty/MediTime?node-id=0%3A1">figma 링크 이동</a>)</h3>
<img src="/readme_img/2.png" width=600 />
<br><br>
<h2>실행 화면</h2>
( 개발중 )
<br><br>
<h2>기술 스택</h2><br>
- Android Kotlin
- SpeechRecognizer, RecognitionListener 을 활용한 STT
- TextToSpeech 를 활용한 TTS
- FullScreenIntent Notification 을 활용하여 전화오는 화면 구현
- AlarmManager, BroadcastReceiver, JobIntentService을 활용하여 알람 시 Notification 생성하는 기능 백그라운드로 구현
- SQLiteOpenHelper 을 활용하여 데이터 관리
<br><br>
<h2>개발 환경</h2><br>
Complie SDK Version: Android 11 (API 30) <br>
Minimum SDK Version: Android 8.1 Oreo (API 27) <br>
Gradle Version: 6.5 <br>
JDK version: jdk_1_8 <br>
Android Studio 4.2.1 <br>
