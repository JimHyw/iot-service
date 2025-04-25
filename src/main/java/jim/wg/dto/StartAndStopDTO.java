package jim.wg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartAndStopDTO extends IotWGBaseParamsDTO {

    private static final long serialVersionUID = 6854499006844954340L;

    // （门）锁编号
    private Integer lockNo;
    
    // 状态：0 常闭 1 在线 2 常开
    private Integer status;

    public StartAndStopDTO(Long villageId, Integer sn, Integer lockNo, Integer status) {
        super(villageId, sn);
        this.lockNo = lockNo;
        this.status = status;
    }
}
