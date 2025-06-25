package com.enosiot.enos.iot_mqtt_sdk.util;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.IEnosError;

import lombok.Value;

@Value
public class CheckError implements IEnosError
{
    public static final CheckError CODE_ERROR_MISSING_ARGS = new CheckError(-117, "CODE_ERROR_MISSING_ARGS");
    public static final CheckError CODE_ERROR_ARG_INVALID = new CheckError(-118, "CODE_ERROR_ARG_INVALID");

    private final int errorCode;
    private final String errorMessage;
}
