package jim.business.netsdk.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jim.business.console.model.TrafficDeviceData;
import jim.business.netsdk.ctrl.HCNetSDKCtrl;
import jim.business.netsdk.util.CommonUtil;
import jim.business.netsdk.util.VideoUtils;
import jim.framework.util.DateUtil;
import jim.framework.util.IDGeneratorUtil;
import jim.framework.websocket.dto.TrafficInfoDTO;
import jim.framework.websocket.dto.WarningEventCameraDTO;
import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.manager.WebSocketManager;
import lombok.extern.slf4j.Slf4j;

/** 
 * 人流管理---现在换成通用管理（包含停车和客流事件）
 * @ClassName: TrafficManager 
 * @Description:
 * @author DanielHyw
 * @date 2024年4月11日 上午11:04:50 
 *  
 */
@Component
@Slf4j
public class TrafficManager implements CommandLineRunner {
	@Autowired
	private WebSocketManager webSocketManager;
	
	@Autowired
	private HCNetSDKManager netSdkManager;
	
	public static TrafficManager impl = null;
	
	private Map<String, TrafficDeviceData> deviceMap = new ConcurrentHashMap<>();
	
	private Map<String, HCNetSDKCtrl> ctrlMap = new ConcurrentHashMap<>();

	@Override
	public void run(String... args) throws Exception {
		impl = this;
		
	}
	
	public Collection<TrafficDeviceData> listDevices() {
		return deviceMap.values();
	}

	public void refreshDeviceByList(List<TrafficDeviceData> list) {
		deviceMap.clear();
		ctrlMap.forEach((k, v) -> {
			v.doLogout();
		});
		ctrlMap.clear();
		for (TrafficDeviceData data : list) {
			deviceMap.put(data.getIp(), data);
			HCNetSDKCtrl ctrl = netSdkManager.createOtherCtrl(data.getId());
			data.setEnabled(ctrl.doLogin(data.getIp(), data.getPort(), data.getUsername(), data.getPassword()));
			ctrlMap.put(data.getIp(), ctrl);
		}
	}
	
	public void addDevice(TrafficDeviceData data) {
		if (deviceMap.containsKey(data.getIp())) {
			// 已经存在，不再处理
			return;
		}
		deviceMap.put(data.getIp(), data);
		HCNetSDKCtrl ctrl = netSdkManager.createOtherCtrl(data.getId());
		ctrl.doLogin(data.getIp(), data.getPort(), data.getUsername(), data.getPassword());
		ctrlMap.put(data.getIp(), ctrl);
	}
	
	public void delDevice(Long id) {
		deviceMap.forEach((k, v) -> {
			if (v.getId().equals(id)) {
				deviceMap.remove(k);
				HCNetSDKCtrl ctrl = ctrlMap.remove(k);
				if (ctrl != null) {
					ctrl.doLogout();
				}
				return;
			}
		});
	}
	
	public void pushTrafficManager(String ip, int inCount, int outCount, int absTime) {
		TrafficDeviceData data = deviceMap.get(ip);
		if (data == null) {
			return;
		}

		TrafficInfoDTO trafficInfo = new TrafficInfoDTO();
		trafficInfo.setId(data.getId());
		trafficInfo.setIp(ip);
		trafficInfo.setInCount(inCount);
		trafficInfo.setOutCount(outCount);
		trafficInfo.setTime(CommonUtil.parseTimeMillis(absTime));
		
		webSocketManager.send(EWebSocketCmd.PushTrafficInfo, trafficInfo);
	}
	
	public void pushCarStop(String ip, int absTime) {
		TrafficDeviceData data = deviceMap.get(ip);

		if (data == null) {
			return;
		}

		try {
			// 先抓图--这里先写死海康威视的地址了，后续如果有不同品牌的摄像机，需要在TrafficDeviceData里增加对应参数，并且从中心推过来
			long time = CommonUtil.parseTimeMillis(absTime);
			String url = getPullUrl(ip, data.getUsername(), data.getPassword());
			String base64Img = VideoUtils.getVideoBufferImage(url, null);
			// 然后发送消息到中心服务
			
            WarningEventCameraDTO warningEventCameraDTO = new WarningEventCameraDTO();
            warningEventCameraDTO.setCameraId(-1);
            warningEventCameraDTO.setDeviceId(data.getId());
            warningEventCameraDTO.setServerIp(ip);
            warningEventCameraDTO.setCameraIp(data.getIp());
            warningEventCameraDTO.setEventId(IDGeneratorUtil.generatorId());
            warningEventCameraDTO.setEventType("EV_CARSTOP");
            warningEventCameraDTO.setEventTime(time);
            warningEventCameraDTO.setEventImage(base64Img);

            webSocketManager.send(EWebSocketCmd.PushWarningCameraEvent, warningEventCameraDTO);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}
	
	public String getPullUrl(String ip, String username, String password) {
        return "rtsp://" + username + ":" + password + "@" + ip + ":554/Streaming/Channels/1";
    }
	
	public String getPlaybackPullUrl(String ip, String username, String password, Date startTime, Date endTime) {
       
        String startTimeStr = DateUtil.format(startTime, DateUtil.DATE_PATTERN_UTC);
        String endTimeStr = DateUtil.format(endTime, DateUtil.DATE_PATTERN_UTC);
        return "rtsp://" + username + ":" + password + "@" + ip + ":554/Streaming/tracks/1?starttime=" + startTimeStr
                + "&endtime=" + endTimeStr;
    }
}
