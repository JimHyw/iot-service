package jim.business.console.model;

import lombok.Data;

/** 
 * @ClassName: DeviceData 
 * @Description: 设备数据
 * @author DanielHyw
 * @date 2024年1月6日 下午5:52:26 
 *  
 */
@Data
public class DeviceData {

	/** 
	 * 设备id，外部提供
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
	 * 是否运行中
	 */
	private boolean enabled;
	
	/**
	 * 设备序列号
	 */
	private String serialNumber;
	
}
