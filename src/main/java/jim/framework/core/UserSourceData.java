package jim.framework.core;

/** 
* @ClassName: UserSourceData 
* @Description: 用户来源数据
* @author DanielHyw
* @date Jul 8, 2020 11:40:29 AM 
*  
*/
public class UserSourceData {

	/** 
	* @Fields userId : 用户id
	*/ 
	private String userId;
	
	/** 
	* @Fields sourceType : 用户来源，1pc，2移动端
	*/ 
	private int sourceType;
	
	public UserSourceData(String userId, int sourceType)
	{
		this.userId = userId;
		this.sourceType = sourceType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	
}
