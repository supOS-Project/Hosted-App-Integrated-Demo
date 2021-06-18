package cn.stylefeng.guns.core.supos.impl;

import cn.stylefeng.guns.core.supos.SupAuth2Api;
import cn.stylefeng.guns.core.util.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 26007 - <huangjianbo@supcon.com>
 * @date 19-8-15 下午3:41
 */
@Component("supAuth2Api")
@Slf4j
public class SupAuth2ApiImpl implements SupAuth2Api {

    //    private static final String OAUTH2_URL = "http://isv-integrate-1.demo.devcloud.supos.net/oauth2";
//    private static final String OAUTH2_URL = "http://saas-002.devcloud.supos.net/oauth2";
//    private static final String OAUTH2_URL = "http://crm.bluetron.cn/oauth2";

//    private static final String APP_ID = "App_b6ac673bba464552bb6761b9316a854b";
//    private static final String APP_ID = "App_359654163b714a7fbafce6fa75889801";
//    private static final String APP_ID = "App_fd50ec33e15b4f27a96676e58353c0bd";

//    private static final String SECRET = "f3b6c5a824baa306be2ffc2b2bae50bc";
//    private static final String SECRET = "9e73ada37fc660e570a550e664aecf51";
//    private static final String SECRET = "4cf86901675ffd372e7f6b43d2ab4476";

//
//    private static final String APP_ID = System.getenv("SUPOS_SUPOS_APP_ACCOUNT_ID");
//
//    private static final String SECRET = System.getenv("SUPOS_SUPOS_APP_SECRET_KEY");

    private static final String OAUTH2_URL = System.getenv("SUPOS_SUPOS_ADDRESS") == null ? "http://isv-integrate-1.demo.devcloud.supos.net/oauth2" : System.getenv("SUPOS_SUPOS_ADDRESS").concat("/oauth2");
    private static final String APP_ID = System.getenv("SUPOS_SUPOS_APP_ACCOUNT_ID") == null ? "App_b6ac673bba464552bb6761b9316a854b" : System.getenv("SUPOS_SUPOS_APP_ACCOUNT_ID");
    private static final String SECRET = System.getenv("SUPOS_SUPOS_APP_SECRET_KEY") == null ? "f3b6c5a824baa306be2ffc2b2bae50bc" : System.getenv("SUPOS_SUPOS_APP_SECRET_KEY");

    /**
     * oAuth2的授权码模式：认证第一步：获得授权码
     *
     * @param redirectUri 回调地址
     * @return
     */
    @Override
    public String authorize(String supOSAddr, String AppId, String redirectUri, String state) {
        String path = OAUTH2_URL + "/code/v1/authorize";
        return path.concat("?responseType=code")
                .concat("&appid=").concat(APP_ID)
                .concat("&state=").concat(state)
                .concat("&redirectUri=").concat(redirectUri);
    }

    @Override
    public AuthAccessToken accessToken(String suposAddress, String accessCode, String logoutUri, String appId, String appSecret) {
        String path = OAUTH2_URL + "/code/v1/accessToken";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("grantType", "authorization_code");
        queryParams.put("appid", APP_ID);
        queryParams.put("secret", SECRET);
        queryParams.put("code", accessCode);
        queryParams.put("logoutUri", logoutUri);
        String result = HttpUtils.doGet(path, queryParams);
        AuthAccessToken authAccessToken = new AuthAccessToken();
        JSONObject jsonObject = JSONObject.parseObject(result);
        authAccessToken.setAccessToken(jsonObject.get("accessToken").toString());
        authAccessToken.setExpiresIn(Long.parseLong(String.valueOf(jsonObject.get("expiresIn"))));
        authAccessToken.setRefreshToken(jsonObject.get("refreshToken").toString());
        String userName = findUser("", jsonObject.get("accessToken").toString());
        authAccessToken.setUsername(userName);
        return authAccessToken;
    }

    @Override
    public AuthAccessToken refresh(String suposAddress, String refreshToken, String accessToken) {
        String path = OAUTH2_URL + "/code/v1/refreshToken?refreshToken=" + refreshToken;
        String result = HttpUtils.doGet(path,null);
        AuthAccessToken authAccessToken = new AuthAccessToken();
        JSONObject jsonObject = JSONObject.parseObject(result);
        authAccessToken.setAccessToken(jsonObject.get("accessToken").toString());
        authAccessToken.setExpiresIn(Long.parseLong(String.valueOf(jsonObject.get("expiresIn"))));
        authAccessToken.setRefreshToken(jsonObject.get("refreshToken").toString());
        String userName = findUser("", jsonObject.get("accessToken").toString());
        authAccessToken.setUsername(userName);
        return authAccessToken;
    }

    @Override
    public String findUser(String suposAddress, String accessToken) {
        String path = OAUTH2_URL + "/v1/" + accessToken + "/username";
        String result = HttpUtils.doGet(path, null);
        return JSONObject.parseObject(result).getString("userName");
    }

}
