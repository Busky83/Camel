package com.example;

import org.apache.camel.builder.RouteBuilder;
import io.minio.MinioClient;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.io.InputStream;
import java.io.FileOutputStream;
import io.minio.errors.MinioException;
import io.minio.GetObjectArgs;
import java.security.InvalidKeyException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.errors.ErrorResponseException;
import io.minio.UploadObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;


import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.builder.RouteBuilder;

import java.rmi.registry.LocateRegistry;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    private SimpleBean bean;
    private MBeanServer server;

    public MyRouteBuilder() throws Exception {
        server = ManagementFactory.getPlatformMBeanServer();
        bean = new SimpleBean();
        // START SNIPPET: e2
        server.registerMBean(bean, new ObjectName("jmxExample", "name", "simpleBean"));
        // END SNIPPET: e2
        LocateRegistry.createRegistry(8081);
        JMXServiceURL url = new JMXServiceURL
                ("service:jmx:rmi:///jndi/rmi://localhost:8081/jmxrmi");
        JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
        jcs.start();
    }
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
            .log("Hello World!");


        // From MQTT
        from("mqtt:foo?host=tcp://mosquitto:1883&subscribeTopicName=Try/MQTT")
            .log("MQTT message received: ${body}")
            .setProperty("originalBody").body()
            .unmarshal().json(JsonLibrary.Jackson)
            .setProperty("uuid").simple("${body[uuid]}")
            .marshal().json(JsonLibrary.Jackson)
            .to("http4://192.168.1.123:5500/insertAlter")
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

        // Minio Download
        String endpoint = "http://192.168.1.123:9090";
        String accessKey = "minio";
        String secretKey = "minio@123";
        String bucketName = "camel";
        String objectName = "example.txt";
        String localDirectory = "/tmp";
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

        // Minio Upload
        from("file:target/upload?noop=true") 
            .process(exchange -> {
                String filePath = exchange.getIn().getHeader("CamelFileAbsolutePath", String.class);
                uploadToMinio(filePath);
            })
            .to("log:Minio-Camel-Route");
    }

    private void uploadToMinio(String filePath) {
        String endpoint = "http://192.168.1.123:9090";
        String accessKey = "minio";
        String secretKey = "minio@123";
        String bucketName = "camel";
        String objectName = filePath.substring(filePath.lastIndexOf("/") + 1); // 使用文件名作为 MinIO 对象名

        try {
            // 创建 Minio 客户端
            MinioClient minioClient = new MinioClient.Builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 检查 Bucket 是否存在，如果不存在则创建
            // boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            // if (!bucketExists) {
            //     minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            // }

            // 上传文件
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build()
            );

            System.out.println("File uploaded successfully to Minio!");
        } catch (InvalidKeyException | IOException | NoSuchAlgorithmException | InsufficientDataException | InternalException | InvalidResponseException | ServerException | XmlParserException | ErrorResponseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }    

}
