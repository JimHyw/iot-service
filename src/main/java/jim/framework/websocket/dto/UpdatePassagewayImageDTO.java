package jim.framework.websocket.dto;

import java.util.Map;

import lombok.Data;

/** 
 * @ClassName: UpdatePassagewayImageDTO 
 * @Description: 更新通道图片dto
 * @author DanielHyw
 * @date 2024年1月13日 下午5:29:16 
 *  
 */
@Data
public class UpdatePassagewayImageDTO {

	/**
	 * 设备id
	 */
	private Long deviceId;
	
	/**
	 * 通道列表
	 */
	private Map<Integer, PassagewayImageDTO> passagewayMap;
}
