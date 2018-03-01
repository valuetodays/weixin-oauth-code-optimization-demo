package com.billy.weixinoauthcodeoptimization.controller.wx;

import com.billy.weixinoauthcodeoptimization.entity.wx.MpBindMsg;
import com.billy.weixinoauthcodeoptimization.task.AccessTokenTask;
import com.billy.weixinoauthcodeoptimization.util.XmlStringUtil;
import com.billy.weixinoauthcodeoptimization.util.wx.MpSignUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 微信绑定
 *
 * to receive msg from mp.weixin.com and return back
 *
 * @author liulei
 */
@Controller
@RequestMapping("/wxmpBind")
public class WxmpBindController {
    private static Logger LOG = Logger.getLogger(WxmpBindController.class);

    @Autowired
    private AccessTokenTask accessTokenTask;
    private static String ERROR_MSG = "error config";

    /**
     * check this server can be visited by mp.weixin.com or not
     *
     * @param mpMsg the common parameters
     */
    @GetMapping("index")
    @ResponseBody
    public String doGet(MpBindMsg mpMsg) throws IOException {
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (MpSignUtil.checkSignature(mpMsg)) {
            ERROR_MSG = mpMsg.getEchostr();
        }

        return ERROR_MSG;
    }

    /**
     * 绑定成功后微信会通过向该接口发送消息
     *
     * @param request request
     */
    @PostMapping("index")
    @ResponseBody
    public String doPost(HttpServletRequest request) throws
            Exception {
        String finalMsg = "success"; // 默认返回
        String stringFromRequest = retrieveStringFromRequest(request.getInputStream());
        Map<String, String> map = XmlStringUtil.xmlString2Map(stringFromRequest);
        LOG.info("data from wx: " + map);
        LOG.info("respMsg: " + finalMsg);
        return finalMsg;
    }


    private String retrieveStringFromRequest(ServletInputStream sis) {
        InputStreamReader isr = null;
        StringBuffer sb = new StringBuffer(1024);
        try {
            isr = new InputStreamReader(sis, "utf-8");
            BufferedReader br = new BufferedReader(isr);

            String readLine = "";
            while (readLine != null) {
                sb.append(readLine);
                try {
                    readLine = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            IOUtils.closeQuietly(br);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(isr);
        }

        return sb.toString();
    }

    @GetMapping("refreshAccessToken")
    @ResponseBody
    public String refreshAccessToken() {
        accessTokenTask.doRefresh();
        return getAccessToken();
    }

    @GetMapping("getAccessToken")
    @ResponseBody
    public String getAccessToken() {
        return accessTokenTask.get();
    }

}
