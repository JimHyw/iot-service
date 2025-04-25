package jim.wg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpenDoorDTO extends IotWGBaseParamsDTO {

    //锁编号
    private Integer lockNo;

    // 卡号
    private Long cardNo;

    public OpenDoorDTO(Long villageId, Integer sn, Integer lockNo, Long cardNo) {
        super(villageId, sn);
        this.lockNo = lockNo;
        this.cardNo = cardNo;
    }
}
