package com.example.pgconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    Context context;
    List<Booking> bookingList;
    DatabaseHelper db;

    public AdminBookingAdapter(Context context, List<Booking> bookingList, DatabaseHelper db) {
        this.context = context;
        this.bookingList = bookingList;
        this.db = db;
    }

    @Override
    public AdminBookingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdminBookingAdapter.ViewHolder holder, int position) {

        Booking booking = bookingList.get(position);

        holder.txtPgName.setText("PG: " + booking.getPgName());
        holder.txtLocation.setText("Location: " + booking.getLocation());
        holder.txtRent.setText("Rent: Rs." + booking.getRent());
        holder.txtUserEmail.setText("User Email: " + booking.getUserEmail());
        holder.txtMobile.setText("Mobile: " + booking.getMobile());
        holder.txtStatus.setText("Status: " + booking.getStatus());

        // View ID Proof
        holder.btnViewIdProof.setOnClickListener(v -> {
            String uriString = booking.getIdProofPath();
            if (uriString != null && !uriString.isEmpty()) {
                try {
                    Uri fileUri = Uri.parse(uriString);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "*/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(intent, "Open ID Proof"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // ✅ APPROVE BUTTON
        holder.btnApprove.setOnClickListener(v -> {
            if (!booking.getStatus().equalsIgnoreCase("Approved")) {

                // Check if rooms are available before approval
                Property property = db.getPropertyById(booking.getPropertyId());
                if (property != null && property.getAvailableRooms() <= 0) {
                    // No rooms left, cannot approve
                    android.widget.Toast.makeText(context, "No rooms available to approve!", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean updated = db.updateBookingStatus(booking.getId(), "Approved");
                if (updated) refreshList();
            }
        });

        // ❌ REJECT BUTTON
        holder.btnReject.setOnClickListener(v -> {
            if (!booking.getStatus().equalsIgnoreCase("Rejected")) {
                boolean updated = db.updateBookingStatus(booking.getId(), "Rejected");
                if (updated) refreshList();
            }
        });

        // 🗑 DELETE BUTTON
        holder.btnDelete.setOnClickListener(v -> {
            boolean deleted = db.deleteBooking(booking.getId());
            if (deleted) refreshList();
        });
    }

    private void refreshList() {
        bookingList.clear();
        bookingList.addAll(db.getAllBookings());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnDelete, btnViewIdProof, btnApprove, btnReject;
        TextView txtPgName, txtLocation, txtRent, txtUserEmail, txtMobile, txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtPgName = itemView.findViewById(R.id.txtPgName);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtRent = itemView.findViewById(R.id.txtRent);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnViewIdProof = itemView.findViewById(R.id.btnViewIdProof);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
