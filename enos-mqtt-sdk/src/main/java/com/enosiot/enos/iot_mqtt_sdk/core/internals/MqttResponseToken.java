package com.enosiot.enos.iot_mqtt_sdk.core.internals;

import com.enosiot.enos.iot_mqtt_sdk.core.IResponseCallback;
import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosError;
import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.core.msg.IMqttResponse;

public class MqttResponseToken<T extends IMqttResponse> {
    private final Object responseLock = new Object();
    private final String responseId;
    private final IResponseCallback<T> callback;

    private volatile EnosException exception = null;
    private volatile T response = null;
    private volatile boolean callbackInvoked = false;

    public MqttResponseToken(String responseId) {
        this(responseId, null);
    }

    public MqttResponseToken(String responseId, IResponseCallback<T> callback) {
        this.responseId = responseId;
        this.callback = callback;
    }

    public String getResponseId() {
        return responseId;
    }

    public T waitForResponse(long timeoutMillis) throws EnosException {
        long start = System.currentTimeMillis();

        synchronized (responseLock) {
            while (!isCompleted()) {
                try {
                    if (timeoutMillis > 0) {
                        responseLock.wait(timeoutMillis);
                    } else {
                        responseLock.wait();
                    }
                } catch (InterruptedException e) {
                    exception = new EnosException(e, EnosError.THREAD_INTERRUPTED);
                }

                if (!isCompleted() && timeoutMillis > 0
                        && timeoutMillis + start <= System.currentTimeMillis()) {
                    // time up and still not completed
                    exception = new EnosException(EnosError.FUTURE_TASK_TIME_OUT);
                }
            }
        }

        invokeCallback(response, exception);

        // throw if hit any unexpected error during waiting
        if (this.exception != null) {
            throw exception;
        }

        return this.response;
    }

    public void markSuccess(T response) {
        if (response == null) {
            throw new IllegalArgumentException("null response not valid");
        }
        this.response = response;
        doNotify(response, null);
    }

    public void markFailure(EnosException exception) {
        if (exception == null) {
            throw new IllegalArgumentException("null exception not valid");
        }
        this.exception = exception;
        doNotify(null, exception);
    }

    /**
     * Notify the callback and potential waiter
     */
    private void doNotify(T response, EnosException exception) {
        invokeCallback(response, exception);
        synchronized (responseLock) {
            responseLock.notifyAll();
        }
    }

    private void invokeCallback(T response, EnosException exception) {
        if (!callbackInvoked && callback != null) {
            if (response != null) {
                callback.onResponse(response);
            } else {
                callback.onFailure(exception);
            }
            callbackInvoked = true;
        }
    }


    /**
     * @return true if response or exception is set
     */
    private boolean isCompleted() {
        return response != null || exception != null;
    }
}
