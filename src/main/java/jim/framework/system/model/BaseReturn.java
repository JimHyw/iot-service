package jim.framework.system.model;

import java.util.Map;

import jim.framework.constant.SystemConstant;
import jim.framework.system.manager.UpdateManager;

/**
 * 业务数据返回基类
 */
public class BaseReturn {
	
	/**
	 * 返回错误码
	 */
	private Integer code = 200;
	
	/**
	 * 提示信息
	 */
	private String msg = "ok";
	
	/**
	 * 返回业务数据
	 */
	private Object body;
	
	/** 
	* @Fields update : 推送数据
	*/ 
	private Map<String, Object> update;
	
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	public Map<String, Object> getUpdate() {
		return update;
	}

	public void setUpdate(Map<String, Object> update) {
		this.update = update;
	}

	public BaseReturn() {
		this.code =SystemConstant.NORMAL_CODE;
	}
	
	public BaseReturn(Integer code) {
		this.code = code;
	}
	
	public BaseReturn(Object body) {
		this.code = SystemConstant.NORMAL_CODE;
		this.body = body;
	}
	
	public BaseReturn(Integer code, Object body) {
		this.code = code;
		this.body = body;
	}
	
	public BaseReturn(Integer code, Object body, String msg) {
		this.code = code;
		this.body = body;
		this.msg = msg;
	}
	
	public BaseReturn(Integer code, Object body, String msg, Map<String, Object> update) {
		this.code = code;
		this.body = body;
		this.msg = msg;
		this.update = update;
	}
	
	public static BaseReturn error(String msg) {
		BaseReturn r = new BaseReturn(SystemConstant.INTERNAL_SERVER_ERROR, null, msg);
		makeUpdateData(r);
		return r;
	}
	
	/** 
	* @Title: error 
	* @Description: 自定义错误
	* @param code
	* @param msg
	* @return  参数说明 
	* @return BaseReturn    返回类型 
	* 
	*/
	public static BaseReturn error(int code, String msg) {
		BaseReturn r = new BaseReturn(code, null, msg);
		makeUpdateData(r);
		return r;
	}
	
	public static BaseReturn error() {
		BaseReturn r = new BaseReturn(SystemConstant.INTERNAL_SERVER_ERROR, null, "服务器内部错误，请联系管理员");
		makeUpdateData(r);
		return r;
	}

	public static BaseReturn ok() {
		BaseReturn r = new BaseReturn();
		makeUpdateData(r);
		return r;
	}
	
	public static BaseReturn ok(Map<String, Object> update) {
		BaseReturn r = new BaseReturn();
		r.setUpdate(update);
		makeUpdateData(r);
		return r;
	}
	
	public static BaseReturn notLogin(){
		BaseReturn r = new BaseReturn(SystemConstant.NOT_LOGIN, null, "未登录服务器");
		return r;
	}
	
	public static BaseReturn unauthorized(){
		BaseReturn r = new BaseReturn(SystemConstant.UNAUTHORIZED, null, "您没有该权限");
		return r;
	}
	
	public static BaseReturn normal(Object body)
	{
		return normal(body, null);
	}
	
	public static BaseReturn normal(Object body, Map<String, Object> update)
	{
		BaseReturn r = new BaseReturn(SystemConstant.NORMAL_CODE, body, "OK", update);
		makeUpdateData(r);
		return r;
	}
	
	/** 
	* @Title: makeUpdateData 
	* @Description: 组合更新推送信息
	* @param br  参数说明 
	* @return void    返回类型 
	* 
	*/
	private static void makeUpdateData(BaseReturn br) {
		UpdateManager.getInstance().makeUpdateData(br);
	}
	
}