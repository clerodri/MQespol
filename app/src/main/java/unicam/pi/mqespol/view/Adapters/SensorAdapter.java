package unicam.pi.mqespol.view.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import unicam.pi.mqespol.R;
import unicam.pi.mqespol.data.mSensor;


public class SensorAdapter extends ListAdapter<mSensor, SensorAdapter.SensorHolder> {


    public SensorAdapter() {
        super(DIFF_CALLBACK);
    }
    private static final  DiffUtil.ItemCallback<mSensor> DIFF_CALLBACK= new DiffUtil.ItemCallback<mSensor>() {
        @Override
        public boolean areItemsTheSame(@NonNull mSensor oldItem, @NonNull mSensor newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull mSensor oldItem, @NonNull mSensor newItem) {
            return false;
        }
    };
    @NonNull
    @Override
    public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_row,parent,false);
        return new SensorHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
        mSensor curretSensor= getItem(position);
   //     holder.type.setText(curretSensor.getType());
//        holder.value_X.setText(curretSensor.getLabel_x());
//        holder.value_Y.setText(curretSensor.getLabel_y());
//        holder.value_Z.setText(curretSensor.getLabel_z());

    }
    static class SensorHolder extends RecyclerView.ViewHolder{
        private TextView value_X;
        private TextView value_Y;
        private TextView value_Z;
        private TextView type;
        public SensorHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.title);
            value_X = itemView.findViewById(R.id.value_X);
            value_Y = itemView.findViewById(R.id.value_Y);
            value_Z = itemView.findViewById(R.id.value_Z);
        }
    }
}
