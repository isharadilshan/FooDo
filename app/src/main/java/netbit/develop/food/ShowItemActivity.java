package netbit.develop.food;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowItemActivity extends AppCompatActivity {

    TextView title,description,pickupDetails,addedTime,category,location,time;
    ImageView imageView;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        title = (TextView) findViewById(R.id.foodTitle);
        description = (TextView) findViewById(R.id.foodDescription);
        pickupDetails = (TextView) findViewById(R.id.foodPickupDetails);
        addedTime = (TextView) findViewById(R.id.foodAddedTime);
        category = (TextView) findViewById(R.id.foodCategory);
        location = (TextView) findViewById(R.id.foodLocation);
        imageView = (ImageView) findViewById(R.id.foodImageView);
        time = (TextView) findViewById(R.id.foodAddedTime);

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            if(getIntent().getExtras().getString("uploadLatitude")==null | getIntent().getExtras().getString("uploadLongitude")==null){
                Toast.makeText(ShowItemActivity.this, "This item not added the upload location", Toast.LENGTH_SHORT).show();
            }else {
                addresses = geocoder.getFromLocation(Double.parseDouble(getIntent().getExtras().getString("uploadLongitude")), Double.parseDouble(getIntent().getExtras().getString("uploadLongitude")), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                city = addresses.get(0).getLocality();
            }
            } catch (IOException e) {
            e.printStackTrace();
        }

//        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); //

        title.setText("   "+getIntent().getExtras().getString("title"));
        description.setText("   "+getIntent().getExtras().getString("description"));
        pickupDetails.setText("   "+getIntent().getExtras().getString("pickupDetails"));
        category.setText("   "+getIntent().getExtras().getString("category"));
        location.setText("   "+getIntent().getExtras().getString("uploadLatitude"));
        time.setText("   "+getIntent().getExtras().getString("uploadTime")+"and expired within"+getIntent().getExtras().getString("expirePeriod"));
        Picasso.with(this).load(Uri.parse(getIntent().getExtras().getString("image"))).into(imageView);

    }
}
