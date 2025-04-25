package jim.business.console.model;

import java.util.List;

import lombok.Data;

/** 
 * @ClassName: PassagewayList 
 * @Description: 通道列表数据
 * @author DanielHyw
 * @date 2024年1月9日 下午5:59:56 
 *  
 */
@Data
public class PassagewayList {
	private long deviceId;
	
	private List<PassagewayData> list;
	
	public PassagewayList() {
		
	}

	public PassagewayList(long deviceId, List<PassagewayData> list) {
		super();
		this.deviceId = deviceId;
		this.list = list;
	}
	
	
}
