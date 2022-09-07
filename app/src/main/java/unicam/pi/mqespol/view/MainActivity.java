package unicam.pi.mqespol.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import org.eclipse.paho.android.service.MqttAndroidClient;

import unicam.pi.mqespol.R;

import unicam.pi.mqespol.model.mqtt.MQTTServerListener;
import unicam.pi.mqespol.model.mqtt.mqttService;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.util.WifiReciber;
import unicam.pi.mqespol.viewModel.DeviceViewModel;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static WifiManager wifiManager;
    public static WifiReciber wifiReciever;
    private Intent serviceMQTT;
    Toolbar toolbar;
    Button btn_service;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    NavHostFragment navHostFragment;
    SplashScreen splashScreen;
    DeviceViewModel deviceViewModel;
    public static MQTTServerListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash();
        setContentView(R.layout.activity_main);




        init_Resources();
        Log.d("IP", "IP LOCALHOST:" + Util.getIpLocal());

        btn_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServerRunning()){
                    stopService(serviceMQTT);
                    btn_service.setText(R.string.start);
                }else{
                    startForegroundService(serviceMQTT);
                    btn_service.setText(R.string.stop);
                }
            }
        });


    }
    public void validateService(){
        if(isServerRunning()){
            btn_service.setText(R.string.stop);
        }else{
            btn_service.setText(R.string.start);
        }
    }


    void init_Resources() {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        btn_service = findViewById(R.id.btn_service);
        validateService();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        listener = new MQTTServerListener(deviceViewModel);
        serviceMQTT = new Intent(this, mqttService.class);
    }

    public void splash() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splashScreen = SplashScreen.installSplashScreen(this);
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }


//    @Override
//    public void onBackPressed() {
//    }

}