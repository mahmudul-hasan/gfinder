package com.mhstudio.gasfinder.gasmappkg;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mhstudio.gasfinder.GasModel;
import com.mhstudio.gasfinder.R;

public class GasMapActivity extends AppCompatActivity {

    private int mPosition = -1;
    private GasModel.GasEntries mEntryData;
    private GasModel mModel;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_map);

        mPosition = getIntent().getIntExtra("DataListEntryPosition", -1);
        mEntryData = (GasModel.GasEntries) getIntent().getSerializableExtra("EntryObject");
        mModel = (GasModel) getIntent().getSerializableExtra("DataModel");

        Log.i("ATMAPACT", ""+mEntryData.getName()+ " "+ mEntryData.getVicinity());

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//
//                LatLng srcLatlng = new LatLng(mModel.getLattitude(), mModel.getLongitude());
//                LatLng destLatlng = new LatLng(mEntryData.getLat(), mEntryData.getLng());
//                mMap.addMarker(new MarkerOptions().position(srcLatlng));
//                mMap.addMarker(new MarkerOptions().position(destLatlng).title(mEntryData.getName()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(srcLatlng));
//                mMap.setMinZoomPreference(15);
//            }
//        });

        String url = "https://maps.google.com/maps?saddr="+mModel.getLattitude()+","+mModel.getLongitude()+"&daddr="+mEntryData.getLat()+","+mEntryData.getLng();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(mapIntent);
    }

    @Override
    protected void onDestroy() {
        Log.i("MAPACTDESTROY", "onDestrou called");
        super.onDestroy();
    }
}
