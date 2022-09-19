package unicam.pi.mqespol.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import unicam.pi.mqespol.R;

import unicam.pi.mqespol.data.Location;
import unicam.pi.mqespol.data.mSensor;
import unicam.pi.mqespol.databinding.ActivityMainBinding;
import unicam.pi.mqespol.model.Location.LocationService;
import unicam.pi.mqespol.model.mqtt.MQTTServerListener;
import unicam.pi.mqespol.model.mqtt.mqttService;
import unicam.pi.mqespol.model.udp.UdpHandler;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.util.WifiReciber;
import unicam.pi.mqespol.viewModel.DeviceViewModel;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String TAG = "MainActivity";

    private Intent serviceMQTT;
    NavController navController;
    NavHostFragment navHostFragment;
    SplashScreen splashScreen;
    DeviceViewModel deviceViewModel;
    public static MQTTServerListener listener;
    public static UdpHandler udpHandler;
    ActivityMainBinding binding;
    SensorManager sensorManager;
    Sensor accleroSensor;
    MqttAndroidClient mqttAndroidClient;
    LocationReciever locationReciever;

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_Resources();
        initSensors();
        checkPermission();


        binding.btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServerRunning()) {
                    stopService(serviceMQTT);
                    binding.btnService.setText(R.string.start);
                } else {
                    Log.d("UDP", "SERVER FALSE");
                    startForegroundService(serviceMQTT);
                    binding.btnService.setText(R.string.stop);
                }
            }
        });
        binding.btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connecClient();
            }
        });

    }


    void init_Resources() {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        validateService();
        setSupportActionBar(binding.toolbar);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        listener = new MQTTServerListener(this);
        serviceMQTT = new Intent(this, mqttService.class);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mqttAndroidClient = Client.get(getApplicationContext()).getClientMqtt();
        locationReciever = new LocationReciever();
        registerReceiver(locationReciever, new IntentFilter(Util.ACTION_SEND_LOCATION_DATA));
    }


    public void connecClient() {
        try {
            IMqttToken token = mqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Conexion Exitosa");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("MQTT", "Conexion Error");
                    Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void validateService() {
        if (isServerRunning()) {
            binding.btnService.setText(R.string.stop);
        } else {
            binding.btnService.setText(R.string.start);
        }
    }

    public void splash() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splashScreen = SplashScreen.installSplashScreen(this);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragmentAddDeviceMenu:
                NavigationUI.onNavDestinationSelected(item, navController);
            case R.id.fragmentListDeviceMenu:

            case R.id.fragmentInformation:
                NavigationUI.onNavDestinationSelected(item, navController);

            case R.id.fragmentSensor:
                NavigationUI.onNavDestinationSelected(item, navController);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigateUp() {
        return navController.navigateUp() || super.onNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public Boolean isServerRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (mqttService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public Boolean isLocationRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

    void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            startLocationService();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
            toast("Permisse Granted");
        } else {
            toast("Permisse Denied");
        }

    }

    private void startLocationService() {
        if (!isLocationRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Util.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            toast("Location Service Started");
        }
    }

    private void stopLocationService() {
        if (isLocationRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Util.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            toast("Location Service Stopped");
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            binding.val.setText(String.valueOf(event.values[0]));
            mSensor mysensor = new mSensor("Accelerometro", event.values[0], event.values[1], event.values[2]);
            Gson gson = new Gson();
            String json = gson.toJson(mysensor);
            MqttMessage msg3 = new MqttMessage(json.getBytes(StandardCharsets.UTF_8));
            try {
                if (Client.get(getApplicationContext()).getStateClient()) {
                    Client.get(getApplicationContext()).getClientMqtt().publish("app_net_acelerometro", msg3);
                    Log.d("SENSOR", "MESAGE PUBLICATED");

                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            Log.d("SENSOR", "" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void initSensors() {
        if (sensorManager != null) {
            accleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    void registerSensor() {
        if (accleroSensor != null) {
            sensorManager.registerListener((SensorEventListener) this, accleroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    void unregisterSensor() {
        sensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerSensor();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReciever);
        stopLocationService();
    }

//    public Double convertData(Double number) {
//        String p=String.valueOf(number);
//        String[] part= p.split("\\.");
//        String decimals=part[1];
//        StringBuilder u= new StringBuilder();
//        int i =0;
//        while(i<=5){
//            u.append(decimals.charAt(i));
//            i++;
//        }
//        String result = part[0]+"."+u;
//        return Double.valueOf(result);
//    }


    static class LocationReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Util.ACTION_SEND_LOCATION_DATA)) {
                Double d1 = intent.getDoubleExtra("X", 0);
                Double d2 = intent.getDoubleExtra("Y", 0);
                //  MqttMessage msg1 = new MqttMessage(convertData(d1));
                // MqttMessage msg2 = new MqttMessage(convertData(d2));
                Location myLocation = new Location(d1, d2);
                Gson gson = new Gson();
                String json = gson.toJson(myLocation);
                MqttMessage msg3 = new MqttMessage(json.getBytes(StandardCharsets.UTF_8));
                //     Log.d("LOCATION UPDATE ", msg1 + " , " + msg2);
                try {
                    if (Client.get(context.getApplicationContext()).getStateClient()) {
                        Client.get(context.getApplicationContext()).getClientMqtt().publish("app_net_location", msg3);
                        //  Client.get(getApplicationContext()).getClientMqtt().publish("app_longitud", msg2);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}