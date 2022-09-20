package unicam.pi.mqespol.data;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class mSensor {
    private String type;
    private float label_x;
    private float label_y;
    private float label_z;

    public mSensor(String type, float label_x, float label_y, float label_z) {
        this.type = type;
        this.label_x = label_x;
        this.label_y = label_y;
        this.label_z = label_z;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getLabel_x() {
        return label_x;
    }

    public void setLabel_x(float label_x) {
        this.label_x = label_x;
    }

    public float getLabel_y() {
        return label_y;
    }

    public void setLabel_y(float label_y) {
        this.label_y = label_y;
    }

    public float getLabel_z() {
        return label_z;
    }

    public void setLabel_z(float label_z) {
        this.label_z = label_z;
    }
}
