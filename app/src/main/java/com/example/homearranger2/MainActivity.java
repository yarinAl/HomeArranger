package com.example.homearranger2;
//ייבוא של קלאסים רלוונטיים לפרוייקט
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//אקטיבי לדף הראשי של האפליקצייה
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //הגדרת משתנים
    private RecyclerView mRecyclerView;
    private RoomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private BottomNavigationView bottomNavigationView;
    private ArrayList<Room> RoomList;
    private ArrayList<Product> ProductList = new ArrayList<Product>();
    private int len;
//    private ArrayList<String> names = new ArrayList<>();
    private String RoomName = "";
    Task<Void> myRef;
    FirebaseAuth mAuth;


    // ניווט  תחתון
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.nav_camera:
                    askCameraPermissions();
                    break;

                case R.id.navigationAddRoom:
                    showAddRoomDialog();
                    Toast.makeText(MainActivity.this, "Add room!", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.navigationMenu:
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                    return true;
            }
            return false;
        }
    };

    @Override
    //מה שקורה בזמן שהפעילות מתחילה
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("User").child(mAuth.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            //לקיחת החדרים מהפיירבייס
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.child("roomList").getChildren()) {
                    String name = ds.getKey();
                    if (name != null) {
                        len = RoomList.size();
//                        names.add(name);
                        RoomList.add(len, new Room(R.drawable.living_room, name, "room", ProductList));
                        mAdapter.notifyItemInserted(len);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);

        createRoomList();
        buildRecyclerView();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //bottomNavigationView.setSelectedItemId(R.id.navigationHome);
    }
   //פונקציה למחיקת חדר
    public void removeItem(int position) {
        RoomList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }
    private void addRoom() {
        mAdapter.notifyDataSetChanged();
    }

    private void createRoomList() {
        RoomList = new ArrayList<>();
    }
//יצירת רשימה דינאמית
    private void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RoomAdapter(RoomList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, RoomContent.class);
                intent.putExtra("TextHeader", RoomList.get(position).getTextHeader());
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
            @Override
            public void onDeleteClick(int position) {
                showDeleteDialog(position);

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //==============================
    //תפריט צידי
    //================================
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, MainActivity.class));

        } else if (id == R.id.nav_camera) {
            askCameraPermissions();

        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, Profile.class));

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 100);
    }

    private void addUserRoom(String RoomName) {
        myRef = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getUid()).child("roomList").child(RoomName).setValue(new Room(1, RoomName, "room", ProductList));

    }
    public void ErrorMessage() {
        Toast.makeText(this, "No Room Added (to add room enter name)", Toast.LENGTH_LONG).show();

    }
//    דיאלוג להוספת חדר
    public void showAddRoomDialog() {
        AlertDialog roomDialog = new AlertDialog.Builder(this).create();
        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Create new Room");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        roomDialog.setCustomTitle(title);

        // Set Message
        final EditText RoomData = new EditText(MainActivity.this);
        roomDialog.setView(RoomData);

        // Set Button
        roomDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            private static final String TAG = "";

            public void onClick(DialogInterface dialog, int which) {
                RoomName = RoomData.getText().toString();
                len = RoomList.size();
                if(!(RoomName.equals("") || RoomName.equals(null))) {
                    RoomList.add(len, new Room(R.drawable.living_room, RoomName, "room", ProductList));
                    mAdapter.notifyItemInserted(len);
                    addUserRoom(RoomName);
                }
                else{
                    ErrorMessage();
                }

            }
        });

        roomDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                RoomName = RoomData.getText().toString();
            }
        });

        new Dialog(getApplicationContext());
        roomDialog.show();

        // Set Properties for OK Button
        final Button okBT = roomDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.rgb(34,139,34));
        okBT.setLayoutParams(neutralBtnLP);
        final Button cancelBT = roomDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }

    //דיאלוג מחיקה
    public  void showDeleteDialog(int pos)
    {
        AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        // Set Custom Title
        TextView title = new TextView(this);
        // Title Properties
        title.setText("Are you sure you want to delete?");
        title.setPadding(10, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        deleteDialog.setCustomTitle(title);

        // Set Button
        deleteDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Yes", new DialogInterface.OnClickListener() {
            private static final String TAG = "";

            public void onClick(DialogInterface dialog, int which) {

                mAuth = FirebaseAuth.getInstance();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = rootRef.child("User").child(mAuth.getUid()).child("roomList");

                Query applesQuery = ref.orderByChild("textHeader").equalTo(RoomList.get(pos).getTextHeader());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
                removeItem(pos);
            }
        });

        deleteDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        new Dialog(getApplicationContext());
        deleteDialog.show();

        // Set Properties for OK Button
        final Button okBT = deleteDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        okBT.setPadding(50, 10, 10, 10);   // Set Position
        okBT.setTextColor(Color.rgb(34,139,34));
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }
}


