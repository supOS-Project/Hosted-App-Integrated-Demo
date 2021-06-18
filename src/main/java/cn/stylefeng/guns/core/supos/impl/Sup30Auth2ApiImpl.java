package cn.stylefeng.guns.core.supos.impl;

import cn.stylefeng.guns.core.supos.SupAuth2Api;
import com.bluetron.eco.sdk.api.SuposApi;
import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;
import com.bluetron.eco.sdk.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @author 26007 - <huangjianbo@supcon.com>
 * @date 19-8-15 下午3:41
 */
@Component("sup30Auth2Api")
@Slf4j
public class Sup30Auth2ApiImpl implements SupAuth2Api {

    private final static Logger logger = LoggerFactory.getLogger(Sup30Auth2ApiImpl.class);

    @Override
    public String authorize(String supOSAddr, String AppId, String redirectUri, String state) {
        return SuposApi.authService.authorize(redirectUri, state);
    }

    @Override
    public AuthAccessToken accessToken(String suposAddress, String accessCode, String logoutUri, String appId, String appSecret) {
        ApiResponse<AuthAccessToken> authAccessTokenApiResponse = SuposApi.authService.accessToken(accessCode, logoutUri);
        logger.info(">>>>>>>>>>>>>>>>>>>>>> authAccessTokenApiResponse: " + authAccessTokenApiResponse.toString());
        if (!authAccessTokenApiResponse.isSuccess()){
            return null;
        }
        return authAccessTokenApiResponse.getData();
    }


    @Override
    public AuthAccessToken refresh(String supOSAddress, String refreshToken, String accessToken ) {
        ApiResponse<AuthAccessToken> authAccessTokenApiResponse = SuposApi.authService.refreshToken(accessToken, refreshToken);
        logger.info(">>>>>>>>>>>>>>>>>>>>>> authAccessTokenApiResponse: " + authAccessTokenApiResponse.toString());
        if (!authAccessTokenApiResponse.isSuccess()){
            return null;
        }
        return authAccessTokenApiResponse.getData();
    }

    @Override
    public String findUser(String suposAddress, String accessToken) {
        return null;
    }


}
