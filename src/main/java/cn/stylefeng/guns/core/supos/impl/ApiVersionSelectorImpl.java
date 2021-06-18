package cn.stylefeng.guns.core.supos.impl;


import cn.stylefeng.guns.core.supos.ApiVersionSelector;
import cn.stylefeng.guns.core.supos.SupAuth2Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * supos api版本选择器实现
 * @author caonuoqi
 */
@Component
public class ApiVersionSelectorImpl implements ApiVersionSelector {
    private final static String SUPOS_VERSION_2 = "2";
    private final static String SUPOS_VERSION_3 = "3";
    @Autowired
    Map<String, SupAuth2Api> supAuth2Api;
    @Override
    public SupAuth2Api getSupAuth2Api(String suposVersion) {
        if (suposVersion.startsWith(SUPOS_VERSION_2)) {
            return supAuth2Api.get("supAuth2Api");
        } else if (suposVersion.startsWith(SUPOS_VERSION_3)) {
            return supAuth2Api.get("sup30Auth2Api");
        }
        return supAuth2Api.get("supAuth2Api");
    }
}
