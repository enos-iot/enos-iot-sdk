package com.enosiot.enos.iot_mqtt_sdk.core.msg;

public interface IAnswerable<T> {

    Class<T> getAnswerType();

    String getAnswerTopic();
}
