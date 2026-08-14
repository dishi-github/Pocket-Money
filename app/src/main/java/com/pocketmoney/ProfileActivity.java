package com.pocketmoney;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class ProfileActivity extends Activity {
    private PocketMoneyDatabase db;
    private ImageView photoPreview;
    private String selectedPhotoUri;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new PocketMoneyDatabase(this);
        selectedPhotoUri = db.getSetting("user_photo", "");
        
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);

        photoPreview = new ImageView(this);
        photoPreview.setClickable(true);
        photoPreview.setFocusable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, 300);
        lp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        photoPreview.setLayoutParams(lp);
        updatePreview(selectedPhotoUri);
        photoPreview.setOnClickListener(v -> showFullScreenImage(selectedPhotoUri));
        root.addView(photoPreview);

        Button pickBtn = new Button(this);
        pickBtn.setText("Change Photo");
        pickBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });
        root.addView(pickBtn);

        root.addView(label("Name"));
        EditText nameInput = new EditText(this);
        nameInput.setText(db.getSetting("user_name", "User"));
        root.addView(nameInput);

        root.addView(label("4-Digit PIN (numeric)"));
        EditText pinInput = new EditText(this);
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setText(db.getSetting("user_pin", ""));
        pinInput.setHint("Leave empty for no lock");
        root.addView(pinInput);

        root.addView(label("Monthly Pocket Money Amount"));
        EditText monthlyInput = new EditText(this);
        monthlyInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        monthlyInput.setText(db.getSetting("monthly_amount", "0.0"));
        root.addView(monthlyInput);

        root.addView(label("Annual Interest Rate (%)"));
        EditText interestInput = new EditText(this);
        interestInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        interestInput.setText(db.getSetting("interest_rate", "6.0"));
        root.addView(interestInput);

        Button save = new Button(this);
        save.setText("Save Profile");
        save.setOnClickListener(v -> {
            String pin = pinInput.getText().toString();
            if (!pin.isEmpty() && pin.length() != 4) {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }
            db.setSetting("user_name", nameInput.getText().toString());
            db.setSetting("user_pin", pin);
            db.setSetting("user_photo", selectedPhotoUri);
            db.setSetting("monthly_amount", monthlyInput.getText().toString());
            db.setSetting("interest_rate", interestInput.getText().toString());
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
            finish();
        });
        root.addView(save);

        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            selectedPhotoUri = uri.toString();
            updatePreview(selectedPhotoUri);
        }
    }

    private void updatePreview(String uriStr) {
        if (uriStr.isEmpty()) return;
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(uriStr));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            photoPreview.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showFullScreenImage(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) return;
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView fullImageView = new ImageView(this);
        fullImageView.setBackgroundColor(android.graphics.Color.BLACK);
        fullImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(uriStr));
            fullImageView.setImageBitmap(BitmapFactory.decodeStream(is));
        } catch (Exception ignored) {}
        fullImageView.setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(fullImageView);
        dialog.show();
    }

    private TextView label(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(0, 20, 0, 5);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }
}
