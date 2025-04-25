package jim.wg.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ResultDTO implements Serializable {
    private static final long serialVersionUID = -6832626895449282698L;

    private String status;
    private String msg;
    private String data;
}
