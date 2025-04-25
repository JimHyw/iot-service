package jim.framework.websocket.dto;

import java.util.Date;

import lombok.Data;

/** 
 * @ClassName: StartPlaybackPushFlowDTO 
 * @Description: 开始回放推流
 * @author DanielHyw
 * @date 2024年1月12日 下午4:56:12 
 *  
 */
@Data
public class StartPlaybackPushFlowDTO {
	
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
	
	/** 
	 * 开始时间
	 */ 
	private Date startTime;
	
	/** 
	 * 结束时间
	 */ 
	private Date endTime;
}
