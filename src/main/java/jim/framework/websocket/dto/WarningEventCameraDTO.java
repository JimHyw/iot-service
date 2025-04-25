package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: WarningEventCameraDTO 
 * @Description: 相机预警事件数据--通信用
 * @author DanielHyw
 * @date 2024年3月16日 下午3:48:05 
 *  
 */
@Data
public class WarningEventCameraDTO {

	/**
	 * 相机id,-1为监控相机
	 */
	private int cameraId;
	
	/**
	 * 设备id，监控时有值
	 */
	private Long deviceId;
	
	/**
	 * 服务器ip
	 */
	private String serverIp;

	/**
	 * 相机ip
	 */
	private String cameraIp;
	
	/**
	 * 事件id
	 */
	private String eventId;

	/**
	 * 事件类型
	 */
	private String eventType;

	/**
	 * 事件图片（base64）
	 */
	private String eventImage;
	
	/**
	 * 事件时间
	 */
	private long eventTime;
	
}
