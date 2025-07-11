package com.enosiot.enos.iot_mqtt_sdk.message.upstream;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.core.msg.IMqttRequest;
import com.enosiot.enos.iot_mqtt_sdk.message.BaseAnswerableMessage;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.UploadFileInfo;
import com.enosiot.enos.iot_mqtt_sdk.util.CheckUtil;
import com.enosiot.enos.iot_mqtt_sdk.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhensheng.cai
 * @date 2018/7/4.
 */
public abstract class BaseMqttRequest<T extends BaseMqttResponse> extends BaseAnswerableMessage<T>
        implements IMqttRequest<T> {
    private static final long serialVersionUID = -1782194368038165072L;

    private int qos = 1;

    private String productKey;
    private String deviceKey;

    @Getter @Setter
    private List<UploadFileInfo> files;

    protected BaseMqttRequest() {
    }

    @SuppressWarnings("rawtypes")
    protected abstract static class Builder<B extends Builder, R extends BaseMqttRequest> {
        protected int qos = 1;
        protected String productKey;
        protected String deviceKey;

        @SuppressWarnings("unchecked")
        public B setQos(int qos){
            this.qos = qos;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B setProductKey(String productKey) {
            this.productKey = productKey;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B setDeviceKey(String deviceKey) {
            this.deviceKey = deviceKey;
            return (B) this;
        }

        protected abstract String createMethod();

        protected abstract Object createParams();

        protected abstract R createRequestInstance();

        public R build() {
            R request = createRequestInstance();
            if (StringUtil.isNotEmpty(productKey)) {
                request.setProductKey(productKey);
            }
            if (StringUtil.isNotEmpty(deviceKey)) {
                request.setDeviceKey(deviceKey);
            }
            request.setMethod(createMethod());
            request.setParams(createParams());
            request.setQos(qos);
            return request;
        }
    }


    @Override
    public String getProductKey() {
        return productKey;
    }

    @Override
    public String getDeviceKey() {
        return deviceKey;
    }

    @Override
    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    @Override
    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    @Override
    public String getMessageId() {
        return getId();
    }

    @Override
    public void setMessageId(String msgId) {
        setId(msgId);
    }

    @Override
    public void check() throws EnosException {
        if (getMessageSize() >= MAX_MESSAGE_SIZE) {
            throw new EnosException("message too large");
        }

        //do basic check
        CheckUtil.checkProductKey(this.getProductKey());
        CheckUtil.checkDeviceKey(this.getDeviceKey());
    }

    @Override
    public int getQos() {
        return qos;
    }

    public void setQos(int qos){
        if(qos < 0 || qos >=2 ){
            throw new IllegalArgumentException("only qos 0 and 1 is support in current version");
        }
        this.qos = qos;
    }

    @Override
    public String getMessageTopic() {
        return String.format(_getPK_DK_FormatTopic(), getProductKey(), getDeviceKey());
    }


    @Override
    public String getAnswerTopic() {
        return getMessageTopic() + "_reply";
    }
    
    protected abstract String _getPK_DK_FormatTopic();

}
