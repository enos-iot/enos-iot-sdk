package com.enosiot.enos.iot_mqtt_sdk.core.exception;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.IEnosError;

import lombok.Value;

/**
 * Errors while handling mqtt connection and messages.
 * 
 * Redesigned, to implement an interface instead of enum.
 * 
 * @author shenjieyuan
 */
@Value
public class EnosError implements IEnosError
{
    public static final EnosError SUCCESS = new EnosError(0, "success");

    public static final EnosError INIT_MQTT_CLIENT_FAILED = new EnosError(-100, "INIT_MQTT_CLIENT_FAILED");
    public static final EnosError MQTT_CLIENT_CONNECT_FAILED = new EnosError(-101,
            "MQTT_CLIENT_CONNECT_FAILED");
    public static final EnosError MQTT_CLIENT_PUBLISH_FAILED = new EnosError(-102,
            "MQTT_CLIENT_PUBLISH_FAILED");
    public static final EnosError MQTT_CLIENT_DISCONNECT_FAILED = new EnosError(-103,
            "MQTT_CLIENT_DISCONNECT_FAILED");
    public static final EnosError MQTT_CLIENT_SUBSCRIEBE_FAILED = new EnosError(-104,
            "MQTT_CLIENT_SUBSCRIEBE_FAILED");
    public static final EnosError MQTT_CLIENT_CLOSE_FAILED = new EnosError(-105, "MQTT_CLIENT_CLOSE_FAILED");
    public static final EnosError INVALID_DEVICE_CREDENTIAL = new EnosError(-106, "INVALID_DEVICE_CREDENTIAL");
    public static final EnosError INVALID_REPLY_MESSAGE_FORMAT = new EnosError(-107,
            "INVALID_REPLY_MESSAGE_FORMAT");
    public static final EnosError INVALID_PAYLOAD = new EnosError(-108, "INVALID_PAYLOAD");
    public static final EnosError EMPTY_PAYLOAD = new EnosError(-109, "EMPTY_PAYLOAD");
    public static final EnosError GET_LOCAL_MODEL_FAILED = new EnosError(-110, "GET_LOCAL_MODEL_FAILED");
    public static final EnosError MODEL_VALIDATION_FAILED = new EnosError(-111, "MODEL_VALIDATION_FAILED");
    public static final EnosError RESPONSE_PARSE_ERR = new EnosError(-112, "RESPONSE_PARSE_ERR");
    public static final EnosError MQTT_RESPONSE_PARSED_FALED = new EnosError(-113,
            "MQTT_RESPONSE_PARSED_FALED");
    public static final EnosError UNSUPPPORTED_REQUEST_CALL_TYPE = new EnosError(-114,
            "UNSUPPPORTED_REQUEST_CALL_TYPE");
    public static final EnosError SESSION_IS_NULL = new EnosError(-115, "SESSION_IS_NULL");
    public static final EnosError STATUS_IS_UNKNOWN = new EnosError(-116, "STATUS_IS_UNKNOWN");
    public static final EnosError CODE_ERROR_MISSING_ARGS = new EnosError(-117, "CODE_ERROR_MISSING_ARGS");
    public static final EnosError CODE_ERROR_ARG_INVALID = new EnosError(-118, "CODE_ERROR_ARG_INVALID");
    public static final EnosError CANNOT_REGISTER_CALLBACK = new EnosError(-119, "CANNOT_REGISTER_CALLBACK");
    public static final EnosError DEVICE_SESSION_IS_NULL = new EnosError(-120, "SESSION IS NULL");
    public static final EnosError CALLBACK_EXECUTION_FAILED = new EnosError(-121, "callback execution failed");
    public static final EnosError STATUS_ERROR = new EnosError(-122, "invalid operation in current status");
    public static final EnosError STATUS_NOT_ALLOW_LOGIN = new EnosError(-123, "status not allow login");
    public static final EnosError STATUS_NOT_ALLOW_LOGOUT = new EnosError(-124, "status not allow logout");
    public static final EnosError FUTURE_TASK_TIME_OUT = new EnosError(-125, "sync request timeout");
    public static final EnosError THREAD_INTERRUPTED = new EnosError(-126, "thread interrupted");
    public static final EnosError QOS_2_NOT_ALLOWED = new EnosError(-127, "qos 2 not allowed");
    public static final EnosError COMPRESS_FAILED = new EnosError(-128, "compress failed");
    public static final EnosError DECOMPRESS_FAILED = new EnosError(-129, "decompress failed");

    private final int errorCode;
    private final String errorMessage;
}
