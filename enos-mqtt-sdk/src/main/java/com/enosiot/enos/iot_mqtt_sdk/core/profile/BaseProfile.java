package com.enosiot.enos.iot_mqtt_sdk.core.profile;

import com.enosiot.enos.iot_mqtt_sdk.core.ClientInfoUtil;
import com.enosiot.enos.iot_mqtt_sdk.core.codec.CompressType;
import com.enosiot.enos.iot_mqtt_sdk.core.compositejks.SslContextBuilder;
import com.enosiot.enos.iot_mqtt_sdk.core.internals.SignMethod;
import com.enosiot.enos.iot_mqtt_sdk.core.internals.SignUtil;
import com.enosiot.enos.iot_mqtt_sdk.core.profile.DeviceCredential;
import com.enosiot.enos.iot_mqtt_sdk.util.SecureMode;
import com.enosiot.enos.iot_mqtt_sdk.util.SecureModeUtil;
import com.enosiot.enos.iot_mqtt_sdk.util.StringUtil;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;

public abstract class BaseProfile {

    public static final String VERSION = "1.1";

    protected Config config = new Config();

    private long timestamp = System.currentTimeMillis();
    private SSLContext sslContext = null;

    /**
     * reload the profile, when properties of the profiles is changed
     */
    public abstract void reload();


    public Config getConfig() {
        return config;
    }

    public String getServerUrl() {
        return config.getServerUrl();
    }

    public String getProductKey() {
        return config.getProductKey();
    }

    public String getDeviceKey() {
        return config.getDeviceKey();
    }

    public String getDeviceSecret() {
        return config.getDeviceSecret();
    }

    public BaseProfile setProductKey(String productKey) {
        this.config.setProductKey(productKey);
        return this;
    }

    public BaseProfile setProductSecret(String productSecret) {
        this.config.setProductSecret(productSecret);
        return this;
    }

    public BaseProfile setDeviceKey(String deviceKey) {
        this.config.setDeviceKey(deviceKey);
        return this;
    }

    public BaseProfile setDeviceSecret(String deviceSecret) {
        this.config.setDeviceSecret(deviceSecret);
        return this;
    }

    public Set<DeviceCredential> getSubDevices() {
        return this.config.getSubDevices();
    }

    public BaseProfile setSubDevices(Set<DeviceCredential> subDevices) {
        this.config.setSubDevices(subDevices);
        return this;
    }

    public BaseProfile addSubDevice(DeviceCredential subDevice) {
        this.config.getSubDevices().add(subDevice);
        return this;
    }

    public MqttConnectOptions createConnectOptions() {
        String mqttUsername = config.getDeviceKey() + '&' + config.getProductKey();
        SecureMode secureMode = getSecureMode();

        Map<String, String> params = new HashMap<String, String>();
        params.put("productKey", getProductKey());
        params.put("deviceKey", getDeviceKey());
        params.put("clientId", secureMode.getClientId() + extraInfo());
        params.put("timestamp", timestamp + "");

        String mqttPassword = SignUtil.sign(secureMode.getSecret(), params, getSignMethod());

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setUserName(mqttUsername);
        connectOptions.setPassword(mqttPassword.toCharArray());
        connectOptions.setKeepAliveInterval(this.config.getKeepAlive());
        connectOptions.setAutomaticReconnect(this.config.getAutoReconnect());
        connectOptions.setConnectionTimeout(this.config.getConnectionTimeout());
        connectOptions.setMaxInflight(this.config.getMaxInFlight());
        connectOptions.setHttpsHostnameVerificationEnabled(this.config.isHostnameVerifyEnabled());
        connectOptions.setMqttVersion(MQTT_VERSION_3_1_1);
        if (config.getSslSecured()) {
            if (this.sslContext == null) {
                try {
                    this.sslContext = createContext(this.config.getSslJksPath(), this.config.getSslPassword(), this.config.getSslAlgorithm());
                } catch (Exception e) {
                    throw new RuntimeException("create SSL context failed", e.fillInStackTrace());
                }
            }
            //  Ensure SSLSocketFactory is created on the basis of enabling bi-directional authentication
            connectOptions.setSocketFactory(new EnosSSLSocketFactory(this.sslContext.getSocketFactory(), this.config.isEccConnect()));
        }

        return connectOptions;
    }


    public int getKeepAlive() {
        return config.getKeepAlive();
    }

