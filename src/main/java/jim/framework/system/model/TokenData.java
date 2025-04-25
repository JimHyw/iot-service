package jim.framework.system.model;

import lombok.Data;

import java.util.Date;

/** 
* @ClassName: TokenData 
* @Description: token数据
* @author DanielHyw
* @date Apr 12, 2020 5:43:04 PM 
*  
*/
@Data
public class TokenData {
	
	/** 
	* @ClassName: ETokenState 
	* @Description: token状态
	* @author DanielHyw
	* @date Apr 14, 2020 3:36:43 PM 
	*  
	*/
	public enum ETokenState {
		UNKNOW(0),
		ONLINE(1),
		OFFLINE(2),
		TIMEOUT(3),
		KICKED(4),
		OTHERLOGIN(5);
		
		public int value;
		
		ETokenState(int value)
		{
			this.value = value;
		}
	}

	/** 
	* @Fields id : 记录id
	*/ 
	private long id;
	/** 
	* @Fields token : token值
	*/ 
	private String token;
	
	/** 
	* @Fields userId : 用户id
	*/ 
	private String userId;
	
	/** 
	* @Fields count : 登录次数
	*/ 
	private int count;
	
	/** 
	* @Fields state : 状态,1在线，2离线，3超时，4被T出，5其他地区已登录
	*/ 
	private int state;
	
	/** 
	* @Fields ip : 登录IP地址
	*/ 
	private String ip;
	
	/** 
	* @Fields sourceType : 来源类型，1pc，2移动端
	*/ 
	private Integer sourceType;
	
	/** 
	* @Fields source : 登录来源（游览器版本，手机信息）
	*/ 
	private String source;
	
	/** 
	* @Fields loginTime : 登录时间
	*/ 
	private Date loginTime;
	
	/** 
	* @Fields updateTime : 更新时间
	*/ 
	private Date updateTime;
	
	/** 
	* @Fields createTime : 创建时间
	*/ 
	private Date createTime;
}
