package com.enosiot.enos.iot_mqtt_sdk.core.compositejks;

public interface ProtocolStep {

    KeyManagerAlgorithmStep usingProtocol(String socketProtocol);

    default KeyManagerAlgorithmStep usingTLS() {
        return usingProtocol("TLS");
    }
}
