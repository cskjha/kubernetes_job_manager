package com.vmware.lambda.watcher;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.http.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

/**
 * Created by amkumar on 16/8/2017.
 */
public class Watcher {

    static int port = 8080;

    static Props props;

    public static void main(String[] args) throws Exception {
        props = new Props();
        props.init();
        System.out.println("Watcher starting for "+props.appName + ":"+props.functionName);
        startServer();
    }

    static HttpServer server;
    private static void startServer() throws IOException {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout((int) TimeUnit.SECONDS.toMillis(60))
                .setTcpNoDelay(true).build();
        server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setServerInfo("LambdaWatcher/0.1")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("/input", newRequestHandler(FIND_INPUT,false, POPULATE_RESP_HEADER))
                .registerHandler("/success", newRequestHandler(SUBMIT_SUCCESS,true, null))
                .registerHandler("/done", newRequestHandler(SUBMIT_SUCCESS,true, null))
                .registerHandler("/error", newRequestHandler(SUBMIT_FAIL,true, null))
                .registerHandler("/fail", newRequestHandler(SUBMIT_FAIL,true, null))
                .registerHandler("/health", newRequestHandler(HEALTH,false, HEALTH_RESP_CODE))
                .create();
        server.start();

        System.out.println("Server listening in "+port);
    }

    private static HttpRequestHandler newRequestHandler(Function<String, String> op, boolean terminate,
                                                        Consumer<org.apache.http.HttpResponse> responseEnricher) {
        return (request, response, context) -> {
            try {
                RequestLine requestLine = request.getRequestLine();
                String entityContent = requestLine.getUri();
                String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
                if (method.equals("GET") || method.equals("POST")) {
                    if (request instanceof HttpEntityEnclosingRequest) {
                        HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                        if (entity != null){
                            entityContent = EntityUtils.toString(entity);
                        }
                    }
                    response.setEntity(new StringEntity(op.apply(entityContent), ContentType.APPLICATION_JSON));
                    response.setStatusCode(HttpStatus.SC_OK);
                } else {
                    response.setEntity(new StringEntity("Method not allowed. Only GET & POST are supported", ContentType.TEXT_HTML));
                    response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
                }
                if(responseEnricher != null){
                    responseEnricher.accept(response);
                }
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                response.setEntity(new StringEntity(sw.toString(), ContentType.TEXT_HTML));
            } finally {
                if (terminate && props.executionId != null) {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    shutdownServer();
                }
            }
        };
    }

