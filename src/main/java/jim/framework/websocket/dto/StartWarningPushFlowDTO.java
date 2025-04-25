package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: StartWarningPushFlowDTO 
 * @Description: 开始预警推流数据结构
 * @author DanielHyw
 * @date 2024年3月23日 上午10:47:21 
 *  
 */
@Data
public class StartWarningPushFlowDTO {
	
	/** 
	 * 播放主键，用于判断播放的是哪个推流
	 */ 
	private String playKey;
	
	/** 
	 * 主服务器ip
	 */ 
	private String serverIp;
	
	/** 
	 * 摄像机id
	 */ 
	private int cameraId;
	
	/** 
	 * 推送地址
	 */ 
	private String pushUrl;

}
