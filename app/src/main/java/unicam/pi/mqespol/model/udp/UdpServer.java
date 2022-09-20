package unicam.pi.mqespol.model.udp;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Handler;

public class UdpServer extends Thread {

    int serverPort;
    DatagramSocket socket;
    boolean running;
    UdpHandler handler;


    public UdpServer(int serverPort,UdpHandler handler){
        this.serverPort = serverPort;
        this.handler=handler;
    }

    public void setRunning(boolean running){
        this.running = running;
    }
    @Override
    public void run() {
        running=true;
        try {
            socket=new DatagramSocket(serverPort);
            while(running){
                Log.d("CICLO","UP UDP");
                byte[] buf=new byte[256];
                DatagramPacket packet= new DatagramPacket(buf,buf.length);
                socket.receive(packet);
                String datos=new String(packet.getData(),0,packet.getLength());
                handler.sendMessage(Message.obtain(handler,UdpHandler.UPDATE_MSG,datos));
                System.out.println("Server Running.");
            }
            Log.e("UDP","UDP Server ended");
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(socket!=null){
                socket.close();
                Log.e("UDP","socket is closed");
            }
        }
        super.run();
    }

    public Boolean getStatusUdp(){
        return running;
    }
}
