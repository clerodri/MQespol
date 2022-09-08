package unicam.pi.mqespol.model.mqtt;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.view.MainActivity;
import unicam.pi.mqespol.viewModel.DeviceViewModel;

public class MQTTServerListener extends AbstractInterceptHandler  {
    private final DeviceViewModel deviceViewModel;
    private final String TAG = "MQTTServerListener";
    public MQTTServerListener(DeviceViewModel deviceViewModel){
       this.deviceViewModel= deviceViewModel;
    }


    @Override
    public String getID() {
        return "MQTTServerListener";
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        Log.d("TAG","CLIENTE: "+msg.getClientID()+" conectado");
//        Log.d("TAG","PROTOCOLO: "+msg.getProtocolName()+" ");
//        Log.d("TAG","PROTOCOLO VERSION : "+ msg.getProtocolVersion()+" ");

        super.onConnect(msg);
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
        String msj= "El Cliente  "+msg.getClientID()+" se ha Desconectado";
        deviceViewModel.onDisconnectClient(msj);
        super.onDisconnect(msg);
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        Log.d("TAG","CONNECTION LOST ON :"+msg.getClientID());
        super.onConnectionLost(msg);
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        super.onMessageAcknowledged(msg);
        Log.d("TAG","on message acknwlegded :"+msg);
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        super.onPublish(msg);
        String name = msg.getClientID().toUpperCase(Locale.ROOT);
        String payload = msg.getPayload().toString(StandardCharsets.UTF_8);
        String topic = msg.getTopicName();
        Log.d("TAG","NAME CLIENT ID: "+name);
        Log.d("TAG","TOPIC : "+topic);
        Log.d("TAG","PAYLOAD: "+ payload);
        deviceViewModel.updateDeviceListener(name, topic,payload);
     //   deviceViewModel.publish(msg.getTopicName(),payload);
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
