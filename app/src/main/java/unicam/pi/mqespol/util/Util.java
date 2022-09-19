package unicam.pi.mqespol.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.Locale;

public class Util {
    static boolean bandera = false;
    public static String TCP = "tcp://";
     public static final String CLIENT_ID = "mQespol";
    public static final int LOCATION_SERVICE_ID =175;
    public static final String ACTION_START_LOCATION_SERVICE="startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE="stopLocationService";
    public static final String ACTION_SEND_LOCATION_DATA="sendLocationData";



    public static String getFormated(String network) {
        String[] separated = network.split("_");
        if (separated.length < 2) {
            return null;
        }
        if (!separated[0].equals("app") && !separated[1].equals("net")) {
            return null;
        } else {
            String topic = separated[separated.length - 1];
            return topic.toUpperCase(Locale.ROOT);
        }
    }

    public static Boolean isValid(String text, int position) {
        bandera = position != -1 && !text.trim().isEmpty();
        return bandera;
    }

    public static String getIpLocal() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> listNetwork = NetworkInterface.getNetworkInterfaces();
                 listNetwork.hasMoreElements(); ) {
                NetworkInterface networkInterface = listNetwork.nextElement();
                for (Enumeration<InetAddress> enuInet = networkInterface.getInetAddresses();
                     enuInet.hasMoreElements(); ) {
                    InetAddress inetAddress = enuInet.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Turn On Internet";
    }
}

