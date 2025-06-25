package com.enosiot.enos.iot_mqtt_sdk.util;

import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;

import java.util.Collection;
import java.util.Map;

/**
 * Description: check request valid
 *
 * @author zhonghua.wu
 * @create 2018-07-12 11:59
 */
public class CheckUtil {

    // specific check productKey
    public static void checkProductKey(String productKey) throws EnosException {
        checkStringNotEmpty(productKey, "productKey");
    }

    // specific check deviceKey
    public static void checkDeviceKey(String deviceKey) throws EnosException {
        checkStringNotEmpty(deviceKey, "deviceKey");
    }

    // check string, collection or map is not empty
    public static void checkNotEmpty(Object value, String fieldName) throws EnosException {
        checkNotNull(value, fieldName);
        if (value instanceof String) {
            checkStringNotEmpty((String) value, fieldName);
        }
        if (value instanceof Collection<?>) {
            checkCollectionNotEmpty((Collection<?>) value, fieldName);
        }
        if (value instanceof Map<?, ?>) {
            checkMapNotEmpty((Map<?, ?>) value, fieldName);
        }
    }

    public static void checkNotNull(Object value, String fieldName) throws EnosException {
        if (value == null) {
            throw new EnosException(CheckError.CODE_ERROR_MISSING_ARGS.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is mandatory");
        }
    }

    // check string not empty
    public static void checkStringNotEmpty(String string, String fieldName) throws EnosException {
        if (StringUtil.isEmpty(string)) {
            throw new EnosException(CheckError.CODE_ERROR_MISSING_ARGS.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is mandatory");
        }
    }

    // check collection not empty
    public static void checkCollectionNotEmpty(Collection<?> collection, String fieldName) throws EnosException {
        if (collection == null || collection.isEmpty()) {
            throw new EnosException(CheckError.CODE_ERROR_MISSING_ARGS.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is mandatory");
        }
    }

    // check map not empty
    public static void checkMapNotEmpty(Map<?, ?> map, String fieldName) throws EnosException {
        if (map == null || map.isEmpty()) {
            throw new EnosException(CheckError.CODE_ERROR_MISSING_ARGS.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is mandatory");
        }
    }

    // check string max length
    public static void checkMaxLength(String string, int maxLength, String fieldName) throws EnosException {
        if (string.length() > maxLength) {
            throw new EnosException(CheckError.CODE_ERROR_ARG_INVALID.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is invalid, length cannot be large than " + maxLength);
        }
    }

    // check collection size max size
    public static void checkMaxSize(Collection<?> collection, int maxSize, String fieldName) throws EnosException {
        if (collection.size() > maxSize) {
            throw new EnosException(CheckError.CODE_ERROR_ARG_INVALID.getErrorCode(),
                    "sdk-client exception: " + fieldName + " is invalid, size cannot be large than " + maxSize);
        }
    }
}
