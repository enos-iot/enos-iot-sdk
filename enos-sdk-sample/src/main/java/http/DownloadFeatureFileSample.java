package http;

import com.enosiot.enos.iot_http_sdk.HttpConnection;
import com.enosiot.enos.iot_http_sdk.SessionConfiguration;
import com.enosiot.enos.iot_http_sdk.StaticDeviceCredential;
import com.enosiot.enos.iot_http_sdk.file.FileCategory;
import com.enosiot.enos.iot_http_sdk.file.IFileCallback;
import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author :charlescai
 * @date :2020-04-21
 */
public class DownloadFeatureFileSample {
    // EnOS HTTP Broker URL, which can be obtained from Environment Information page in EnOS Console
    static final String BROKER_URL = "https://broker_url";

    // Device credentials, which can be obtained from Device Details page in EnOS Console
    static final String PRODUCT_KEY = "productKey";
    static final String DEVICE_KEY = "deviceKey";
    static final String DEVICE_SECRET = "deviceSecret";

    public static void main(String[] args) throws EnosException {
        // construct a static device credential via ProductKey, DeviceKey and DeviceSecret
        StaticDeviceCredential credential = new StaticDeviceCredential(
                PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET);

        SessionConfiguration configuration = SessionConfiguration.builder()
                .lifetime(30_000)
                .build();

        // construct a http connection
        HttpConnection connection = new HttpConnection.Builder(BROKER_URL, credential)
                .sessionConfiguration(configuration)
                .build();

        // fileUri is an enos scheme file uri
        String fileUri = "enos-connect://xxx.txt";
        int bufferLength = 1024;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            InputStream inputStream = connection.downloadFile(fileUri, FileCategory.FEATURE);
            byte[] buffer = new byte[bufferLength];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            System.out.println(outputStream);
        } catch (EnosException | IOException e) {
            e.printStackTrace();
        }

        // Asynchronously call the file download request
        try {
            connection.downloadFileAsync(fileUri, FileCategory.FEATURE, new IFileCallback() {
                        @Override
                        public void onResponse(InputStream inputStream) throws IOException {
                            System.out.println("download feature file asynchronously");
                            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                                byte[] buffer = new byte[bufferLength];
                                int len;
                                while ((len = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, len);
                                }
                                System.out.println(outputStream);
                            }
                        }

                        @Override
                        public void onFailure(Exception failure) {
                            failure.printStackTrace();
                        }
                    }
            );
        } catch (EnosException e) {
            e.printStackTrace();
        }
    }
}
