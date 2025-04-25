package jim.framework.websocket.manager;

import jim.business.console.manager.WarningManager;
import jim.business.console.model.DeviceData;
import jim.business.console.model.PassagewayData;
import jim.business.console.model.TrafficDeviceData;
import jim.business.exception.BusinessException;
import jim.business.netsdk.manager.DeviceManager;
import jim.business.netsdk.manager.HCNetSDKManager;
import jim.business.netsdk.manager.PushFlowManager;
import jim.business.netsdk.manager.TrafficManager;
import jim.framework.common.TimerExpireHashMap;
import jim.framework.constant.SystemConstant;
import jim.framework.util.AESUtil;
import jim.framework.util.IDGeneratorUtil;
import jim.framework.websocket.WebSocketClient;
import jim.framework.websocket.bean.WebSocketRequest;
import jim.framework.websocket.bean.WebSocketResponse;
import jim.framework.websocket.dto.*;
import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.interfaces.ISocketCallback;
import jim.wg.logic.IotWgApiLogic;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketManager implements CommandLineRunner {

    public static WebSocketManager impl;

    @Autowired
    private WebSocketClient webSocketClient;

    @Autowired
    private DeviceManager deviceManager;

    @Autowired
    private WarningManager warningManager;

    @Autowired
    private HCNetSDKManager sdkManager;

    @Autowired
    private PushFlowManager pushFlowManager;
    
    @Autowired
    private TrafficManager trafficManager;

    @Autowired
    private IotWgApiLogic iotWgApiLogic;

    private Map<String, ISocketCallback> callMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @Fields CALL_TIMEOUT_TIME : 请求超时时间，30秒
     */
    private final long CALL_TIMEOUT_TIME = 30;

    private TimerExpireHashMap<String, WebSocketRequest> callMsgMap = new TimerExpireHashMap<>();

    private TimerExpireHashMap.TimerExpireHashMapCallback<String, WebSocketRequest> timerExpireHashMapCallback = new TimerExpireHashMap.TimerExpireHashMapCallback<String, WebSocketRequest>() {

        @Override
        public void callback(String key, WebSocketRequest value) throws RuntimeException {
            // 超时回调
            onCallTimeout(key, value);
        }
    };

    public WebSocketManager() {
        impl = this;
        callMsgMap.setTimerExpireHashMapCallback(timerExpireHashMapCallback);
    }

    @Override
    public void run(String... args) throws Exception {
    	log.info("start WebSocketManager");
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                    call(EWebSocketCmd.Heartbeat, "heartbeat", heartbeatCallback, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ISocketCallback heartbeatCallback = new ISocketCallback() {

        @Override
        public void timeout(Object exParams) {
            // TODO Auto-generated method stub

        }

        @Override
        public void respErr(int errCode, String errMsg, Object exParams) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onResp(WebSocketResponse response, Object exParams) {
            // 刷新播放中的过期时间
            List<String> playKeys = bodyToListClass(response.getBody(), String.class);
            pushFlowManager.refreshPlayExpireTime(playKeys);
        }
    };

    public boolean send(EWebSocketCmd cmd, Object body) {
        WebSocketRequest request = getRequest(false, cmd, body, null);
        return sendMessage(request, false);
    }

    public boolean call(EWebSocketCmd cmd, Object body, ISocketCallback callback, Object exParams) {

        WebSocketRequest request = getRequest(true, cmd, body, exParams);
        callMap.put(request.getCode(), callback);
        return sendMessage(request, true);
    }

    private String encryptSign(String code, long time, String cmd) {
        return AESUtil.AES_Encrypt(code + "_" + time + "_" + cmd, SystemConstant.SOCKET_MESSAGE_SIGN_KEY, "ECB");
    }

    private boolean checkSign(WebSocketResponse response) {
        String sign = encryptSign(response.getCode(), response.getTime(), response.getCmd().toString());
        return sign != null && sign.equals(response.getSign());
    }

    public void onCallTimeout(String key, WebSocketRequest request) {
        if (callMap.containsKey(key)) {
            ISocketCallback callback = callMap.remove(key);
            callback.timeout(request.getExParams());
        }
    }

    private void onCallback(String key, WebSocketRequest request, WebSocketResponse response) {
        if (callMap.containsKey(key)) {
            ISocketCallback callback = callMap.remove(key);
            if (!checkSign(response)) {
                logger.info("签名错误");
                callback.respErr(-1, "签名错误", request.getExParams());
                return;
            }
            if (request.getCmd() != response.getCmd()) {
                logger.info("指令不同");
                callback.respErr(-2, "指令不同", request.getExParams());
                return;
            }
            if (response.getRetCode() != 200) {
                callback.respErr(response.getRetCode(), response.getErrMsg(), request.getExParams());
                return;
            }
            logger.debug("onCallback: " + request.getCmd());
            callback.onResp(response, request.getExParams());
        }
    }

    public void onReceive(String message) {
        WebSocketResponse response = JSONObject.parseObject(message, WebSocketResponse.class);
        if (response == null)
            return;
        if (!response.getCode().isEmpty() && callMsgMap.containsKey(response.getCode())) {
            logger.debug("callback :" + response.getCode());
            WebSocketRequest request = callMsgMap.remove(response.getCode());
            onCallback(response.getCode(), request, response);
        } else {
            onReceive(response);
        }
    }

    private void onReceive(WebSocketResponse response) {
        try {
            if (response.getCmd() == null) {
                throw new BusinessException("指令为空");
            }
            if (!checkSign(response)) {
                throw new BusinessException("签名错误");
            }
            logger.debug("receive:" + response.getCmd().toString());
            switch (response.getCmd()) {
                case InitDevice: {
                    List<MonitorDeviceDTO> listDevice = bodyToListClass(response.getBody(), MonitorDeviceDTO.class);
                    List<DeviceData> returnList = new ArrayList<>();
                    for (MonitorDeviceDTO device : listDevice) {
                        returnList.add(deviceManager.addOrUpdateDevice(device.getId(), device.getIp(), device.getPort(), device.getUsername(), device.getPassword()));
                    }
                    replyRequestOk(response, returnList);
                }
                return;
                case AddDevice: {
                    MonitorDeviceDTO device = bodyToClass(response.getBody(), MonitorDeviceDTO.class);
                    DeviceData deviceData = deviceManager.addOrUpdateDevice(device.getId(), device.getIp(), device.getPort(), device.getUsername(), device.getPassword());
                    replyRequestOk(response, deviceData);
                }
                return;
                case UpdateDevice: {
                    MonitorDeviceDTO device = bodyToClass(response.getBody(), MonitorDeviceDTO.class);
                    DeviceData deviceData = deviceManager.addOrUpdateDevice(device.getId(), device.getIp(), device.getPort(), device.getUsername(), device.getPassword());
                    replyRequestOk(response, deviceData);
                }
                return;
                case DelDevice: {
                    deviceManager.removeDevice((Long) response.getBody());
                }
                break;
                case ListDevice: {
                    Collection<DeviceData> list = deviceManager.listDevices();
                    replyRequestOk(response, list);
                }
                return;
                case ListPassageway: {
                    long deviceId = (Long) response.getBody();
                    List<PassagewayData> list = sdkManager.listPassagewayByDeviceId(deviceId);
                    replyRequestOk(response, list);
                }
                return;
                case StartPushFlow: {
                    StartPushFlowDTO startPushFlow = bodyToClass(response.getBody(), StartPushFlowDTO.class);
                    String pullUrl = deviceManager.getPullUrl(startPushFlow.getDeviceId(), startPushFlow.getPassagewayId());
                    if (pullUrl != null) {
                        pushFlowManager.startPush(startPushFlow.getPlayKey(), pullUrl, startPushFlow.getPushUrl());
                    } else {
                        throw new BusinessException("设备不存在");
                    }
                }
                break;
                case StartPlayback: {
                    StartPlaybackPushFlowDTO startPlaybackPushFlow = bodyToClass(response.getBody(), StartPlaybackPushFlowDTO.class);
                    String pullUrl = deviceManager.getPlaybackPullUrl(startPlaybackPushFlow.getDeviceId(), startPlaybackPushFlow.getPassagewayId(), startPlaybackPushFlow.getStartTime(), startPlaybackPushFlow.getEndTime());
                    if (pullUrl != null) {
                        pushFlowManager.startPush(startPlaybackPushFlow.getPlayKey(), pullUrl, startPlaybackPushFlow.getPushUrl());
                    } else {
                        throw new BusinessException("设备不存在");
                    }
                }
                break;
                case StartWarningPushFlow: {
                    StartWarningPushFlowDTO startWarningPushFlowDTO = bodyToClass(response.getBody(), StartWarningPushFlowDTO.class);
                    String pullUrl = warningManager.getPullUrl(startWarningPushFlowDTO.getServerIp(), startWarningPushFlowDTO.getCameraId());
                    if (pullUrl != null) {
                        pushFlowManager.startPush(startWarningPushFlowDTO.getPlayKey(), pullUrl, startWarningPushFlowDTO.getPushUrl());
                    } else {
                        throw new BusinessException("设备不存在");
                    }
                }
                break;
                case StartWarningPlayback: {
                    StartWarningPlaybackPushFlowDTO startWarningPlaybackPushFlowDTO = bodyToClass(response.getBody(), StartWarningPlaybackPushFlowDTO.class);
                    String pullUrl = warningManager.getPlaybackPullUrl(startWarningPlaybackPushFlowDTO.getServerIp(), startWarningPlaybackPushFlowDTO.getCameraId(), startWarningPlaybackPushFlowDTO.getStartTime(), startWarningPlaybackPushFlowDTO.getEndTime());
                    if (pullUrl != null) {
                        pushFlowManager.startPush(startWarningPlaybackPushFlowDTO.getPlayKey(), pullUrl, startWarningPlaybackPushFlowDTO.getPushUrl());
                    } else {
                        throw new BusinessException("设备不存在");
                    }
                }
                break;
                case StopPushFlow: {
                    String playKey = (String) response.getBody();
                    pushFlowManager.stopPush(playKey);
                }
                break;
                case StopAllPushFlow: {
                    pushFlowManager.stopAllPush();
                }
                break;
                case StartPushFlowEx: {
                	StartPushFlowExDTO startPushFlowEx = bodyToClass(response.getBody(), StartPushFlowExDTO.class);
                	pushFlowManager.startPush(startPushFlowEx.getPlayKey(), startPushFlowEx.getPullUrl(), startPushFlowEx.getPushUrl());
                }
                break;
                case RefreshTrafficDevice: {
                	List<TrafficDeviceData> trafficDeviceDatas = bodyToListClass(response.getBody(), TrafficDeviceData.class);
                	trafficManager.refreshDeviceByList(trafficDeviceDatas);
                }
                break;
                case AddTrafficDevice: {
                	TrafficDeviceData trafficDeviceData = bodyToClass(response.getBody(), TrafficDeviceData.class);
                	trafficManager.addDevice(trafficDeviceData);
                }
                break;
                case DelTrafficeDevice: {
                	Long id = (long) response.getBody();
                	trafficManager.delDevice(id);
                }
                break;
//			case Heartbeat:	// 心跳
////				WSHeartbeatResp heartbeatResp = bodyToClass(response.getBody(), WSHeartbeatResp.class);
//				// 发送回复信息
//				replyRequestOk(response);
//				break;
                case Common: {
                    WebsocketRequestDTO websocketRequestDTO = bodyToClass(response.getBody(), WebsocketRequestDTO.class);
                    switch (websocketRequestDTO.getCmd()) {
                        case "getCtlParams": // 获取门控参数
                            websocketRequestDTO.setParam(iotWgApiLogic.getCtlParams((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;
                        case "authorization": // 授权
                            websocketRequestDTO.setParam(iotWgApiLogic.authorization((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;
                        case "doorStartOrStop": // 禁用 启用
                            websocketRequestDTO.setParam(iotWgApiLogic.doorStartOrStop((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;
                        case "deleteAuthorization": // 删除权限
                            websocketRequestDTO.setParam(iotWgApiLogic.deleteAuthorization((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;
                        case "removeLock": // 删除锁
                            websocketRequestDTO.setParam(iotWgApiLogic.removeLock((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;
                        case "openDoor": // 远程开门
                            websocketRequestDTO.setParam(iotWgApiLogic.openDoor((String) websocketRequestDTO.getParam()));
                            replyRequestOk(response, websocketRequestDTO);
                            return;


                    }
                }
                break;

                default:
                    return;
            }
        } catch (BusinessException e) {
            logger.info(e.getMessage());
            replyRequestErr(response, 500, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("receive err", e);
            replyRequestErr(response, 500, "接口错误");
        }
        replyRequestOk(response);
    }

    public boolean isLinked() {
        return webSocketClient.linked();
    }

    private <T> T bodyToClass(Object object, Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(object), clazz);
    }

    private <T> List<T> bodyToListClass(Object object, Class<T> clazz) {
        return JSONObject.parseArray(JSONObject.toJSONString(object), clazz);
    }

    private WebSocketRequest getRequest(boolean isCall, EWebSocketCmd cmd, Object body, Object exParams) {
        WebSocketRequest request = new WebSocketRequest();
        request.setCode(isCall ? IDGeneratorUtil.generatorId() : "");
        request.setCmd(cmd);
        request.setTime(System.currentTimeMillis());
        request.setBody(body);
        request.setRetCode(0);
        request.setErrMsg("");
        request.setExParams(exParams);
        request.setSign(encryptSign(request.getCode(), request.getTime(), request.getCmd().toString()));
        return request;
    }

    private boolean replyRequestOk(WebSocketResponse resp) {
        return replyRequestOk(resp.getCode(), resp.getCmd(), new Object());
    }

    private boolean replyRequestOk(WebSocketResponse resp, Object body) {
        return replyRequestOk(resp.getCode(), resp.getCmd(), body);
    }

    private boolean replyRequestOk(String code, EWebSocketCmd cmd, Object body) {
        if ("".equals(code)) {
            return true;
        }

        WebSocketRequest request = new WebSocketRequest();
        request.setCode(code);
        request.setCmd(cmd);
        request.setTime(System.currentTimeMillis());
        request.setBody(body);
        request.setRetCode(200);
        request.setErrMsg("");
        request.setSign(encryptSign(request.getCode(), request.getTime(), request.getCmd().toString()));

        return sendMessage(request, false);
    }

    private boolean replyRequestErr(WebSocketResponse resp, int errCode, String errMsg) {
        return replyRequestErr(resp.getCode(), resp.getCmd(), errCode, errMsg);
    }

    private boolean replyRequestErr(String code, EWebSocketCmd cmd, int errCode, String errMsg) {
        if ("".equals(code)) {
            return true;
        }
        
        WebSocketRequest request = new WebSocketRequest();
        request.setCode(code);
        request.setCmd(cmd);
        request.setTime(System.currentTimeMillis());
        request.setBody(new Object());
        request.setRetCode(errCode);
        request.setErrMsg(errMsg);
        request.setSign(encryptSign(request.getCode(), request.getTime(), request.getCmd().toString()));
        
        return sendMessage(request, false);
    }

    private boolean sendMessage(WebSocketRequest request, boolean needReceive) {
        if (!isLinked()) {
            return false;
        }
        String message = JSONObject.toJSONString(request);
        logger.debug("send:" + request.getCmd());
        webSocketClient.send(message);
        
        if (needReceive) {
            callMsgMap.put(request.getCode(), request, CALL_TIMEOUT_TIME);
        }
        return true;
    }
}
