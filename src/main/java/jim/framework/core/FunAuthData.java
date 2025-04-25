package jim.framework.core;

import java.util.List;

/** 
* @ClassName: FunAuthData 
* @Description: 方法权限数据
* @author DanielHyw
* @date Apr 14, 2020 12:06:42 PM 
*  
*/
public class FunAuthData {

	/** 
	* @Fields type : 分类
	*/ 
	private String type;
	
	/** 
	* @Fields code : 编码列表
	*/ 
	private List<String> code;
	
	/** 
	* @Fields accessType : 访问类型，0全部，1pc端，2移动端
	*/ 
	private int accessType;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getCode() {
		return code;
	}

	public void setCode(List<String> code) {
		this.code = code;
	}

	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}


}
