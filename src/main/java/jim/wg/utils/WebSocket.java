package jim.wg.utils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Queue;

/**
 * socket推送消息(客户端)
 */
public class WebSocket extends WebSocketClient {
    public Queue<String> queue;

    public WebSocket(URI serverUri) {
        super(serverUri);
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String s) {
        synchronized (queue) {
            queue.offer(s);  //1001数据放入缓存队列
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
    }
}


