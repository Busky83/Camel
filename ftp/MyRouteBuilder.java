package org.apache.camel.learn;

import org.apache.camel.builder.RouteBuilder;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        from("file:target/upload?moveFailed=../error")
            .log("Uploading file ${file:name}")
            .to("ftp://localhost:21/?autoCreate=false&username=user&password=123456&binary=true&passiveMode=true")
            .log("Uploaded file ${file:name} complete.");
    }

}
