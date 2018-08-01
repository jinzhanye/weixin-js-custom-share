package cn.wzzy.wzzy.controller;

import cn.wzzy.wzzy.annotation.Unsecured;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;

@RequestMapping(value = "/weixin")
@Api(value = "微信请求token", description = "微信请求token")
public class WeixinController {

    public static String APPID="appid";
    private static String APPSECRET="appsecret";

    @RequestMapping(value="access_token",method = RequestMethod.GET)
    public String access_token(){
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APPID + "&secret=" + APPSECRET;
        String access_token=null;
        String expires_in=null;
        try {
            URL urlGet=new URL(url);
            HttpURLConnection http=(HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.connect();
            InputStream is=http.getInputStream();
            int size=is.available();
            byte[] jsonBytes=new byte[size];
            is.read(jsonBytes);
            String message=new String(jsonBytes,"UTF-8");
            JSONObject json= JSON.parseObject(message);
            access_token=json.getString("access_token");
            expires_in=json.getString("expires_in");
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return access_token;
    }

    @RequestMapping(value="jsapi_ticket",method = RequestMethod.GET)
    public String jsapi_ticket(final String access_token) {
        String str = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";
        String jsapi_ticket = null;
        String expiresin = null;
        try {
            URL url = new URL(str);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject json =JSON.parseObject(message);
            jsapi_ticket = json.getString("ticket");
            expiresin = json.getString("expires_in");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsapi_ticket;
    }

    @Unsecured
    @RequestMapping(value="sign",method = RequestMethod.GET)
    public JSONObject sign(String url) {
        url = url.replaceAll("&", "%26");
        String token=access_token();
        String jsapi_ticket=jsapi_ticket(token);
        JSONObject jsons = new JSONObject();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";
        // 注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //需要返回的参数
        jsons.put("appId", APPID);
        jsons.put("url", url);
        jsons.put("jsapi_ticket", jsapi_ticket);
        jsons.put("nonceStr", nonce_str);
        jsons.put("timestamp", timestamp);
        jsons.put("signature", signature);
        return jsons;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    // 获得我们的​noncestr
    private String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    // 获得我们的timestamp
    private String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

}
