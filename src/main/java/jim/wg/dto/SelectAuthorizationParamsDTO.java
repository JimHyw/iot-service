package jim.wg.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectAuthorizationParamsDTO extends IotWGBaseParamsDTO {

    // 授权卡号（住户手机号）
    private List<Long> cardNoList;
}
