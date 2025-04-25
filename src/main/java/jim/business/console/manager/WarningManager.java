package jim.business.console.manager;

import jim.business.console.model.*;
import jim.business.console.utils.CheckDataUtil;
import jim.business.netsdk.util.VideoUtils;
import jim.framework.constant.SystemConstant;
import jim.framework.sse.AnsMsgHandler;
import jim.framework.sse.SseClient;
import jim.framework.util.*;
import jim.framework.websocket.dto.*;
import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.manager.WebSocketManager;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DanielHyw
 * @ClassName: WarningManager
 * @Description: 预警管理
 * @date 2024年3月16日 下午2:19:45
 */
@Component
@Slf4j
public class WarningManager implements CommandLineRunner {
    @Autowired
    private WebSocketManager webSocketManager;
    @Value("${device.passageway.screenshot.localdir:}")
    private String localFileDir;

    /**
     * 预警设备字典
     */
    private Map<String, WarningDeviceData> deviceMap = new ConcurrentHashMap<>();

    /**
     * 预警相机字典--根据主设备ip分组
     */
    private Map<String, List<WarningCameraData>> deviceCameraMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        // 开启udp监听
        UdpUtil.startUdpGroupListen(SystemConstant.UDP_IP, SystemConstant.UDP_PORT, new IUdpCallback() {

            @Override
            public void callback(String ip, int port, String message, int length) {
                ip = ip.replaceAll("/", "");

                if (length > 32) {
                    // 大于32为有效消息。
                    tryAddWarningDevice(ip);
                }
            }
        });

