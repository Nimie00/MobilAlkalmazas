package com.nimie.myapplication4;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedItemAdapter extends RecyclerView.Adapter<SavedItemAdapter.ViewHolder> {
    private List<SavedItem> savedItems;

    public SavedItemAdapter(List<SavedItem> savedItems) {
        this.savedItems = savedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View savedItemView = inflater.inflate(R.layout.item_saved, parent, false);
        return new ViewHolder(savedItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedItem savedItem = savedItems.get(position);
        holder.keyTextView.setText(savedItem.getKey());
        holder.valueTextView.setText(savedItem.getValue());
    }

    @Override
    public int getItemCount() {
        return savedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView keyTextView;
        public TextView valueTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.keyTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
        }
    }
}
