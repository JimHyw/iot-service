package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: WarningEventCameraStatusDTO 
 * @Description: 相机状态事件数据
 * @author DanielHyw
 * @date 2024年3月16日 下午5:31:23 
 *  
 */
@Data
public class WarningEventCameraStatusDTO {

	/** 
	 * 摄像机id
	 */ 
	private int cameraId;
	
	/**
	 * 服务器ip
	 */
	private String serverIp;
	
	/** 
	 * 摄像机名称
	 */ 
	private String cameraName;
	
	/** 
	 * 相机状态
	 */ 
	private String status;
}
