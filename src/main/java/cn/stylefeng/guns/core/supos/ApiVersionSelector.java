package cn.stylefeng.guns.core.supos;

import cn.stylefeng.guns.core.supos.SupAuth2Api;

/**
 * open-api 版本选择器
 * @author liangzhihui
 *
 */
public interface ApiVersionSelector {
    /**
     * 根据supos版本选择对应auth2实现
     * @param suposVersion  supos版本
     * @return
     */
    SupAuth2Api getSupAuth2Api(String suposVersion);
}
