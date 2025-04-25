package jim.wg.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoveLockDTO extends IotWGBaseParamsDTO {

    // 锁编号
    private Integer lockNo;

    public MoveLockDTO(Long villageId, Integer sn, Integer lockNo) {
        super(villageId, sn);
        this.lockNo = lockNo;
    }
}
