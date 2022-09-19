package unicam.pi.mqespol.data;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Location {
    Double longitud;
    Double latitud;
    public Location(Double longitud,Double latitud){
        this.longitud=  longitud;
        this.latitud= latitud;
    }
}
