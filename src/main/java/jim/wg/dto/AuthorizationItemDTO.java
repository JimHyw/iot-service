package jim.wg.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class AuthorizationItemDTO implements Serializable {
    private static final long serialVersionUID = -5640979469117110571L;

    private Long id;
    private Long cardNo;
    private String startDate;
    private String endDate;
    private String password;
    private Map<Integer, Integer> lockList;
}
