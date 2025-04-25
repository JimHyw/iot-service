package jim.business.console.model;

import java.util.List;

import lombok.Data;

/** 
 * @ClassName: WarningCameraData 
 * @Description: 预警相机数据
 * @author DanielHyw
 * @date 2024年3月16日 上午11:34:11 
 *  
 */
@Data
public class WarningCameraData {
	/**
	 * 摄像头id
	 */
	private int camID;
	
	/** 
	 * 摄像头名称
	 */ 
	private String camName;
	
	/** 
	 * 相机状态。run=运行中
	 */ 
	private String camStatus;

	/** 
	 * 摄像头ip
	 */ 
	private String camIP;

	/** 
	 * 访问账号
	 */ 
	private String username;

	/** 
	 * 密码
	 */ 
	private String password;

	/** 
	 * 拉流地址
	 */ 
	private String streamPullPath;

	/** 
	 * 拉流端口
	 */ 
	private int streamPullPort;

	/** 
	 * 拉流编码
	 */ 
	private String streamPullCode;

	/** 
	 * 回放地址
	 */ 
	private String streamPlaybackURI;

	/** 
	 * 回放编码
	 */ 
	private String streamPlaybackCodec;

	/** 
	 * 激活的事件
	 */ 
	private List<String> activeEvents;
	
}
