package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private List<String> rows;


    public ResultAdapter() {
        this.rows = new ArrayList<>();
    }

    public void setData(List<String> rows) {
        this.rows = rows;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        String row = rows.get(position);
        holder.bind(row);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void bind(String row) {
            textView.setText(row);
        }
    }
}
