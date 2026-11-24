package com.example.apimonitor.Service;

import com.example.apimonitor.Dtos.Checkers.CheckRequest;
import com.example.apimonitor.Dtos.Checkers.CheckResponse;
import com.example.apimonitor.exception.InvalidUrlException;
import com.example.apimonitor.exception.UnsafeDestinationException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final RestClient restClient;

    public CheckResponse checkApi(CheckRequest checkRequest) {

        String url = checkRequest.url();

        validateDestination(url);

        Integer statusCode = null;
        boolean success = false;
        String failureType = null;

        long startTime = System.nanoTime();

        try {

            statusCode = restClient.get()
                    .uri(URI.create(url))
                    .exchange((request, response) ->
                            response.getStatusCode().value());

            success = statusCode >= 200 && statusCode < 300;

            if (!success) {
                failureType = "HTTP_ERROR";
            }

        } catch (ResourceAccessException e) {

            if (hasTimeoutCause(e)) {
                failureType = "TIMEOUT";
            } else {
                failureType = "NETWORK_ERROR";
            }

        } catch (RestClientException e) {

            failureType = "HTTP_CLIENT_ERROR";
        }

        long responseTimeMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime
        );

        return new CheckResponse(
                url,
                statusCode,
                responseTimeMs,
                success,
                failureType
        );
    }


    private boolean hasTimeoutCause(Throwable exception) {

        Throwable cause = exception;

        while (cause != null) {

            if (cause instanceof HttpTimeoutException
                    || cause instanceof SocketTimeoutException) {

                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }


    private void validateDestination(String url) {

        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL cannot be empty");
        }

        URI uri;

        try {

            uri = new URI(url);

        } catch (URISyntaxException e) {

            throw new InvalidUrlException(e.getMessage());
        }

        if (uri.getScheme() == null || uri.getHost() == null) {

            throw new InvalidUrlException(
                    "URL must contain a valid scheme and host"
            );
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || !"example.com".equalsIgnoreCase(uri.getHost())
                || (uri.getPort() != -1 && uri.getPort() != 443)
                || uri.getRawUserInfo() != null
                || uri.getRawFragment() != null) {

            throw new UnsafeDestinationException(
                    "Only https://example.com is allowed for now"
            );
        }
    }

}