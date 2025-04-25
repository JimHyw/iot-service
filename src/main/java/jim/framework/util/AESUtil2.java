package jim.framework.util;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AESUtil2 {

    //加密方式
    public static String KEY_ALGORITHM = "AES";
    //数据填充方式
    private static String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    //避免重复new生成多个BouncyCastleProvider对象，因为GC回收不了，会造成内存溢出
    //只在第一次调用decrypt()方法时才new 对象
    private static boolean initialized = false;

    /**
     * 
     * @param originalContent
     * @param encryptKey
     * @param ivByte
     * @return
     */
    public static byte[] encrypt(byte[] originalContent, byte[] encryptKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(encryptKey, KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivByte));
            byte[] encrypted = cipher.doFinal(originalContent);
            return encrypted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES解密
     * 填充模式AES/CBC/PKCS7Padding
     * 解密模式128
     * @param content
     *            目标密文
     * @return
     * @throws Exception 
     * @throws InvalidKeyException 
     * @throws NoSuchProviderException
     */
    public static byte[] decrypt(byte[] content, byte[] aesKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            Key sKeySpec = new SecretKeySpec(aesKey, KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String decrypt(String content, String aesKey, String iv) {
    	byte[] contentByte = Base64.decodeBase64(content);;
        byte[] aesKeyByte = Base64.decodeBase64(aesKey);;
        byte[] ivByte = Base64.decodeBase64(iv);;
        byte[] result = decrypt(contentByte, aesKeyByte, ivByte);
        try {
			return new String(result,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }

    /**BouncyCastle作为安全提供，防止我们加密解密时候因为jdk内置的不支持改模式运行报错。**/
    public static void initialize() {
        if (initialized)
            return;
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    // 生成iv
    public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }
}