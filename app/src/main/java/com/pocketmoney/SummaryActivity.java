package com.pocketmoney;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class SummaryActivity extends Activity {
    private PocketMoneyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PocketMoneyDatabase(this);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));
        scrollView.addView(root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(dp(20), bars.top + dp(10), dp(20), bars.bottom + dp(10));
            return insets;
        });

        TextView title = new TextView(this);
        title.setText("Transaction Summary");
        title.setTextSize(24);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, 0, 0, dp(20));
        root.addView(title);

        List<PocketMoneyDatabase.Transaction> transactions = db.getAllTransactions();
        if (transactions.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No transactions found.");
            root.addView(empty);
        } else {
            for (PocketMoneyDatabase.Transaction t : transactions) {
                root.addView(createTransactionView(t));
            }
        }

        setContentView(scrollView);
    }

    private LinearLayout createTransactionView(PocketMoneyDatabase.Transaction t) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(10), dp(10), dp(10), dp(10));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(10));
        card.setLayoutParams(params);
        card.setBackgroundColor(Color.rgb(245, 245, 245));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView desc = new TextView(this);
        desc.setText(t.description);
        desc.setTextSize(16);
        desc.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(desc, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView amount = new TextView(this);
        amount.setText((t.type.equals("CREDIT") ? "+ " : "- ") + "₹" + String.format("%.2f", t.amount));
        amount.setTextColor(t.type.equals("CREDIT") ? Color.rgb(76, 175, 80) : Color.rgb(244, 67, 54));
        amount.setTextSize(16);
        row.addView(amount);

        Button delBtn = new Button(this);
        delBtn.setText("🗑");
        delBtn.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Transaction?")
                .setMessage("Are you sure you want to revert this?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.removeTransaction(t.id);
                    recreate();
                })
                .setNegativeButton("No", null)
                .show();
        });
        row.addView(delBtn, new LinearLayout.LayoutParams(dp(50), dp(50)));

        card.addView(row);

        TextView date = new TextView(this);
        date.setText(t.date);
        date.setTextSize(12);
        card.addView(date);

        return card;
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density + 0.5f);
    }
}