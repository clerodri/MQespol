package unicam.pi.mqespol.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import unicam.pi.mqespol.model.Device;
import unicam.pi.mqespol.model.DeviceRepository;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.view.MainActivity;


public class DeviceViewModel extends AndroidViewModel {

    private final DeviceRepository repository;
    private final LiveData<List<Device>> allDevices;
    private List<ScanResult> listWifi;
    private  final MutableLiveData<List<ScanResult>> listMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isServiceOn = new MutableLiveData<>();
    private final MutableLiveData<String> isclientDisconnect = new MutableLiveData<>();
    public DeviceViewModel(@NonNull Application application) {

        super(application);
        Log.d("TAG","device create model");
        listWifi = new ArrayList<>();
        repository = new DeviceRepository(application);
        allDevices = repository.getAllDevices();
        listMutableLiveData.setValue(listWifi);
       // isServiceOn.setValue(false);
        isclientDisconnect.setValue("");

    }

    public void insert(Device device) {
        repository.insert(device);
    }

    public void update(Device device) {
        repository.update(device);
    }

    public void delete(Device device) {
        repository.delete(device);
    }

    public void deleteAllDevices() {
        repository.deleteAllDevices();
    }

    public List<ScanResult> getListWifi() {
        return listWifi;
    }
    public LiveData<String>  getIsClientDisconnect(){
        return isclientDisconnect;
    }

    public LiveData<List<ScanResult>> get() {
        return listMutableLiveData;
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }



    public void onDisconnectClient(String msg){
        isclientDisconnect.setValue(msg);
    }

    public Boolean addDevice(int position, String nameDevice) {
        String topic = listWifi.get(position).SSID;
      //   topic = Util.getFormated(topic);
        if (topic != null) {
            String name = nameDevice.toUpperCase(Locale.ROOT);
            Device newDevice = new Device(name, topic, "10");
            insert(newDevice);
            return true;
        } else {
            return false;
        }
    }
    public void updateDeviceListener(String clientId,String topic, String message) {
        Device newDevice=null;
        for (Device device : repository.getAllData()) {
            Log.d("U","NAME: "+device.getName()+" TOPIC "+device.getTopic());
            if (device.getTopic().equals(topic) && device.getName().equals(clientId)) {
                 newDevice = new Device(device.getName(), topic, message);
                newDevice.setId(device.getId());
                update(newDevice);
                publish(topic,message);
            }
        }
        if(newDevice==null){
            Log.d("TAG","Cliente no registrado");
        }else{
            Log.d("TAG","Cliente registrado");
        }
    }


    public void scanWifi(WifiManager wifiManager, BroadcastReceiver wifiReciever, FragmentActivity fragmentActivity) {
        fragmentActivity.registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (ActivityCompat.checkSelfPermission(getApplication().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragmentActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            wifiManager.startScan();
            listWifi = wifiManager.getScanResults();
            listMutableLiveData.setValue(listWifi);
        }
    }
    public void connecClient(Context context) {
        try {
            IMqttToken token = MainActivity.mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT","Conexion Exitosa");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("MQTT","Conexion Error");
                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic,String msj){
        Log.d("TAG","PUBLISH");
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = msj.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            MainActivity.mqttAndroidClient.publish(topic, message);
        } catch (MqttException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
