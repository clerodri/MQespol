package unicam.pi.mqespol.view;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;

import unicam.pi.mqespol.util.Util;

public class Client {
    MqttAndroidClient mqttAndroidClient;
    private  Client(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, Util.TCP + "broker.hivemq.com", Util.CLIENT_ID);
    }

    private static Client INSTANCE;
    public  static Client get(Context context){
        if(INSTANCE==null){
            INSTANCE = new Client(context);
        }
        return INSTANCE;
    }
    public MqttAndroidClient getClientMqtt(){
           return mqttAndroidClient;
    }
    public boolean getStateClient(){
        return mqttAndroidClient.isConnected();
    }
}