        // 启动定时器截取预警相机图片
        new Thread(() -> {
            while (true) {
                try {
                    // 三分钟更新一次预览图
                    Thread.sleep(180000);
                    if (deviceCameraMap.isEmpty())
                        continue;
                    // 判断是否已连接
                    while (!webSocketManager.isLinked()) {
                        // 如果未连接则等待30秒后再试
                        Thread.sleep(30000);
                    }
                    log.info("开始预警相机截图");
                    Set<String> serverIps = new HashSet<>(deviceCameraMap.keySet());
                    for (String serverIp : serverIps) {
                        List<WarningCameraData> list = deviceCameraMap.get(serverIp);
                        if (list == null)
                            continue;
                        for (WarningCameraData data : list) {
                            if (!"run".equals(data.getCamStatus())) {
                                continue;
                            }
                            try {
                                String url = getPullUrl(data);
                                String base64Img = VideoUtils.getVideoBufferImage(url, localFileDir.isEmpty() ? null : localFileDir + File.separator + serverIp + File.separator + data.getCamName());
                                if (base64Img != null) {
                                    UpdateWarningCameraImageDTO dto = new UpdateWarningCameraImageDTO();
                                    dto.setServerIp(serverIp);
                                    dto.setCameraId(data.getCamID());
                                    dto.setBase64Image(base64Img);
                                    webSocketManager.send(EWebSocketCmd.UpdateWarningCameraImage, dto);
                                    // 发送完休息1秒
									Thread.sleep(1000);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error(e.getMessage());
                            }
                        }
                    }

                    log.info("完成预警相机截图");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }

        }).start();
    }

    public Collection<WarningDeviceData> listDevice() {
        return deviceMap.values();
    }

    /**
     * @param ip
     * @Title: tryAddWarningDevice
     * @Description: 尝试添加预警设备
     */
    private void tryAddWarningDevice(String ip) {
        if (deviceMap.containsKey(ip)) {
            return;
        }
        // 获取相机设备列表
        Map<String, String> header = new HashMap<>();
        header.put("Host", "http://" + ip + ":" + SystemConstant.HTTP_CAMERA_PORT);
        WarningCameraList listCamera = null;
        try {
            String result = HttpUtil.newInstance().doGet("http://" + ip + ":" + SystemConstant.HTTP_CAMERA_PORT + "/cameras", null, header);
            listCamera = JSONObject.parseObject(result, WarningCameraList.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 推送相机数据到服务器
        WarningCameraListDTO warningCameraListDTO = new WarningCameraListDTO();
        warningCameraListDTO.setServerIp(ip);
        List<WarningCameraDTO> list = new ArrayList<>();
        if (CheckDataUtil.isNotNull(listCamera.getData())) {
            deviceCameraMap.put(ip, listCamera.getData());
            for (WarningCameraData data : listCamera.getData()) {
                WarningCameraDTO dto = new WarningCameraDTO();
                dto.setCameraId(data.getCamID());
                dto.setCameraName(data.getCamName());
                dto.setCameraStatus(data.getCamStatus());
                dto.setServerIp(ip);
                dto.setCameraIp(data.getCamIP());
                dto.setUsername(data.getUsername());
                dto.setPassword(data.getPassword());
                dto.setStreamPullPath(data.getStreamPullPath());
                dto.setStreamPullPort(data.getStreamPullPort());
                dto.setStreamPullCode(data.getStreamPlaybackCodec());
                dto.setStreamPlaybackURI(data.getStreamPlaybackURI());
                dto.setStreamPlaybackCodec(data.getStreamPlaybackCodec());
                dto.setActiveEvents(data.getActiveEvents());
                list.add(dto);
            }
        } else {
            deviceCameraMap.put(ip, new ArrayList<>());
        }
        warningCameraListDTO.setList(list);
        webSocketManager.send(EWebSocketCmd.UpdateWarningCameras, warningCameraListDTO);

        WarningDeviceData data = new WarningDeviceData();
        data.setIp(ip);
        deviceMap.put(ip, data);
        // 推送数据到服务器--感觉不需要

        // 添加监听
        addWarningListen(ip);
    }

    private void addWarningListen(String ip) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);

                String urlPath = "http://" + ip + ":" + SystemConstant.SOCKET_PORT + "/api/events/notifications";
                InputStream inputStream = SseClient.getSseInputStream(urlPath);
                SseClient.readStream(inputStream, new AnsMsgHandler() {

                    private WarningSocketData data = new WarningSocketData();

                    public void actMsg(InputStream is, String line) {
                        if (StringUtils.isBlank(line)) {
                            if (data.getEvent() != null) {
                                addWarningEvent(ip, data);
                            }
                        } else if (": keepAlive".equals(line)) {
                            data.setEvent(null);
                        } else {
                            if (line.indexOf("event: ") == 0) {
                                data.setEvent(line.substring("event: ".length()));
                            } else if (line.indexOf("data: ") == 0) {
                                data.setData(line.substring("data: ".length()));
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            deviceMap.remove(ip);
            deviceCameraMap.remove(ip);
        }).start();
    }

    private void addWarningEvent(String ip, WarningSocketData data) {
        switch (data.getEvent()) {
            case "evSnap":    // 预警事件
                WarningCameraEventData warningEventData = JSONObject.parseObject(data.getData(), WarningCameraEventData.class);
                WarningEventCameraDTO warningEventCameraDTO = new WarningEventCameraDTO();
                warningEventCameraDTO.setCameraId(warningEventData.getCamID());
                warningEventCameraDTO.setServerIp(ip);
                warningEventCameraDTO.setCameraIp(warningEventData.getCamIP());
                warningEventCameraDTO.setEventId(warningEventData.getEvID());
                warningEventCameraDTO.setEventType(warningEventData.getEvent());
                warningEventCameraDTO.setEventTime(warningEventData.getEvTime());
                warningEventCameraDTO.setEventImage(Base64OrMultipartFile.getImageStrFromUrl("http://" + ip + ":" + SystemConstant.HTTP_EVENT_PORT + warningEventData.getSnapFile()));

                webSocketManager.send(EWebSocketCmd.PushWarningCameraEvent, warningEventCameraDTO);
                break;
            case "camStatus":    // 摄像机状态变化事件
                WarningCameraStatusEventData warningCameraEventData = JSONObject.parseObject(data.getData(), WarningCameraStatusEventData.class);
                WarningEventCameraStatusDTO warningEventCameraStatusDTO = new WarningEventCameraStatusDTO();
                warningEventCameraStatusDTO.setCameraId(warningCameraEventData.getCamID());
                warningEventCameraStatusDTO.setServerIp(ip);
                warningEventCameraStatusDTO.setCameraName(warningCameraEventData.getCamName());
                warningEventCameraStatusDTO.setStatus(warningCameraEventData.getStatus());

                webSocketManager.send(EWebSocketCmd.PushWarningCameraStatusEvent, warningEventCameraStatusDTO);
                break;
        }
    }

    public String getPullUrl(String serverIp, int cameraId) {
        WarningCameraData data = getCameraByServerIpAndCameraId(serverIp, cameraId);
        if (data == null)
            return null;
        return getPullUrl(data);
    }

    public String getPullUrl(WarningCameraData data) {
        return "rtsp://" + data.getUsername() + ":" + data.getPassword() + "@" + data.getCamIP() + ":" +
                data.getStreamPullPort() + data.getStreamPullPath();
    }

    public String getPlaybackPullUrl(String serverIp, int cameraId, Date startTime, Date endTime) {
        WarningCameraData data = getCameraByServerIpAndCameraId(serverIp, cameraId);
        if (data == null)
            return null;
        if (StringUtils.isBlank(data.getStreamPlaybackURI())) {
            return null;
        }
        String startTimeStr = DateUtil.format(startTime, DateUtil.DATE_PATTERN_UTC);
        String endTimeStr = DateUtil.format(endTime, DateUtil.DATE_PATTERN_UTC);
        return "rtsp://" + data.getUsername() + ":" + data.getPassword() + "@" + data.getCamIP() + ":" +
                data.getStreamPullPort() + data.getStreamPlaybackURI() + "?starttime=" + startTimeStr
                + "&endtime=" + endTimeStr;
    }

    private WarningCameraData getCameraByServerIpAndCameraId(String serverIp, int cameraId) {
        List<WarningCameraData> list = deviceCameraMap.get(serverIp);
        if (list == null) {
            return null;
        }
        for (WarningCameraData data : list) {
            if (data.getCamID() == cameraId)
                return data;
        }
        return null;
    }
}