    static class StdErrorExceptionLogger implements ExceptionLogger {
        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                System.err.println("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                System.err.println(ex.getMessage());
            } else {
                ex.printStackTrace();
            }
        }
    }

    static Consumer<org.apache.http.HttpResponse> POPULATE_RESP_HEADER = httpResponse -> {
        if (props.configs != null) {
            String[] names = JSONObject.getNames(props.configs);
            if (names != null) {
                Stream.of(names)
                        .forEach(name -> httpResponse.addHeader(name, props.configs.getString(name)));
            }
        }
    };

    private static void shutdownServer() {
        System.out.println("Shutting down server");
        server.stop();
    }

    static Consumer<org.apache.http.HttpResponse> HEALTH_RESP_CODE = httpResponse -> {
        long now = System.currentTimeMillis();
        long inputTimeOut = props.startTimeInMillis + TimeUnit.MINUTES.toMillis(props.inputTimeout);
        long processTimeOut = props.startTimeInMillis + TimeUnit.MINUTES.toMillis(props.processingTimeout);
        if(now > inputTimeOut && props.executionId == null){
            httpResponse.setStatusCode(408);
            System.out.println("For "+props.inputTimeout+" minutes, lambda didn't read the input yet. So shutdown");
            shutdownServer();
        } else if(now > processTimeOut && props.executionId != null){
            httpResponse.setStatusCode(408);
            String resp = "Lambda didn't complete for more than " + props.processingTimeout + " minutes,  So timeout & shutdown";
            System.out.println(resp);
            try {
                Map<String, Object> body = new HashMap<>();
                body.put(Props.FIELD_VERSION, props.version);
                body.put(Props.FIELD_STATUS, "TIMEOUT");
                body.put(Props.FIELD_RES_BODY, resp);
                updateEvt(new JSONObject(body).toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            shutdownServer();
        }
    };


    static Function<String,String> HEALTH = payload -> {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put(Props.FIELD_VERSION, props.version);
            body.put(Props.FIELD_QRY_APP, props.appName);
            body.put(Props.FIELD_QRY_FUNC, props.functionName);
            return new JSONObject(body).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    static Function<String,String> SUBMIT_FAIL = payload -> {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put(Props.FIELD_VERSION, props.version);
            body.put(Props.FIELD_STATUS, "FAIL");
            body.put(Props.FIELD_RES_BODY, payload);
            return updateEvt(new JSONObject(body).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    static Function<String,String> SUBMIT_SUCCESS = payload -> {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put(Props.FIELD_VERSION, props.version);
            body.put(Props.FIELD_STATUS, "DONE");
            body.put(Props.FIELD_RES_BODY, payload);
            return updateEvt(new JSONObject(body).toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    static Function<String, String> FIND_INPUT = input -> {
        try {
            if (props.executionId != null) {
                HttpResponse<JsonNode> existing = Unirest.get(Props.FIND_EVT_URI + props.executionId).asJson();
                String status = existing.getBody().getObject().getString(Props.FIELD_STATUS);
                if (Objects.equals(status, "NQUE") || Objects.equals(status, "BQUE")) {
                    return String.valueOf(existing.getBody().getObject().get(Props.FIELD_REQ_PAYLOAD));
                }
            }
            HttpResponse<JsonNode> evtResp = Unirest.get(Props.FIND_EXECUTABLE_EVT_URI)
                    .queryString(Props.FIELD_QRY_APP, props.appName)
                    .queryString(Props.FIELD_QRY_FUNC, props.functionName).asJson();
            JsonNode body = evtResp.getBody();
            String[] fieldsInBody = JSONObject.getNames(body.getObject());
            String payload = null;
            if (fieldsInBody == null || fieldsInBody.length == 0) {
                System.out.println("No valid object received. Queue is empty at the moment. Obtained body was " + body.toString());
                return payload;
            } else {
                System.out.println("Object received was " + body);
                payload = String.valueOf(body.getObject().get(Props.FIELD_REQ_PAYLOAD));
            }
            props.version = body.getObject().getInt(Props.FIELD_VERSION);
            props.executionId = body.getObject().getString(Props.FIELD_ID);
            if(body.getObject().has(Props.FIELD_CONFIGS))
                props.configs = body.getObject().getJSONObject(Props.FIELD_CONFIGS);
            else
                props.configs = new JSONObject(emptyMap());
            System.out.println("Payload to be returned is" + payload);

            Map<String, Object> updateCnt = new HashMap<>();
            updateCnt.put(Props.FIELD_VERSION, props.version);
            updateCnt.put(Props.FIELD_STATUS, "BQUE");
            updateEvt(new JSONObject(updateCnt).toString());
            return payload;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    static String updateEvt(String ctnt) throws Exception {
        System.out.println("Updating API server with " + ctnt);
        HttpResponse<JsonNode> evtResp = Unirest.put(Props.UPDATE_EVENT_URI + props.executionId)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()).body(ctnt).asJson();
        System.out.println("Http response is " + evtResp.getStatus());
        JsonNode body = evtResp.getBody();
        System.out.println("Updated successfully with" + body);
        props.version = body.getObject().getInt("version");
        return body.toString();
    }

    //FIXME: refactor this after lambda api exposes contracts as a jar
    static class Props {
        final static String DEF_LAMBDA_SVC_URI = "http://127.0.0.1:8000";
        final static String CONST_LAMBDA_SVC_URI = "LAMBDA_SVC_URI";
        final static String CONST_APP_NAME = "LAMBDA_APP_NAME";
        final static String CONST_FUNC_NAME = "LAMBDA_FUNCTION_NAME";
        final static String CONST_INPUT_READ_TIMEOUT = "INPUT_READ_TIMEOUT";
        final static String CONST_PROCESS_READ_TIMEOUT = "PROCESS_READ_TIMEOUT";

        final static String FIELD_STATUS = "status";
        final static String FIELD_QRY_APP = "app";
        final static String FIELD_QRY_FUNC = "function";
        final static String FIELD_REQ_PAYLOAD = "payload";
        final static String FIELD_RES_BODY = "responseBody";
        final static String FIELD_VERSION = "version";
        final static String FIELD_ID = "id";
        final static String FIELD_CONFIGS = "configs";

        static String LAMBDA_CTRL_URI;
        static String FIND_EXECUTABLE_EVT_URI;
        static String FIND_EVT_URI;
        static String UPDATE_EVENT_URI;
        String appName, functionName, executionId;
        JSONObject configs;
        long startTimeInMillis;
        int version;
        int inputTimeout, processingTimeout;

        void init() {
            String uri = getProp(CONST_LAMBDA_SVC_URI, DEF_LAMBDA_SVC_URI);
            appName = getProp(CONST_APP_NAME, "App1");
            functionName = getProp(CONST_FUNC_NAME, "demo");
            LAMBDA_CTRL_URI = uri + "/lambda/api/v1/executable";
            FIND_EXECUTABLE_EVT_URI = LAMBDA_CTRL_URI + "/event/byPriority";
            UPDATE_EVENT_URI = LAMBDA_CTRL_URI + "/event/";
            FIND_EVT_URI = uri + "/lambda/api/v1/activate/";
            inputTimeout = Integer.getInteger(CONST_INPUT_READ_TIMEOUT,3);
            processingTimeout = Integer.getInteger(CONST_PROCESS_READ_TIMEOUT,15);
            startTimeInMillis = System.currentTimeMillis();
        }

        private static String getProp(String key, String def) {
            String uri = System.getenv(key);
            if (uri == null) {
                uri = System.getProperty(key, def);
            }
            return uri;
        }
    }
}
