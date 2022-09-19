package unicam.pi.mqespol.model.udp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import unicam.pi.mqespol.R;
import unicam.pi.mqespol.view.MainActivity;


public class UdpService extends Service {


    final String CHANNELID = "UPD_SERVICE";
    UdpServer udpServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int NOTIFICATION_ID = 2;
        startForeground(NOTIFICATION_ID, mostrarNotificacion());
        Toast.makeText(getApplicationContext(),"Service UDP started",Toast.LENGTH_SHORT).show();
       doTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e("UDP","Service UDP Create");
        super.onCreate();
    }
    void doTask(){
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.e("UDP", "Service UDP is running...");
                        try {
                            udpServer = new UdpServer(1883,MainActivity.udpHandler);
                            udpServer.start();
                            Log.d("UDP","SERVER NEW STATUS"+udpServer.running);
                            Thread.sleep(2000);
                        } catch (InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }


    private Notification mostrarNotificacion(){

        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                "Notificacion UDP-BROKER",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager=getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Service is Running")
                .setContentTitle("Server  UDP running...")
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher_round);
        return notification.build();
    }


    @Override
    public void onDestroy() {
        udpServer.setRunning(false);

        Log.e("UDP","Server UDP Stopped");
        super.onDestroy();
    }
}
