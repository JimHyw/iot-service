package jim.business.console.model;

import java.util.List;

import lombok.Data;

/** 
 * @ClassName: WarningCameraList 
 * @Description: 预警相机列表数据--接口返回参数
 * @author DanielHyw
 * @date 2024年3月16日 下午5:56:36 
 *  
 */
@Data
public class WarningCameraList {
	/**
	 * 相机列表数据
	 */
	private List<WarningCameraData> data;
}
