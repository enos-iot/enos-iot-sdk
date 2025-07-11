package com.enosiot.enos.iot_mqtt_sdk.core.profile;

import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author qiwei.tan
 * @version 1.0
 * @program enos-iot-sdk-parent
 * @ClassName EccSSLSocketFactory
 * @Description Ecc connection support
 * @date 2020/2/11 9:28
 */
public class EnosSSLSocketFactory extends SSLSocketFactory {
    private static final String[] ECC_CIPHER_SUITES = new String[]{
            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"
    };
    private static final String[] RSA_CIPHER_SUITES = new String[]{
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256"
    };

    private final SSLSocketFactory factory;
    private final String[] cipherSuites;

    EnosSSLSocketFactory(SSLSocketFactory factory, Boolean isEccConnect) {
        if (factory == null) {
            try {
                factory = new SSLSocketFactoryFactory().createSocketFactory(null);
            } catch (MqttSecurityException e) {
                throw new RuntimeException("don't not create SSLSocketFactory!");
            }
        }
        this.factory = factory;
        this.cipherSuites = Boolean.TRUE.equals(isEccConnect)
                ? ECC_CIPHER_SUITES :
                RSA_CIPHER_SUITES;
    }

    @Override
    public Socket createSocket() throws IOException {
        return encapsulated(factory.createSocket());
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return cipherSuites;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return cipherSuites;
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        return encapsulated(factory.createSocket(socket, s, i, b));
    }

    private Socket encapsulated(Socket socket) {
        if (socket instanceof SSLSocket) {
            ((SSLSocket) socket).setEnabledCipherSuites(getDefaultCipherSuites());
        }
        return socket;
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException {
        return encapsulated(factory.createSocket(s, i));
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException {
        return encapsulated(factory.createSocket(s, i, inetAddress, i1));
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return encapsulated(factory.createSocket(inetAddress, i));
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return encapsulated(factory.createSocket(inetAddress, i, inetAddress1, i1));
    }
}
