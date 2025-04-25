package jim.business.console.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.alibaba.fastjson.JSONObject;

import jim.framework.websocket.manager.WebSocketManager;
import jim.business.console.manager.WarningManager;
import jim.business.exception.BusinessException;
import jim.business.netsdk.manager.DeviceManager;
import jim.business.netsdk.manager.HCNetSDKManager;
import jim.business.netsdk.manager.PushFlowManager;
import jim.business.netsdk.manager.TrafficManager;

/** 
* @ClassName: SystemController 
* @Description: 系统接口
* @author DanielHyw
* @date May 11, 2024 5:12:10 PM
*  
*/
@RestController
@RequestMapping(method = RequestMethod.POST, value = "/system")
public class SystemController extends AbstractController {
	@Autowired
	private DeviceManager deviceManager;
	@Autowired
	private WebSocketManager webSocketManager;
	@Autowired
	private WarningManager warningManager;
	@Autowired
	private PushFlowManager pushFlowManager;
	@Autowired
	private TrafficManager trafficManager;
	
	@RequestMapping(method = RequestMethod.GET, value = "/info")
	public String info() {
		String info = "Websocket link status: " + (webSocketManager.isLinked() ? "online" : "offline");
		info += "\nsuper info list：" + JSONObject.toJSONString(deviceManager.listDevices());
		info += "\nwarn device list：" + JSONObject.toJSONString(warningManager.listDevice());
		info += "\npush flow device list：" + JSONObject.toJSONString(trafficManager.listDevices());
		info += "\nplayed device list: " + JSONObject.toJSONString(pushFlowManager.getPushFlowCtrlMap());

		return info;
	}
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
