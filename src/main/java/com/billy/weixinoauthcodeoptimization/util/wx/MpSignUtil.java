package com.billy.weixinoauthcodeoptimization.util.wx;

import com.billy.weixinoauthcodeoptimization.entity.wx.MpBindMsg;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * 请求校验工具类，用于服务端验证来自微信的消息的准确性
 */
public class MpSignUtil {
    private static String token = WXBizMsgCrypt.token;

    private MpSignUtil() {}

    /**
     * 验证签名是否正确
     * @param mpMsg mpMsg
     */
    public static boolean checkSignature(MpBindMsg mpMsg) {
        if (mpMsg == null) {
            return false;
        }
        return checkSignature(mpMsg.getSignature(),
                mpMsg.getTimestamp(),
                mpMsg.getNonce(), mpMsg.getEchostr());
    }

    /**
     * 验证签名
     *
     * @param signature signature
     * @param timestamp timestamp
     * @param nonce nonce
     */
    private static boolean checkSignature(String signature, String timestamp,
                                         String nonce, String echostr) {
        if (signature == null || timestamp == null || nonce == null) {
            return false;
        }

        String[] arr = new String[]{token, timestamp, nonce};
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null && tmpStr.equals(signature.toUpperCase());
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray byteArray
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte mByte
     */
    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        return new String(tempArr);
    }
}
