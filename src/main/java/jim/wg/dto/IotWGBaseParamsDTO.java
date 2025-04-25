package jim.wg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotWGBaseParamsDTO implements Serializable {
    private static final long serialVersionUID = 6464729084498764012L;

    // 乡村ID
    private Long villageId;

    // 控制器编号
    private Integer sn;
    

}
