package com.mhstudio.gasfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OnConnectionFailedListener {

    private TextView mTVCurrentPlace;
    private Button mBtnCurrLocRefresh;
    private Button mBtnProceed;
    private ProgressBar mPbCurrentLoc;
    private ListView mGasListView;
    private Button mNotYourLocation;

    private GaslistAdapter leAdapter;

    private GoogleApiClient mGAPIClient;

    private double lat=0, lng=0;

    private List<GasModel.GasEntries> mNearbyList;
    private GasModel mGasModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        getSupportActionBar().setSubtitle(R.string.app_subtitle_home);

        mGasModel = new GasModel();

        mNearbyList = new ArrayList<>();

        mGAPIClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mTVCurrentPlace = (TextView) findViewById(R.id.tv_current_location);
        mBtnCurrLocRefresh = (Button) findViewById(R.id.btn_base_current_loc_refresh);
        mBtnProceed = (Button) findViewById(R.id.btn_base_proceed);
        mPbCurrentLoc = (ProgressBar) findViewById(R.id.pb_surrent_location);
        mNotYourLocation = (Button) findViewById(R.id.btn_not_yout_location);

        mBtnCurrLocRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guessCurrentPlace();
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

        mNotYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPlacePicker();
            }
        });

        mBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent leIntent = new Intent(BaseActivity.this, GaslistActivity.class);
//                startActivity(leIntent);

//                guessCurrentPlace();
//                displayPlacePicker();
//                guessCurrentFilteredPlace();
                findNearby();

            }
        });
        mBtnProceed.setVisibility(View.GONE);//FOR NOW

        mGasListView = (ListView) findViewById(R.id.lv_gaslist);
        leAdapter = new GaslistAdapter(this, mGasModel.getEntryList(), mGasModel);
        mGasListView.setAdapter(leAdapter);

        mGasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent leIntent = new Intent(BaseActivity.this, GasMapActivity.class);
//                leIntent.putExtra("DataListEntryPosition", position);
//                leIntent.putExtra("EntryObject", mGasModel.getEntryList().get(position));
//                leIntent.putExtra("DataModel", mGasModel);
//                startActivity(leIntent);

                String dLat = ""+mGasModel.getEntryList().get(position).getLat();
                String dLng = ""+mGasModel.getEntryList().get(position).getLng();
                String url = "https://maps.google.com/maps?saddr="+mGasModel.getLattitude()+","+mGasModel.getLongitude()+"&daddr="+dLat+","+dLng;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(mapIntent);
            }
        });

//        guessCurrentPlace();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGAPIClient != null){
            mGAPIClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(mGAPIClient != null && mGAPIClient.isConnected()){
            mGAPIClient.disconnect();
        }
        super.onStop();
    }

    //ConnectionCallbacks methods
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("CONNLOG", "Connection Susp");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("CONNLOG", "Connected");
        guessCurrentPlace();
//        Toast.makeText(this, "onConnected", Toast.LENGTH_LONG).show();
    }
    //ConnectionCallbacks methods ends


    //ConnectionFailedListener method
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("CONNLOG", "Connection Failed");
    }
    //ConnectionFailedListener method ends


    //------------------------------------------------Helper Methods
    private void guessCurrentPlace(){
        mPbCurrentLoc.setVisibility(View.VISIBLE);
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGAPIClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
//                Toast.makeText(BaseActivity.this, "onResult called "+likelyPlaces.getCount(), Toast.LENGTH_SHORT).show();
                try {
                    StringBuffer placeStr = new StringBuffer("");
                    if(likelyPlaces.getCount() > 0){
                        PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
                        if(placeLikelihood != null && placeLikelihood.getPlace() != null && ((int) (placeLikelihood.getLikelihood() * 100)) != 0){
                            placeStr.append(placeLikelihood.getPlace().getName()+"\n");
                            placeStr.append(placeLikelihood.getPlace().getAddress());

                            lat = placeLikelihood.getPlace().getLatLng().latitude;
                            lng = placeLikelihood.getPlace().getLatLng().longitude;

                            mGasModel.setLattitude(lat);
                            mGasModel.setLongitude(lng);
                        }
                    }
                    mPbCurrentLoc.setVisibility(View.GONE);
//                    for(PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        if (placeLikelihood != null && placeLikelihood.getPlace() != null) {
//                            placeStr.append("Most likely place: " +
//                                    placeLikelihood.getPlace().getName() +
//                                    " Percent chance: " + (int) (placeLikelihood.getLikelihood() * 100) + "\n");
//                        }
////                        Toast.makeText(BaseActivity.this, "onResult called "+placeStr, Toast.LENGTH_SHORT).show();
//                    }
                    mTVCurrentPlace.setText(placeStr.toString());

                    //first empty the existing data
                    mGasModel.resetEntryList();

                    findNearby();
                }catch (IllegalStateException e){
                    //something ...
                    mPbCurrentLoc.setVisibility(View.GONE);
                    likelyPlaces.release();
                }
                likelyPlaces.release();
            }
        });
    }

    private void findNearby(){
        String locType = "gas_station";
        String url = getUrl(lat, lng, locType);

        Log.e("THEURL", url);

        new RequestResponseTask().execute(new String[]{url});
    }

    private String getUrl(double lat, double lng, String type){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lng);
