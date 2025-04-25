package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: StartPushFlowDTO 
 * @Description: 开始推送数据结构
 * @author DanielHyw
 * @date 2024年1月10日 下午3:17:18 
 *  
 */
@Data
public class StartPushFlowDTO {

	/**
	 * 播放主键，用于判断播放的是哪个推流
	 */
	private String playKey;
	
	/** 
	 * 设备id
	 */ 
	private Long deviceId;
	
	/** 
	 * 通道id
	 */ 
	private Integer passagewayId;
	
	/** 
	 * 推送地址
	 */ 
	private String pushUrl;
}
