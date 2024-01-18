package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherRVModel weatherRVModel = weatherRVModelArrayList.get(position);

        holder.txtTemp.setText(weatherRVModel.getTemperature() + "°c");
        holder.txtWindSpeed.setText(weatherRVModel.getWindSpeed() + "Km/h");

        // load the weather conditions image
        Picasso.get().load("https:".concat(weatherRVModel.getIcon())).into(holder.imgWeatherCondition);

        // covert time to only hh:mm
        SimpleDateFormat inputTime = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputTime = new SimpleDateFormat("hh:mm aa");

        try {
            Date time = inputTime.parse(weatherRVModel.getTime());
            if (time != null) {
                holder.txtTime.setText(outputTime.format(time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtWindSpeed, txtTemp, txtTime;
        private ImageView imgWeatherCondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtWindSpeed = itemView.findViewById(R.id.txtWindSpeed);
            txtTemp = itemView.findViewById(R.id.txtTemp);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgWeatherCondition = itemView.findViewById(R.id.imgWeatherCondition);

        }
    }
}
