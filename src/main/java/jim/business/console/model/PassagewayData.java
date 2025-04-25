package jim.business.console.model;

import lombok.Data;

/** 
 * 通道数据
 * @ClassName: PassagewayData 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author DanielHyw
 * @date 2024年1月9日 下午1:46:19 
 *  
 */
@Data
public class PassagewayData {
	/**
	 * 通道id号，从1开始
	 */
	private int id;
	
	/**
	 * ip
	 */
	private String ip;
	
	/**
	 * 端口
	 */
	private short port;
	
	/**
	 * 账号
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 通道是否运行中
	 */
	private boolean enabled;
}
