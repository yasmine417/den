package com.example.denticaree;

public interface  ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
