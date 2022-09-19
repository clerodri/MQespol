package unicam.pi.mqespol.model.Location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import unicam.pi.mqespol.R;
import unicam.pi.mqespol.util.Util;

public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("NOT YET");
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            super.onLocationResult(result);
            if (result != null && result.getLastLocation() != null) {
                double latitud = result.getLastLocation().getLatitude();
                double longitud = result.getLastLocation().getLongitude();
                Log.d("LOCATION UPDATE ", latitud + " , " + longitud);
                sendDataLocation(latitud,longitud);
            }
        }
    };

    private void sendDataLocation(double latitud,double longitud){
        Intent intent = new Intent();
        intent.setAction(Util.ACTION_SEND_LOCATION_DATA);
        intent.putExtra("X",latitud);
        intent.putExtra("Y",longitud);
        sendBroadcast(intent);
    }

    private void startLocationService() {
        String channelID = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notification = new Notification.Builder(this, channelID)
                .setContentText("Running")
                .setContentTitle("Location Service")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setTicker("MQTT")
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher_round);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used for location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Util.LOCATION_SERVICE_ID,notification.build());

    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent !=null){
            String action = intent.getAction();
            if(action!=null){
                if(action.equals(Util.ACTION_START_LOCATION_SERVICE)){
                    startLocationService();
                }else if(action.equals(Util.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);


    }
}
