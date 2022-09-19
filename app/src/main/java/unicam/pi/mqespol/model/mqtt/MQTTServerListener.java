package unicam.pi.mqespol.model.mqtt;


import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.netty.buffer.ByteBuf;
import unicam.pi.mqespol.model.Device;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.view.Client;
import unicam.pi.mqespol.view.MainActivity;
import unicam.pi.mqespol.viewModel.DeviceViewModel;

public class MQTTServerListener extends AbstractInterceptHandler {
    private final DeviceViewModel deviceViewModel;
    private final String TAG = "MQTTServerListener";

    public MQTTServerListener( AppCompatActivity activity) {
        deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
    }


    @Override
    public String getID() {
        return "MQTTServerListener";
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        Log.d("TAG", "CLIENTE: " + msg.getClientID() + " conectado");
        super.onConnect(msg);
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String msj = "El Cliente  " + msg.getClientID() + " se ha Desconectado";
        //  deviceViewModel.onDisconnectClient(msj);
        super.onDisconnect(msg);
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        Log.d("TAG", "CONNECTION LOST ON :" + msg.getClientID());
        super.onConnectionLost(msg);
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        super.onMessageAcknowledged(msg);
        Log.d("TAG", "on message acknwlegded :" + msg);
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        super.onPublish(msg);
        String name = msg.getClientID().toUpperCase(Locale.ROOT);
        String payload = msg.getPayload().toString(StandardCharsets.UTF_8);
        String topic = Util.getFormated(msg.getTopicName());
        byte[] mydata = payload.getBytes(StandardCharsets.UTF_8);
        MqttMessage msf = new MqttMessage(mydata);
        Log.d("TAG", "CLIENT: " + name + " TOPIC : " + topic + " PAYLOAD: " + msf);
        Device device = new Device(name, topic, payload);
        deviceViewModel.insert(device);

        try {
            if (Client.get(deviceViewModel.getapp().getApplicationContext()).getStateClient()) {
                Client.get(deviceViewModel.getapp().getApplicationContext()).getClientMqtt().publish(topic, msf);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
        super.onSubscribe(msg);
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
        super.onUnsubscribe(msg);
    }


}
