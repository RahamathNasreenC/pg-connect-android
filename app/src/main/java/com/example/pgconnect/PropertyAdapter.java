package com.example.pgconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private Context context;
    private List<Property> propertyList;
    private DatabaseHelper db;

    public PropertyAdapter(Context context, List<Property> propertyList, DatabaseHelper db) {
        this.context = context;
        this.propertyList = propertyList;
        this.db = db;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.tvTitle.setText(property.getTitle());
        holder.tvLocation.setText(property.getLocation());
        holder.tvRent.setText("₹" + property.getRent());

        // Load property image using Glide
        if (!property.getImagePath().isEmpty()) {
            Glide.with(context)
                    .load(property.getImagePath())
                    .into(holder.ivPropertyImage);
        } else {
            holder.ivPropertyImage.setImageResource(R.drawable.ic_launcher_background); // default image
        }

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            boolean deleted = db.deleteProperty(property.getId());
            if (deleted) {
                Toast.makeText(context, "Property deleted successfully", Toast.LENGTH_SHORT).show();
                propertyList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, propertyList.size());
            } else {
                Toast.makeText(context, "Failed to delete property", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvRent;
        ImageView ivPropertyImage;
        Button btnDelete;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRent = itemView.findViewById(R.id.tvRent);
            ivPropertyImage = itemView.findViewById(R.id.ivPropertyImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
