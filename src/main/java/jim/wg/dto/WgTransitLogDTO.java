package jim.wg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WgTransitLogDTO implements Serializable {

    private static final long serialVersionUID = 6854499006844954340L;

    private Long EventID;   // 索引ID
    private String sn; // 控制器名称
    private String type;//记录类型
    private String desc;//描述
    private String cardNo;// 卡号
    private String doorNum;//门号
    private String inOut;//进出门
    private String status;//有效
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date passTime;//时间
}
