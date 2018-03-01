package com.billy.weixinoauthcodeoptimization.util;

import java.util.ResourceBundle;

/**
 * 从src/main/resources/server.properties里读取数据
 * 使用该类而不使用spring的@Value的原因是，该类可以在页面上直接使用
 */
public class ServerPropertiesUtil {
    private ServerPropertiesUtil() {}

    /**
     * 公众平台上，开发者设置的EncodingAESKey
     */
    private static String encodingAesKey;
    /**
     * 公众平台上，开发者设置的token
     */
    private static String token;
    /**
     * 公众平台appid
     */
    private static String appId;
    /**
     * secret
     */
    private static String secret;
    /**
     * projectBaseUrl
     */
    private static String projectBaseUrl;


    static {
        ResourceBundle rb = ResourceBundle.getBundle("server");
        encodingAesKey = rb.getString("encodingAesKey");
        token = rb.getString("token");
        appId = rb.getString("appId");
        secret = rb.getString("secret");
        projectBaseUrl = rb.getString("project.base.url");
    }

    public static String getEncodingAesKey() {
        return encodingAesKey;
    }

    public static String getToken() {
        return token;
    }

    public static String getAppId() {
        return appId;
    }

    public static String getSecret() {
        return secret;
    }

    public static String getProjectBaseUrl() {
        return projectBaseUrl;
    }
}
