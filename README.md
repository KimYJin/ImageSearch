# ImageSearch
- 카카오 이미지 API 를 이용한 이미지 검색 앱
<br><br>
### 데모 영상

<div>
<img src="https://user-images.githubusercontent.com/23073504/62593986-d45ea380-b913-11e9-8e8e-065343916ba2.gif">
</div>
<br><br>
### 설명<br><br>
 (1) MVVM 패턴, RxJava + Retrofit를 이용한 네트워킹 및 옵저빙<br>
 (2) Live Data를 이용한 Data Binding<br>
 (3) RecyclerView, GridLayoutManager, Glide 를 이용해 검색 결과 화면 구현<br>
 (4) 정확도 순, 최신 순 으로 검색이 가능하도록 구현<br>
 (5) ViewPager를 구현해 상세보기 뷰에서 좌[←]/우[→]로 슬라이드 해 이전/다음 이미지를 볼 수 있도록 구현<br>
 (6) 상세보기 뷰에서 해당 이미지를 클릭하면, position, doc_url, Intent를 이용해 웹 브라우저로 문서 URL을 연다<br>
 (7) 앱 하단의 [↑] 버튼을 클릭하면, 스크롤 최상단으로 이동<br>
 (8) 디바이스 자판의 엔터 버튼을 누르면 검색 실행되도록 구현<br>
 (8) 검색어를 입력하지 않고 검색버튼을 누르면, 검색어를 입력하도록 토스트 메시지를 띄움<br>
 (9) 검색어에 대한 검색 결과가 없는 경우, 검색결과가 없다는 토스트 메시지를 띄움<br>
 
 <br><br>
### 제작 기간 
 - 19.08.01 ~ 19.08.07
 <br>
 
- 개발 환경

  |  Name  |  Version  |
	---|---
   Android Gradle Plugin  |  3.4.2  
    Gradle  |  5.11  
    kotlin  |  1.3.41  
    Compile Sdk | 28   
    
<br>

- 사용 라이브러리

	|Library|Version|
	---|---
  rxjava | 2.2.10  
  Retrofit | 2.6.0  
  glide | 4.9.0  