    /**
     * set connection keepAlive SECONDS
     *
     * @param keepAlive seconds
     * @return
     */
    public BaseProfile setKeepAlive(int keepAlive) {
        this.config.setKeepAlive(keepAlive);
        return this;
    }

    public int getConnectionTimeout() {
        return this.config.getConnectionTimeout();
    }

    /**
     * set connection timeout SECONDS
     *
     * @param connectionTimeout seconds
     * @return
     */
    public BaseProfile setConnectionTimeout(int connectionTimeout) {
        this.config.setConnectionTimeout(connectionTimeout);
        return this;
    }

    public int getTimeToWait() {
        return this.config.getOperationTimeout();
    }

    /**
     * @param timeToWait SECONDS to wait for the response message
     */
    public BaseProfile setTimeToWait(int timeToWait) {
        this.config.setOperationTimeout(timeToWait);
        return this;
    }

    public String getClientId() {
        return getSecureMode().getClientId() + extraInfo()
                + "|securemode=" + getSecureMode().getModeId()
                + ",signmethod=" + getSignMethod().getName()
                + ",timestamp=" + timestamp + "|";
    }

    public BaseProfile setMaxInFlight(int maxInFlight) {
        this.config.setMaxInFlight(maxInFlight);
        return this;
    }

    public BaseProfile setAutoReconnect(boolean autoReconnect) {
        this.config.setAutoReconnect(autoReconnect);
        return this;
    }

    public boolean isAutoReconnect() {
        return this.config.getAutoReconnect();
    }

    public BaseProfile setCompressType(CompressType compressType) {
        this.config.setCompressType(compressType);
        return this;
    }

    public CompressType getCompressType() {
        return this.config.getCompressType();
    }

    public BaseProfile setAutoLoginSubDevice(boolean autoLoginSubDevice) {
        this.config.setAutoLoginSubDevice(autoLoginSubDevice);
        return this;
    }

    public boolean isAutoLoginSubDevice() {
        return this.config.getAutoLoginSubDevice();
    }

    public BaseProfile setSSLSecured(boolean sslSecured) {
        this.config.setSslSecured(sslSecured);
        return this;
    }


    public BaseProfile setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    public SecureMode getSecureMode() {
        return SecureModeUtil.getSecureMode(config.getProductKey(), config.getProductSecret(),
                config.getDeviceKey(), config.getDeviceSecret());
    }

    public BaseProfile setSSLJksPath(String sslJksPath, String sslPassword) {
        this.config.setSslJksPath(sslJksPath);
        this.config.setSslPassword(sslPassword);
        return this;
    }

    public BaseProfile setSSLAlgorithm(String sslAlgorithm) {
        this.config.setSslAlgorithm(sslAlgorithm);
        return this;
    }

    public SignMethod getSignMethod() {
        return this.config.getSignMethod();
    }

    public BaseProfile setSignMethod(SignMethod signMethod) {
        this.config.setSignMethod(signMethod);
        return this;
    }

    public BaseProfile setServerUrl(String serverUrl) {
        this.config.setServerUrl(serverUrl);
        return this;
    }

    public BaseProfile setEccConnect(boolean isEccConnect) {
        this.config.setEccConnect(isEccConnect);
        return this;
    }

    public BaseProfile setHostnameVerifyEnabled(boolean hostnameVerifyEnabled) {
        this.config.setHostnameVerifyEnabled(hostnameVerifyEnabled);
        return this;
    }

    private String extraInfo() {
        String extraInfo = "";
        StringBuilder sb = new StringBuilder();

        String ip = ClientInfoUtil.getClientIp();
        String version = ClientInfoUtil.getClientVersion();

        if (StringUtil.isNotEmpty(ip)) {
            sb.append(String.format(",ip=%s", ip));
        }
        if (StringUtil.isNotEmpty(version)) {
            sb.append(String.format(",version=%s", version));
        }
        if (config.getCompressType() != null) {
            sb.append(String.format(",compress=%s", config.getCompressType()));
        }

        if (sb.toString().startsWith(",")) {
            extraInfo = String.format("{%s}", sb.substring(1));
        }

        return extraInfo;
    }

    private static SSLContext createContext(String keyPath, String pwd, String algorithm) throws Exception {
        return SslContextBuilder.builder()
                .keyStoreFromFile(keyPath, pwd)
                .usingTLS()
                .usingAlgorithm(algorithm)
                .usingKeyManagerPasswordFromKeyStore()
                .buildMergedWithSystem();

    }

}
