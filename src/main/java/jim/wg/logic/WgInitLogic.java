package jim.wg.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @title 当Spring Boot应用启动时，实现了CommandLineRunner接口的run方法会被自动调用
 * @author DanielHyw
 */

@Slf4j
@Component
public class WgInitLogic implements CommandLineRunner {
    @Autowired
    private IotWgApiLogic iotWgApiLogic;

    @Override
    public void run(String... args) throws Exception {

        log.info("wg websocket start");
        iotWgApiLogic.wgSocket();
    }
}
