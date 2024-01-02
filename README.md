# Camel Demo
本專案為簡易展示Camel之功能
建立Flask, Minio, FTP server 並透過Minio來實現數據傳導的功能。
## Contributors
|組員|系級|學號|
|-|-|-|
|周正晏|資科碩專二|111971003|
|施宗佑|資科碩專二|111971005|
|胡元亨|資科碩專二|111971024|
## Flask
啟動Flask前，須確保本地有PostgreSQL或是利用以下指令建立容器快速啟動
```
cd ./flask/db
sh db.sh
```
於本地啟動Flask，預設port 5500
```python
cd flask
python main.py
```
## Minio
利用Minio作為Log的存儲空間
Minio 設定帳號密碼為minio/minio@123、開啟port 9000 & 9090（以上皆可在minio.sh更改）
啟動Script
```Script
  cd ./minio
  sh minio.sh  
```
## FTP
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

