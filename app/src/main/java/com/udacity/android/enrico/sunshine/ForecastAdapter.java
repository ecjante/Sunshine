package com.udacity.android.enrico.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.android.enrico.sunshine.utilities.SunshineDateUtils;
import com.udacity.android.enrico.sunshine.utilities.SunshineWeatherUtils;

/**
 * Created by enrico on 1/26/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private ForecastAdapterOnClickHandler mListener;

    public interface ForecastAdapterOnClickHandler {
        void onListItemClicked(long date);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler listener) {
        this.mContext = context;
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
        mCursor.moveToPosition(position);

        long date = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateStr = SunshineDateUtils.getFriendlyDateString(mContext, date, false);

        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String condition = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);

        double minTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        double maxTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String temp = SunshineWeatherUtils.formatHighLows(mContext, maxTemp, minTemp);

        String weatherData = dateStr + " - " + condition + " - " + temp;

        holder.mWeatherTextView.setText(weatherData);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
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
            mCursor.moveToPosition(getAdapterPosition());
            mListener.onListItemClicked(mCursor.getLong(MainActivity.INDEX_WEATHER_DATE));
        }
    }
}
