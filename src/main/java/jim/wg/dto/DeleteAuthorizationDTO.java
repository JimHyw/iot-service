package jim.wg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeleteAuthorizationDTO extends IotWGBaseParamsDTO {

    // （门）锁编号
    private Integer lockNo;
    
    // 授权卡号（住户手机号）
    private List<Long> cardNoList;

    public DeleteAuthorizationDTO(Long villageId, Integer sn, Integer lockNo, List<Long> cardNoList) {
        super(villageId, sn);
        this.lockNo = lockNo;
        this.cardNoList = cardNoList;
    }
}