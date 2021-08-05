

package com.example.homearranger2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RoomContent extends AppCompatActivity {
    //    משתנים
    FirebaseAuth mAuth;
    StorageReference storageReference;
    private Button addItemBtn;
    private Button cameraBtn;
    private ImageView selectedImage;
    ArrayList<Product> ItemList;
    long items_counter;
    Calendar calendar = Calendar.getInstance();
    final int year = calendar.get(Calendar.YEAR);
    final int month = calendar.get(Calendar.MONTH);
    final int day = calendar.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog.OnDateSetListener setListener;
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    Button galleryBtn;
    String currentPhotoPath;
    String imgUrl;
    String roomName;
    String nm;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_content);
        long countItems = items_counter;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        roomName = getIntent().getStringExtra("TextHeader");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName).child("productList");
        DatabaseReference refRoom = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName);
        LinearLayout header = findViewById(R.id.content_act);
        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("position");
        storageReference = FirebaseStorage.getInstance().getReference();
        //=========================================================================================
        //קריאה מהפיירבייס
        ValueEventListener eventListener = new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                items_counter = snapshot.child("User").child(mAuth.getUid()).child("roomList").child(roomName).getChildrenCount();
                items_counter = snapshot.getChildrenCount();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product temp = ds.getValue(Product.class);
                    mAdapter.notifyItemInserted(i);
                    ItemList.add(temp);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
        createItemList();
        buildRecyclerView();
        addItemBtn = findViewById(R.id.btn);
        //=========================================================================================
        //דיאלוג ללחצן ההוספת פריט לרשימה
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(RoomContent.this);
                View ItmView = getLayoutInflater().inflate(R.layout.dialog_item, null);
                cameraBtn = (Button) ItmView.findViewById(R.id.cameraBtn);
                galleryBtn = (Button) ItmView.findViewById(R.id.galleryBtn);
                selectedImage = (ImageView) ItmView.findViewById(R.id.displayImageView);
                EditText item_name = (EditText) ItmView.findViewById(R.id.itmName);
                EditText item_amount = (EditText) ItmView.findViewById(R.id.itmAmount);
                EditText item_location = (EditText) ItmView.findViewById(R.id.itmLocation);
                EditText item_Date = (EditText) ItmView.findViewById(R.id.itmDate);
                Button itmAddBtn = (Button) ItmView.findViewById(R.id.btnItmAdd);
                mBuilder.setView(ItmView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                item_Date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                RoomContent.this, android.R.style.Theme_Holo_Dialog_MinWidth,
                                setListener, year, month, day
                        );
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });
                setListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        item_Date.setText(date);
                    }
                };
                cameraBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askCameraPermissions();
                    }
                });
                galleryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                    }
                });
                itmAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int amount = 0;
                        Toast.makeText(RoomContent.this, "current counter items is: " + String.valueOf(items_counter), Toast.LENGTH_LONG).show();
                        String name = item_name.getText().toString();
                        if (item_amount.getText().toString() != "") {
                            try {
                                amount = Integer.parseInt(item_amount.getText().toString());
                            } catch (NumberFormatException ex) { // handle your exception

                            }
                        }
                        String location = item_location.getText().toString();
                        String date = item_Date.getText().toString();
                        if (imgUrl != null && name != null && amount != 0 && location != null && date != null) {
                            Toast.makeText(RoomContent.this, "current counter items is: " + String.valueOf(items_counter), Toast.LENGTH_LONG).show();
                            InsertItem(Integer.parseInt(String.valueOf(items_counter)), new Product(imgUrl, name, amount, location, date,"f"));
                            ref.child(String.valueOf(items_counter)).setValue(new Product(imgUrl, name, amount, location, date,"f"));
                            int len = ItemList.size();
                            mAdapter.notifyItemInserted(len);
                            items_counter++;
                        } else {
                            Toast.makeText(RoomContent.this, "Error invalid inputs", Toast.LENGTH_SHORT).show();
                        }
                        nm = name;
                        dialog.dismiss();
                        Toast.makeText(RoomContent.this, "count!" + items_counter, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    //============================================
    //יצירת הרשימה
    public void createItemList() {
        ItemList = new ArrayList<>();
    }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.productList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ItemAdapter(ItemList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            //לחיצה על פריט
            //=============
            public void onItemClick(int position) {
                Intent intent = new Intent(RoomContent.this, ItemImageActivity.class);
                intent.putExtra("Name", ItemList.get(position).getName());
                intent.putExtra("imgRes", ItemList.get(position).getImageResource());
                intent.putExtra("room", roomName);
                startActivity(intent);
            }
            //מחיקה של פריט
            //=============
            @Override
            public void onDeleteClick(int position) {
                showDeleteDialog(position);

            }

            @Override
            public void onFavouritesClick(int position) {
              ItemList.get(position).setFavourite("t");
            }
        });
    }

    public void InsertItem(int position, Product p) {
        ItemList.add(position, p);
        mAdapter.notifyItemInserted(position);
    }

    //=======================================================================================
    //כל מה שקשור למצלמה
    //====================
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                Toast.makeText(this, "Path Here!!!!: "+contentUri.toString(), Toast.LENGTH_LONG).show();
                this.sendBroadcast(mediaScanIntent);
                uploadImageToFirebase(f.getName(), contentUri);
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                selectedImage.setImageURI(contentUri);
                Toast.makeText(RoomContent.this, contentUri.toString(), Toast.LENGTH_LONG).show();
                uploadImageToFirebase(imageFileName, contentUri);
            }
        }
    }

    //  העלאת תמונה לפיירבייס
    //===========================
    private void uploadImageToFirebase(String name, Uri contentUri) {
        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef1.child("User").child(mAuth.getUid());
        final StorageReference image = storageReference.child(mAuth.getUid()).child("pictures/" + name);
        String roomName = getIntent().getStringExtra("TextHeader");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName).child("productList");
        DatabaseReference refRoom = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName);

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgUrl = contentUri.toString();
                Toast.makeText(RoomContent.this, imgUrl, Toast.LENGTH_LONG).show();
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());

                    }
                });

                Toast.makeText(RoomContent.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RoomContent.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public void removeItem(int position) {
        ItemList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }
//==================================================================================================
    //דיאלוג למחיקת פריט
    //========================
    public void showDeleteDialog(int pos) {
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
        deleteDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes", new DialogInterface.OnClickListener() {
            private static final String TAG = "";

            public void onClick(DialogInterface dialog, int which) {
                mAuth = FirebaseAuth.getInstance();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                String roomName = getIntent().getStringExtra("TextHeader");
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = rootRef.child("User").child(mAuth.getUid()).child("roomList").child(roomName).child("productList");
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mAuth.getUid()).child("pictures/").child(ItemList.get(pos).getImageResource().split("/")[11]);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });
                ref.removeValue();
                ref.child(String.valueOf(pos)).removeValue();
                removeItem(pos);
                items_counter--;
            }
        });

        deleteDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
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
        okBT.setTextColor(Color.rgb(34, 139, 34));
        okBT.setLayoutParams(neutralBtnLP);

        final Button cancelBT = deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
        negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
        cancelBT.setTextColor(Color.RED);
        cancelBT.setLayoutParams(negBtnLP);
    }
}




