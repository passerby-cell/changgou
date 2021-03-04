package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;


public class CreateJwtTestDemo {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken() {
        //加载证书,读取类路径中的文件
        ClassPathResource resource = new ClassPathResource("changgou.jks");
        //读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "changgou".toCharArray());
        //获取证书中的一对密钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("changgou", "changgou".toCharArray());
        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //创建令牌，私钥加盐【RSA算法】
        Map<String,Object> payLoad=new HashMap<>();
        payLoad.put("name","tomcat");
        payLoad.put("password","tomcat");
        payLoad.put("age",14);
        payLoad.put("role","user");
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payLoad), new RsaSigner(privateKey));
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
    @Test
    public void test(){
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjA0Njc1NjEwMywiYXV0aG9yaXRpZXMiOlsidmlwIiwiYWRtaW4iLCJ1c2VyIl0sImp0aSI6ImU5MWZkNGY3LTExNzItNDM5Yy04ODE4LTc1ODEzNThjM2RkMiIsImNsaWVudF9pZCI6ImNoYW5nZ291IiwidXNlcm5hbWUiOiJzeml0aGVpbWEifQ.NmoMVOgz78r7Ogp7bcsLrm_bKzIcy1fLJidz5JkjmeJ34Ogs_WSHokilfJCVzB7ykbCHQwJ5iLn6ktuzGJDLnJ23POl1mzUiAJAHw_n5eKroqzKKwK0N0VFJKZ5_B-9T5DBDXJDyYn1yw1pHT_3VU4fG0Zknctx72OqO2MmaFBeu3UADdhfjamJMOLX9ZS1KVdoV6kexqFeazxNXpRI_W_0Cqp4_z86DI5PSVwRuhueIspjQJCWj7mWgmnK4gl1kaxpNbGGn0bxdBGFsz6D6CNl3aeK0K0Np-q7iNRLgv2cJuWNFj2MOxrsAWWIQe3NH--xFrPLlWgn791S9StV7zQ";
        String publicKey="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlpR64WW3w2IxECN1p4POFhh/fyCwQEu3cvue76JTJjh4ZMcw6+FJtnKWbNTWfKXHdhfLYmjORbP1nahlB50jlAMBNH2lJCKCJj90XHOczJxQxhaHcrboio33P6MvVLIjRMgrSFC394YjWZYy5l+fMsPBN2ahvN94DCfk3GSSNOEP+VWvw7ocYuFZk6lWerS76WpfbedaWfni0xxnMVnKbvOzMDWwBq1PeZfQflyn1EUST6OpplG+6A0NSZZxSHRMC/pcP2oWlBCZ6tw6ZOEoqwl6domjSCR5TCKm5fYm4s0LU28z6ir5iKYze+zQsc0ESqEWgnI+OELOLnKcq8p4aQIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        String s = jwt.getClaims().toString();
        System.out.println(s);


    }
}
