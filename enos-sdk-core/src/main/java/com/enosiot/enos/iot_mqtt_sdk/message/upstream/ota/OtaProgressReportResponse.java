package com.enosiot.enos.iot_mqtt_sdk.message.upstream.ota;

import com.enosiot.enos.iot_mqtt_sdk.core.internals.constants.ArrivedTopicPattern;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.BaseMqttResponse;

import java.util.regex.Pattern;

/**
 * @author zhensheng.cai
 */
public class OtaProgressReportResponse extends BaseMqttResponse {

    /**
     *
     */
    private static final long serialVersionUID = -5564903543466716658L;
    private static Pattern pattern = Pattern.compile(ArrivedTopicPattern.PROGRESS_REPORT_TOPIC_REPLY);

    @Override
    public Pattern getMatchTopicPattern() {
        return pattern;
    }
}
