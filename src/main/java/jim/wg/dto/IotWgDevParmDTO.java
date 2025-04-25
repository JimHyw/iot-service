package jim.wg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 微耕：设备参数--控制器，锁
 *
 * @author dongjian 384880231@qq.com
 * @since 3.0 2022-12-16
 */
@Data
// 微耕：设备参数
public class IotWgDevParmDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //控制器编号
    private Long sn;

    //锁数量
    private Integer lockCount;

    //控制器对应的锁
    private List<Lock> locks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Lock {
        //锁初始名
        private String lockInitName;

        // 锁编号:1,2,3,4
        private Integer loackNo;

        // 状态：在线，常开，常闭
        private String status;

        // 开门延时(秒)：默认3秒, 可设置25秒
        private Integer doordelay;

    }
}