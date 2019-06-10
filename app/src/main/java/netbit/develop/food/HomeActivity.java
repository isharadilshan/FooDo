package netbit.develop.food;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import netbit.develop.food.Model.Item;
import netbit.develop.food.Model.User;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirestoreRecyclerAdapter<Item, ItemViewHolder> adapter;

    String userName,userEmail;
    Uri userPhoto;
    TextView txtUserName,txtUserEmail;
    ImageView userImage;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
            startActivity(intent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        if (acct != null) {

            userName = acct.getDisplayName();
            userEmail = acct.getEmail();
            userPhoto = acct.getPhotoUrl();

        }else{

            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("UserProfiles").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    Toast.makeText(getApplicationContext(),"else part is working",Toast.LENGTH_SHORT).show();

                    userName = documentSnapshot.getString("userName");
                    String photo  = documentSnapshot.getString("profileImageUrl");
                    userPhoto = Uri.parse(photo);
                    userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    final View headerView = navigationView.getHeaderView(0);
                    txtUserName = (TextView)headerView.findViewById(R.id.userName);
                    txtUserName.setText(userName);
                    txtUserEmail = (TextView) headerView.findViewById(R.id.userEmail);
                    txtUserEmail.setText(userEmail);
                    userImage = (ImageView) headerView.findViewById(R.id.userImage);
//                    Picasso.with(HomeActivity.this).load(userPhoto).into(userImage);

                }
            });

        }

//        final View headerView = navigationView.getHeaderView(0);
//        txtUserName = (TextView)headerView.findViewById(R.id.userName);
//        txtUserName.setText(userName);
//        txtUserEmail = (TextView) headerView.findViewById(R.id.userEmail);
//        txtUserEmail.setText(userEmail);
//        userImage = (ImageView) headerView.findViewById(R.id.userImage);
//        Picasso.with(this).load(userPhoto).into(userImage);

        //Load Images to home page
        RecyclerView recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Items");

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Item, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder , final int position, @NonNull final Item productModel) {
                holder.setTitle(productModel.getTitle());
                holder.setImage(getBaseContext(),productModel.getPhotoUrl());
                holder.setCategory(productModel.getCategory());
                holder.setTime(productModel.getExpirePeriod());
                holder.relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this,ShowItemActivity.class);
                        intent.putExtra("title",productModel.getTitle());
                        intent.putExtra("description",productModel.getDescription());
                        intent.putExtra("pickupDetails",productModel.getPickupDetails());
                        intent.putExtra("category",productModel.getCategory());
                        intent.putExtra("image",productModel.getPhotoUrl());
                        intent.putExtra("uploadLatitude",productModel.getUploadLatitude());
//                        intent.putExtra("uploadLongitude",productModel.getUploadLongitude());
                        intent.putExtra("uploadTime",productModel.getUploadTime());
                        intent.putExtra("expirePeriod",productModel.getExpirePeriod());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
                return new ItemViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private View view;
        RelativeLayout relative;

        ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            relative = itemView.findViewById(R.id.menu_card);

        }

        void setTitle(String title) {
            TextView textViewTitle = view.findViewById(R.id.item_title);
            textViewTitle.setText(title);
        }
        void setImage(Context ctx,String photoUrl){
            ImageView itemImage = view.findViewById(R.id.item_image);
            Picasso.with(ctx).load(photoUrl).into(itemImage);
        }
        void setCategory(String category){
            TextView textViewCategory = view.findViewById(R.id.item_category);
            textViewCategory.setText(category);
        }
        void setTime(String expireTime){
            TextView textViewExpire = view.findViewById(R.id.item_time);
            textViewExpire.setText(expireTime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_donations) {
            Intent intent = new Intent(HomeActivity.this,DonatedActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(this,ChatActivity.class));

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this,LocationActivity.class));

        } else if (id == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            // Google sign out
            mGoogleSignInClient.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
