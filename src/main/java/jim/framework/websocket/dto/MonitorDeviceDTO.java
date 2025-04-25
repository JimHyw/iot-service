package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: MonitorDeviceDTO 
 * @Description: 设备数据
 * @author DanielHyw
 * @date 2024年1月10日 下午2:11:49 
 *  
 */
@Data
public class MonitorDeviceDTO {
	
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

}
