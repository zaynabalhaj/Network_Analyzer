package com.example.testtest;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    private List<Pair<String, String>> data;

    public TableAdapter(List<Pair<String, String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> item = data.get(position);
        holder.textViewKey.setText(item.first);
        holder.textViewValue.setText(item.second);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKey, textViewValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKey = itemView.findViewById(R.id.textViewKey);
            textViewValue = itemView.findViewById(R.id.textViewValue);
        }
    }
}
