package jim.framework.websocket.dto;

import lombok.Data;

/**
 *
 * @author DanielHyw
 * @date 2023/04/04 13:52
 */
@Data
public class WebsocketRequestDTO {
	
	/**
     * 小区id
     */
    private Long villageId;
    
    /**
     * 指令，自定义，用于本地服务器接收后识别
     */
    private String cmd;

    /**
     * 参数
     */
    private Object param;
}
