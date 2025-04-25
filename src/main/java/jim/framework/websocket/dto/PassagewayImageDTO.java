package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: PassagewayImageDTO 
 * @Description: 通道图片数据
 * @author DanielHyw
 * @date 2024年1月13日 下午5:40:09 
 *  
 */
@Data
public class PassagewayImageDTO {

	/**
	 * 通道id
	 */
	private Integer passagewayId;
	
	/** 
	 * base64图片数据
	 */ 
	private String base64Image;
	
}
