package com.qcloud.live.handles;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* @author hwang 
* @E-mail wanghao@dingjintz.com
* @version 1.0
* @创建时间 2017年5月2日 下午4:33:56
* MD5.java
* 说明 ☞ 实现MD5加密
*/
public class MD5 {
	//静态方法，便于作为工具类  
    public static String getMd5(String plainText) {  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(plainText.getBytes());  
            byte b[] = md.digest();  
            int i;  
  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0)  
                    i += 256;  
                if (i < 16)  
                    buf.append("0");  
                buf.append(Integer.toHexString(i));  
            }  
            //32位加密  
            return buf.toString();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
}
