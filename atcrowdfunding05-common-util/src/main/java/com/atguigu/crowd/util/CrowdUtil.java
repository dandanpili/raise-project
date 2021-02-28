package com.atguigu.crowd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.exception.AccessForbiddenException;
import com.atguigu.crowd.exception.LoginFailedException;


/**
 * 尚筹网通用工具方法类
 * @author 邵瑞琳
 *
 */
public class CrowdUtil {
	
	/**
	 * 给远程第三方短信接口发送请求把验证码发送到用户手机上
	 * @param phoneNum	接收验证码的手机号
	 * @param appCode	用来调用第三方短信API的AppCode
	 * @param sign	签名编号
	 * @param skin	模板编号
	 * @param host	短信接口调用的URL地址
	 * @param path	具体发送断行功能的地址
	 * @return
	 */
	public static ResultEntity<String> sendCodeByShortMessage(
				String phone,
				String appcode,
				String sign,
				String skin,
				String host,
				String path
			){
		//String host = "https://feginesms.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        //String path = "/codeNotice";// 【2】后缀
        //String appcode = "4bac7ece03454e4f9b13410582036365"; // 【3】开通服务后 买家中心-查看AppCode
        //String sign = "1"; // 【4】请求参数，详见文档描述
        //String skin = "8"; // 【4】请求参数，详见文档描述
		
		//生成验证码
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < 4; i++) {
			str.append((int)(Math.random() * 10));
		}
		String param = str.toString();
        //String param = "shaoruilin"; // 【4】请求参数，详见文档描述
        //String phone = "15279381891"; // 【4】请求参数，详见文档描述
        String urlSend = host + path + "?sign=" + sign + "&skin=" + skin+ "&param=" + param+ "&phone=" + phone; // 【5】拼接请求链接
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
                                                                                        // (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                //操作成功，把生成的验证码返回
                return ResultEntity.successWithData(param);
//                System.out.println("正常请求计费(其他均不计费)");
//                System.out.println("获取返回的json:");
//                System.out.print(json);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    System.out.println("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    System.out.println("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    System.out.println("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    System.out.println("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    System.out.println("套餐包次数用完 ");
                } else {
                    System.out.println("参数名错误 或 其他错误");
                    System.out.println(error);
                }
                return ResultEntity.failed(error);
            }

        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
	}
	/*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
	}
	/**
	 * 对明文字符串进行MD5加密
	 * @param source 传入的明文字符串
	 * @return 加密结果
	 */
	public static String md5(String source) {
		
		//1.判断source是否有效
		if(source == null || source.length() == 0) {
			//2.如果不是有效字符串就抛出异常
			throw new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
		}
		try {
			//3.获取MessageDigest对象
			String algorithm = "md5";
			
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			
			//4.获取明文字符串对应的字节数组
			byte[] input = source.getBytes();
			
			//5.执行加密
			byte[] output = messageDigest.digest(input);
			
			//6.创建BigInteger对象
			int signum = 1;
			BigInteger bigInteger = new BigInteger(signum, output);
			
			//7.按照16进制将bigInteger的值转换为字符串
			int radix = 16;
			String encoded = bigInteger.toString(radix).toUpperCase();
			
			return encoded;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 判断当前请求是否Ajax请求
	 * @param request
	 * @return
	 * 		true:当前请求是Ajax请求
	 * 		false:
	 */
	public static boolean judgeRequestType(HttpServletRequest request) {
		//获取请求消息头
		String acceptHeader = request.getHeader("Accept");
		String xRequestHeader = request.getHeader("X-Requested-With");
		//判断
		return (acceptHeader != null && acceptHeader.contains("application/json")) 
				||
			   (xRequestHeader != null && xRequestHeader.contains("XMLHttpRequest"));
	}
}
 