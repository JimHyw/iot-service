package jim.framework.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * @ClassName: UdpUtil 
 * @Description: udp工具
 * @author DanielHyw
 * @date 2024年3月16日 下午1:37:54 
 *  
 */
public class UdpUtil {
	
	private static Map<String, MulticastSocket> runningSocketMap = new ConcurrentHashMap<>();

	/** 
	 * 
	 * @Title: sendUdpGroup 
	 * @Description: 发送udp组播数据
	 * @param ip
	 * @param port
	 * @param message
	 * 
	 */
	public static void sendUdpGroup(String ip, int port, String message) {
        // 1、创建发送端对象：发送端自带默认的端口号
		try {
			DatagramSocket socket = new DatagramSocket();
			byte[] buffer = message.getBytes();
	        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(ip),port);

	        // 3、发送数据出去
	        socket.send(packet);
	        socket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 
	 * @Title: startUdpGroupListen 
	 * @Description: 开启udp组播监听
	 * @param ip
	 * @param port
	 * @param callback
	 * @return
	 * 
	 */
	public static String startUdpGroupListen(String ip, int port, IUdpCallback callback) {
		String runId = IDGeneratorUtil.generatorId();

		new Thread(() -> {
			try {
	        	// 1.创建一个接受端对象，注册端口
	            MulticastSocket socket = new MulticastSocket(port);
	            
	            runningSocketMap.put(runId, socket);
	            //新绑定方法
				socket.joinGroup(new InetSocketAddress(InetAddress.getByName(ip), port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
				// 2.创建一个数据包对象，封装接受的数据
		        byte[] buffer=new byte[1024*64];
		        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
		        while (runningSocketMap.containsKey(runId)) {
		        	try {
		        		//3.等待接收消息
				        socket.receive(packet);

				        //4.打印接收的内容
				        int len = packet.getLength();
				        StringBuffer sbuf = new StringBuffer();
				        for(int i = 0; i < len; i++){
			                if(buffer[i] == 0){
			                    break;
			                }
			                sbuf.append((char) buffer[i]);
			            }			
				        
				        callback.callback(packet.getAddress().toString(), packet.getPort(), sbuf.toString(), len);
			        } catch (IOException e) {
						e.printStackTrace();
					}
		        }
		        socket.close();
	        } catch (IOException e) {
				e.printStackTrace();
			}
			
		}).start();
		
        return runId;
	}
	
	/**
	 * 
	 * @Title: stopUdpGroupListen 
	 * @Description: 停止udp消息监听
	 * @param runId
	 *
	 */
	public static void stopUdpGroupListen(String runId) {
		MulticastSocket socket = runningSocketMap.remove(runId);
		if (socket != null) {
			socket.close();
		}
	}
	
}
