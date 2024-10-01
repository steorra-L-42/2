# nginx 이미지를 사용합니다. 뒤에 tag가 없으면 latest 를 사용합니다.
FROM nginx:1.26.1

# root 에 app 폴더를 생성
RUN mkdir /app

# work dir 고정
WORKDIR /app

# work dir 에 build 폴더 생성 /app/build
RUN mkdir /build

# host pc의 현재경로의 guide, pos 폴더를 build 폴더로 복사
ADD ./guide ./build/guide
ADD ./pos ./build/pos

# 80 포트 오픈
EXPOSE 80
# 443 포트 오픈
EXPOSE 443

# container 실행 시 자동으로 실행할 command. nginx 시작함
CMD ["nginx", "-g", "daemon off;"]
