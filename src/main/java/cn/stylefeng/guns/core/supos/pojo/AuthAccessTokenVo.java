package cn.stylefeng.guns.core.supos.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 26007 - <huangjianbo@supcon.com>
 * @date 19-8-15 下午3:24
 */
@Slf4j
@ToString
public class  AuthAccessTokenVo extends Vo {
//    private static final long serialVersionUID = 4229698091473283894L;
//
//    /**
//     * 凭证
//     */
//    @Setter
//    @Getter
//    private String accessToken;
//    /**
//     * 刷新交换凭证
//     */
//    @Setter
//    @Getter
//    private String refreshToken;
//    /**
//     * 过期时间（毫秒）
//     */
//    @Setter
//    @Getter
//    private Long expiresIn;
//
//    @Setter
//    @Getter
//    private String userName;
//
//    @Setter
//    @Getter
//    private String personCode;
//
//    @Setter
//    @Getter
//    private String companyCode;
//
//    @Setter
//    @Getter
//    private Integer accountType;
//    private static volatile AuthAccessTokenVo token;
//
//    public static AuthAccessTokenVo getToken() {
//        if (token == null) {
//            synchronized (AuthAccessTokenVo.class) {
//                if (token == null) {
//                    throw new HttpClientCallException(ErrorDefine.CALL_SUPOS_AUTH_FIALED);
//                }
//                return token;
//            }
//        }
//        return token;
//    }
//
//    public static void setToken(AuthAccessTokenVo token) {
//        AuthAccessTokenVo.token = token;
//        if (token != null && log.isDebugEnabled()) {
//            log.debug("AuthAccessTokenVo is {}", token.toString());
//        }
//    }
}
