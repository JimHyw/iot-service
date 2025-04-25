package jim.framework.websocket.dto;

import lombok.Data;

/** 
 * @ClassName: StartPushFlowExDTO 
 * @Description:开始推送数据结构--根据推流地址和拉流地址直接推流
 * @author DanielHyw
 * @date 2024年4月7日 上午11:05:34 
 *  
 */
@Data
public class StartPushFlowExDTO {

	/**
	 * 播放主键，用于判断播放的是哪个推流
	 */
	private String playKey;
	
	/** 
	 * 拉流地址
	 */ 
	private String pullUrl;
	
	/** 
	 * 推送地址
	 */ 
	private String pushUrl;
}
