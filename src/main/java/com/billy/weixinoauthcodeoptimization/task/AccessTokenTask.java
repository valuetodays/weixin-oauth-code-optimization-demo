package com.billy.weixinoauthcodeoptimization.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.billy.weixinoauthcodeoptimization.util.HttpClientUtil;
import com.billy.weixinoauthcodeoptimization.util.ServerPropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时更新access_token，策略是每2分钟刷新一下调试任务，
 * 当上个access_token的创建时间在110分钟之后时，就重新刷新
 */
@Component
public class AccessTokenTask {
    public static String secret = ServerPropertiesUtil.getSecret();
    public static String appId = ServerPropertiesUtil.getAppId();
    private static AccessTokenEntity ACCESS_TOKEN = new AccessTokenEntity(null);
    private static long OFFSET_TIME = 110*60*1000; // 110分钟
    private static Logger LOG = Logger.getLogger(AccessTokenTask.class);

    public String get() {
        String accessToken = ACCESS_TOKEN.getAccessToken();
        if (accessToken == null) {
            doRefresh();
        }
        return ACCESS_TOKEN.getAccessToken();
    }


    /**
     */
    @Scheduled(cron = "0 0/2 * * * *") // 每隔2分钟开始工作
    public void refresh() {
        String accessToken = ACCESS_TOKEN.getAccessToken();
        long createAt = ACCESS_TOKEN.getCreateAt();

        if (accessToken == null || System.currentTimeMillis() - createAt > OFFSET_TIME) {
            doRefresh();
        }
    }


    /**
     * 该方法置为public会有其它风险
     */
    public void doRefresh() {
        LOG.info("access_token is going to expire, refreshing....");
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appId + "&secret=" + secret;
        String doGet = HttpClientUtil.doGet(url, null, null);

        LOG.info("result: " + doGet);
        if (StringUtils.isNotBlank(doGet)) {
            JSONObject accessTokenObject = JSON.parseObject(doGet);
            Object object = accessTokenObject.get("access_token");
            String accessTokenStr = ((object == null) ? null : object.toString());
            put(accessTokenStr);
        }
    }


    private synchronized void put(String accessTokenStr) {
        if (accessTokenStr == null) {
            return ;
        }

        ACCESS_TOKEN = new AccessTokenEntity(accessTokenStr);
        LOG.info("access_token["+accessTokenStr+"] was refreshed.");
    }

    private static class AccessTokenEntity {
        final private String accessToken;
        final private long createAt;

        public AccessTokenEntity(String accessToken) {
            this.accessToken = accessToken;
            createAt = System.currentTimeMillis();
        }

        public String getAccessToken() {
            return accessToken;
        }
        public long getCreateAt() {
            return createAt;
        }


    }


}
