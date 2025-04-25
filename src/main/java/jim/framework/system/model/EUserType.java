package jim.framework.system.model;

/** 
* @ClassName: EUserType 
* @Description: 用户类型枚举
* @author DanielHyw
* @date Jul 19, 2021 5:28:28 PM 
*  
*/
public enum EUserType {

	/** 
	* @Fields manager : 管理员
	*/ 
	manager(0),
	/** 
	* @Fields user : app用户
	*/ 
	appUser(1);
	
	public int value;
	
	EUserType(int value) {
		this.value = value;
	}
	
	public static EUserType valueOf(int value) {
		if(value < 0 || value >= EUserType.values().length)
			return EUserType.manager;
		return EUserType.values()[value];
	}
}
