package jim.business.console.model;

import lombok.Data;

/** 
 * @ClassName: WarningSocketData 
 * @Description: 预警socket消息数据
 * @author DanielHyw
 * @date 2024年3月16日 下午3:28:44 
 *  
 */
@Data
public class WarningSocketData {
	/** 
	 * 信息类型
	 */ 
	private String event;
	
	/** 
	 * 信息内容
	 */ 
	private String data;
}
