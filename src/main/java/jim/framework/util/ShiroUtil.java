package jim.framework.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import jim.framework.system.model.EUserType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ShiroUtil 存放当前用户信息
 *
 */
public class ShiroUtil {
	private static TransmittableThreadLocal<ConcurrentHashMap<String,String>> idLocal= new TransmittableThreadLocal<>();

	/**
	 * 获取当前用户ID
	 * @return
	 */
	public static String getUserId() {
		return idLocal.get() == null ? null : idLocal.get().get("userId");
	}
	
	public static EUserType getUserType() {
		return idLocal.get() == null ? EUserType.manager : EUserType.valueOf(idLocal.get().get("userType"));
	}
	
	/** 
	* @Title: getUserIp 
	* @Description: 获取当前用户ip
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public static String getUserIp() {
		return idLocal.get() == null ? null : idLocal.get().get("userIp");
	}
	
	/** 
	* @Title: getUserSourceType 
	* @Description: 获取当前用户来源类型，1pc，2移动端
	* @return  参数说明 
	* @return String    返回类型 
	* 
	*/
	public static String getUserSourceType() {
		return idLocal.get() == null ? null : idLocal.get().get("userSourceType");
	}
	
	/**
	 * 设置当前用户ID
	 * @param userId
	 */
	// public static void setUserId(String userId){
	// 	idLocal.set(userId);
	// }

	/**
	 * 设置token解析的用户信息
	 * @param map
	 */
	public static void set(ConcurrentHashMap<String,String> map){
		idLocal.set(map);
	}

	public static ConcurrentHashMap<String,String> getMap(){
		return idLocal.get();
	}

//	/**
//	 * 获取当前用户服务系统标识
//	 * @return
//	 */
//	public static String getUserClientType() {
//		return idLocal.get() == null ? null : idLocal.get().get("userClientType");
//	}

}
