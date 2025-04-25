package jim.wg.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 授权参数
 */
@Data
@NoArgsConstructor
public class AuthorizationDTO extends IotWGBaseParamsDTO {


    // （门）锁编号
    private Integer lockNo;

    //授权卡号（住户手机号、IC卡号）
    private List<Long> cardNoList;

    // 0 禁用 1 启用
    private Integer enable;

    // 有效时间
    private Date validityDate;

    public AuthorizationDTO(Long villageId, Integer sn, Integer lockNo, List<Long> cardNoList, Integer enable) {
        super(villageId, sn);
        this.lockNo = lockNo;
        this.cardNoList = cardNoList;
        this.enable = enable;
    }
    
}
