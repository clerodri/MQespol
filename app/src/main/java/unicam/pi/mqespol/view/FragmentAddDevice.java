package unicam.pi.mqespol.view;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Toast;

import java.util.List;

import unicam.pi.mqespol.R;
import unicam.pi.mqespol.databinding.FragmentAddDeviceBinding;
import unicam.pi.mqespol.util.Util;
import unicam.pi.mqespol.util.WifiReciber;
import unicam.pi.mqespol.view.Adapters.SsidAdapter;
import unicam.pi.mqespol.viewModel.DeviceViewModel;


public class FragmentAddDevice extends Fragment {


    private SsidAdapter ssidAdapter;
    private FragmentAddDeviceBinding binding;
    private DeviceViewModel deviceViewModel;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();


        binding.btnScan.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       toast("Scanning Networks...");
                       deviceViewModel.getScanWifi();

                   }
               }
        );


        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Util.isValid(binding.lblNameDevice.getText().toString(), ssidAdapter.getItemSelected())){
                    toast("Please Type a Name and Select a Network");
                }else{
                    deviceViewModel.sendIP();
                    deviceViewModel.addDevice(ssidAdapter.getItemSelected(), binding.lblNameDevice.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_fragmentAddDeviceMenu_to_fragmentListDeviceMenu);
                }
            }
        });




        deviceViewModel.get().observe(getViewLifecycleOwner(), new Observer<List<ScanResult>>() {
            @Override
            public void onChanged(List<ScanResult> scanResults) {
                if(scanResults!=null) {
                    ssidAdapter.setSSID(scanResults);
                }
            }
        });

    }


    public void initRecyclerView() {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        binding.recyclerViewSsid.setLayoutManager(new LinearLayoutManager(getContext()));
       // binding.recyclerViewSsid.setHasFixedSize(false);
        ssidAdapter = new SsidAdapter(deviceViewModel.getListWifi());
        binding.recyclerViewSsid.setAdapter(ssidAdapter);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddDeviceBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    public void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
