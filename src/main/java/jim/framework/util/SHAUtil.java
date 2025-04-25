package jim.framework.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

public class SHAUtil {

	
	/**
	* @Comment SHA1加密密码
	* @Author Ron
	* @Date 2017年9月12日 下午2:46:31
	* @return
	*/
	public static String encodeSHA1(String data) {
	    if(StringUtils.isEmpty(data)){
	        return null;
	    }else{
	        return DigestUtils.sha1Hex(data);
	    }
	} 
	 
}
