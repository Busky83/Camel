package com.example;

import org.apache.camel.builder.RouteBuilder;
import io.minio.MinioClient;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.io.InputStream;
import java.io.FileOutputStream;
import io.minio.errors.MinioException;
import io.minio.GetObjectArgs;


/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        // here is a sample which processes the input files
        // (leaving them in place - see the 'noop' flag)
        // then performs content based routing on the message using XPath
        // from("file:src/data?noop=true")
        //     .choice()
        //         .when(xpath("/person/city = 'London'"))
        //             .log("UK message")
        //             .to("file:target/messages/uk")
        //         .otherwise()
        //             .log("Other message")
        //             .to("file:target/messages/others");

        // Timer
        from("timer:foo?period=5000")
            // .log("start");
            .log("Hello 2026!");
        
        // Restful API
        // rest("/api")
        //     .get("/sea/v2/seascore/test_jude_1/")
        //         .to("direct:Rest hello");
        
        // from("direct:Rest hello")
        //     .transform().constant("Hello, RESTful World!")
        //     .log("Response from RESTful API: ${body}");

        // from("direct:start")
        //     .to("http4://localhost:80/sea/v2/seascore/test_jude_1/1")
        //     .log("Response from Django API: ${body}");

        // To django api
        // from("timer:foo?period=10000")
        //     .log("Start request Django API")
        //     .to("http4://localhost:80/sea/v2/test")
        //     .log("Response from Django API: ${body}");

        // from("http4://localhost:80/sea/v2/seascore/test_jude_1/1")
        //     .log("Response from Django API: ${body}");

        // From MQTT
        from("mqtt:foo?host=tcp://mosquitto:1883&subscribeTopicName=Try/MQTT")
            .log("MQTT message received: ${body}")
            .setProperty("originalBody").body()
            .unmarshal().json(JsonLibrary.Jackson)
            .setProperty("uuid").simple("${body[uuid]}")
            .marshal().json(JsonLibrary.Jackson)
            .to("http4://10.109.101.74:5500/insertAlter")
            .log("Response from Flask API: ${body}")
            .setBody().exchangeProperty("originalBody")
            .unmarshal().json(JsonLibrary.Jackson)
            .transform().simple("${body[timestamp]}\\n${body[temperature]}")
            .toD("file:target/upload?fileName=${exchangeProperty.uuid}.txt")
            .log("File created in local directory");
        
        // FTP
        from("file:target/upload?moveFailed=../error")
            .log("Uploading file ${file:name}")
            .to("ftp://pure-ftpd:21/?autoCreate=false&username=user&password=123456&binary=true")
            .log("Uploaded file ${file:name} complete.");  

        // Minio
        String endpoint = "http://localhost:9000";
        String accessKey = "minio";
        String secretKey = "minio@123";
        String bucketName = "camel";
        String objectName = "example.txt";
        String localDirectory = "D:/test";
        MinioClient minioClient = new MinioClient.Builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
        from("direct:minioToLocal")
            .process(exchange -> {
                try {
                    InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
                    );

                    String localFilePath = localDirectory + "/" + objectName;
                    try (FileOutputStream outputStream = new FileOutputStream(localFilePath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    exchange.getIn().setBody("File transferred successfully");
                } catch (MinioException e) {
                    exchange.setException(e);
                }
            })
            .to("log:MinioToLocal-Log");

        from("timer://myTimer?period=5000")
            .to("direct:minioToLocal");
    }

}
