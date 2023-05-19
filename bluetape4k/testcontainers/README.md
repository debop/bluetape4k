# module bluetape4k-test-testcontainers

## Docker Desktop 대체제

Docker Desktop이 유료가 되어서 대체재로 lima 를 사용합니다.
모든 대체재가 Apple Silicon arm64 이미지가 아니면 실행하지 못합니다.
그마나 lima 가 가장 쉽고, 빠른 대체재입니다.

[Docker Desktop의 대체재를 찾아보자](https://byungwoo.oopy.io/0e5485ba-dc5e-4951-a611-81ce08291817)

를 참고하여 환경을 설정하면 됩니다.

## FAQ

### Q. Mac의 port 7000 이 사용 중이다.

Redis Cluster 는 기본 7000:7005 를 사용하는데, Mac 에서는 이미 사용 중이라고 합니다.
Mac AirPlay 를 중단하면 됩니다.

* [MacOS Montrey AirPlay Port 5000 and 7000](https://jaynamm.tistory.com/entry/%EB%A7%A5%EB%B6%81%EC%97%90%EC%84%9C-5000%EB%B2%88-%ED%8F%AC%ED%8A%B8%EA%B0%80-%EC%82%AC%EC%9A%A9%EC%A4%91%EC%9D%B4%EB%8B%A4-MacOS-Montrey)

### Q. Docker 재실행 시 address already in use 에러 해결

* [Docker address already in use](https://www.baeldung.com/linux/docker-address-already-in-use)
* [[Docker] bind:address already in use](https://steady-coding.tistory.com/488)
* [포트가 이미 할달되어 있어서 발생한 에러 해결](https://a-half-human-half-developer.tistory.com/18)  
