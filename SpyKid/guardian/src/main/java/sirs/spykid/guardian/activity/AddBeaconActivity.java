package sirs.spykid.guardian.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sirs.spykid.guardian.R;
import sirs.spykid.guardian.model.BeaconUser;
import sirs.spykid.util.ChildId;
import sirs.spykid.util.EncryptionAlgorithm;
import sirs.spykid.util.ServerApiKt;
import sirs.spykid.util.SharedKey;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddBeaconActivity extends AppCompatActivity {

    private Button createUserButton;
    private EditText userInput;
    private EditText passInput;
    private EncryptionAlgorithm ea;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon);

        createUserButton = findViewById(R.id.add_beacon_button);
        userInput = findViewById(R.id.user_input);
        passInput = findViewById(R.id.password_input);
        ea = new EncryptionAlgorithm(this);
        mDatabase = FirebaseDatabase.getInstance().getReference("beacons");
        Intent intent = getIntent();
        createUserButton.setOnClickListener(v -> createUser());
    }

    private void createUser() {

        String username = userInput.getText().toString();
        String password = passInput.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Invalid input, try again...", Toast.LENGTH_SHORT).show();
        } else {
            ServerApiKt.registerChild(username, password, r -> r.match(
                    ok -> saveChild(ok.getChildId()),
                    err -> Toast.makeText(this, "Error registering child, please try again...", Toast.LENGTH_SHORT).show()
            ));

        }

    }

    private void saveChild(ChildId childId) {
        try {
            SharedKey key = ea.generateSecretKey("SharedSecret");
            BeaconUser beaconUser = new BeaconUser(key, childId);
            String id = mDatabase.push().getKey();
            if(id != null) {
                mDatabase.child(id).setValue(beaconUser);
                showQRcode(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showQRcode(SharedKey key) {
        Intent intent = new Intent(getApplicationContext(), QRActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
        finish();
    }


}
