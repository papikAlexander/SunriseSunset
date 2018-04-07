package com.alex.sunrisesunset;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.sunrisesunset.model.Model;
import com.alex.sunrisesunset.repositories.Repository;
import com.alex.sunrisesunset.utils.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FloatingActionButton floatingActionButton;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvCity;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabLocation);
        tvCity = (TextView) findViewById(R.id.tvCity);
        tvSunrise = (TextView) findViewById(R.id.tvSunrise);
        tvSunset = (TextView) findViewById(R.id.tvSunset);

        repository = new Repository();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        currentLocation(mGoogleApiClient);

        floatingActionButton.setOnClickListener(v -> currentLocation(mGoogleApiClient));


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment
                .setFilter(new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build());

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                tvCity.setText(String.format("City: %s UTC time", place.getName()));
                List<String> list = StringUtils.coordinate(place.getLatLng().toString());

                setData(repository.getData(list.get(0), list.get(1)), tvSunset, tvSunrise);
            }

            @Override
            public void onError(Status status) {
                Log.i("test", "An error occurred: " + status);
            }
        });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("test", "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    callPlaceDetectionApi();
                }
                break;
        }
    }

    private void currentLocation(GoogleApiClient googleApiClient){
        if (googleApiClient.isConnected()){
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            } else {
                callPlaceDetectionApi();
            }
        }
    }

    private void callPlaceDetectionApi() throws SecurityException {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        result.setResultCallback((PlaceLikelihoodBuffer likelyPlaces) -> {

            float maxLikelihood = 0;
            PlaceLikelihood maxPlaceLikelihood = likelyPlaces.get(0);
            for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                if (maxLikelihood < placeLikelihood.getLikelihood()){
                    maxLikelihood = placeLikelihood.getLikelihood();
                    maxPlaceLikelihood = placeLikelihood;
                }
            }
            List<String> list = StringUtils.coordinate(maxPlaceLikelihood.getPlace().getLatLng().toString());

            setData(repository.getData(list.get(0), list.get(1)), tvSunset, tvSunrise);
            tvCity.setText(R.string.current_location);

            likelyPlaces.release();
        });
    }

    private void setData(Call<Model> modelCall, TextView textViewSunset, TextView textViewSunrise){
        modelCall.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                textViewSunset.setText(response.body().getResults().getSunset());
                textViewSunrise.setText(response.body().getResults().getSunrise());
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {

            }
        });
    }

}
