package unicam.pi.mqespol.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import unicam.pi.mqespol.data.mSensor;
import unicam.pi.mqespol.model.Device;
import unicam.pi.mqespol.model.DeviceRepository;
import unicam.pi.mqespol.util.Util;


public class DeviceViewModel extends AndroidViewModel {

    private final DeviceRepository repository;
    private final LiveData<List<Device>> allDevices;
    private List<ScanResult> listWifi;
    private final MutableLiveData<List<ScanResult>> listMutableLiveData = new MutableLiveData<>();
    private final Application app;

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        listWifi = new ArrayList<>();
        repository = new DeviceRepository(application);
        allDevices = repository.getAllDevices();
        listMutableLiveData.setValue(listWifi);
        this.app = application;
    }

    public void insert(Device device) {
        if (!isDeviceIn(device)) {
            repository.insert(device);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(app.getApplicationContext(), "Device Connected", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            updateDevice(device);
        }
    }
    public Application getapp(){
        return app;
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

    public LiveData<List<ScanResult>> get() {
        return listMutableLiveData;
    }

    public LiveData<List<Device>> getAllDevices() {
        return allDevices;
    }

    public void addDevice(int position, String nameDevice) {
        String topic = listWifi.get(position).SSID;
        topic = Util.getFormated(topic);
        if (topic == null) {
            Toast.makeText(app.getApplicationContext(), "Topic Device Wrong Format, Not Added", Toast.LENGTH_SHORT).show();
        } else {
            String name = nameDevice.toUpperCase(Locale.ROOT);
            Device newDevice = new Device(name, topic, "");
            insert(newDevice);
        }

    }

    public void updateDevice(Device d) {
        for (Device device : repository.getAllData()) {
            if (device.getTopic().equals(d.getTopic()) && device.getName().equals(d.getName())) {
                d.setId(device.getId());
                update(d);
            }
        }
    }

    public boolean isDeviceIn(Device device) {
        boolean bandera = false;
        if (repository.getAllData() != null) {
            for (Device d : repository.getAllData()) {
                if (d.getName().equals(device.getName()) && (d.getTopic().equals(device.getTopic()))) {
                    bandera = true;
                    break;
                }
            }
        } else {
            Log.d("MQTT", "LISTA DEVICA NULL");
        }
        return bandera;
    }

    public void getScanWifi() {
        if (isConnectedToInternet()) {
            Log.d("WIFI", "SCAN WIFI FUNCTION");
            WifiManager wifiManager = (WifiManager) app.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            BroadcastReceiver wifiScanRecieber = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("WIFI", "ON RECIEBE WIFI LISTENER");
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    if (success) {
                        listWifi.clear();
                        Log.d("WIFI", "WIFI SUCCESS");
                        listWifi = wifiManager.getScanResults();
                        if (listWifi != null) {
                            listMutableLiveData.setValue(listWifi);
                        }
                    } else {
                        Log.d("WIFI", "FALLO EL SCAN");
                    }
                    app.unregisterReceiver(this);
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            app.getApplicationContext().registerReceiver(wifiScanRecieber, intentFilter);
            wifiManager.startScan();

        } else {
            Log.d("WIFI", "WIFI OFF");
            listWifi.clear();
            listMutableLiveData.setValue(listWifi);
        }

    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }


    public void sendIP() {
        String ip = "192.168.224.29";
        byte[] buff=ip.getBytes();
        Handler myhandle = new Handler();
        myhandle.post(new Runnable() {
            @Override
            public void run() {
               StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
               StrictMode.setThreadPolicy(policy);
                try{
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket p= new DatagramPacket(buff,ip.length(), InetAddress.getByName("192.168.4.1"),1883);
                    socket.send(p);
                    Log.d("UDP", "package send it ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}


