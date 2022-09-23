import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Slf4j
@EnableDubbo(scanBasePackages = "com.gyl.order.service")
@EnableAutoConfiguration
@MapperScan(basePackages = "com.gyl.order.dao")
public class MallOrderApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(MallOrderApplication.class, args);
            log.info("============程序启动============");
        }catch (Exception e){
            log.info("<=启动失败=>", e);
        }
    }
}
