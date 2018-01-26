package com.udacity.android.enrico.sunshine;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by enrico on 1/26/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;
    private ForecastAdapterOnClickHandler mListener;

    public interface ForecastAdapterOnClickHandler {
        void onListItemClicked(String data);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler listener) {
        this.mListener = listener;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.forecast_list_item, parent, false);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        String weatherData = mWeatherData[position];
        holder.mWeatherTextView.setText(weatherData);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null)
            return 0;
        return mWeatherData.length;
    }

    public void setWeatherData(String[] weatherData) {
        this.mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String data = mWeatherData[position];
            mListener.onListItemClicked(data);
        }
    }
}
