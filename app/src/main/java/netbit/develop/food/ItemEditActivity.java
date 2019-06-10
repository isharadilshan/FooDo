package netbit.develop.food;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import netbit.develop.food.Model.Item;

public class ItemEditActivity extends AppCompatActivity {

    TextView title,description,pickupDetails,addedTime,category,location,time;
    ImageView imageView;
    Button deleteBtn,editBtn;
    String city;
    FirebaseFirestore db;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        title = (TextView) findViewById(R.id.food_title);
        description = (TextView) findViewById(R.id.food_description);
        pickupDetails = (TextView) findViewById(R.id.food_pickupDetails);
        imageView = (ImageView) findViewById(R.id.food_image);
        deleteBtn = (Button) findViewById(R.id.buttonDelete);
        db = FirebaseFirestore.getInstance();

        title.setText("   "+getIntent().getExtras().getString("title"));
//        description.setText("   "+getIntent().getExtras().getString("description"));
//        pickupDetails.setText("   "+getIntent().getExtras().getString("pickupDetails"));
        Picasso.with(this).load(Uri.parse(getIntent().getExtras().getString("image"))).into(imageView);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Items").document(getIntent().getExtras().getString("id"))
                        .delete().addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ItemEditActivity.this, "Item deleted !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ItemEditActivity.this,DonatedActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });
    }
}
