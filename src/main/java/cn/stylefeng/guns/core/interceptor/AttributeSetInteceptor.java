/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.stylefeng.guns.core.interceptor;

import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.guns.core.shiro.ShiroUser;
import cn.stylefeng.guns.core.supos.ApiVersionSelector;
import cn.stylefeng.guns.core.supos.SupAuth2Api;
import cn.stylefeng.guns.core.util.DefaultImages;
import cn.stylefeng.guns.modular.system.controller.LoginController;
import cn.stylefeng.guns.modular.system.entity.User;
import cn.stylefeng.guns.modular.system.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.bluetron.eco.sdk.api.SuposApi;
import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;
import com.bluetron.eco.sdk.dto.common.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


/**
 * 自动渲染当前用户信息登录属性 的过滤器
 *
 * @author fengshuonan
 * @Date 2018/10/30 4:30 PM
 */
//@Component
public class AttributeSetInteceptor extends HandlerInterceptorAdapter {


    private UserService userService;

    private SupAuth2Api supAuth2Api;

    public AttributeSetInteceptor(SupAuth2Api supAuth2Api,UserService userService){
       this.userService=userService;
        this.supAuth2Api=supAuth2Api;
    }


    private final static Logger logger = LoggerFactory.getLogger(AttributeSetInteceptor.class);

    private static final Map<String,JSONObject> cache = new HashMap<>();


    /**
     * Cookie
     */
    private static String COOKIE_SESSION_ID = null;

    static {
        COOKIE_SESSION_ID = "PROXY_SESSION_ID_" + new Random().nextInt(100000);
    }




    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>>>> requestUrl: " + request.getRequestURI());

        //没有视图的直接跳过过滤器
        if (modelAndView == null || modelAndView.getViewName() == null) {
            return;
        }

        //视图结尾不是html的直接跳过
        if (!modelAndView.getViewName().endsWith("html")) {
            return;
        }
        ShiroUser user = ShiroKit.getUser();

        if (user == null) {

            //以下为 Oauth2授权码认证过程
            String sessionId = getSessionIdFromCookie(request);
            //第一次 判断sessionId是否为空，如果为空 生成sessionID并且获得授权码  302地址
            if (StringUtils.isEmpty(sessionId)){
                //1. oAuth2的授权码模式：认证第一步：获得授权码   设置302地址
                redirect(request, response);
            } else {

                //根据supos版本选择auth接口


                //当session不为空， 302重新跳转进来  判断缓存中的token是否存在
                JSONObject cacheToken = cache.get(sessionId);
                //如果不存在
                if (cacheToken == null || StringUtils.isEmpty(cacheToken.getString("username"))) {
                    //获取授权code
                    String queryString = request.getQueryString();
                    logger.info(">>>>>>>>>>>>>>>>>>>>>> queryString: " + queryString);
                    String accessCode = queryString.substring(queryString.indexOf("=") + 1,queryString.indexOf("&"));
                    logger.info(">>>>>>>>>>>>>>>>>>>>>> accessCode: " + accessCode);
                    String logoutUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/loginController/";
                    JSONObject token = new JSONObject();
                    //2. 授权码模式获取accessToken
                    AuthAccessToken authAccessToken = supAuth2Api.accessToken("", accessCode, logoutUri, "", "");
                    token.put("username",authAccessToken.getUsername());
                    token.put("tokenResult",authAccessToken);
                    cache.put(sessionId, token);

                } else {
                    AuthAccessToken refreshToken = supAuth2Api.refresh("", cacheToken.getJSONObject("tokenResult").getString("refreshToken"), cacheToken.getJSONObject("tokenResult").getString("accessToken"));
                    logger.info(">>>>>>>>>>>>>>>>>>>>>> refreshToken: " + refreshToken);
                    //当refreshToken接口返回成功，重新设置缓存
                    if (refreshToken != null && StringUtils.isNotEmpty(refreshToken.getAccessToken())){
                        cache.get(sessionId).put("username",refreshToken.getUsername());
                        cache.get(sessionId).put("tokenResult",refreshToken);
                    } else {
                        //当refreshToken失效时，需要重新进行授权码认证
                        //并且删除sessionId的缓存
                        cache.remove(sessionId);
                        //重走授权码认证
                        redirect(request, response);
                    }
                }
                User suposUser = userService.getByAccount(cache.get(sessionId).getString("username"));
                if(suposUser != null){
                    ShiroKit.getSession().setAttribute("username", suposUser.getAccount());
                    new LoginController().loginVali();
                } else {
                    throw new AuthenticationException("当前没有登录supos的账号登录该系统！");
                }
            }
            return;
        } else {
            modelAndView.addObject("name", user.getName());
            modelAndView.addObject("avatar", DefaultImages.defaultAvatarUrl());
            modelAndView.addObject("email", user.getEmail());
        }
    }


    public void redirect(HttpServletRequest request, HttpServletResponse response){
        String redirectUrl = request.getScheme() + "://" + request.getServerName() + ":"+request.getServerPort() + request.getRequestURI();
        // 在supOS的APP容器中运行，到后端的请求path，不会带上APP_PATH_PREFIX（/apps/{venderName}-{name}）
        String prefixPath = System.getenv("APP_PATH_PREFIX");
        String suposAddress = System.getenv("SUPOS_SUPOS_ADDRESS");
        if (StringUtils.isNotEmpty(prefixPath) && StringUtils.isNotEmpty(suposAddress)) {
            redirectUrl = suposAddress.concat(prefixPath).concat(request.getRequestURI());
        }
        Cookie cookie = new Cookie(COOKIE_SESSION_ID, UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
        logger.info(">>>>>>>>>>>>>>>>>>>>>> COOKIE_SESSION_ID : " + COOKIE_SESSION_ID);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //1. oAuth2的授权码模式：认证第一步：获得授权码   设置302地址
        response.addCookie(cookie);
        response.addHeader(HttpHeaders.LOCATION, supAuth2Api.authorize("","",redirectUrl,"1"));
        response.setStatus(HttpStatus.FOUND.value());
    }


    public static String getSessionIdFromCookie(final HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_SESSION_ID)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


//    private void forward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        //超时，未登陆页面跳转
//        response.sendRedirect(request.getServletContext().getContextPath()+"/loginController.do?login");
//
////		response.sendRedirect(request.getSession().getServletContext().getContextPath()+"/webpage/login/timeout.jsp");
//
//        //request.getRequestDispatcher("loginController.do?login").forward(request, response);
//
//    }

}
