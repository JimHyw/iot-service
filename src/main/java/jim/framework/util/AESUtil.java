package jim.framework.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.LoggerFactory;

/**
 * AES加解密类
 */
public class AESUtil {
	
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(AESUtil.class);
	
	private static final String KEY_ALGORITHM = "AES";
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";// 默认的加密算法

	/**
	 * AES 加密操作
	 *
	 * @param content
	 *            待加密内容
	 * @param password
	 *            加密密码
	 * @return 返回Base64转码后的加密数据
	 */
	public static String encrypt(String content, String password) {
		try {
			Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

			byte[] byteContent = content.getBytes("utf-8");

			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));// 初始化为加密模式的密码器

			byte[] result = cipher.doFinal(byteContent);// 加密

			return Base64.getEncoder().encodeToString(result);// 通过Base64转码返回
		} catch (Exception ex) {
			Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		return null;
	}

	/**
	 * AES 解密操作
	 *
	 * @param content
	 * @param password
	 * @return
	 */
	public static String decrypt(String content, String password) {

		try {
			// 实例化
			Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

			// 使用密钥初始化，设置为解密模式
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));

			// 执行操作
			byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));

			return new String(result, "utf-8");
		} catch (Exception ex) {
			Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		return null;
	}

	/**
	 * 生成加密秘钥
	 *
	 * @return
	 */
	private static SecretKeySpec getSecretKey(final String password) {
		// 返回生成指定算法密钥生成器的 KeyGenerator 对象
		KeyGenerator kg = null;

		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);

			// AES 要求密钥长度为 128
			//kg.init(128, new SecureRandom(password.getBytes()));
			
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.setSeed(password.getBytes());
			kg.init(128, random);

			// 生成一个密钥
			SecretKey secretKey = kg.generateKey();

			return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		return null;
	}
	
	/**
     * AES加密
     *
     * @param plaintext 明文
     * @param Key 密钥
     * @param EncryptMode AES加密模式，CBC或ECB
     * @return 该字符串的AES密文值
     */
    public static String AES_Encrypt(Object plaintext, String Key, String EncryptMode) {
        String PlainText=null;
        try {
            PlainText=plaintext.toString();
            if (Key == null) {
                return null;
            }
            // Key = getMD5(Key);
            byte[] raw = Key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/"+EncryptMode+"/PKCS5Padding");
            if(EncryptMode=="ECB") {
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            }else {
                IvParameterSpec iv = new IvParameterSpec(Key.getBytes("utf-8"));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            }
            byte[] encrypted = cipher.doFinal(PlainText.getBytes("utf-8"));
            String encryptedStr=new String(Base64.getEncoder().encodeToString(encrypted));
            return encryptedStr;
            //return new String(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception ex) {
            return null;
        }
    }
	
	/**
     * AES解密
     *
     * @param cipertext 密文
     * @param Key 密钥
     * @param EncryptMode AES加密模式，CBC或ECB
     * @return 该密文的明文
     */
    public static String AES_Decrypt(Object cipertext, String Key, String EncryptMode) {
        String CipherText=null;
        try {
            CipherText=cipertext.toString();
            // 判断Key是否正确
            if (Key == null) {
                return null;
            }

            byte[] raw = Key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher=Cipher.getInstance("AES/"+EncryptMode+"/PKCS5Padding");

            if(EncryptMode=="ECB") {
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            }
            else
            {
                IvParameterSpec iv = new IvParameterSpec(Key.getBytes("utf-8"));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            }
            byte[] encrypted1 = Base64.getDecoder().decode(CipherText.getBytes());//先用base64解密
            //byte[] encrypted1 = CipherText.getBytes();
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

	/**
	 * 生成随机码值，包含数字、大小写字母
	 * @param number
	 *            生成的随机码位数
	 * @return
	 */
	public static String getRandomCode(int number) {
		String codeNum = "";
		int[] code = new int[3];
		Random random = new Random();
		for (int i = 0; i < number; i++) {
			int num = random.nextInt(10) + 48;
			int uppercase = random.nextInt(26) + 65;
			int lowercase = random.nextInt(26) + 97;
			code[0] = num;
			code[1] = uppercase;
			code[2] = lowercase;
			codeNum += (char) code[random.nextInt(3)];
		}
		logger.debug(codeNum);

		return codeNum;
	}

}
