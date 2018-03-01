package com.billy.weixinoauthcodeoptimization.util;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * httpclient util.
 *  it contains:
 *      <ul>
 *          <li>send request by GET</li>
 *          <li>send request by POST</li>
 *          <li>post file </li>
 *          <li>download file </li>
 *      </ul>
 *
 * 
 * @author liulei
 * @since  2016-04-01 11:21
 */
public class HttpClientUtil {
    // default charset 'utf-8'
    private static final String DEFAULT_CHARSET = "utf-8";

    // make constructor private
    private HttpClientUtil () { }

    /**
     * close <code>CloseableHttpResponse</code> and <code>CloseableHttpClient</code>
     * 
     * @param response <code>CloseableHttpResponse</code>
     * @param httpclient <code>CloseableHttpClient</code>
     */
    private static void close(CloseableHttpResponse response,
            CloseableHttpClient httpclient) {
        if (response != null) {
            try {
                response.close();
                response = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (httpclient != null) {
            try {
                httpclient.close();
                httpclient = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * do get request
     * @param url url
     * @param params parameter map
     * @param charset charset, {@link HttpClientUtil#DEFAULT_CHARSET} is default
     * @return string
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        return doGet(url, null, params, charset);
    }

    public static String doGet(String url, Map<String, String> headers, Map<String, String> params, String charset) {
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        String paramString = "";
        if (MapUtils.isNotEmpty(params)) {
            Set<Entry<String, String>> entrySet = params.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue();
                paramString += "&" + key + "=" + value;
            }
        }

        if (!url.contains("?")) {
            paramString = paramString.replaceFirst("&", "?");
        }
        String urlpath = url + paramString;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String ret = null;
        try {
            HttpGet httpget = new HttpGet(urlpath);
            if (MapUtils.isNotEmpty(headers)) {
                Set<Entry<String, String>> entries = headers.entrySet();
                for (Entry<String, String> entry : entries) {
                    httpget.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            ret = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(response, httpclient);
        }

        return ret;
    }

    /**
     * do post request
     * @param url url
     * @param params params map
     * @param charset charset, {@link HttpClientUtil#DEFAULT_CHARSET} is default
     * @return
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String ret = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (params != null && !params.isEmpty()) {
                Set<Entry<String, String>> entrySet = params.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs.add(new BasicNameValuePair(key, value));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, charset));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            ret = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(response, httpclient);
        }

        return ret;
    }

    /**
  * 发送HTTP请求
  * @param url
  * @param parameter
  * @return
  * @throws Exception
  */
 public static String doPostString(String url,String parameter) {
     CloseableHttpClient httpclient = HttpClients.createDefault();
     CloseableHttpResponse response = null;
     String ret = "";
     try {
         HttpPost httpPost = new HttpPost(url);
         HttpEntity entityReq = new StringEntity(parameter, 
                 CharsetUtils.get("utf-8"));  
         httpPost.setEntity(entityReq);
         httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");  
         response = httpclient.execute(httpPost);
         if (response.getStatusLine().getStatusCode() == 200) {
             HttpEntity entity = response.getEntity();
             ret = EntityUtils.toString(entity);
             EntityUtils.consume(entity);
             ret = new String(ret.getBytes("iso8859-1"), "utf-8");
         } 
     } catch (Exception e) {
         e.printStackTrace();
     } finally {
         close(response, httpclient);
     }

     return ret;
 }

    /**
     * download file
     * @param url url
     * @param filePathTo to
     */
    public static void doDownloadFile(String url, String filePathTo) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(url);
        try {
            get.setHeader("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
            response = httpclient.execute(get);

            File storeFile = new File(filePathTo);
            FileOutputStream output = new FileOutputStream(storeFile);
            HttpEntity entity = response.getEntity();
            InputStream instream = null;
            if (entity != null) {
                instream = entity.getContent();
                byte b[] = new byte[2048];
                int j = 0;
                while ((j = instream.read(b)) != -1) {
                    output.write(b, 0, j);
                }
            }
            output.flush();
            EntityUtils.consume(entity);
            if (output != null) {
                output.close();
            }
            if (instream != null) {
                instream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(response, httpclient);
        }
    }
    
}
