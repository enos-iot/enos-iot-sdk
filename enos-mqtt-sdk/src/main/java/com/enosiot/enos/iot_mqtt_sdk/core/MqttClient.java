package com.enosiot.enos.iot_mqtt_sdk.core;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.core.internals.MqttConnection;
import com.enosiot.enos.iot_mqtt_sdk.core.msg.*;
import com.enosiot.enos.iot_mqtt_sdk.core.profile.BaseProfile;
import com.enosiot.enos.iot_mqtt_sdk.core.profile.DefaultProfile;
import com.enosiot.enos.iot_mqtt_sdk.extension.ExtServiceFactory;

/**
 * This class defines the most important APIs to communicate with enos mqtt broker.
 * The implementations are mainly delegated to class {@link MqttConnection}. To know
 * more about how to make use of the APIs. please refer to our documentation here
 * <a href="https://github.com/enosiot/enos-iot-mqtt-java-sdk">enos-iot-mqtt-java-sdk</a>.
 *
 * @author zhensheng.cai
 * @author jian.zhang4
 */
public class MqttClient {

    private MqttConnection connection;
    private ExtServiceFactory serviceFactory = new ExtServiceFactory();


    /**
     * @param uri          mqtt broker server uri
     * @param productKey   productKey of login device credential
     * @param deviceKey    deviceKey
     * @param deviceSecret deviceSecret
     */
    public MqttClient(String uri, String productKey, String deviceKey, String deviceSecret) {
        this(new DefaultProfile(uri, productKey, deviceKey, deviceSecret));
    }

    /**
     * init with client profile
     * @param profile client config profile
     */
    public MqttClient(BaseProfile profile) {
        this(profile, new ExecutorFactory());
    }

    /**
     * init with client profile and customized executor factory (thread-pool factory)
     * @param profile         client config profile
     * @param executorFactory customized executor factory (thread-pool factory)
     */
    public MqttClient(BaseProfile profile, IExecutorFactory executorFactory) {
        connection = new MqttConnection(profile, executorFactory);
    }

    public BaseProfile getProfile() {
        return connection.getProfile();
    }

    /**
     * Publish the request and NOT care about the response. However, this method still
     * blocks until request is sent out and mqtt ack is returned if qos is not 0. But
     * it doesn't need to wait for broker response, which answers the request in more
     * business-oriented way.
     *
     * @throws EnosException
     */
    public void fastPublish(IMqttDeliveryMessage request) throws EnosException {
        this.connection.fastPublish(request);
    }

    /**
     * Publish the request and wait for the response.
     *
     * @throws EnosException
     */
    public <T extends IMqttResponse> T publish(IMqttRequest<T> request) throws EnosException {
        return connection.publish(request);
    }

    /**
     * Publish the request in async way (thus doesn't block current thread). When response
     * is ready or error happens, the callback would be invoked.
     */
    public <T extends IMqttResponse> void publish(IMqttRequest<T> request, IResponseCallback<T> callback) {
        connection.publish(request, callback);
    }

    /**
     * set the msg handler for specific arrived msg
     */
    public <T extends IMqttArrivedMessage, D extends IMqttDeliveryMessage> void setArrivedMsgHandler(Class<T> arrivedMsgCls, IMessageHandler<T, D> handler) {
        connection.setArrivedMsgHandler(arrivedMsgCls, handler);
    }

    public ExtServiceFactory getExtServiceFactory() {
        return serviceFactory;
    }

    /**
     * Connect to broker. This method blocks until it connects successfully or fails. It's
     * recommended to use {@link MqttClient#connect(IConnectCallback)} since it allows for
     * notification when connection is lost in the future.
     *
     * @throws EnosException if error happens
     */
    public void connect() throws EnosException {
        this.connection.connect();
    }

    public void connect(ConnCallback callback) {
        this.connection.connect(callback);
    }

    /**
     * This method is used to re-connect to broker after invoking {@link MqttClient#disconnect()}. Also
     * this method can even be called to force re-connecting to broker after {@link MqttClient#connect()}
     * or {@link MqttClient#connect(ConnCallback)}.
     *
     * @throws EnosException
     */
    public void reconnect() throws EnosException {
        this.connection.reconnect();
    }

    /**
     * Disconnect from mqtt broker. This method mainly tears down the underlying network
     * connection but still keeps other resources like thread pools. <br/>
     * <br/>
     * If you want to re-use this MqttClient to publish message later, you just need invoke
     * {@link MqttClient#connect()} or {@link MqttClient#connect(IConnectCallback)}. <br/>
     * <br/>
     * If you don't need this MqttClient any more, you MUST invoke {@link MqttClient#close()}
     * to release all the underlying resources.
     *
     * @throws EnosException
     */
    public void disconnect() throws EnosException {
        this.connection.disconnect();
    }

    /**
     * Close this MqttClient and release all underlying resources. After this operation,
     * you should not invoke any method any more. Otherwise, exception would be thrown.
     */
    public void close() {
        this.connection.close();
    }

    public boolean isConnected() {
        return this.connection.isConnected();
    }

}
