package com.enosiot.enos.iot_mqtt_sdk.message.downstream;


import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.core.msg.IMqttReply;
import com.enosiot.enos.iot_mqtt_sdk.message.BaseAckMessage;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.ResponseCode;
import com.enosiot.enos.iot_mqtt_sdk.util.CheckUtil;
import com.enosiot.enos.iot_mqtt_sdk.util.StringUtil;

import java.util.List;

/**
 * Notice： Need to have a constructor that contains only topic parameters
 *
 * @author zhensheng.cai
 * @date 2018/7/10.
 */
public abstract class BaseMqttReply extends BaseAckMessage implements IMqttReply {
    private static final long serialVersionUID = -5315349562529412528L;
    private String productKey;
    private String deviceKey;
    @SuppressWarnings("unused")
    private int qos = 1;

    @SuppressWarnings("rawtypes")
    protected abstract static class Builder<B extends Builder, R extends BaseMqttReply> {
        private int qos = 1;
        private Integer code;
        private String message;
        private String productKey;
        private String deviceKey;


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
        @SuppressWarnings("unchecked")
        public B setQos(int qos) {
            this.qos = qos;
            return ((B) this);
        }

        @SuppressWarnings("unchecked")
        public B setCode(int code) {
            this.code = code;
            return ((B) this);
        }

        @SuppressWarnings("unchecked")
        public B setMessage(String message) {
            this.message = message;
            return ((B) this);
        }

        protected abstract Object createData();

        protected abstract R createRequestInstance();

        public R build() {
            R reply = createRequestInstance();
            reply.setCode(code != null ? code : ResponseCode.SUCCESS);
            if (StringUtil.isNotEmpty(message)) {
                reply.setMessage(message);
            }
            if(StringUtil.isNotEmpty(productKey)){
                reply.setProductKey(productKey);
            }
            if(StringUtil.isNotEmpty(deviceKey)){
                reply.setDeviceKey(deviceKey);
            }
            reply.setData(createData());
            reply.setQos(qos);
            return reply;
        }
    }

    protected BaseMqttReply() {
        this.setCode(ResponseCode.SUCCESS);
        this.setMessage("");
    }

    @Override
    public void check() throws EnosException {
        CheckUtil.checkProductKey(this.getProductKey());
        CheckUtil.checkDeviceKey(this.getDeviceKey());
    }

    public void setTopicArgs(List<String> args) {
        if (args.size() == 2) {
            this.setProductKey(args.get(0));
            this.setDeviceKey(args.get(1));
        } else {
            throw new UnsupportedOperationException("topic args size not match!");
        }
    }


    /*The default method, which needs to be overridden for scenarios with multiple topic parameters*/
    @Override
    public String getMessageTopic() {
        return String.format(_getPK_DK_FormatTopic(), getProductKey(), getDeviceKey());
    }

    @Override
    public void setMessageTopic(String topic) {
        throw new UnsupportedOperationException("answer message type can't set topic");
    }

    @Override
    public void setMessageId(String msgId) {
        setId(msgId);
    }

    @Override
    public String getMessageId() {
        return getId();
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

    public void setQos(int qos) {
        this.qos = qos;
    }

    @Override
    public int getQos() {
        return 0;
    }

    protected abstract String _getPK_DK_FormatTopic();


}
