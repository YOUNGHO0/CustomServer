

# Java를 사용해 서버를 직접 만들고 부하테스트로 개선하기 


## 1-1 전체 다이어그램


![ServerUml](https://github.com/user-attachments/assets/9252ca3b-6192-4b8e-8bb9-450191280f42)

1. Main에서 무한 루프를 돌면서 Accpet 되는지 확인하고
2. Accept가 되었다면 해당 요청을 처리하기 위해 요청으로 변환함
3. readLine 하면서 하나씩 읽어들여 요청을 확인하고
4. 해당 요청에 맞는 핸들러를 main에서 호출함
5. 실제 비즈니스 로직 처리는 모든것이 추상화 된 RequestService에서 진행함

## 1-2 처리 방식에 따라 동기식 처리 클래스, 비동기(멀티스레드)로 나뉘어짐

![handler](https://github.com/user-attachments/assets/a8673d1c-1046-40f3-ba12-bf3b7979bc4d)

## 1-3 커넥션 풀을 인터페이스화 하고 기본적으로 Hikari CP를 사용함
![datasource](https://github.com/user-attachments/assets/1ba17e99-160c-4da1-af0d-907963ac4fcd)


## 1-4 커넥션 풀을 데이터베이스 객체에서 사용함
### SmartDatabase의 경우 같은 요청이 많이 발생되는 경우 락을 걸어 DB 부하를 줄임
### GeneralDatabase는 일반적인 DB 클래스
![Database](https://github.com/user-attachments/assets/0bf9ed1b-c6c6-4fc2-9825-ee75f19f0655)


# 2. 부하 테스트 분석
테스트 환경 : 1코어 1기가의 Ubuntu Linux 

OpenJdk 21 사용

Jmeter를 사용해 부하테스트 진행

요약 : 서버 성능에 주는 요소는 비타민과 같음

결핍하면 문제가 발생하나, 충분할 때 성능 향상은 없었음

## 2-1 동기식 처리에서 퍼포먼스가 나오지 않는 문제 발생
RequestService 객체에 Cpu 집약적인 작업을 위해 약간의 sleep 시간을 주고 테스트 해본 결과 너무 느린 throughput 확인

멀티 스레드를 사용해 비동기 처리를 하게끔 개선함 :AsyncRequestHandler

동기 요청처리시 서버에 바로 요청이 넘어오는 것이 아닌 소켓 버퍼에 어느정도 찬 상태에서 천천히 요청이 들어오는 것을 확인 

## 2-2 스레드 풀에서의 스레드 갯수는 일정 갯수를 넘어가면 무의미 해졌음
스레드 갯수가 3 - 30 - 100 은 성능 차이가 발생했지만

100 - 300 구간은 크게 차이가 벌어지지 않았음
## 2-3  스레드 갯수를 매우 많이 만들어 보았을 때 OS단에서 문제가 발생했음
SystemCall로 추가적인 Pthread 생성이 불가능 해 예외 발생

![image](https://github.com/user-attachments/assets/579cbcd4-c466-4193-a382-a2c1f22eee3e)

## 2-4 동시 요청량이 특정 임계값을 넘어가면 일시적으로 처리가 중단되는 현상 발생
WireShark로 패킷을 캡처해본 결과 초기에는 연결에 문제가 없었지만, 이후 요청에는 RST 패킷이 두드러졌음

리눅스에서 한번에 열 수 있는 소켓 값을 65535로 변경함으로써 해결함

## 2-5 System.out.println 은 문제가 많았음
콘솔에 io 작업이 진행되는 순간 처리량이 갑작스럽게 줄어들었음.

처리량이 줄어드는 시점은 예측이 불가능했음

리눅스에서 ctrl + alt + f2 로 다른 창으로 이동시 콘솔 출력 방식이 내부적으로 변하였는지 처리량이 개선됨

println 코드를 모두 주석 처리하자 처리량이 안정적으로 변했음

logback(slf4j) 사용하게 변경

## 2-6 커넥션 풀 갯수와 성능은 대체적으로 비례하는 성향을 보였음
커넥션 풀 갯수가 1개에서 최대 30개까지 변경 시 요청을 처리하는 데 있어서 성능 개선이 있었음

커넥션 풀이 1개인 경우, 스레드가 300개인 경우 한개의 커넥션을 사용하기 위해 대기 시작이 길어지면서 Timeout 발생
<img width="1246" alt="image" src="https://github.com/user-attachments/assets/6fcbdafb-9366-48c1-9409-09a18614895a" />

## 2-7 부하를 준 뒤 htop 으로 확인해보면 실행중인 프로세스가 mariaDB로 도배됨
<img width="914" alt="image" src="https://github.com/user-attachments/assets/2064a026-886c-4e47-8837-58a63b50f7a4" />

DB부하를 해결하기 위해 서버와 DB분리 필요



## 결론
여러가지 요소들이 서버 퍼포먼스에 영향을 줄 수가 있으나, 비타민과 같이 작용함

부족하면 문제가 생겼지만 충분하다고 더 좋아지진 않음

결국 모든 문제들이 해결된 상태라면 DB 내부 처리에서 많은 시간을 잡아먹으므로, DB개선이 필요함

요청(비즈니스)를 잘 분석했을 때 get요청과 같은 데이터가 없는 경우에 커넥션 1개만 DB에 접근하게 하여 성능을 개선할 수 있어보였음

실제로 한번만 DB를 접근하게 변경한 경우, 다른 스레드는 기다리지만, DB를 중복 방문하지 않으므로 처리량이 36에서 102정도로 약 3배 증가함

테스트에서는 DB와 서버를 한개의 컴퓨터에 같이 두고 사용했는데, DB 성능을 개선하려면 아예 DB만으로 인스턴스를 한대 만들어야 해결될 것으로 보임










