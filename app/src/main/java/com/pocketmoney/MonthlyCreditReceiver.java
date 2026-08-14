package com.pocketmoney;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyCreditReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PocketMoneyDatabase db = new PocketMoneyDatabase(context);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String currentMonthKey = String.format(Locale.US, "%04d-%02d", year, month);

        // Handle Monthly Interest FIRST (on closing balance of previous month)
        String lastInterest = db.getSetting("last_interest_credit", "");
        if (!lastInterest.equals(currentMonthKey)) {
            double balance = db.getBalance();
            double rate = Double.parseDouble(db.getSetting("interest_rate", "6.0"));
            if (balance > 0) {
                double monthlyInterest = Math.ceil((balance * (rate / 100.0)) / 12.0);
                if (monthlyInterest >= 1) {
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
                    db.insertTransaction(monthlyInterest, "CREDIT", "Monthly Interest", date);
                }
            }
            db.setSetting("last_interest_credit", currentMonthKey);
        }

        // Handle Monthly Allowance SECOND
        String lastCredit = db.getSetting("last_monthly_credit", "");
        if (!lastCredit.equals(currentMonthKey)) {
            double monthlyAmount = Double.parseDouble(db.getSetting("monthly_amount", "0.0"));
            if (monthlyAmount > 0) {
                String date = String.format(Locale.US, "%04d-%02d-01", year, month);
                db.insertTransaction(monthlyAmount, "CREDIT", "Monthly Pocket Money", date);
                db.setSetting("last_monthly_credit", currentMonthKey);
            }
        }
    }
}
