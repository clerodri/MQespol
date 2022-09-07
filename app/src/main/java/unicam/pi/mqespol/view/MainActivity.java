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
    public static final String TAG = "TORO";
    public static WifiManager wifiManager;
    public static WifiReciber wifiReciever;
    private Intent serviceMQTT;
    // ProgressBar progressBar;
    Toolbar toolbar;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    NavHostFragment navHostFragment;
    SplashScreen splashScreen;
    DeviceViewModel deviceViewModel;
   public  static MQTTServerListener listener;
    Bundle mbundle ;
    @SuppressLint("StaticFieldLeak")
    public static MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //splash();
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
         wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        init_Resources();
        Log.d("IP","IP LOCALHOST:" +Util.getIpLocal());
        deviceViewModel.getStateService().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    stopService(serviceMQTT);
                }else{
                    startForegroundService(serviceMQTT);
                }
            }
        });


    }


    void init_Resources() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        listener = new MQTTServerListener(deviceViewModel);

        serviceMQTT  = new Intent(this, mqttService.class);

    }
    void splash(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splashScreen= SplashScreen.installSplashScreen(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
    }

}