package com.example.weatherapp;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView txtCityName, txtTempDisc, txtTemperature;
    private EditText edtCityName;
    private ImageView imgSearch, imgTempDisc, imgBackground;
    private RecyclerView recyclerViewWeather;

    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;

    private LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // make full screen of our app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        init();

        // initialize arrayList and adapter
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(MainActivity.this, weatherRVModelArrayList);
        recyclerViewWeather.setAdapter(weatherRVAdapter);

        getWeatherInfo("India");

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = edtCityName.getText().toString();
                if (city.isEmpty()) {
                    edtCityName.setError("city name can't be empty!");
                } else {
                    txtCityName.setText(city);
                    loadingPB.setVisibility(View.VISIBLE);
                    homeRL.setVisibility(View.GONE);
                    /*setProgressBar(true, false);*/
                    getWeatherInfo(city);
                }
            }
        });


    }

    private void getWeatherInfo(String cityName) {

        Log.e("City Name", cityName);
        String url = "https://api.weatherapi.com/v1/forecast.json?key=3ce2dce7a90f4d64bb1133911241601&q=+%20" + cityName + "%20+%20%22&days=1&aqi=no&alerts=no";
        txtCityName.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Res", response.toString());

                        loadingPB.setVisibility(View.GONE);
                        homeRL.setVisibility(View.VISIBLE);

                        weatherRVModelArrayList.clear();

                        try {
                            String temperature = response.getJSONObject("current").getString("temp_c");
                            txtTemperature.setText(temperature + "°c");


                            int isDay = response.getJSONObject("current").getInt("is_day");
                            String tempDisc = response.getJSONObject("current").getJSONObject("condition").getString("text");
                            String tempIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                            Log.e("Image", tempIcon);
                            Picasso.get().load("https:".concat(tempIcon)).into(imgTempDisc);
                            txtTempDisc.setText(tempDisc);

                            // morning
                            if (isDay == 1) {
                                Picasso.get().load("https://th.bing.com/th/id/OIP.jpYq6KxIsvIJJ0UBUcRYfAHaD7?rs=1&pid=ImgDetMain").into(imgBackground);
                            } else {
                                // night
                                Picasso.get().load("https://th.bing.com/th/id/OIP.hjUaTfJLki9CheerKPAcOwHaEo?rs=1&pid=ImgDetMain").into(imgBackground);
                            }

                            // fetch today's weather forecast
                            JSONObject forecastObj = response.getJSONObject("forecast");
                            JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                            JSONArray hourArray = forecastO.getJSONArray("hour");


                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String temper = hourObj.getString("temp_c");
                                String img = hourObj.getJSONObject("condition").getString("icon");
                                String wind_speed = hourObj.getString("wind_kph");
                                weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind_speed));
                                weatherRVAdapter.notifyDataSetChanged();
                                Log.e("i", time + " " + img + " " + wind_speed);
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name!", Toast.LENGTH_SHORT).show();
                // setProgressBar(false, false);
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void init() {
        homeRL = findViewById(R.id.homeRL);
        loadingPB = findViewById(R.id.loadingPB);
        txtCityName = findViewById(R.id.txtCityName);
        txtTempDisc = findViewById(R.id.txtTempDisc);
        txtTemperature = findViewById(R.id.txtTemperature);
        edtCityName = findViewById(R.id.edtCityName);
        imgSearch = findViewById(R.id.imgSearch);
        imgTempDisc = findViewById(R.id.imgTempDisc);
        imgBackground = findViewById(R.id.imgBackground);
        recyclerViewWeather = findViewById(R.id.recyclerViewWeather);
    }
}