package netbit.develop.food;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity implements LocationListener {

    Spinner categorySpinner;
    Spinner expireSpinner;
    EditText inputTitle, inputDescription, inputPickupDetails;
    TextView locationView;
    Button submitButton;
    ImageButton cameraButton, fileButton;
    ImageView itemImage;
    ProgressBar progressBar;
    private Uri mImageUri = null;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    LocationManager locationManager;
    Double latitude=null,longitude=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        inputTitle = (EditText) findViewById(R.id.title);
        inputDescription = (EditText) findViewById(R.id.description);
        inputPickupDetails = (EditText) findViewById(R.id.pickupDetails);
        categorySpinner = (Spinner) findViewById(R.id.category);
        expireSpinner = (Spinner) findViewById(R.id.expiration);
        locationView = (TextView) findViewById(R.id.location);
        submitButton = (Button) findViewById(R.id.button1);
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        fileButton = (ImageButton) findViewById(R.id.fileButton);
        itemImage = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        mStorageRef = FirebaseStorage.getInstance().getReference().child("foodItems");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Items");

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                uploadFile();
                progressBar.setVisibility(v.VISIBLE);
            }
        });

    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    itemImage.setImageBitmap(imageBitmap);
                    mImageUri = imageReturnedIntent.getData();
//                    Toast.makeText(AddItemActivity.this, "Added photo.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    itemImage.setImageURI(selectedImage);
                    mImageUri = imageReturnedIntent.getData();
                }
                break;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            //fileReference
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String title = inputTitle.getText().toString();
                            String description = inputDescription.getText().toString();
                            String pickupDetails = inputPickupDetails.getText().toString();
                            String category = categorySpinner.getSelectedItem().toString();
                            String expireTime = expireSpinner.getSelectedItem().toString();
                            Date uploadTime = Calendar.getInstance().getTime();

                            String url = uri.toString();
                            Map<String, Object> item = new HashMap<>();
                            item.put("title", title);
                            item.put("photoUrl",url);
                            item.put("description",description);
                            item.put("pickupDetails",pickupDetails);
                            item.put("category",category);
                            item.put("expirePeriod",expireTime);
                            item.put("uploadTime",uploadTime);
                            item.put("uploadLatitude",latitude);
                            item.put("uploadLongitude",longitude);
                            item.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            db.collection("Items").document()
                                    .set(item)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "DocumentSnapshot successfully written!");
                                            Intent intent = new Intent(AddItemActivity.this, DonatedActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error writing document", e);
                                        }
                                    });
                        }
                    });
                    Toast.makeText(AddItemActivity.this,"Upload Successful",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddItemActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText(this,"No File Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(AddItemActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
