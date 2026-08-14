package com.pocketmoney;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WishlistActivity extends Activity {
    private PocketMoneyDatabase db;
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PocketMoneyDatabase(this);
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("My Wishlist 🎁");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 20);
        root.addView(title);

        EditText nameIn = new EditText(this); nameIn.setHint("Item Name (e.g. LEGO)"); root.addView(nameIn);
        EditText priceIn = new EditText(this); priceIn.setHint("Price"); priceIn.setInputType(2); root.addView(priceIn);

        Button addBtn = new Button(this);
        addBtn.setText("Add to Wishlist");
        addBtn.setOnClickListener(v -> {
            String name = nameIn.getText().toString();
            String priceStr = priceIn.getText().toString();
            if (!name.isEmpty() && !priceStr.isEmpty()) {
                db.addWishlistItem(name, Double.parseDouble(priceStr));
                nameIn.setText(""); priceIn.setText("");
                refreshList();
            }
        });
        root.addView(addBtn);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setPadding(0, 20, 0, 0); // Fix cropping
        ScrollView sv = new ScrollView(this);
        sv.addView(listContainer);
        root.addView(sv);

        setContentView(root);
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAllViews();
        double balance = db.getBalance();
        for (PocketMoneyDatabase.WishItem item : db.getWishlist()) {
            LinearLayout row = new LinearLayout(this);
            row.setPadding(0, 20, 0, 20);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            TextView tv = new TextView(this);
            boolean canAfford = balance >= item.price;
            tv.setText(item.name + " - ₹" + item.price + (canAfford ? " ✅" : " ❌"));
            tv.setTextSize(18);
            row.addView(tv, new LinearLayout.LayoutParams(0, -2, 1));

            if (canAfford) {
                Button buy = new Button(this);
                buy.setText("Buy!");
                buy.setBackgroundColor(Color.rgb(76, 175, 80));
                buy.setTextColor(Color.WHITE);
                buy.setOnClickListener(v -> {
                    db.insertTransaction(item.price, "DEBIT", "Bought: " + item.name, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                    db.removeWishItem(item.id);
                    refreshList();
                    Toast.makeText(this, "Enjoy your " + item.name + "! 🥳", Toast.LENGTH_SHORT).show();
                });
                row.addView(buy);
            }

            Button del = new Button(this);
            del.setText("🗑");
            del.setOnClickListener(v -> {
                db.removeWishItem(item.id);
                refreshList();
            });
            row.addView(del);

            listContainer.addView(row);
        }
    }
}
