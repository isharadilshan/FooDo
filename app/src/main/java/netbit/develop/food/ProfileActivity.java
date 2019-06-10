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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements LocationListener,View.OnClickListener {

    EditText userName, phoneNumber;
    Double latitude = null, longitude = null;
    ImageView profileImage;
    ImageButton cameraImage;
    Uri uriProfileImage;
    ProgressBar progressBar;
    StorageReference mStorageRef;
    FirebaseAuth mAuth;
//    TextView verifyView;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.editTextUserName);
        phoneNumber = findViewById(R.id.editTextUserPhone);
        profileImage = findViewById(R.id.profileImage);
        cameraImage = findViewById(R.id.cameraBtn);
        progressBar = findViewById(R.id.progressBarProfile);
//        verifyView = findViewById(R.id.textViewVerified);
        findViewById(R.id.textViewSetLocation).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
                profileImage.setImageURI(uriProfileImage);
            }
        });

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });

        profileImage.setImageURI(uriProfileImage);

        final FirebaseUser user = mAuth.getCurrentUser();
//        if(user.isEmailVerified()){
//            verifyView.setText("Email Verified");
//        }else{
//            verifyView.setText("Email is not Verified,Click here to verify");
//            verifyView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Toast.makeText(ProfileActivity.this,"Verification Email Sent",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//        }

        //check permission runtime
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                getLocation();

                if (uriProfileImage != null) {

                    final String name = userName.getText().toString();
                    final String phone = phoneNumber.getText().toString();

                    UserProfileChangeRequest profileUpdateImage = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uriProfileImage).build();
                    UserProfileChangeRequest profileUpdateName = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    user.updateProfile(profileUpdateImage);
                    user.updateProfile(profileUpdateName);

                    //storageReference
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("foodItems");
                    final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uriProfileImage));

                    //fileReference
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    fileReference.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    validateInputs();

                                    String url = uri.toString();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("userName", name);
                                    user.put("phoneNumber", phone);
                                    user.put("currentLatitude", latitude);
                                    user.put("currentLongitude", longitude);
                                    user.put("profileImageUrl", url);

                                    db.collection("UserProfiles").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ProfileActivity.this, "Successfully Profile data added", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                                                    finish();
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
                            Toast.makeText(ProfileActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "No File Selected", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validateInputs(){

        String name = userName.getText().toString();
        String phone = phoneNumber.getText().toString();
        if(name.isEmpty()){
            userName.setError("Name Required");
            userName.requestFocus();
            return;
        }
        if(name.length()<5){
            userName.setError("Please Enter Proper User Name");
            userName.requestFocus();
            return;
        }
        if(!Patterns.PHONE.matcher(phone).matches()){
            phoneNumber.setError("Please Enter a valid Phone Number");
            phoneNumber.requestFocus();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            finish();startActivity(new Intent(this,LoginActivity.class));
        }
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

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK && data != null && data.getData() != null){
                    uriProfileImage = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                        profileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            case 1:
                if(resultCode == RESULT_OK && data != null && data.getData() != null){
                    uriProfileImage = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                        profileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ProfileActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewSetLocation:
                finish();
                startActivity(new Intent(this,LocationActivity.class));
                break;
        }
    }
}
