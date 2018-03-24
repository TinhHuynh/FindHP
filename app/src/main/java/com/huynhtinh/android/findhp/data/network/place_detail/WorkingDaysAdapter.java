package com.huynhtinh.android.findhp.data.network.place_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huynhtinh.android.findhp.R;

import java.util.List;

/**
 * Created by Chip Caber on 24-Mar-18.
 */

public class WorkingDaysAdapter extends RecyclerView.Adapter<WorkingDaysAdapter.ViewHolder> {
    private Context context;
    private List<String> workingDays;

    public WorkingDaysAdapter(Context context, List<String> workingDays) {
        this.context = context;
        this.workingDays = workingDays;
    }

    @Override
    public WorkingDaysAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_working_day_item, parent, false);
        return new WorkingDaysAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkingDaysAdapter.ViewHolder holder, int position) {
        holder.workingDayTv.setText(workingDays.get(position));
    }

    @Override
    public int getItemCount() {
        return workingDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView workingDayTv;

        public ViewHolder(View itemView) {
            super(itemView);
            workingDayTv = (TextView) itemView.findViewById(R.id.working_day_text_view);
        }
    }
}
