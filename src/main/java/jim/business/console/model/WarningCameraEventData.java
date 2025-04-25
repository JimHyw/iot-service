package jim.business.console.model;

import lombok.Data;

/**
 * 
 * @ClassName: WarningCameraEventData 
 * @Description: 相机预警事件数据
 * @author DanielHyw
 * @date 2024年3月16日 上午11:48:04 
 *
 */
@Data
public class WarningCameraEventData {
	/** 
	 * 事件ID
	 */ 
	private String evID;

	/** 
	 *摄像机ID
	 */ 
	private int camID;

	/** 
	 * 摄像机IP
	 */ 
	private String camIP;

	/** 
	 * 摄像机名称
	 */ 
	private String camName;

	/** 
	 * 服务器ID
	 */ 
	private String srvID;

	/** 
	 * 服务器名称
	 */ 
	private String srvName;

	/** 
	 * 事件名称
	 */ 
	private String event;

	/** 
	 * 事件时间
	 */ 
	private long evTime;

	/** 
	 * 事件村方路径
	 */ 
	private String baseFolder;

	/** 
	 * 快照文件路径
	 */ 
	private String snapFile;

	/** 
	 * 回放地址
	 */ 
	private String playbackURI;

	/** 
	 * 回放编码
	 */ 
	private String playbackCodec;
	
}
