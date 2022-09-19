package unicam.pi.mqespol.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import unicam.pi.mqespol.data.mSensor;
import unicam.pi.mqespol.databinding.FragmentSensorBinding;
import unicam.pi.mqespol.view.Adapters.SensorAdapter;
import unicam.pi.mqespol.viewModel.DeviceViewModel;

public class FragmentSensor extends Fragment  {

    FragmentSensorBinding binding;
    List<mSensor> list;
    SensorAdapter sensorAdapter;
    DeviceViewModel deviceViewModel;
    TextView txt_x,txt_y,txt_z;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSensorBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        init();
//        binding.recyclerViewSensor.setAdapter(sensorAdapter);
//        binding.recyclerViewSensor.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        deviceViewModel.getListSensor().observe(getViewLifecycleOwner(), new Observer<List<mSensor>>() {
//            @Override
//            public void onChanged(List<mSensor> mSensors) {
//                sensorAdapter.submitList(mSensors);
//            }
//        });
    }


    void init(){
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        sensorAdapter=new SensorAdapter();
        list=new ArrayList<>();
       // list.add(new mSensor("ACCELEROMETER","","",""));
//        list.add(new mSensor("GYROSCOPIO","","",""));
//        list.add(new mSensor("MAGNOMETER","","",""));
       // sensorAdapter.submitList(list);
       // deviceViewModel.setSensorList(list);
    //    sensorAdapter.submitList(list);

    }




}