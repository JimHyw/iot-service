package jim.framework.websocket.dto;

import java.util.List;

import lombok.Data;

/** 
 * @ClassName: WarningCameraDTO 
 * @Description: 预警相机数据
 * @author DanielHyw
 * @date 2024年3月16日 下午5:33:25 
 *  
 */
@Data
public class WarningCameraDTO {

	/** 
	 * 相机id
	 */ 
	private int cameraId;
	
	/**
	 * 服务器ip
	 */
	private String serverIp;
	
	/** 
	 * 相机名称
	 */ 
	private String cameraName;
	
	/** 
	 * 相机状态
	 */ 
	private String cameraStatus;
	
	/** 
	 * 相机ip
	 */ 
	private String cameraIp;
	
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
