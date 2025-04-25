package jim.wg.logic;

import jim.framework.websocket.enums.EWebSocketCmd;
import jim.framework.websocket.manager.WebSocketManager;
import jim.wg.dto.*;
import jim.wg.exception.BizException;
import jim.wg.utils.WebSocket;
import jim.wg.utils.WgWebapi;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class IotWgApiLogic {
    @Autowired
    WebSocketManager webSocketManager;

    private final String url = "http://127.0.0.1:61080";  //请求地址
    private final String wsUrl = "ws://127.0.0.1:61081"; // websocket 地址


    /**
     * 读取门控制器参数
     *
     * @param requestdata
     * @return
     * @throws BizException
     */
    public String getCtlParams(String requestdata) throws BizException {
        ResultDTO res = new ResultDTO();
        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));
        IotWGBaseParamsDTO p = JSON.parseObject(requestdata, IotWGBaseParamsDTO.class);

        IotWgDevParmDTO dto = new IotWgDevParmDTO();

        int controllerSN = p.getSn();               //微耕控制器编号
        String op = "读取门控制参数";
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "读取门控制参数");
            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);
            params.add(param);
            webcmd.put("id", 4006);

            String body = webcmd.toString();
            WgWebapi.logInfo("发出指令:\r\n" + (new ObjectMapper()).readTree(body).toPrettyString());
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(this.url, body);

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                return JSON.toJSONString(res.setStatus("error").setMsg("通信失败"));
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //.toString());
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                } else {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "失败..."));
                    return JSON.toJSONString(res.setStatus("error").setMsg(String.format("%d %s  %s ", controllerSN, op, "失败...")));
                }

                WgWebapi.logInfo(strResult);
                JSONObject jsonObject = JSONObject.parseObject(strResult);
                if (jsonObject != null) {
                    JSONObject jsonResult = null;
                    if (jsonObject.containsKey("result")) {
                        jsonResult = jsonObject.getJSONObject("result");
                    }
                    Long sn = jsonResult.getLong("设备序列号");
                    List<IotWgDevParmDTO.Lock> locks = new ArrayList<>();

                    Integer lockNo = 1;
                    while (true) {
                        String key = lockNo + "号门控制方式";
                        if (!jsonResult.containsKey(key)) break;

                        locks.add(new IotWgDevParmDTO().new Lock(
                                lockNo + "号门",
                                lockNo,
                                jsonResult.getString(lockNo + "号门控制方式"),
                                jsonResult.getInteger(lockNo + "号门开门延时(秒)")
                        ));

                        lockNo++;
                    }

                    dto.setSn(sn);
                    dto.setLocks(locks);
                    if (locks != null && locks.size() > 0) {
                        dto.setLockCount(locks.size());
                    }
                    return JSON.toJSONString(res.setStatus("success").setMsg("成功").setData(JSON.toJSONString(dto)));
                } else {
                    return null;//无此设备
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 控制器卡号授权
     *
     * @param requestdata
     * @return
     */
    public String authorization(String requestdata) {
        ResultDTO res = new ResultDTO();
        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));

        AuthorizationDTO p = JSON.parseObject(requestdata, AuthorizationDTO.class);

        List<AuthorizationItemDTO> authorizationItemDTOS = authorizationList(p);
        Map<Long, Map<Integer, Integer>> cardNoLockListMap = authorizationItemDTOS.stream().collect(Collectors.toMap(AuthorizationItemDTO::getCardNo, AuthorizationItemDTO::getLockList, (k1, k2) -> k1));

        String url = this.url; // 服务地址
        int controllerSN = p.getSn();// 控制器编号
        String op = "权限添加或修改";
        String startTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()); // 开始时间
        String endTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(p.getValidityDate());  // 结束时间

        WgWebapi.logInfo("权限添加或修改");
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "权限添加或修改");

            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);

            ArrayNode arrprivs = param.putArray("权限");

            // 添加卡号
            p.getCardNoList().stream().forEach(cardNo -> {
                ObjectNode userpriv = mapper.createObjectNode();
                ArrayNode arrcard = userpriv.putArray("卡号");
                arrcard.add(cardNo);

                userpriv.put("起始日期时间", startTime);
                userpriv.put("截止日期时间", endTime);

                // 设置权限
                if (cardNoLockListMap.containsKey(cardNo)) {
                    cardNoLockListMap.get(cardNo).forEach((doorNo, enable) -> {
                        if (p.getLockNo().equals(doorNo)) {
                            enable = p.getEnable();
                        }
                        userpriv.put(doorNo + "号门控制时段", enable); // 锁编号（门）
                    });
                } else {
                    for (int i = 1; i <= 4; i++) {
                        Integer enable = 0;
                        if (p.getLockNo().equals(i)) {
                            enable = p.getEnable();
                        }
                        userpriv.put(i + "号门控制时段", enable); // 锁编号（门）
                    }
                }
                userpriv.put("用户密码", 0);
                arrprivs.add(userpriv);
            });

            params.add(param);
            webcmd.put("id", 4001);
            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(url, body);  //增加超时

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                return JSON.toJSONString(res.setStatus("error").setMsg("通信失败"));
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //采用json格式输出接收到的数据
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                    return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
                } else {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "失败..."));
                    return JSON.toJSONString(res.setStatus("error").setMsg(String.format("%d %s  %s ", controllerSN, op, "失败...")));
                }
            }
        } catch (Exception e) {
            return JSON.toJSONString(res.setStatus("error").setMsg(e.getMessage()));
        }

    }


    /**
     * 权限总数读取
     */
    public int getAuthorizationCount(IotWGBaseParamsDTO p) {
        int privnum = -1;

        String url = this.url;
        int controllerSN = p.getSn();
        WgWebapi.logInfo("权限总数读取");
        String op = "权限总数读取";
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "权限总数读取");
            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);
            params.add(param);
            webcmd.put("id", 4003);
            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(url, body);

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //.toString());
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                } else {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "失败..."));
                }
                if (bvalid) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(strResult);
                    JsonNode result = jsonNode.get("result");
                    privnum = result.get("权限总数").asInt();
                    WgWebapi.logInfo(String.format("权限总数: %d ", privnum));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return privnum;
    }


    /**
     * 门禁设备禁用
     *
     * @param requestdata
     * @return
     */
    public String doorStartOrStop(String requestdata) {
        ResultDTO res = new ResultDTO();
        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));
        StartAndStopDTO p = JSON.parseObject(requestdata, StartAndStopDTO.class);

        String url = this.url;         //"http://localhost:61080/";
        int controllerSN = p.getSn(); //451000003;

        WgWebapi.logInfo("设置门控制参数");
        String op = "设置门控制参数";
        int doorno = p.getLockNo();
        List<String> statusList = Arrays.asList("常开", "在线", "常闭");

        String doorMode = statusList.get(p.getStatus());
        int doordelay = 3;  //默认3秒, 可设置25秒

        try {

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "设置门控制参数");
            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);
            param.put("门号", doorno);
            param.put("控制方式", doorMode);
            param.put("开门延时(秒)", doordelay);
            params.add(param);
            webcmd.put("id", 1004);


            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(url, body);

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                return JSON.toJSONString(res.setStatus("error").setMsg("通信失败"));
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //.toString());
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                    return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
                } else {
                    return JSON.toJSONString(res.setStatus("error").setMsg(String.format("%d %s  %s ", controllerSN, op, "失败...")));
                }
            }
        } catch (Exception e) {
            return JSON.toJSONString(res.setStatus("error").setMsg(e.getMessage()));
        }

    }


    /**
     * 提取权限
     */
    public List<AuthorizationItemDTO> authorizationList(IotWGBaseParamsDTO p) {
        int privilegenum = getAuthorizationCount(p);
        List<AuthorizationItemDTO> arrPrivileges = new ArrayList();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "提取权限");
            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", p.getSn());
            params.add(param);
            webcmd.put("id", 4005);
            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();

            int timeout = 3000;
            if (privilegenum >= 600) {
                timeout = privilegenum * 5 + timeout;  //控制 5ms 一个 [另加多3秒]
                WgWebapi.logInfo(String.format("         大约需要%d秒左右 ", privilegenum * 5 / 1000));
            }
            String strResult = webapi.PushToWebWithjson(this.url, body, timeout);
            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString());
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(strResult);
                    JsonNode result = jsonNode.get("result");
                    int privnum = result.get("提取权限数").asInt();
                    WgWebapi.logInfo("提取权限数: " + String.valueOf(privnum));
                    if (privnum > 0) {
                        ArrayNode arrRecords = result.withArray("记录信息");
                        JsonNode jrec;
                        for (int i = 0; i < arrRecords.size(); i++) {
                            jrec = arrRecords.get(i);

                            Map<Integer, Integer> lockMap = new HashMap<>();
                            for (int j = 1; j <= 4; j++) {
                                String doorKey = j + "号门控制时段";
                                lockMap.put(j, jrec.get(doorKey).asInt());
                            }

                            AuthorizationItemDTO authorizationItemDTO = (new AuthorizationItemDTO())
                                    .setId(jrec.get("序号").asLong())
                                    .setCardNo(jrec.get("卡号").asLong())
                                    .setStartDate(jrec.get("起始日期时间").asText())
                                    .setEndDate(jrec.get("截止日期时间").asText())
                                    .setPassword(jrec.get("用户密码").asText())
                                    .setLockList(lockMap);

                            arrPrivileges.add(authorizationItemDTO);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrPrivileges;
    }

    /**
     * 权限删除
     *
     * @return
     */
    public String deleteAuthorization(String requestdata) {

        ResultDTO res = new ResultDTO();
        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));

        DeleteAuthorizationDTO p = JSON.parseObject(requestdata, DeleteAuthorizationDTO.class);

        String url = this.url;
        int controllerSN = p.getSn();

        String op = "权限删除";

        WgWebapi.logInfo("权限删除");
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "权限删除");

            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);

            ArrayNode arrprivs = param.putArray("权限");
            ObjectNode userpriv = mapper.createObjectNode();
            ArrayNode arrcard = userpriv.putArray("卡号");
            p.getCardNoList().stream().forEach(cardNo -> {
                arrcard.add(cardNo);
            });
            params.add(param);
            arrprivs.add(userpriv);

            webcmd.put("id", 4002);
            String body = webcmd.toString();
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(url, body);  //增加超时

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                return JSON.toJSONString(res.setStatus("error").setMsg("通信失败"));
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //采用json格式输出接收到的数据
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                    return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
                }
            }
        } catch (Exception e) {
            return JSON.toJSONString(res.setStatus("error").setMsg(e.getMessage()));
        }

        return JSON.toJSONString(res.setStatus("error").setMsg("失败"));
    }


    /**
     * 移动锁删除数据
     *
     * @return
     */
    public String removeLock(String requestdata) {
        ResultDTO res = new ResultDTO();

        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));

        MoveLockDTO p = JSON.parseObject(requestdata, MoveLockDTO.class);

        // 获取控制器卡号
        List<AuthorizationItemDTO> authorizationList = authorizationList(p);
        if (authorizationList.isEmpty()) return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
        List<Long> cardNoList = authorizationList.stream().collect(Collectors.mapping(AuthorizationItemDTO::getCardNo, Collectors.toList()));

        String authorizationP = JSON.toJSONString(new AuthorizationDTO(p.getVillageId(), p.getSn(), p.getLockNo(), cardNoList, 0));
        // 修改授权
        if (authorization(authorizationP).equals("true")) {
            authorizationList = authorizationList(p);

            // 获取删除卡号
            List<Long> deleteCardNoList = new ArrayList<>();
            authorizationList.stream().forEach(item -> {
                AtomicBoolean tag = new AtomicBoolean(true);
                item.getLockList().forEach((doorNo, enable) -> {
                    if (enable.equals(1)) {
                        tag.set(false);
                    }
                });

                if (tag.get()) {
                    deleteCardNoList.add(item.getCardNo());
                }
            });

            if (deleteCardNoList.isEmpty()) return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
            String deleteAuthorizationParams = JSON.toJSONString(new DeleteAuthorizationDTO(p.getVillageId(), p.getSn(), p.getLockNo(), deleteCardNoList));
            return deleteAuthorization(deleteAuthorizationParams);
        }

        return JSON.toJSONString(res.setStatus("error").setMsg("失败"));
    }


    /**
     * 远程开门
     *
     * @param requestdata
     * @return
     */
    public String openDoor(String requestdata) {

        ResultDTO res = new ResultDTO();

        if (StringUtils.isBlank(requestdata)) return JSON.toJSONString(res.setStatus("error").setMsg("参数为空"));
        OpenDoorDTO p = JSON.parseObject(requestdata, OpenDoorDTO.class);

        int controllerSN = p.getSn(); //451000003;

        WgWebapi.logInfo("远程开门");
        String op = "远程开门";
        int doorno = p.getLockNo();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode webcmd = mapper.createObjectNode();
            webcmd.put("jsonrpc", "2.0");
            webcmd.put("method", "远程开门");
            ArrayNode params = webcmd.putArray("params");
            ObjectNode param = mapper.createObjectNode();
            param.put("设备序列号", controllerSN);
            param.put("门号", doorno);
            param.put("模拟卡号", p.getCardNo());
            params.add(param);
            webcmd.put("id", 1001);

            String body = webcmd.toString();

            WgWebapi.logInfo("微耕开门参数： " + body);
            WgWebapi webapi = new WgWebapi();
            String strResult = webapi.PushToWebWithjson(this.url, body);

            if (strResult.length() == 0) {
                WgWebapi.logInfo("通信失败!");
                return JSON.toJSONString(res.setStatus("error").setMsg(String.format("开门参数：%s | 返回结果：%s ", body, "通信失败")));
            } else {
                WgWebapi.logInfo((new ObjectMapper()).readTree(strResult).toPrettyString()); //.toString());
                boolean bvalid = webapi.successIsTrue(strResult);
                if (bvalid) {
                    WgWebapi.logInfo(String.format("%d %s  %s ", controllerSN, op, "成功."));
                    return JSON.toJSONString(res.setStatus("success").setMsg("成功"));
                } else {
                    return JSON.toJSONString(res.setStatus("error").setMsg(String.format("开门参数：%s | 返回结果：%s ", body, strResult)));
                }
            }
        } catch (Exception e) {
            return JSON.toJSONString(res.setStatus("error").setMsg(e.getMessage()));
        }

    }

    private static Queue<String> queue = new LinkedList<String>();  //1001数据缓存队列
    // socket 获取状态和开门记录


    public void wgSocket(){

        
        
        new Thread(() -> {
        	final String SERVER = wsUrl;  //微耕提供的外网测试服务器
            final int TIMEOUT = 5000; // 套接字连接的超时值（以毫秒为单位）。
            int wsRunning = 0;  //用于连接失败后 重连接  建立连接超时为2秒
            
        	while (true)  //引入循环
            {
        		try {
        			if (wsRunning == 0) {
                        WgWebapi.logInfo("尝试连接ws服务器 " + SERVER);
                        WebSocket webSocket = new WebSocket(new URI(SERVER));
                        webSocket.queue = queue;
                        webSocket.connect();
                        Thread.sleep(2000);
                        if (webSocket.isOpen()) {

                            WgWebapi.logInfo("socket链接成功");
                            wsRunning = 1;
                            int chkcount = 0;
                            long heartcycle = 120 * 1000;  //2分钟 与1001连接通信. 否则关闭重启
                            long endTicks = System.currentTimeMillis() + heartcycle;
                            while (endTicks > System.currentTimeMillis())  //(text = in.readLine()) != null)
                            {
                                if (!queue.isEmpty()) {
                                    dealDataFrom1001Server(webSocket);  //处理来自1001数据
                                    endTicks = System.currentTimeMillis() + heartcycle;  //刷新超时点
                                } else {
                                    try {
                                    	// 为了性能考虑，检测从10毫秒调整为100毫秒一次
                                        Thread.sleep(100);
                                        if (++chkcount > 100) {
                                            chkcount = 0;
                                            if (!webSocket.isOpen()) {
                                                break;
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            webSocket.close();
                            WgWebapi.logInfo("已经关闭和服务端的channel");
                        }
                        if (!webSocket.isClosed()) {
                            webSocket.close();
                        }
                        wsRunning = 0;
                    }
                    //循环
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
                
            }
        }).start();
        

    }


    public void dealDataFrom1001Server(WebSocket ws)  //处理接收到的数据信息
    {
        String msg;
        try {
            synchronized (queue) {
                msg = queue.poll();
            }
            ObjectMapper infojsn = new ObjectMapper();
            JsonNode node = infojsn.readTree(msg);

            if (!node.findPath("刷新时间").isMissingNode()) { //表示 门信息
                for (int i = 0; i < node.size(); i++) {
                    //返回信息示例
                    //    [{
                    //        "SN": 225043096,
                    //            "1号门": "开",
                    //            "2号门": "开",
                    //            "刷新时间": "2021-12-03 10:10:38"
                    //    }]
                    JsonNode doorinfo = node.get(i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date refreshtime = sdf.parse(doorinfo.get("刷新时间").asText());
                    Date curtime = new Date();
                    int controllerSN = doorinfo.get("SN").asInt();
                    if (Math.abs(refreshtime.getTime() - curtime.getTime()) > 60 * 1000)  //超过60秒没同步 通信异常
                    {
                        WgWebapi.logInfo(String.format("控制器SN = %d 通信异常????", controllerSN));
                    }
                }
            } else {
                WgWebapi.logInfoInJson(msg); //加入显示 可以关闭

                // 通行记录推送
                for (int i = 0; i < node.size(); i++) {
                    JsonNode recinfo = node.get(i);
                    webSocketManager.send(EWebSocketCmd.WgTransitRecord, recinfo.toString());
                }
            }

        } catch (JsonProcessingException e) {

        } catch (ParseException e) {

        }
    }

}
