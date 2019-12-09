package sirs.spykid.guardian.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.security.Key;

import sirs.spykid.guardian.R;
import sirs.spykid.guardian.service.QRGenerator;

public class QRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ImageView qrImage = findViewById(R.id.qr_image);
        Intent intent = getIntent();
        Key key = (Key) intent.getSerializableExtra("key");
        if(key != null) {
            Bitmap bmp = QRGenerator.qrFromString(key.getEncoded().toString());
            qrImage.setImageBitmap(bmp);
            qrImage.setVisibility(View.VISIBLE);
        }
    }
}
