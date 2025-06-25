package com.enosiot.enos.iot_mqtt_sdk.core.msg;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;

/**
 * This class describes the message delivered to mqtt broker by mqtt client.
 * <br/>
 * It includes upstream request {@link IMqttRequest} and the reply of downstream
 * command {@link IMqttReply}
 * <br/>
 * This interface is contrary to {@link IMqttArrivedMessage}.
 */
public interface IMqttDeliveryMessage extends IMqttMessage {
    /**
     * implement the parameter validation by each request
     */
    void check() throws EnosException;

    /**
     * get the payload of the message
     *
     * @return
     */
    byte[] encode();

    /**
     * get the qos of publish message
     *
     * @return the qos
     */
    int getQos();

    @Override
    default void setMessageTopic(String topic) {
        throw new UnsupportedOperationException("answer message type can't set topic");
    }
}
