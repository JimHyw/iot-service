package jim.framework.websocket.dto;

import java.util.List;

import lombok.Data;

/** 
 * @ClassName: WarningCameraListDTO 
 * @Description: 预警相机列表数据
 * @author DanielHyw
 * @date 2024年3月17日 下午1:56:05 
 *  
 */
@Data
public class WarningCameraListDTO {
	
	/**
	 * 服务器ip
	 */
	private String serverIp;
	
	/**
	 * 相机列表
	 */
	private List<WarningCameraDTO> list;

}
