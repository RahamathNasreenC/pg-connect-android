package com.example.pgconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingTrackerAdapter extends RecyclerView.Adapter<BookingTrackerAdapter.ViewHolder> {

    Context context;
    List<Booking> bookingList;
    DatabaseHelper db;

    public BookingTrackerAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BookingTrackerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_tracker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingTrackerAdapter.ViewHolder holder, int position) {

        Booking booking = bookingList.get(position);

        holder.txtPgName.setText(booking.getPgName());
        holder.txtLocation.setText(booking.getLocation());
        holder.txtRent.setText("Rent: Rs." + booking.getRent());
        holder.txtStatus.setText("Status: " + booking.getStatus());

        // Set status color
        String status = booking.getStatus();
        if (status.equalsIgnoreCase("Pending")) {
            holder.txtStatus.setTextColor(Color.parseColor("#FFA500")); // Orange
        } else if (status.equalsIgnoreCase("Approved")) {
            holder.txtStatus.setTextColor(Color.parseColor("#008000")); // Green
        } else if (status.equalsIgnoreCase("Rejected")) {
            holder.txtStatus.setTextColor(Color.parseColor("#FF0000")); // Red
        } else {
            holder.txtStatus.setTextColor(Color.BLACK);
        }

        // CANCEL BOOKING BUTTON CLICK
        holder.btnCancelBooking.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Cancel Booking");
            builder.setMessage("Are you sure you want to cancel this booking request?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                boolean deleted = db.deleteBooking(booking.getId());

                if (deleted) {
                    Toast.makeText(context, "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show();

                    bookingList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookingList.size());
                } else {
                    Toast.makeText(context, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                }

            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPgName, txtLocation, txtRent, txtStatus;
        Button btnCancelBooking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPgName = itemView.findViewById(R.id.txtPgName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtRent = itemView.findViewById(R.id.txtRent);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnCancelBooking = itemView.findViewById(R.id.btnCancel);
        }
    }
}
