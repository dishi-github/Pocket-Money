package com.pocketmoney;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockActivity extends Activity {
    private PocketMoneyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PocketMoneyDatabase(this);
        String pin = db.getSetting("user_pin", "");

        if (pin.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(50, 50, 50, 50);

        TextView tv = new TextView(this);
        tv.setText("Enter 4-digit PIN");
        tv.setTextSize(24);
        root.addView(tv);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setGravity(Gravity.CENTER);
        input.setTextSize(30);
        root.addView(input);

        Button btn = new Button(this);
        btn.setText("Unlock");
        btn.setOnClickListener(v -> {
            if (input.getText().toString().equals(pin)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(btn);

        setContentView(root);
    }
}
