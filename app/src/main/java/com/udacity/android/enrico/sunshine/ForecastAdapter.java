package com.udacity.android.enrico.sunshine;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.android.enrico.sunshine.data.WeatherData;
import com.udacity.android.enrico.sunshine.databinding.ForecastListItemBinding;

import java.util.List;

/**
 * Created by enrico on 1/26/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private Context mContext;
    private List<WeatherData> mData;
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

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.forecast_list_item, parent, false);

        return new ForecastAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        WeatherData data = mData.get(position);

        if (holder.mBinding instanceof ForecastListItemBinding) {
            ForecastListItemBinding binding = (ForecastListItemBinding) holder.mBinding;
            binding.setWeatherData(data);
        }
        holder.mBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public void swapData(List<WeatherData> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ViewDataBinding mBinding;

        public ForecastAdapterViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            WeatherData data = mData.get(getAdapterPosition());
            mListener.onListItemClicked(data.getDateMillis());
        }
    }
}
