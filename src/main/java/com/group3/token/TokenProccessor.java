package com.group3.token;

/**
 * @author group3
 * @date 2024/04
 */

import java.util.UUID;

public class TokenProccessor {

    /*
     *Single case design pattern (guarantee that there is only one object of the class in memory)
     *1. Make the constructor of the class private
     *2. Create an object of your own class
     *3. Provide a public method to the outside world and return an object of the class.
     */
    private TokenProccessor() {
    }

    private static final TokenProccessor instance = new TokenProccessor();

    /**
     * @return
     */
    public static TokenProccessor getInstance() {
        return instance;
    }

    /**
     * generate Token
     * Token：Nv6RRuGEVvmGjB+jimI/gw==
     *
     * @return
     */
    public String makeToken() {  //checkException
        //  7346734837483  834u938493493849384  43434384
//        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";
        //数据指纹   128位长   16个字节  md5
//        try {
//            MessageDigest md = MessageDigest.getInstance("md5");
//            byte md5[] = md.digest(token.getBytes());
//            //base64编码--任意二进制编码明文字符   adfsdfsdfsf
//            Base64 encoder = new BASE64Encoder();
//            return encoder.encode(md5);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
        return UUID.randomUUID().toString();
    }
}