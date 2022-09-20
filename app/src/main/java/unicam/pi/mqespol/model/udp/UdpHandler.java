package unicam.pi.mqespol.model.udp;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.logging.LogRecord;

import unicam.pi.mqespol.view.MainActivity;

public class UdpHandler extends Handler {
    public static final int UPDATE_STATE=0;
    public static final int UPDATE_MSG=1;
    public static final int UPDATE_END=2;
    private final MainActivity parent;

    public UdpHandler(MainActivity parent){
        super();
        this.parent=parent;
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case UPDATE_STATE:
           //     parent.updateState((String)msg.obj);
            case UPDATE_MSG:
        //        parent.updateRx((String)msg.obj);
            case UPDATE_END:
          //      parent.cliendEnd();
            default:
                super.handleMessage(msg);
        }
    }
}
