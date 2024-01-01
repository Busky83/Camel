## Minio
Minio 開啟 port 9000 & 9090 
啟動Script
```Script
  cd ./minio
  sh minio.sh  
```
## Minio
使用docker compose up 啟動寫好的docker-compose.yml
其中設定port為21，帳號user以及密碼123456，因為使用同一台機器進行測試所以使用localhost即可。
以下route為從本地端資料夾上傳檔案至FTP
```Route寫法
  from("file:target/upload?moveFailed=../error")
            .log("Uploading file ${file:name}")
            .to("ftp://localhost:21/?autoCreate=false&username=user&password=123456&binary=true&passiveMode=true")
            .log("Uploaded file ${file:name} complete.");  
```

```pom.xml
  請在pom.xml的dependencies下加入
  <dependency>
      <groupId>org.apache.camel</groupId>
      <artifactId>camel-ftp</artifactId>
  </dependency>  
```

