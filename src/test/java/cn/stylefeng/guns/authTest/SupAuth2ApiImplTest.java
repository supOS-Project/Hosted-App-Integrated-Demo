package cn.stylefeng.guns.authTest;

import cn.hutool.core.lang.Assert;
import cn.stylefeng.guns.core.supos.impl.SupAuth2ApiImpl;
import com.bluetron.eco.sdk.dto.auth.AuthAccessToken;
import org.junit.Before;
import org.junit.Test;

/**
 * @Description
 * @Author: LZH <liangzhihui@supcon.com>
 * @Date: 2021/6/17
 */
public class SupAuth2ApiImplTest {

    private SupAuth2ApiImpl supAuth2ApiImpl;


    @Before
    public void before(){
        supAuth2ApiImpl = new SupAuth2ApiImpl();
    }


    @Test
    public void authorize() {
        String authorize = supAuth2ApiImpl.authorize("","", "https://www.baidu.com/","1");
        System.out.println(authorize);
        Assert.notEmpty(authorize);
    }

    @Test
    public void accessToken() {
        AuthAccessToken authAccessToken = supAuth2ApiImpl.accessToken("", "5e5b1918038c5043cfc94cf727311ad3", "", "", "");
        System.out.println(authAccessToken);
        Assert.notNull(authAccessToken);
    }

    @Test
    public void refresh() {
        AuthAccessToken refresh = supAuth2ApiImpl.refresh("", "bd3186dca2d201cb7ade067dafb9e2b8", "");
        System.out.println(refresh);
        Assert.notNull(refresh);
    }

    @Test
    public void findUser()  {
        String user = supAuth2ApiImpl.findUser("", "b9cd035352549ade611f6b7c48ccce68");
        System.out.println(user);
        Assert.notNull(user);
    }
}
