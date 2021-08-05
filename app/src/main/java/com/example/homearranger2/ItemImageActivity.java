package com.example.homearranger2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class ItemImageActivity extends AppCompatActivity {
    Button btnLogout;
    ImageView img;
    FirebaseAuth mAuth;
    String impPath;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_image);
        Bundle extras = getIntent().getExtras();
        name = extras.getString("imgRes");

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String roomName = getIntent().getStringExtra("room");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(this, roomName, Toast.LENGTH_SHORT).show();
       DatabaseReference ref = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName).child("imageList");
ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
//        int i=0;
//        for (DataSnapshot ds : snapshot.getChildren())
//        {
//            if(i==pos)
//            showTxt(ds.getValue().toString());
//            i++;
//        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        img=findViewById(R.id.itmImg);
        Uri uri=Uri.parse(name);
        img.setImageURI(uri);

    }
    private void showTxt(String st) {
        Toast.makeText(ItemImageActivity.this, st, Toast.LENGTH_SHORT).show();
    }
}