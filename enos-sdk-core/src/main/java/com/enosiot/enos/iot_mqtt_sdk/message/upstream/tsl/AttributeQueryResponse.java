package com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl;

import com.enosiot.enos.iot_mqtt_sdk.core.internals.constants.ArrivedTopicPattern;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.BaseMqttResponse;

import java.util.regex.Pattern;

/**
 * @author zhensheng.cai
 * @date 2018/11/12.
 */
public class AttributeQueryResponse extends BaseMqttResponse{

    private static final long serialVersionUID = -5197339728712464117L;

    private static final Pattern pattern = Pattern.compile(ArrivedTopicPattern.ATTRIBUTE_QUERY_REPLY);

    @Override
    public Pattern getMatchTopicPattern() {
        return pattern;
    }
}
