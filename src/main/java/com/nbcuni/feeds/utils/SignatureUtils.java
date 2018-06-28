package com.nbcuni.feeds.utils;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;


@Component
public class SignatureUtils {

    public static String getSignature(String requestWithTs, String privateKey) throws Exception {
        byte[] data = (requestWithTs).getBytes("UTF-8");
        byte[] key = privateKey.getBytes("UTF-8");

        return Base64.getEncoder().encodeToString(HmacSHA256(data, key));
    }

    static byte[] HmacSHA256(byte[] data, byte[] key) throws Exception {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }
}
