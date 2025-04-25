package jim.framework.core.jwt;

/**
 * 校验返回结果
 */
public class CheckResult {
	private String code;
	
	private String userId;
	
	private String userType;

	private String userSourceType;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserSourceType() {
		return userSourceType;
	}

	public void setUserSourceType(String userSourceType) {
		this.userSourceType = userSourceType;
	}

}
