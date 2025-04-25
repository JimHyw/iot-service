package jim.business.console.model;

import lombok.Data;

/**
 * 人流设备数据
 * @ClassName: TrafficDeviceData 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author DanielHyw
 * @date 2024年4月11日 上午11:07:05 
 *
 */
@Data
public class TrafficDeviceData {
	/**
	 * 设备id，中心服务器的
	 */
	private Long id;
	
	/**
	 * ip
	 */
	private String ip;
	
	/**
	 * 端口
	 */
	private short port;
	
	/**
	 * 账号
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 运行中
	 */
	private boolean enabled;

}
