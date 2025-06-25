package com.enosiot.enos.iot_mqtt_sdk.core.exception;

import com.enosiot.enos.iot_mqtt_sdk.util.StringUtil;

/**
 * @author zhensheng.cai
 * @date 2018/7/3
 */
public class EnosException extends Exception {
    private static final long serialVersionUID = 5874811335473710877L;

    private static final int NO_AVAIL_ERROR_CODE = -99999;

    private final int errorCode;
    private final String errorMessage;

    public EnosException(String errorMessage) {
        this(NO_AVAIL_ERROR_CODE, errorMessage);
    }

    /**
     * Without known exception cause
     */
    public EnosException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * With known exception cause
     */
    public EnosException(String message, Throwable cause, int errorCode, String errorMessage) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public EnosException(Throwable cause, int errorCode, String errorMessage) {
        this(errorMessage, cause, errorCode, errorMessage);
    }

    public EnosException(Throwable cause, IEnosError error) {
        this(error.getErrorMessage(), cause, error);
    }

    public EnosException(String message, Throwable cause, IEnosError error) {
        this(message, cause, error.getErrorCode(), error.getErrorMessage());
    }

    public EnosException(IEnosError error) {
        this(error, null);
    }

    public EnosException(IEnosError error, String extraMsg) {
        this(error.getErrorCode(), combinedErrorMessage(error, extraMsg));
    }

    private static String combinedErrorMessage(IEnosError error, String extraMsg) {
        if (StringUtil.isNotEmpty(extraMsg)) {
            return error.getErrorMessage() + "(" + extraMsg + ")";
        }
        return error.getErrorMessage();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
