package jim.business.console.model;

import lombok.Data;

/** 
 * @ClassName: WarningCameraStatusEventData 
 * @Description: 相机状态预警数据
 * @author DanielHyw
 * @date 2024年3月16日 上午11:48:57 
 *  
 */
@Data
public class WarningCameraStatusEventData {
	/** 
	 * 摄像机ID
	 */ 
	private int camID;
	
	/** 
	 * 摄像机名称
	 */ 
	private String camName;
	
	/** 
	 * 摄像机状态
	 * error:出错
	 * timeout:超时
	 * online:在线
	 * offline:离线
	 * abnormal:异常状态
	 * pause:暂停
	 */ 
	private String status;
}
