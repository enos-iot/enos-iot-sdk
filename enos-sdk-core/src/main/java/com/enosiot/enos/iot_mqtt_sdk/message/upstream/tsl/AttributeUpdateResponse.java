package com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl;

import com.enosiot.enos.iot_mqtt_sdk.core.internals.constants.ArrivedTopicPattern;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.BaseMqttResponse;

import java.util.regex.Pattern;

/**
 * @author zhensheng.cai
 * @date 2018/11/9.
 */
public class AttributeUpdateResponse extends BaseMqttResponse{

    private static final long serialVersionUID = 176089448308286493L;

    private static final Pattern pattern = Pattern.compile(ArrivedTopicPattern.ATTRIBUTE_UPDATE_REPLY);

    @Override
    public Pattern getMatchTopicPattern() {
        return pattern;
    }
}
