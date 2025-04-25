package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: UpdateWarningCameraImageDTO 
 * @Description: 更新预警相机图片dto
 * @author DanielHyw
 * @date 2024年3月28日 下午2:16:52 
 *  
 */
@Data
public class UpdateWarningCameraImageDTO {

	/**
	 * 设备id
	 */
	private String serverIp;
	
	/**
	 * 通道id
	 */
	private int cameraId;
	
	/** 
	 * base64图片数据
	 */ 
	private String base64Image;
	
}
