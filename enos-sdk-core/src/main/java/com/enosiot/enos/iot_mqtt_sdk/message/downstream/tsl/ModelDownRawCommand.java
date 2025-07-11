package com.enosiot.enos.iot_mqtt_sdk.message.downstream.tsl;

import com.enosiot.enos.iot_mqtt_sdk.core.internals.constants.ArrivedTopicPattern;
import com.enosiot.enos.iot_mqtt_sdk.message.downstream.BaseMqttCommand;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhensheng.cai
 * @date 2018/7/12.
 */
public class ModelDownRawCommand extends BaseMqttCommand<ModelDownRawReply>
{
	private static final long serialVersionUID = -2665555261792974946L;
	private static Pattern pattern = Pattern.compile(ArrivedTopicPattern.MODEL_DOWN_RAW_COMMAND);

	private byte[] payload;

	public ModelDownRawCommand()
	{}

	public ModelDownRawCommand(byte[] payload)
    {
        this.payload = payload;
    }

	public void setPayload(byte[] payload)
	{
		this.payload = payload;
	}

	public byte[] getPayload()
	{
		return payload;
	}

	@Override
	public DecodeResult decode(String topic, byte[] payload)
    {
		List<String> topicArg = this.match(topic);
		if (topicArg == null) {
			return null;
		}
		ModelDownRawCommand mdc = new ModelDownRawCommand();
	    mdc.payload = payload;
		return new DecodeResult(mdc, topicArg);
	}

    @Override
    public Class<ModelDownRawReply> getAnswerType()
    {
        return ModelDownRawReply.class;
    }

    @Override
    public Pattern getMatchTopicPattern()
    {
        return pattern;
    }

}
