package jim.framework.websocket.dto;

import lombok.Data;

/**
 * 客流信息数据
 * @ClassName: TrafficInfoDTO 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author DanielHyw
 * @date 2024年4月11日 上午11:50:46 
 *
 */
@Data
public class TrafficInfoDTO {
	
	/**
	 * 设备id，对应中心服务器的设备id
	 */
	private Long id;

	/**
	 * 设备ip
	 */
	private String ip;
	
	/**
	 * 进入人数--累积
	 */
	private int inCount;
	
	/**
	 * 离开人数--累积
	 */
	private int outCount;
	
	/**
	 * 时间
	 */
	private long time;
}
