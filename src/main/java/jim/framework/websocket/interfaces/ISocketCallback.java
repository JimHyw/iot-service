package jim.framework.websocket.interfaces;

import jim.framework.websocket.bean.WebSocketResponse;

/** 
* @ClassName: ISocketCallback 
* @Description: socket消息回调接口
* @author DanielHyw
* @date Jan 12, 2021 9:52:34 AM 
*  
*/
public interface ISocketCallback {
	
	/** 
	* @Title: onResp 
	* @Description: 收到回调
	* @param response 收到的消息
	* @param exParams  请求的额外参数
	* @return void    返回类型 
	* 
	*/
	void onResp(WebSocketResponse response, Object exParams);
	
	/** 
	* @Title: timeout 
	* @Description: 超时
	* @param exParams  请求的额外参数
	* @return void    返回类型 
	* 
	*/
	void timeout(Object exParams);
	
	/** 
	* @Title: respErr 
	* @Description: 返回错误，
	* @param errCode -1签名不正确，-2指令不统一，大于0，其他错误
	* @param exParams  参数说明 
	* @return void    返回类型 
	* 
	*/
	void respErr(int errCode, String errMsg, Object exParams);
}
