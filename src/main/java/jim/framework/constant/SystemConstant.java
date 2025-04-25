package jim.framework.constant;

/**
 * 系统级静态变量
 *
 */
public class SystemConstant {

	/**
	 * 日志交换机
	 */
	public static final String USER_LOG_EXCHANGE = "user_log_exchange";

	/**
	 * 日志队列
	 */
	public static final String USER_LOG_QUEUE = "user_log_queue";

	/**
	 * 日志队列路由键
	 */
	public static final String USER_LOG_ROUTE_KEY = "jim.user.log";

	/**
	 * 正常返回
	 */
	public static final Integer NORMAL_CODE = 200;

	/**
	 * 未登录
	 */
	public static final Integer NOT_LOGIN = 400;

	/**
	 * 没有权限
	 */
	public static final Integer UNAUTHORIZED = 401;

	/**
	 * token超时
	 */
	public static final Integer TOKEN_EXPRIED = 402;

	/**
	 * token非法
	 */
	public static final Integer TOKEN_SIGNATURE = 403;
	
	/**
	 * 已被t下线
	 */
	public static final Integer TOKEN_OUT = 405;
	
	/**
	 * 已被其他地区登录
	 */
	public static final Integer TOKEN_OTHERLOGIN = 406;

	/**
	 * 服务器错误
	 */
	public static final Integer INTERNAL_SERVER_ERROR = 500;

	/**
	 * 异常类大的状态码：操作失败。
	 */
	public static final Integer BUSSINESS_ERROR = 600;
	
	/** 
	* @Fields SOCKET_SESSION_SERVER_ID_KEY : socket连接储存服务器id的key
	*/ 
	public static final String SOCKET_SESSION_SERVER_ID_KEY = "CHANGJU_WEBSOCKET_CODE";

	/** 
	* @Fields SOCKET_LINK_SIGN_KEY : socket连接加密密钥
	*/ 
	public static final String SOCKET_LINK_SIGN_KEY = "LgHNdM8SeDd4C704";
	
	/** 
	* @Fields SOCKET_MESSAGE_SIGN_KEY : socket请求加密密钥
	*/ 
	public static final String SOCKET_MESSAGE_SIGN_KEY = "g3JxTKLfkItk1kmd";

	/** 
	 * udpIP地址
	 */ 
	public static final String UDP_IP = "239.251.0.28";
	
	/** 
	 * udp端口
	 */ 
	public static final int UDP_PORT = 28950;
	
	/** 
	 * socket端口
	 */ 
	public static final int SOCKET_PORT = 28830;
	
	/** 
	 * 系统接口端口
	 */ 
	public static final int HTTP_SYS_PORT = 28800;
	
	/** 
	 * 摄像机接口端口
	 */ 
	public static final int HTTP_CAMERA_PORT = 28810;
	
	/** 
	 * 消息接口端口
	 */ 
	public static final int HTTP_EVENT_PORT = 28820;
	
	/**
	 * topic交换机
	 */
	//public static final String STCSM_COMMON_EXCHANGE = "exchange.stcsm.common";

}
