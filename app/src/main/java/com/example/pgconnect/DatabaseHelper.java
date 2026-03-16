package com.example.pgconnect;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PGConnect.db";
    private static final int DATABASE_VERSION = 7; // updated version

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PROPERTIES = "properties";
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // USERS
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT)");

        ContentValues admin = new ContentValues();
        admin.put("name", "Admin");
        admin.put("email", "admin@pg.com");
        admin.put("password", "admin123");
        db.insert(TABLE_USERS, null, admin);

        // PROPERTIES
        db.execSQL("CREATE TABLE " + TABLE_PROPERTIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "location TEXT," +
                "room_type TEXT," +
                "rent INTEGER," +
                "amenities TEXT," +
                "image_path TEXT," +
                "description TEXT," +
                "available_rooms INTEGER," +
                "contact_number TEXT," +
                "bed_image_path TEXT)");

        // BOOKINGS
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "property_id INTEGER," +
                "pg_name TEXT," +
                "location TEXT," +
                "rent INTEGER," +
                "room_type TEXT," +
                "user_email TEXT," +
                "mobile TEXT," +
                "id_proof_path TEXT," +
                "status TEXT DEFAULT 'Pending')");

        // NOTIFICATIONS
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT," +
                "message TEXT," +
                "is_read INTEGER DEFAULT 0," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    // ================= USERS =================
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // ================= PROPERTIES =================
    public boolean insertProperty(String title, String location, String roomType, int rent,
                                  String amenities, String imagePath,
                                  String description, int availableRooms,
                                  String contactNumber, String bedImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("location", location);
        values.put("room_type", roomType);
        values.put("rent", rent);
        values.put("amenities", amenities);
        values.put("image_path", imagePath);
        values.put("description", description);
        values.put("available_rooms", availableRooms);
        values.put("contact_number", contactNumber);
        values.put("bed_image_path", bedImagePath);
        long result = db.insert(TABLE_PROPERTIES, null, values);
        return result != -1;
    }

    public boolean deleteProperty(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PROPERTIES, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public ArrayList<Property> getAllProperties() {
        ArrayList<Property> propertyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                Property property = new Property(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getString(cursor.getColumnIndex("room_type")),
                        cursor.getInt(cursor.getColumnIndex("rent")),
                        cursor.getString(cursor.getColumnIndex("amenities")),
                        cursor.getString(cursor.getColumnIndex("image_path")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getInt(cursor.getColumnIndex("available_rooms")),
                        cursor.getString(cursor.getColumnIndex("contact_number")),
                        cursor.getString(cursor.getColumnIndex("bed_image_path"))
                );
                propertyList.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return propertyList;
    }

    // ================= BOOKINGS =================
    // User can book only once per PG if previous is pending/approved
    public boolean insertBooking(int propertyId, String pgName, String location, int rent,
                                 String roomType, String userEmail,
                                 String mobile, String idProofPath) {

        if (!canUserBookPG(userEmail, propertyId)) {
            return false; // Already booked this PG, cannot book again
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("property_id", propertyId);
        values.put("pg_name", pgName);
        values.put("location", location);
        values.put("rent", rent);
        values.put("room_type", roomType);
        values.put("user_email", userEmail);
        values.put("mobile", mobile);
        values.put("id_proof_path", idProofPath);
        values.put("status", "Pending");
        long result = db.insert(TABLE_BOOKINGS, null, values);
        return result != -1;
    }

    // Check if user can book a PG
    public boolean canUserBookPG(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_BOOKINGS + " WHERE user_email=? AND property_id=? AND LOWER(status) IN ('pending','approved')",
                new String[]{userEmail, String.valueOf(propertyId)}
        );
        boolean canBook = cursor.getCount() == 0;
        cursor.close();
        return canBook;
    }

    // Get bookings by user
    public ArrayList<Booking> getBookingsByUser(String userEmail) {
        ArrayList<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " WHERE user_email=? ORDER BY id DESC",
                new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getInt(cursor.getColumnIndex("property_id")),
                        cursor.getString(cursor.getColumnIndex("pg_name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getInt(cursor.getColumnIndex("rent")),
                        cursor.getString(cursor.getColumnIndex("room_type")),
                        cursor.getString(cursor.getColumnIndex("user_email")),
                        cursor.getString(cursor.getColumnIndex("mobile")),
                        cursor.getString(cursor.getColumnIndex("id_proof_path")),
                        cursor.getString(cursor.getColumnIndex("status"))
                );
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingList;
    }

    // Get all bookings (admin)
    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getInt(cursor.getColumnIndex("property_id")),
                        cursor.getString(cursor.getColumnIndex("pg_name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getInt(cursor.getColumnIndex("rent")),
                        cursor.getString(cursor.getColumnIndex("room_type")),
                        cursor.getString(cursor.getColumnIndex("user_email")),
                        cursor.getString(cursor.getColumnIndex("mobile")),
                        cursor.getString(cursor.getColumnIndex("id_proof_path")),
                        cursor.getString(cursor.getColumnIndex("status"))
                );
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingList;
    }

    // Get booking by ID
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " WHERE id=?",
                new String[]{String.valueOf(bookingId)});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            Booking booking = new Booking(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("property_id")),
                    cursor.getString(cursor.getColumnIndex("pg_name")),
                    cursor.getString(cursor.getColumnIndex("location")),
                    cursor.getInt(cursor.getColumnIndex("rent")),
                    cursor.getString(cursor.getColumnIndex("room_type")),
                    cursor.getString(cursor.getColumnIndex("user_email")),
                    cursor.getString(cursor.getColumnIndex("mobile")),
                    cursor.getString(cursor.getColumnIndex("id_proof_path")),
                    cursor.getString(cursor.getColumnIndex("status"))
            );
            cursor.close();
            return booking;
        }
        cursor.close();
        return null;
    }

    // Update booking status (admin)
    public boolean updateBookingStatus(int bookingId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        Booking booking = getBookingById(bookingId);
        if (booking == null) return false;

        String oldStatus = booking.getStatus();
        if (oldStatus.equalsIgnoreCase(status)) return false;

        // Handle room count
        if (oldStatus.equalsIgnoreCase("Pending") && status.equalsIgnoreCase("Approved")) {
            // Reduce room only if available
            if (!reduceAvailableRoom(booking.getPropertyId())) return false;
        } else if (oldStatus.equalsIgnoreCase("Approved") && (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Cancelled"))) {
            increaseAvailableRoom(booking.getPropertyId());
        }

        ContentValues values = new ContentValues();
        values.put("status", status);
        int result = db.update(TABLE_BOOKINGS, values, "id=?", new String[]{String.valueOf(bookingId)});

        if (result > 0) {
            insertNotification(booking.getUserEmail(), "Your booking for " + booking.getPgName() + " is " + status);
        }
        return result > 0;
    }

    // Delete booking
    public boolean deleteBooking(int bookingId) {
        Booking booking = getBookingById(bookingId);
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BOOKINGS, "id=?", new String[]{String.valueOf(bookingId)});
        if (result > 0 && booking != null) {
            // If booking was approved, restore room
            if (booking.getStatus().equalsIgnoreCase("Approved")) {
                increaseAvailableRoom(booking.getPropertyId());
            }
            insertNotification(booking.getUserEmail(), "Your booking for " + booking.getPgName() + " has been cancelled");
        }
        return result > 0;
    }

    // Reduce room
    public boolean reduceAvailableRoom(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT available_rooms FROM " + TABLE_PROPERTIES + " WHERE id=?", new String[]{String.valueOf(propertyId)});
        if (cursor.moveToFirst()) {
            int currentRooms = cursor.getInt(0);
            cursor.close();
            if (currentRooms <= 0) return false;
            ContentValues values = new ContentValues();
            values.put("available_rooms", currentRooms - 1);
            int result = db.update(TABLE_PROPERTIES, values, "id=?", new String[]{String.valueOf(propertyId)});
            return result > 0;
        }
        cursor.close();
        return false;
    }

    // Increase room
    public boolean increaseAvailableRoom(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT available_rooms FROM " + TABLE_PROPERTIES + " WHERE id=?", new String[]{String.valueOf(propertyId)});
        if (cursor.moveToFirst()) {
            int currentRooms = cursor.getInt(0);
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("available_rooms", currentRooms + 1);
            int result = db.update(TABLE_PROPERTIES, values, "id=?", new String[]{String.valueOf(propertyId)});
            return result > 0;
        }
        cursor.close();
        return false;
    }

    // Get property by ID
    public Property getPropertyById(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES + " WHERE id=?", new String[]{String.valueOf(propertyId)});
        if (cursor.moveToFirst()) {
            Property property = new Property(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    cursor.getString(cursor.getColumnIndexOrThrow("room_type")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("rent")),
                    cursor.getString(cursor.getColumnIndexOrThrow("amenities")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image_path")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("available_rooms")),
                    cursor.getString(cursor.getColumnIndexOrThrow("contact_number")),
                    cursor.getString(cursor.getColumnIndexOrThrow("bed_image_path"))
            );
            cursor.close();
            return property;
        }
        cursor.close();
        return null;
    }

    // Get bookings by status (admin filter)
    public ArrayList<Booking> getBookingsByStatus(String status) {
        ArrayList<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_BOOKINGS + " WHERE LOWER(status)=? ORDER BY id DESC",
                new String[]{status.toLowerCase()}
        );

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getInt(cursor.getColumnIndex("property_id")),
                        cursor.getString(cursor.getColumnIndex("pg_name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getInt(cursor.getColumnIndex("rent")),
                        cursor.getString(cursor.getColumnIndex("room_type")),
                        cursor.getString(cursor.getColumnIndex("user_email")),
                        cursor.getString(cursor.getColumnIndex("mobile")),
                        cursor.getString(cursor.getColumnIndex("id_proof_path")),
                        cursor.getString(cursor.getColumnIndex("status"))
                );
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingList;
    }

    // Notifications
    public boolean insertNotification(String userEmail, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_email", userEmail);
        values.put("message", message);
        values.put("is_read", 0);
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        return result != -1;
    }

    public ArrayList<NotificationItem> getNotificationsByUser(String userEmail) {
        ArrayList<NotificationItem> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE user_email=? ORDER BY created_at DESC",
                new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                NotificationItem item = new NotificationItem(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("user_email")),
                        cursor.getString(cursor.getColumnIndex("message")),
                        cursor.getInt(cursor.getColumnIndex("is_read")),
                        cursor.getString(cursor.getColumnIndex("created_at"))
                );
                notifications.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notifications;
    }

    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        int result = db.update(TABLE_NOTIFICATIONS, values, "id=?", new String[]{String.valueOf(notificationId)});
        return result > 0;
    }
}
