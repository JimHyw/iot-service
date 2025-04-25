package jim.framework.websocket.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jim.framework.websocket.enums.EWebSocketCmd;

/** 
* @ClassName: WebSocketRequest 
* @Description: websocket请求数据结构
* @author DanielHyw
* @date Jan 11, 2021 5:41:46 PM 
*  
*/
public class WebSocketRequest {
	
	/** 
	* @Fields code : 请求编号，每次都不同
	*/ 
	private String code;
	
	/** 
	* @Fields cmd : 指令
	*/ 
	private EWebSocketCmd cmd;
	
	/** 
	* @Fields time : 发送时间
	*/ 
	private long time;
	
	/** 
	* @Fields body : 数据
	*/ 
	private Object body;
	
	/** 
	* @Fields sign : 签名，aes加密
	*/ 
	private String sign;
	
	/** 
	* @Fields retCode : 返回编码，非200时代表错误，主动发送时，使用0
	*/ 
	private int retCode;
	
	/** 
	* @Fields errMsg : 错误信息
	*/ 
	private String errMsg;
	
	@JSONField(serialize=false)
	@JsonIgnore
	private Object exParams;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public EWebSocketCmd getCmd() {
		return cmd;
	}

	public void setCmd(EWebSocketCmd cmd) {
		this.cmd = cmd;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getExParams() {
		return exParams;
	}

	public void setExParams(Object exParams) {
		this.exParams = exParams;
	}
	
}
