package com.pocketmoney;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private PocketMoneyDatabase db;
    private TextView balanceText;
    private TextView greetingText;
    private ImageView profileImage;
    private TextView interestRateText;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PocketMoneyDatabase(this);
        setContentView(buildLayout());
        
        checkMonthlyInterest();
        checkMonthlyCredit();
        
        if (db.getAllTransactions().isEmpty()) {
            showInitialBalanceDialog();
        }
        
        refreshUI();
    }

    private void showInitialBalanceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Initial Balance");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("0.00");
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String val = input.getText().toString();
            if (!val.isEmpty()) {
                double amount = Double.parseDouble(val);
                String date = sdf.format(new Date());
                db.insertTransaction(amount, "CREDIT", "Initial Balance", date);
                refreshUI();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private View buildLayout() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));
        root.setBackgroundColor(Color.WHITE);
        scrollView.addView(root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(dp(20), bars.top + dp(10), dp(20), bars.bottom + dp(10));
            return insets;
        });

        profileImage = new ImageView(this);
        profileImage.setClickable(true);
        profileImage.setFocusable(true);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(200, 200);
        imgLp.gravity = Gravity.CENTER_HORIZONTAL;
        profileImage.setLayoutParams(imgLp);
        profileImage.setOnClickListener(v -> showFullScreenImage());
        root.addView(profileImage);

        TextView title = new TextView(this);
        title.setText("Pocket Money");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        greetingText = new TextView(this);
        greetingText.setTextSize(20);
        greetingText.setGravity(Gravity.CENTER);
        greetingText.setTextColor(Color.DKGRAY);
        root.addView(greetingText);

        balanceText = new TextView(this);
        balanceText.setTextSize(36);
        balanceText.setTextColor(Color.rgb(0, 150, 136));
        balanceText.setGravity(Gravity.CENTER);
        balanceText.setPadding(0, dp(20), 0, dp(20));
        root.addView(balanceText);

        LinearLayout topBtns = new LinearLayout(this);
        topBtns.setOrientation(LinearLayout.HORIZONTAL);
        
        Button summaryBtn = primaryButton("Summary");
        summaryBtn.setOnClickListener(v -> startActivity(new Intent(this, SummaryActivity.class)));
        LinearLayout.LayoutParams sumParams = new LinearLayout.LayoutParams(0, -2, 1);
        sumParams.setMargins(0, 0, dp(5), 0);
        topBtns.addView(summaryBtn, sumParams);

        Button profileBtn = secondaryButton("Profile");
        profileBtn.setBackgroundColor(Color.rgb(255, 152, 0)); 
        profileBtn.setTextColor(Color.WHITE);
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        LinearLayout.LayoutParams profParams = new LinearLayout.LayoutParams(0, -2, 1);
        profParams.setMargins(dp(5), 0, dp(5), 0);
        topBtns.addView(profileBtn, profParams);
        
        Button wishBtn = primaryButton("Wishlist");
        wishBtn.setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));
        LinearLayout.LayoutParams wishParams = new LinearLayout.LayoutParams(0, -2, 1);
        wishParams.setMargins(dp(5), 0, 0, 0);
        topBtns.addView(wishBtn, wishParams);
        
        root.addView(topBtns);

        interestRateText = label("Interest Rate: " + db.getSetting("interest_rate", "6.0") + "% yearly");
        interestRateText.setPadding(0, dp(10), 0, dp(10));
        root.addView(interestRateText);

        LinearLayout transactionBtns = new LinearLayout(this);
        transactionBtns.setOrientation(LinearLayout.HORIZONTAL);
        transactionBtns.setPadding(0, dp(10), 0, 0);

        Button creditDialogBtn = new Button(this);
        creditDialogBtn.setText("Credit Money");
        creditDialogBtn.setBackgroundColor(Color.rgb(76, 175, 80));
        creditDialogBtn.setTextColor(Color.WHITE);
        creditDialogBtn.setOnClickListener(v -> showTransactionDialog("CREDIT"));
        LinearLayout.LayoutParams creditParams = new LinearLayout.LayoutParams(0, -2, 1);
        creditParams.setMargins(0, 0, dp(5), 0);
        transactionBtns.addView(creditDialogBtn, creditParams);

        Button debitDialogBtn = new Button(this);
        debitDialogBtn.setText("Debit Money");
        debitDialogBtn.setBackgroundColor(Color.rgb(244, 67, 54));
        debitDialogBtn.setTextColor(Color.WHITE);
        debitDialogBtn.setOnClickListener(v -> showTransactionDialog("DEBIT"));
        LinearLayout.LayoutParams debitParams = new LinearLayout.LayoutParams(0, -2, 1);
        debitParams.setMargins(dp(5), 0, 0, 0);
        transactionBtns.addView(debitDialogBtn, debitParams);

        root.addView(transactionBtns);

        return scrollView;
    }

    private void showTransactionDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(type.equals("CREDIT") ? "Add Credit" : "Add Debit");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(10), dp(20), dp(10));

        EditText amountInput = new EditText(this);
        amountInput.setHint("Amount");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(amountInput);

        EditText remarkInput = new EditText(this);
        remarkInput.setHint("Remark (optional)");
        layout.addView(remarkInput);

        Button dateBtn = new Button(this);
        dateBtn.setText(sdf.format(new Date()));
        dateBtn.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            try {
                Date d = sdf.parse(dateBtn.getText().toString());
                if (d != null) cal.setTime(d);
            } catch (Exception ignored) {}
            new DatePickerDialog(this, (view, year, month, day) -> {
                cal.set(year, month, day);
                dateBtn.setText(sdf.format(cal.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        layout.addView(dateBtn);

        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String val = amountInput.getText().toString();
            if (val.isEmpty()) return;
            double amount = Double.parseDouble(val);
            String remark = remarkInput.getText().toString();
            if (remark.isEmpty()) remark = type.equals("CREDIT") ? "Manual Credit" : "Manual Debit";
            String date = dateBtn.getText().toString();
            db.insertTransaction(amount, type, remark, date);
            refreshUI();
            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showFullScreenImage() {
        String photoUri = db.getSetting("user_photo", "");
        if (photoUri.isEmpty()) return;

        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView fullImageView = new ImageView(this);
        fullImageView.setBackgroundColor(Color.BLACK);
        fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(photoUri));
            fullImageView.setImageBitmap(android.graphics.BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}
        
        fullImageView.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(fullImageView);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        String name = db.getSetting("user_name", "");
        setTitle("Pocket Money");
        greetingText.setText(name.isEmpty() ? "" : "Hello " + name);
        balanceText.setText("Balance: ₹" + String.format("%.2f", db.getBalance()));
        
        String photoUri = db.getSetting("user_photo", "");
        if (!photoUri.isEmpty()) {
            try {
                InputStream is = getContentResolver().openInputStream(Uri.parse(photoUri));
                profileImage.setImageBitmap(android.graphics.BitmapFactory.decodeStream(is));
            } catch (Exception ignored) {}
        }
    }

    private void checkMonthlyCredit() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; 
        String currentMonthKey = String.format(Locale.US, "%04d-%02d", year, month);
        
        String lastCredit = db.getSetting("last_monthly_credit", "");
        if (!lastCredit.equals(currentMonthKey)) {
            double monthlyAmount = Double.parseDouble(db.getSetting("monthly_amount", "0.0"));
            if (monthlyAmount > 0) {
                String date = String.format(Locale.US, "%04d-%02d-01", year, month);
                db.insertTransaction(monthlyAmount, "CREDIT", "Monthly Pocket Money", date);
                db.setSetting("last_monthly_credit", currentMonthKey);
                Toast.makeText(this, "Monthly allowance credited!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkMonthlyInterest() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String currentMonthKey = String.format(Locale.US, "%04d-%02d", year, month);

        String lastInterest = db.getSetting("last_interest_credit", "");
        if (!lastInterest.equals(currentMonthKey)) {
            double balance = db.getBalance();
            double rate = Double.parseDouble(db.getSetting("interest_rate", "6.0"));
            
            if (balance > 0) {
                double monthlyInterest = Math.ceil((balance * (rate / 100.0)) / 12.0);
                if (monthlyInterest >= 1) { 
                    String date = sdf.format(new Date());
                    db.insertTransaction(monthlyInterest, "CREDIT", "Monthly Interest", date);
                    Toast.makeText(this, "Interest credited: ₹" + String.format("%.0f", monthlyInterest), Toast.LENGTH_LONG).show();
                }
            }
            db.setSetting("last_interest_credit", currentMonthKey);
        }
    }

    private TextView label(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(0, dp(15), 0, dp(5));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }

    private Button primaryButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setBackgroundColor(Color.rgb(33, 150, 243));
        b.setTextColor(Color.WHITE);
        return b;
    }

    private Button secondaryButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setBackgroundColor(Color.LTGRAY);
        return b;
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density + 0.5f);
    }
}