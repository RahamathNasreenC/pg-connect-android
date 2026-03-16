package com.example.pgconnect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class UserPropertyAdapter extends RecyclerView.Adapter<UserPropertyAdapter.ViewHolder> {

    Context context;
    ArrayList<Property> propertyList;

    public UserPropertyAdapter(Context context, ArrayList<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Property property = propertyList.get(position);

        holder.txtTitle.setText(property.getTitle());

        String imagePath = property.getImagePath();

        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                    .load(new File(imagePath))
                    .into(holder.imgProperty);
        } else {
            holder.imgProperty.setImageResource(R.drawable.ic_launcher_background);
        }

        // CLICK LISTENER FOR DETAIL PAGE
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PropertyDetailActivity.class);

            // ⭐ IMPORTANT FIX: pass property ID
            intent.putExtra("id", property.getId());

            intent.putExtra("title", property.getTitle());
            intent.putExtra("location", property.getLocation());
            intent.putExtra("rent", property.getRent());
            intent.putExtra("rooms", property.getAvailableRooms());
            intent.putExtra("description", property.getDescription());
            intent.putExtra("contact", property.getContactNumber());
            intent.putExtra("amenities", property.getAmenities());
            intent.putExtra("propertyImage", property.getImagePath());
            intent.putExtra("bedImage", property.getBedImagePath());
            intent.putExtra("roomType", property.getRoomType());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProperty;
        TextView txtTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProperty = itemView.findViewById(R.id.imgproperty);
            txtTitle = itemView.findViewById(R.id.txttitle);
        }
    }
}
