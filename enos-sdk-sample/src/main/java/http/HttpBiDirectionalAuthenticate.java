package http;

import com.enosiot.enos.iot_http_sdk.HttpConnection;
import com.enosiot.enos.iot_http_sdk.SessionConfiguration;
import com.enosiot.enos.iot_http_sdk.StaticDeviceCredential;
import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostRequest;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostResponse;
import com.google.gson.GsonBuilder;

import java.io.IOException;

/**
 * @author :charlescai
 * @date :2021-03-09
 */
public class HttpBiDirectionalAuthenticate {
    // EnOS HTTP Broker URL, which can be obtained from Environment Information page in EnOS Console
    // ssl port 8443
    static final String BROKER_URL = "https://broker_url:8443";

    // Device credentials, which can be obtained from Device Details page in EnOS Console
    static final String PRODUCT_KEY = "productKey";
    static final String DEVICE_KEY = "deviceKey";
    static final String DEVICE_SECRET = "deviceSecret";

    private static String jksPath = "jskPath";
    private static String jksPassword = "jskPassword";

    /** Ecc cert flag
     * if use ECC certificate, chose true
     * if use RSA certificate, chose false */
    static final boolean IS_ECC_CONNECT = false;

    public static void main(String[] args) throws EnosException {
        // construct a static device credential via ProductKey, DeviceKey and DeviceSecret
        StaticDeviceCredential credential = new StaticDeviceCredential(
                PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET);

        // construct a http connection
        SessionConfiguration configuration = SessionConfiguration
                .builder()
                .lifetime(30_000)
                .sslSecured(true)
                .isEccConnect(IS_ECC_CONNECT)
                .jksPath(jksPath)
                .jksPassword(jksPassword)
                .build();

        HttpConnection connection = new HttpConnection.Builder(BROKER_URL, credential)
                .sessionConfiguration(configuration)
                .build();

        MeasurepointPostRequest request = buildMeasurepointPostRequest();

        try
        {
            MeasurepointPostResponse response = connection.publish(request, null);
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(response));
        } catch (EnosException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static MeasurepointPostRequest buildMeasurepointPostRequest()
    {
        // Measurepoints are defined in ThingModel
        return MeasurepointPostRequest.builder()
                .addMeasurePoint("Int_value", 100)
                .addMeasurePoint("DI_value_01", 5)
                .build();
    }
}

