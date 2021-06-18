package cn.stylefeng.guns.authTest;

import cn.hutool.core.lang.Assert;
import com.bluetron.eco.sdk.api.SuposApi;
import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;
import com.bluetron.eco.sdk.dto.common.ApiResponse;
import com.bluetron.eco.sdk.service.impl.AuthServiceImpl;
import org.junit.Before;
import org.junit.Test;

public class AuthServiceImplTest {

    private AuthServiceImpl authService;

    private static final String ACCESS_TOKEN = "19b034e43195420fb0c70cac40cddf06";

    @Before
    public void before(){
        authService = new AuthServiceImpl();
    }

    @Test
    public void authorize() {
//        String authorize =  SuposApi.authService.authorize("123");
//        System.out.println(authorize);
//        Assert.notEmpty(authorize);
    }

    @Test
    public void accessToken() {
        ApiResponse<AuthAccessToken> authAccessTokenApiResponse = SuposApi.authService.accessToken("68c51e1d3c74e1d59c412b69dfabcecc","123");
        AuthAccessToken authAccessToken = authAccessTokenApiResponse.getData();
        Assert.notNull(authAccessToken);
    }

    @Test
    public void refreshToken() {
        String refreshToken = "";
        ApiResponse<AuthAccessToken> authAccessTokenApiResponse = SuposApi.authService.refreshToken("accessToken","refreshToken");
        AuthAccessToken authAccessToken = authAccessTokenApiResponse.getData();
        Assert.notNull(authAccessToken);
    }



}