package jim.framework.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import javax.annotation.PostConstruct;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jim.framework.constant.SystemConstant;
import jim.framework.util.AESUtil;
import jim.framework.websocket.manager.WebSocketManager;

/**
 * @title MyWebSocketClient client = new MyWebSocketClient()
 */
@Component
@ClientEndpoint
public class WebSocketClient {
	@Value("${websocket.server.url:36.133.245.51:9080/websocket/changju_Socket}")
    private String serverUrl;

    @Value("${websocket.server.user:1635913493071982594}")
    private String user;

    private Session session;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 是否已连接
     * @return
     */
    public boolean linked() {
    	return session != null && session.isOpen();
    }

    @PostConstruct
    void init() {
    	doLinkServer();
    }
    
    private void doLinkServer() {
    	new Thread(() -> {
    		while (true) {
    			try {
    				// 每5秒检测一次连接状态
    				Thread.sleep(5000);
    				logger.info("check websocket:" + linked());
    				if (linked()) {
    					continue;
    				}
    				linkServer();
    				// 尝试连接后，额外等待5秒。
    				Thread.sleep(5000);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    		
    	}).start();
    }
    
    private void linkServer() {
    	try {
            // 本机地址
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String userId = URLEncoder.encode(AESUtil.AES_Encrypt(user, SystemConstant.SOCKET_LINK_SIGN_KEY, "ECB"), "UTF-8"); 
            String wsUrl = "ws://" + serverUrl + "?userId=" + userId;
            URI uri = URI.create(wsUrl);
            session = container.connectToServer(WebSocketClient.class, uri);
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
    	logger.info("websocket 已连接");
        this.session = session;
    }

    /**
     * 接收消息
     * @param text
     */
    @OnMessage
    public void onMessage(String text) {
        logger.info("websocket 收到消息：" + text);
        WebSocketManager.impl.onReceive(text);
    }

    /**
     * 异常处理
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        logger.error("websocker error:" + throwable.getMessage());
        
        try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClosing() throws IOException {
    	logger.info("websocket 已断开");
        session.close();
    }

    /**
     * 主动发送消息
     */
    public void send(String message) {
    	// logger.info("websocket 发送消息：" + message);
        this.session.getAsyncRemote().sendText(message);
    }
    public void close() throws IOException{
        if(this.session.isOpen()){
            this.session.close();
        }
    }
	
}
