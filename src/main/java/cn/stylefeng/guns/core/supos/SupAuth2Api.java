package cn.stylefeng.guns.core.supos;

import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;

/**
 * Auth2的认证
 *
 * @author 26007 - <huangjianbo@supcon.com>
 * @date 19-8-15 下午3:21
 */
public interface SupAuth2Api {

    /**
     * oAuth2的授权码模式：组装302重定向地址
     *
     * @param supOSAddr   supOS的地址
     * @param AppId
     * @param redirectUri 当前请求的地址
     * @param state       app的状态值
     * @return
     */
    String authorize(String supOSAddr, String AppId, String redirectUri, String state);

    /**
     * oAuth2的授权码模式：认证第二步：通过授权码，获得凭证
     *
     * @param suposAddress
     * @param accessCode
     * @param logoutUri
     * @param appId
     * @param appSecret
     * @return
     */
    AuthAccessToken accessToken(String suposAddress, String accessCode, String logoutUri, String appId, String appSecret);
//    /**
//     * 校验凭证
//     *
//     * @param suposAddress supOS地址
//     * @param accessToken 凭证
//     * @return
//     */
//    boolean valid(String suposAddress, String accessToken);

    /**
     * oAuth2的授权码模式：刷新凭证
     *
     * @param suposAddress supOS地址
     * @param refreshToken 原交换凭证
     * @return 新的凭证信息
     */
    AuthAccessToken refresh(String suposAddress, String refreshToken, String accessToken);
    /**
     * 通过凭证获得当前supOS登录的用户名
     *
     * @param suposAddress supOS地址
     * @param accessToken 凭证
     * @return
     */
    String findUser(String suposAddress, String accessToken);

//    /**
//     * oAuth2的客户端模式：获得token
//     *
//     * @param suposAddress supOS地址
//     * @param appId
//     * @param secretKey
//     * @return
//     */
//    Token getClientToken(String suposAddress, String appId, String secretKey);
//
//    /**
//     * oAuth2的客户端模式：刷新token
//     *
//     * @param suposAddress supOS地址
//     * @param token
//     * @return
//     */
//    Token refreshClientToken(String suposAddress, Token token);
}
