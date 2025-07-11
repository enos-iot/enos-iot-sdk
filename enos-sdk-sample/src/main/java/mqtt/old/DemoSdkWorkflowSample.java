package mqtt.old;

import com.enosiot.enos.iot_mqtt_sdk.core.ConnCallback;
import com.enosiot.enos.iot_mqtt_sdk.core.MqttClient;
import com.enosiot.enos.iot_mqtt_sdk.core.exception.EnosException;
import com.enosiot.enos.iot_mqtt_sdk.core.internals.SignMethod;
import com.enosiot.enos.iot_mqtt_sdk.core.profile.DefaultProfile;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostRequest;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.MeasurepointPostResponse;
import mqtt.old.helper.Helper;

/**
 * This sample demo how to correctly use SDK APIs defined in MqttClient.
 *
 * @author jian.zhang4
 */
public class DemoSdkWorkflowSample {

    /**
     * We can publish whatever messages here, such as measurepoints,
     * attributes, events and raw data.
     *
     * @throws EnosException
     */
    private static boolean publishMessages(final MqttClient client) throws EnosException {
        MeasurepointPostRequest request = MeasurepointPostRequest.builder().addMeasurePoint("value", 100).build();

        MeasurepointPostResponse response = client.publish(request);
        if (!response.isSuccess()) {
            System.err.println("failed to publish measurepoint: " + response.getMessage());
        }
        return response.isSuccess();
    }

    private static void checkConnectNotAllowedAfter(final MqttClient client, String behavior) throws EnosException {
        try {
            client.connect();
            throw new RuntimeException("[BUG]: connect not allowed after " + behavior);
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            client.connect(new ConnCallback() {
                @Override
                public void connectComplete(boolean reconnect) {
                    throw new RuntimeException("[BUG]: connect(callback) not allowed after " + behavior);
                }

                @Override
                public void connectLost(Throwable cause) {

                }

                @Override
                public void connectFailed(Throwable cause) {

                }
            });
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private static void checkInvalidOpBeforeConnect(final MqttClient client) throws EnosException {
        try {
            publishMessages(client);
            throw new RuntimeException("[BUG]: publish messages not allowed before connect");
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            client.reconnect();
            throw new RuntimeException("[BUG]: reconnect not allowed before connect");
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            client.disconnect();
            throw new RuntimeException("[BUG]: disconnect not allowed before connect");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private static void checkInvalidOpAfterConnect(final MqttClient client) throws EnosException {
        checkConnectNotAllowedAfter(client, "connect");
    }

    private static void checkInvalidOpAfterDisconnect(final MqttClient client) throws EnosException {
        try {
            publishMessages(client);
            throw new RuntimeException("[BUG]: publish messages not allowed after disconnect");
        } catch (IllegalStateException e) {
            // expected
        }

        checkConnectNotAllowedAfter(client, "disconnect");
    }

    private static void checkInvalidOpAfterClose(final MqttClient client) throws EnosException {
        try {
            publishMessages(client);
            throw new RuntimeException("[BUG]: publish messages not allowed after close");
        } catch (IllegalStateException e) {
            // expected
        }

        checkConnectNotAllowedAfter(client, "close");

        try {
            client.reconnect();
            throw new RuntimeException("[BUG]: reconnect not allowed after close");
        } catch (IllegalStateException e) {
            // expected
        }

        try {
            client.disconnect();
            throw new RuntimeException("[BUG]: disconnect not allowed after close");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private static void demoNormalWorkflow() throws EnosException {
        System.out.println("-------------demoNormalWorkflow-------------");

        final MqttClient client = new MqttClient(new DefaultProfile(Helper.getNormalDeviceLoginInput()));
        client.getProfile().setSignMethod(SignMethod.SHA1);

        try {
            checkInvalidOpBeforeConnect(client);

            // Step 1: connect
            client.connect();
            System.out.println("[norm] connected: " + client.isConnected());

            checkInvalidOpAfterConnect(client);

            // Step 2: publish messages
            if (publishMessages(client)) {
                System.out.println("[norm] messages published");
            }

            // Step 3: disconnect
            client.disconnect();
            System.out.println("[norm] disconnected: " + !client.isConnected());

            checkInvalidOpAfterDisconnect(client);

            // Step 4: close
            client.close();
            System.out.println("[norm] closed");

            checkInvalidOpAfterClose(client);
        } finally {
            client.close();
        }
        System.out.println("-----------------------------------------\n");
    }

    /**
     * This method demos how to apply the workflow in more flexible way
     *
     * @throws EnosException
     */
    private static void demoMoreFlexibleWorkflow() throws EnosException {
        System.out.println("-------------demoMoreFlexibleWorkflow-------------");

        final MqttClient client = new MqttClient(new DefaultProfile(Helper.getNormalDeviceLoginInput()));
        client.getProfile().setSignMethod(SignMethod.SHA1);

        try {
            // Step 1: connect
            client.connect();
            System.out.println("[flex] connected: " + client.isConnected());

            // Step 2: publish messages
            if (publishMessages(client)) {
                System.out.println("[flex] messages published");
            }

            // Step 3: we can force reconnecting to broker if we don't want current connection
            client.reconnect();
            System.out.println("[flex] after connected, re-connected: " + client.isConnected());

            // Step 4: publish messages after re-connecting
            if (publishMessages(client)) {
                System.out.println("[flex] messages published after re-connecting");
            }

            // Step 5: disconnect
            client.disconnect();
            System.out.println("[flex] disconnected: " + !client.isConnected());

            // Step 6: re-connect after disconnect
            client.reconnect();
            System.out.println("[flex] after disconnected, re-connected: " + client.isConnected());

            // Step 7: publish messages after disconnecting and then re-connecting
            if (publishMessages(client)) {
                System.out.println("[flex] messages published after disconnecting and then re-connecting");
            }

            // Step 8: close (disconnect is not necessary)
            client.close();
            System.out.println("[flex] closed");
        } finally {
            client.close();
        }

        System.out.println("-----------------------------------------\n");
    }


    public static void main(String[] args) throws EnosException {
        demoNormalWorkflow();
        demoMoreFlexibleWorkflow();
    }

}
