package jim.framework.websocket.dto;

import java.util.Date;

import lombok.Data;

/** 
 * @ClassName: StartWarningPlaybackPushFlowDTO 
 * @Description: 开始回放预警推流
 * @author DanielHyw
 * @date 2024年3月23日 上午10:54:35 
 *  
 */
@Data
public class StartWarningPlaybackPushFlowDTO {
	
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
	
	/** 
	 * 开始时间
	 */ 
	private Date startTime;
	
	/** 
	 * 结束时间
	 */ 
	private Date endTime;
}
