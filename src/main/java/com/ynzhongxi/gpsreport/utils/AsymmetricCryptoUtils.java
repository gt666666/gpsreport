package com.ynzhongxi.gpsreport.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Objects;

/**
 * 非对称加密有公钥和私钥两个概念，私钥自己拥有，不能给别人，公钥公开。根据应用的不同，我们可以选择使用不同的密钥加密：
 * 签名：使用私钥加密，公钥解密。用于让所有公钥所有者验证私钥所有者的身份并且用来防止私钥所有者发布的内容被篡改，但是不用来保证内容不被他人获得。
 * 加密：用公钥加密，私钥解密。用于向公钥所有者发布信息,这个信息可能被他人篡改,但是无法被他人获得。
 *
 * @author lixingwu
 */
@Slf4j
public class AsymmetricCryptoUtils {

    /**
     * 构建算法
     */
    private static final String ALGORITHM = "RSA";
    /**
     * ClassPath
     */
    private static final String ROOT_PATH = ClassUtil.getClassPath();

    /**
     * <p>
     * 方法描述：根据名字生成密钥对.
     * 公钥：{classpath}/cer/{name}.cer
     * 私钥：{classpath}/cer/{name}.pfx
     * 如果已经存在公钥或者私钥，将不会重复生成
     * </p>
     * <p> 创建时间：2019-07-08 11:06:40 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param name the name
     * @return 返回密钥对的地址 publicKeyPath：公钥地址、privateKeyPath：私钥地址
     * @author "lixingwu"
     */
    public static HashMap<String, String> generateKeyPair(String name) {
        KeyPair pair = SecureUtil.generateKeyPair(ALGORITHM);
        // 判断私钥和公钥是否已经存在
        String publicKeyPath = "/cer/" + name + ".cer";
        String privateKeyPath = "/cer/" + name + ".pfx";
        String publicKeyUrl = ROOT_PATH + publicKeyPath;
        String privateKeyUrl = ROOT_PATH + privateKeyPath;
        // 只有公钥或者私钥不存在时才生成
        if (!FileUtil.exist(publicKeyUrl) || !FileUtil.exist(privateKeyUrl)) {
            // 保存私钥
            byte[] privateKey = pair.getPrivate().getEncoded();
            FileUtil.writeBytes(privateKey, privateKeyUrl);
            log.info("{}的私钥保存地址：{}", name, privateKeyUrl);
            // 保存公钥
            String publicKey = Base64.encode(pair.getPublic().getEncoded());
            FileUtil.writeUtf8String(publicKey, publicKeyUrl);
            log.info("{}的公钥保存地址：{}", name, publicKeyUrl);
        } else {
            log.info("{}对应的密钥对已存在", name);
        }
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("publicKeyPath", publicKeyPath);
        map.put("privateKeyPath", privateKeyPath);
        return map;
    }

