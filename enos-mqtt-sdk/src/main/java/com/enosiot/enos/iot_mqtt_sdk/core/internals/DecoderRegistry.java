package com.enosiot.enos.iot_mqtt_sdk.core.internals;

import com.enosiot.enos.iot_mqtt_sdk.core.msg.IMqttArrivedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhensheng.cai
 * @date 2018/7/10.
 */
public class DecoderRegistry {

    public static final String DECODER_PACKAGE = "com.enosiot.enos.iot_mqtt_sdk.message";

    private static final Logger logger = LoggerFactory.getLogger(DecoderRegistry.class);
    private static List<IMqttArrivedMessage> decoderList = new ArrayList<>();
    private static List<IMqttArrivedMessage> unmodefiDecoderList = Collections.unmodifiableList(decoderList);

    static {
        // For uplink response can be dynamically loaded
        // Just need to statically load the encoding method of various Commands
        try {
            String[] classNames = new String[]{
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.tsl.MeasurepointSetCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.tsl.ModelDownRawCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.tsl.MeasurepointGetCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.tsl.ServiceInvocationCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.device.SubDeviceDisableCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.device.SubDeviceEnableCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.device.SubDeviceDeleteCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.activate.DeviceActivateInfoCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.ota.OtaUpgradeCommand",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.topo.TopoAddResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.topo.TopoDeleteResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.topo.TopoGetResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.AttributeDeleteResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.AttributeUpdateResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.AttributeQueryResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.EventPostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostBatchResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.TslTemplateGetResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.ModelUpRawResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.integration.IntEventPostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.integration.IntAttributePostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.integration.IntMeasurepointPostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.integration.IntModelUpRawResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.resume.MeasurepointResumeBatchResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.resume.MeasurepointResumeResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.status.SubDeviceLoginBatchResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.status.SubDeviceLogoutResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.status.SubDeviceLoginResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.register.DeviceRegisterResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.ota.OtaProgressReportResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.ota.OtaGetVersionResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.ota.OtaVersionReportResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tag.TagDeleteResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tag.TagUpdateResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.tag.TagQueryResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.log.LogPostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.network.NetworkStatusReportResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.upstream.connection.ConnectionStatePostResponse",
                    "com.enosiot.enos.iot_mqtt_sdk.message.downstream.traffic.TrafficControlCommand",
            };
            Arrays.stream(classNames).forEach(n -> {
                IMqttArrivedMessage decoder;
                try {
                    decoder = (IMqttArrivedMessage) Class.forName(n).newInstance();
                    decoderList.add(decoder);
                } catch (Exception e) {
                    logger.error("register downstream command decoder failed ", e);
                }
            });
        } catch (Throwable e) {
            logger.error("register downstream command decoder failed ", e);
        }
    }

    public static List<IMqttArrivedMessage> getDecoderList() {
        return unmodefiDecoderList;
    }
}
