package jim.framework.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * MD5加密工具
 */
public class MD5Util {

	private static final String SALT = "gytzgtofz20328";

	private static final String ALGORITH_NAME = "md5";

	private static final int HASH_ITERATIONS = 2;

	/**
	 * 使用md5生成加密后的密码
	 * 
	 * @param pswd
	 * @return
	 */
	public static String encrypt(String pswd) {
		String newPassword = new SimpleHash(ALGORITH_NAME, pswd,
				ByteSource.Util.bytes(SALT), HASH_ITERATIONS).toHex();
		return newPassword;
	}

	/**
	 * 使用md5生成加密后的密码
	 * 
	 * @param username
	 * @param pswd
	 * @return
	 */
	public static String encrypt(String username, String pswd) {
		String newPassword = new SimpleHash(ALGORITH_NAME, pswd,
				ByteSource.Util.bytes(username + SALT), HASH_ITERATIONS)
				.toHex();
		return newPassword;
	}

}