    /**
     * <p> 方法描述：根据名字获取对应的私钥. </p>
     * <p> 创建时间：2019-07-08 12:05:56 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param name the name
     * @return the private key
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成私钥失败
     * @author "lixingwu"
     */
    public static PrivateKey getPrivateKey(String name) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = FileUtil.readBytes(ROOT_PATH + "/cer/" + name + ".pfx");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * <p> 方法描述：获取公钥. </p>
     * <p> 创建时间：2019-07-08 12:08:30 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param name the name
     * @return the public key
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成公钥失败
     * @throws IOException              证书解码失败
     * @author "lixingwu"
     */
    public static PublicKey getPublicKey(String name) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String keyStr = FileUtil.readUtf8String(ROOT_PATH + "/cer/" + name + ".cer");
        byte[] keyBytes = Base64.decode(keyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    ///////////////////////////////////////////////////////////////////
    /////////////////////// 私钥加密，公钥解密 ///////////////////////////
    ///////////////////////////////////////////////////////////////////

    /**
     * <p> 方法描述：私钥加密. </p>
     * <p> 创建时间：2019-07-08 12:18:44 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 内容
     * @param name    名字
     * @return the string
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成私钥失败
     * @author "lixingwu"
     */
    public static String rencrypt(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (Tools.isBlank(content)) {
            return "";
        }
        generateKeyPair(name);
        RSA rsa = new RSA(getPrivateKey(name), null);
        byte[] bytes = rsa.encrypt(content, KeyType.PrivateKey);
        return Convert.toHex(bytes);
    }

    /**
     * <p> 方法描述：公钥解密. </p>
     * <p> 创建时间：2019-07-08 12:18:44 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 内容
     * @param name    名字
     * @return the string
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成公钥失败
     * @author "lixingwu"
     */
    public static String rdecrypt(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        if (Tools.isBlank(content)) {
            return "";
        }
        generateKeyPair(name);
        RSA rsa = new RSA(null, getPublicKey(name));
        byte[] bytes = rsa.decrypt(Convert.hexToBytes(content), KeyType.PublicKey);
        return Convert.hexToStr(Convert.toHex(bytes), CharsetUtil.CHARSET_UTF_8);
    }

    ///////////////////////////////////////////////////////////////////
    /////////////////////// 公钥加密，私钥解密 ///////////////////////////
    ///////////////////////////////////////////////////////////////////

    /**
     * <p> 方法描述：公钥加密. </p>
     * <p> 创建时间：2019-07-08 12:18:44 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 内容
     * @param name    名字
     * @return the string
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成公钥失败
     * @author "lixingwu"
     */
    public static String uencrypt(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        if (Tools.isBlank(content)) {
            return "";
        }
        generateKeyPair(name);
        RSA rsa = new RSA(null, getPublicKey(name));
        byte[] bytes = rsa.encrypt(content, KeyType.PublicKey);
        return Convert.toHex(bytes);
    }

    /**
     * <p> 方法描述：私钥解密. </p>
     * <p> 创建时间：2019-07-08 12:18:44 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 内容
     * @param name    名字
     * @return the string
     * @throws NoSuchAlgorithmException 创建key失败
     * @throws InvalidKeySpecException  生成公钥失败
     * @author "lixingwu"
     */
    public static String udecrypt(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        if (Tools.isBlank(content)) {
            return "";
        }
        generateKeyPair(name);
        RSA rsa = new RSA(getPrivateKey(name), null);
        byte[] bytes = rsa.decrypt(Convert.hexToBytes(content), KeyType.PrivateKey);
        return Convert.hexToStr(Convert.toHex(bytes), CharsetUtil.CHARSET_UTF_8);
    }

    ///////////////////////////////////////////////////////////////////
    ///////////////////// 私钥数据签名，公钥数据验签 //////////////////////
    ///////////////////////////////////////////////////////////////////

    /**
     * <p> 方法描述：私钥数据签名，把内容进行MD5后，使用私钥进行数据加密. </p>
     * <p> 创建时间：2019-07-08 15:15:42 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 签名的内容
     * @param name    名字
     * @return 返回签名MD5
     * @author "lixingwu"
     */
    public static String rsign(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // 内容MD5后私钥数据加密
        String contentMd5 = SecureUtil.md5(content);
        generateKeyPair(name);
        return rencrypt(contentMd5, name);
    }

    /**
     * <p>
     * 方法描述：公钥数据验签.
     * 1.签名内容使用公钥进行数据解密后得到一串MD5，此MD5值就是私钥签名的源数据的MD5值。
     * 2.然后把解密得到的MD5和实际内容的MD5值进行对比，MD5值相同，说明私钥签名的源数据和实际内容一致。
     * </p>
     * <p> 创建时间：2019-07-08 15:37:23 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param name    名字
     * @param sign    签名字符串
     * @param content 需要验证的字符串
     * @return the boolean
     * @author "lixingwu"
     */
    public static boolean rverify(String name, String sign, String content) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        generateKeyPair(name);
        // 签名内容sign公钥数据解密后得到内容MD5
        String rdecrypt = rdecrypt(sign, name);
        // 把解密得到的MD5和实际内容content的MD5进行对比
        return Objects.equals(SecureUtil.md5(content), rdecrypt);
    }

    ///////////////////////////////////////////////////////////////////
    ///////////////////// 公钥数据签名，私钥数据验签 //////////////////////
    ///////////////////////////////////////////////////////////////////

    /**
     * <p> 方法描述：公钥数据签名，把内容进行MD5后，使用公钥进行数据加密. </p>
     * <p> 创建时间：2019-07-08 15:15:42 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param content 签名的内容
     * @param name    名字
     * @return 返回签名MD5
     * @author "lixingwu"
     */
    public static String usign(String content, String name) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        generateKeyPair(name);
        // 内容MD5后私钥数据加密
        String contentMd5 = SecureUtil.md5(content);
        return uencrypt(contentMd5, name);
    }

    /**
     * <p>
     * 方法描述：私钥数据验签.
     * 1.签名内容使用私钥进行数据解密后得到一串MD5，此MD5值就是公钥签名的源数据的MD5值。
     * 2.然后把解密得到的MD5和实际内容的MD5值进行对比，MD5值相同，说明公钥签名的源数据和实际内容一致。
     * </p>
     * <p> 创建时间：2019-07-08 15:37:23 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param name    名字
     * @param sign    签名字符串
     * @param content 需要验证的字符串
     * @return the boolean
     * @throws InvalidKeySpecException  the invalid key spec exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException              the io exception
     * @author "lixingwu"
     */
    public static boolean uverify(String name, String sign, String content) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        generateKeyPair(name);
        // 签名内容sign公钥数据解密后得到内容MD5
        String udecrypt = udecrypt(sign, name);
        // 把解密得到的MD5和实际内容content的MD5进行对比
        return Objects.equals(SecureUtil.md5(content), udecrypt);
    }
}
