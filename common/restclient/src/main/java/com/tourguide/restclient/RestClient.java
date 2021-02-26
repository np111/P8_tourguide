package com.tourguide.restclient;

import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestClient {
    private final HttpUrl baseUrl;
    private final RestSerializer serializer;
    private final OkHttpClient httpClient;

    @lombok.Builder(builderClassName = "Builder")
    private RestClient(
            String baseUrl,
            RestSerializer serializer,
            Integer maxRequests,
            Integer maxRequestsPerHost,
            Integer maxIdleConnections,
            Duration keepAliveDuration,
            Duration callTimeout
    ) {
        Objects.requireNonNull(baseUrl, "baseUrl cannot be null");
        Objects.requireNonNull(serializer, "serializer cannot be null");
        maxRequests = initDefault(maxRequests, 64);
        maxRequestsPerHost = initDefault(maxRequestsPerHost, maxRequests);
        maxIdleConnections = initDefault(maxIdleConnections, 5);
        keepAliveDuration = initDefault(keepAliveDuration, Duration.ofMinutes(5));
        callTimeout = initDefault(callTimeout, Duration.ofSeconds(30));

        this.baseUrl = HttpUrl.parse(baseUrl);
        this.serializer = serializer;
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(maxRequests);
        dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
        this.httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration.toMillis(), TimeUnit.MILLISECONDS))
                .dispatcher(dispatcher)
                .callTimeout(callTimeout)
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ZERO)
                .writeTimeout(Duration.ZERO)
                .build();
    }

    public <T> CompletableFuture<T> call(RestCall<T> call) {
        Request.Builder req = new Request.Builder();

        HttpUrl.Builder url = baseUrl.newBuilder();
        call.paths.forEach(url::addPathSegment);
        for (Iterator<String> it = call.queryParams.iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = it.next();
            url.addQueryParameter(key, value);
        }
        req.url(url.build());

        CompletableFuture<T> ret = new CompletableFuture<>();
        httpClient
                .newCall(req.build())
                .enqueue(new Callback() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponse(Call okCall, Response response) {
                        //noinspection TryFinallyCanBeTryWithResources
                        try {
                            int httpStatus = response.code();
                            switch (httpStatus / 100) {
                                case 2:
                                    Object bodyObj;
                                    if (httpStatus == 204) {
                                        bodyObj = null;
                                    } else {
                                        Reader body = Objects.requireNonNull(response.body()).charStream();
                                        bodyObj = call.responseList ? serializer.deserializeList(body, call.responseType) : serializer.deserialize(body, call.responseType);
                                    }
                                    ret.complete((T) bodyObj);
                                    break;

                                case 4:
                                    // TODO: read error object

                                default:
                                    throw new IllegalStateException("Unsupported HTTP status: " + httpStatus);
                            }
                        } catch (Throwable ex) {
                            ret.completeExceptionally(ex);
                        } finally {
                            response.close();
                        }
                    }

                    @Override
                    public void onFailure(Call okCall, IOException ex) {
                        ret.completeExceptionally(ex);
                    }
                });
        return ret;
    }

    private static <T> T initDefault(T a, T b) {
        return a != null ? a : b;
    }
}