//        googlePlacesUrl.append("&radius=" + 8050);//about 5 miles
        googlePlacesUrl.append("&rankby=" + "distance");
        googlePlacesUrl.append("&type=" + type);
//        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCFb4v65zoKAKdntl7OemA3OXEOiM7M4Qg");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public class RequestResponseTask extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //Do something with the JSON string
            try {
                JSONObject jObject = new JSONObject(result);
                JSONArray jArray = jObject.getJSONArray("results");

                for(int i=0; i<jArray.length(); i++){
                    JSONObject jEachObject = jArray.getJSONObject(i);
                    String name = jEachObject.getString("name");
                    String vicinity = jEachObject.getString("vicinity");

                    JSONObject geometry = jEachObject.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    Double lati = Double.valueOf(location.getString("lat"));
                    Double longi = Double.valueOf(location.getString("lng"));

                    GasModel.GasEntries gasEntries = mGasModel.getNewEntry();
                    gasEntries.setName(name);
                    gasEntries.setVicinity(vicinity);
                    gasEntries.setLat(lati);
                    gasEntries.setLng(longi);
                    mGasModel.setEntry(gasEntries);

                    Log.i("JSONTEXT", lati + " " + longi + "\n");
                }

                for(int i=0; i<mGasModel.getEntryList().size(); i++) {
                    Log.i("JSONDATA", mGasModel.getEntryList().get(i).getName() + " " + mGasModel.getEntryList().get(i).getVicinity()+"\n");
                }

                leAdapter.notifyDataSetChanged();


            }catch (JSONException e){

            }

        }

    }

//    private void guessCurrentFilteredPlace(){
////        mPbCurrentLoc.setVisibility(View.VISIBLE);
//        List<String> filterList = new ArrayList<>();
//        filterList.add(Integer.toString(Place.TYPE_GAS_STATION));
//        PlaceFilter placeFilter = new PlaceFilter(false, filterList);
//        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGAPIClient, placeFilter);
//        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//            @Override
//            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
////                Toast.makeText(BaseActivity.this, "onResult called "+likelyPlaces.getCount(), Toast.LENGTH_SHORT).show();
//                try {
//                    StringBuffer placeStr = new StringBuffer("");
////                    if(likelyPlaces.getCount() > 0){
////                        PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
////                        if(placeLikelihood != null && placeLikelihood.getPlace() != null && ((int) (placeLikelihood.getLikelihood() * 100)) != 0){
////                            placeStr.append(placeLikelihood.getPlace().getName()+"\n");
////                            placeStr.append(placeLikelihood.getPlace().getAddress());
////                        }
////                    }
////                    mPbCurrentLoc.setVisibility(View.GONE);
//                    for(PlaceLikelihood placeLikelihood : likelyPlaces) {
//                        if (placeLikelihood != null && placeLikelihood.getPlace() != null) {
//                            placeStr.append("Most likely place: " +
//                                    placeLikelihood.getPlace().getName() +
//                                    " Percent chance: " + (int) (placeLikelihood.getLikelihood() * 100) + "\n");
//                        }
////                        Toast.makeText(BaseActivity.this, "onResult called "+placeStr, Toast.LENGTH_SHORT).show();
//                    }
//                    mTVCurrentPlace.setText(placeStr.toString());
//                }catch (IllegalStateException e){
//                    //something ...
//                    mPbCurrentLoc.setVisibility(View.GONE);
//                    likelyPlaces.release();
//                }
//                likelyPlaces.release();
//            }
//        });
//    }

    //ONLY TO CHECK, MAY NOT NEED THIS METHOD
    private void displayPlacePicker() {
        if( mGAPIClient == null || !mGAPIClient.isConnected() )
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult( builder.build( this ), 127 );
        } catch ( GooglePlayServicesRepairableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == 127 && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace( data, this ) );
        }
    }

    private void displayPlace( Place place ) {
        if( place == null )
            return;

        lat = place.getLatLng().latitude;
        lng = place.getLatLng().longitude;

        mGasModel.setLattitude(lat);
        mGasModel.setLongitude(lng);

        String content = "";
        if( !TextUtils.isEmpty( place.getName() ) ) {
            content += place.getName() + "\n";
        }
        if( !TextUtils.isEmpty( place.getAddress() ) ) {
            content += place.getAddress();
        }
//        if( !TextUtils.isEmpty( place.getPhoneNumber() ) ) {
//            content += "Phone: " + place.getPhoneNumber();
//        }

        mTVCurrentPlace.setText(content);

        Log.e("CONTENT", content);

        //first empty the existing data
        mGasModel.resetEntryList();

        findNearby();
    }
    //ONLY TO CHECK, MAY NOT NEED THIS METHOD ends
}
