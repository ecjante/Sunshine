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
import com.udacity.android.enrico.sunshine.databinding.ListItemForecastTodayBinding;

import java.util.List;

/**
 * Created by enrico on 1/26/18.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int TODAY_VIEW_TYPE = 0;
    private static final int FUTURE_DAY_VIEW_TYPE = 1;

    private Context mContext;
    private List<WeatherData> mData;

    private boolean mUseTodayLayout;

    private ForecastAdapterOnClickHandler mListener;

    public interface ForecastAdapterOnClickHandler {
        void onListItemClicked(long date);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler listener) {
        this.mContext = context;
        this.mListener = listener;

        mUseTodayLayout = context.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return TODAY_VIEW_TYPE;
        } else {
            return FUTURE_DAY_VIEW_TYPE;
        }
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int layoutId;
        switch (viewType) {
            case TODAY_VIEW_TYPE:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case FUTURE_DAY_VIEW_TYPE:
                layoutId = R.layout.forecast_list_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
        return new ForecastAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        WeatherData data = mData.get(position);

        int viewType = getItemViewType(position);

        if (viewType == FUTURE_DAY_VIEW_TYPE && holder.mBinding instanceof ForecastListItemBinding) {
            ForecastListItemBinding binding = (ForecastListItemBinding) holder.mBinding;
            binding.setWeatherData(data);
        } else if (viewType == TODAY_VIEW_TYPE && holder.mBinding instanceof ListItemForecastTodayBinding) {
            ListItemForecastTodayBinding binding = (ListItemForecastTodayBinding) holder.mBinding;
            binding.setWeatherData(data);
        } else {
            throw new IllegalArgumentException("Invalid view type, value of " + viewType);
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
