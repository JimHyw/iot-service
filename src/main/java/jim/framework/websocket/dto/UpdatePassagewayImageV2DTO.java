package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: UpdatePassagewayImageV2DTO 
 * @Description: 更新通道图片dto v2版本，每次同步单个通道
 * @author DanielHyw
 * @date 2024年3月28日 下午1:49:34 
 *  
 */
@Data
public class UpdatePassagewayImageV2DTO {

	/**
	 * 设备id
	 */
	private Long deviceId;
	
	/**
	 * 通道id
	 */
	private Integer passagewayId;
	
	/** 
	 * base64图片数据
	 */ 
	private String base64Image;
	
}
