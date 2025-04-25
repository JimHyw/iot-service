package jim.business.netsdk.manager;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jim.business.console.model.DeviceData;
import jim.business.console.model.PassagewayData;
import jim.business.netsdk.util.VideoUtils;
import jim.framework.util.DateUtil;
import jim.framework.websocket.dto.UpdatePassagewayImageV2DTO;
import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.manager.WebSocketManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: DeviceManager
 * @Description: 设备管理
 * @author DanielHyw
 * @date 2024年1月6日 下午6:03:37
 * 
 */
@Component
@Slf4j
public class DeviceManager implements CommandLineRunner {
	@Autowired
	private HCNetSDKManager sdkManager;
	@Autowired
	private WebSocketManager webSocketManager;
	@Value("${device.passageway.screenshot.localdir:}")
	private String localFileDir;

	/**
	 * 设备列表
	 */
	private Map<Long, DeviceData> deviceMap = new ConcurrentHashMap<>();

	@Override
	public void run(String... args) throws Exception {
		log.info("start DeviceManager");
		// 启动定时器截取通道图片
		new Thread(() -> {
			while (true) {
				try {
					// 三分钟更新一次预览图
					Thread.sleep(180000);
					if (deviceMap.isEmpty())
						continue;
					// 判断是否已连接
					while (!webSocketManager.isLinked()) {
						// 如果未连接则等待30秒后再试
						Thread.sleep(30000);
					}
					log.info("开始超脑相机截图");
					for (Long deviceId : deviceMap.keySet()) {
						List<PassagewayData> list = sdkManager.listPassagewayByDeviceId(deviceId);
						if (list.isEmpty()) {
							continue;
						}
						// 使用V2版本
						for (PassagewayData passagewayData : list) {
							// #todo 
							try {
								String url = getPullUrl(deviceId, passagewayData.getId());
								String base64Img = VideoUtils.getVideoBufferImage(url, localFileDir.isEmpty() ? null : localFileDir + File.separator + deviceId + File.separator + passagewayData.getId());

								if (base64Img != null) {
									UpdatePassagewayImageV2DTO dto = new UpdatePassagewayImageV2DTO();
									dto.setDeviceId(deviceId);
									dto.setPassagewayId(passagewayData.getId());
									dto.setBase64Image(base64Img);
									webSocketManager.send(EWebSocketCmd.UpdatePassagewayImageV2, dto);
									// 发送完休息1秒
									Thread.sleep(1000);
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						// todo: v2.0.0版本，批量发

					}

					log.info("完成超脑相机截图");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 添加或者更新设备
	 * 
	 * @Title: addOrUpdateDevice
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @param ip
	 * @param port
	 * @param username
	 * @param password
	 *
	 */
	public DeviceData addOrUpdateDevice(Long id, String ip, short port, String username, String password) {
		DeviceData deviceData = deviceMap.get(id);
		if (deviceData != null) {

		} else {
			deviceData = new DeviceData();
			deviceData.setId(id);
			deviceMap.put(id, deviceData);
		}
		deviceData.setIp(ip);
		deviceData.setPassword(password);
		deviceData.setPort(port);
		deviceData.setUsername(username);
		sdkManager.createCtrl(deviceData);

		return deviceData;
	}

	/**
	 * 
	 * @Title: removeDevice
	 * @Description: 删除设备
	 * @param id
	 * 
	 */
	public void removeDevice(Long id) {
		DeviceData deviceData = deviceMap.remove(id);
		if (deviceData != null) {
			sdkManager.removeCtrl(id);
		}
	}

	public Collection<DeviceData> listDevices() {
		return deviceMap.values();
	}

	public String getPullUrl(Long deviceId, Integer passagewayId) {
		DeviceData deviceData = deviceMap.get(deviceId);
		if (deviceData == null)
			return null;
		String pullUrl = "rtsp://" + deviceData.getUsername() + ":" + deviceData.getPassword() + "@"
				+ deviceData.getIp() + ":554/Streaming/Channels/" + passagewayId + "01";

		return pullUrl;
	}

	public String getPlaybackPullUrl(Long deviceId, Integer passagewayId, Date startTime, Date endTime) {
		DeviceData deviceData = deviceMap.get(deviceId);
		if (deviceData == null)
			return null;
		String startTimeStr = DateUtil.format(startTime, DateUtil.DATE_PATTERN_UTC);
		String endTimeStr = DateUtil.format(endTime, DateUtil.DATE_PATTERN_UTC);
		String pullUrl = "rtsp://" + deviceData.getUsername() + ":" + deviceData.getPassword() + "@"
				+ deviceData.getIp() + ":554/Streaming/tracks/" + passagewayId + "01?starttime=" + startTimeStr
				+ "&endtime=" + endTimeStr;

		return pullUrl;
	}
}
