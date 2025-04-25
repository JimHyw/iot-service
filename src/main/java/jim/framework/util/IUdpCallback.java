package jim.framework.util;

/** 
 * @ClassName: IUdpCallback 
 * @Description: udp消息回调
 * @author DanielHyw
 * @date 2024年3月16日 下午1:46:06 
 *  
 */
public interface IUdpCallback {
	
	void callback(String ip, int port, String message, int length);

}
