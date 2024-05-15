package com.ulla.binance.client.utils;

public interface SignatureGenerator {
    String getSignature(String payload);
}
