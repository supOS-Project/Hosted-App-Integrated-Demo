package cn.stylefeng.guns.core.supos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 26007 - <huangjianbo@supcon.com>
 * @date 19-8-7 下午1:37
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "envoy.app")
public class AppConfig {

    /**
     * app的ID
     */
    private String id;
    /**
     * app的密令
     */
    private String secret;


    private String username;

    private String password;
}
