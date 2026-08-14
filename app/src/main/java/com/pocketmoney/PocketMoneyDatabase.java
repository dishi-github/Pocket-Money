package com.pocketmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PocketMoneyDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pocket_money.db";
    private static final int DATABASE_VERSION = 3;

    public PocketMoneyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount REAL, type TEXT, description TEXT, date TEXT)");
        db.execSQL("CREATE TABLE settings (key TEXT PRIMARY KEY, value TEXT)");
        db.execSQL("CREATE TABLE wishlist (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, price REAL)");
        
        // Default settings
        db.execSQL("INSERT INTO settings (key, value) VALUES ('interest_rate', '6.0')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('monthly_amount', '300.0')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('last_monthly_credit', '2026-06')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('last_interest_credit', '2026-06')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('user_name', 'User')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('user_pin', '')");
        db.execSQL("INSERT INTO settings (key, value) VALUES ('user_photo', '')");

        // Prepopulated Ledger Entries
        insertInitialData(db, 3800, "CREDIT", "Mama+Nani", "2025-09-02");
        insertInitialData(db, 200, "CREDIT", "PM", "2025-09-01");
        insertInitialData(db, 240, "CREDIT", "Rakhi", "2025-10-04");
        insertInitialData(db, 3101, "CREDIT", "Mama", "2025-10-22");
        insertInitialData(db, 1101, "CREDIT", "Dada", "2025-10-22");
        insertInitialData(db, 100, "CREDIT", "Meena Kaki", "2025-10-22");
        insertInitialData(db, 285, "CREDIT", "PM", "2025-11-01");
        insertInitialData(db, 200, "CREDIT", "Rakhi", "2025-11-14");
        insertInitialData(db, 290, "CREDIT", "PM", "2025-12-01");
        insertInitialData(db, 1100, "CREDIT", "Nani", "2025-12-06");
        insertInitialData(db, 1625, "CREDIT", "Wedding / Manu Mama B'day+Bonus / Birthday (merged)", "2025-12-07");
        insertInitialData(db, 50, "CREDIT", "Mumma", "2026-01-05");
        insertInitialData(db, 308, "CREDIT", "Jan PM", "2026-01-05");
        insertInitialData(db, 100, "CREDIT", "Increment PM", "2026-01-05");
        insertInitialData(db, 300, "CREDIT", "Feb PM", "2026-02-01");
        insertInitialData(db, 400, "CREDIT", "Jan+Feb Bonus PM", "2026-02-26");
        insertInitialData(db, 1100, "CREDIT", "Beena Maasi", "2026-03-15");
        insertInitialData(db, 700, "CREDIT", "PM March + PM April", "2026-04-03");
        insertInitialData(db, 300, "CREDIT", "PM (June)", "2026-06-01");
        insertInitialData(db, 1500, "CREDIT", "Nani", "2026-05-04");
        insertInitialData(db, 200, "CREDIT", "Rakhi", "2026-08-01");
        insertInitialData(db, 300, "CREDIT", "Mama", "2026-08-01");
        insertInitialData(db, 150, "CREDIT", "Raju Mama", "2026-08-01");
        insertInitialData(db, 50, "CREDIT", "Bonus", "2026-08-01");
    }

    private void insertInitialData(SQLiteDatabase db, double amount, String type, String description, String date) {
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("type", type);
        values.put("description", description);
        values.put("date", date);
        db.insert("transactions", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db);
    }

    public void insertTransaction(double amount, String type, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("type", type);
        values.put("description", description);
        values.put("date", date);
        db.insert("transactions", null, values);
    }

    public double getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE -amount END) FROM transactions", null);
        double balance = 0;
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }
        cursor.close();
        return balance;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM transactions ORDER BY date DESC, id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Transaction(
                    cursor.getInt(0),
                    cursor.getDouble(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void setSetting(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value);
        db.replace("settings", null, values);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("settings", new String[]{"value"}, "key=?", new String[]{key}, null, null, null);
        String value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    public void addWishlistItem(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("item_name", name);
        v.put("price", price);
        db.insert("wishlist", null, v);
    }

    public List<WishItem> getWishlist() {
        List<WishItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM wishlist", null);
        if (c.moveToFirst()) {
            do {
                list.add(new WishItem(c.getInt(0), c.getString(1), c.getDouble(2)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void removeWishItem(int id) {
        getWritableDatabase().delete("wishlist", "id=?", new String[]{String.valueOf(id)});
    }

    public void removeTransaction(int id) {
        getWritableDatabase().delete("transactions", "id=?", new String[]{String.valueOf(id)});
    }

    public static class WishItem {
        public int id;
        public String name;
        public double price;
        public WishItem(int id, String name, double price) {
            this.id = id; this.name = name; this.price = price;
        }
    }

    public static class Transaction {
        public int id;
        public double amount;
        public String type; // "CREDIT" or "DEBIT"
        public String description;
        public String date;

        public Transaction(int id, double amount, String type, String description, String date) {
            this.id = id;
            this.amount = amount;
            this.type = type;
            this.description = description;
            this.date = date;
        }
    }
}
