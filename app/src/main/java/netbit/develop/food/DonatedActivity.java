package netbit.develop.food;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import netbit.develop.food.Model.Item;

public class DonatedActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Item, DonatedActivity.ItemViewHolder> adapter;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donated);

        //Load Images to home page
        RecyclerView recyclerView = findViewById(R.id.recycler_added_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Items").whereEqualTo("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Item, DonatedActivity.ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DonatedActivity.ItemViewHolder holder , final int position, @NonNull final Item productModel) {
                holder.setTitle(productModel.getTitle());
                holder.setImage(getBaseContext(),productModel.getPhotoUrl());
                holder.setCategory(productModel.getCategory());
//                holder.setTime(productModel.getExpirePeriod());
                holder.relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.getSnapshots().getSnapshot(position);
                        id = adapter.getSnapshots().getSnapshot(position).getId();

                        Intent intent = new Intent(DonatedActivity.this,ItemEditActivity.class);
                        intent.putExtra("id",id);
                        intent.putExtra("title",productModel.getTitle());
                        intent.putExtra("description",productModel.getDescription());
                        intent.putExtra("pickupDetails",productModel.getPickupDetails());
//                        intent.putExtra("category",productModel.getCategory());
                        intent.putExtra("image",productModel.getPhotoUrl());
//                        intent.putExtra("uploadLatitude",productModel.getUploadLatitude());
//                        intent.putExtra("uploadLongitude",productModel.getUploadLongitude());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public DonatedActivity.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_items, parent, false);
                return new DonatedActivity.ItemViewHolder(view);
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
            relative = itemView.findViewById(R.id.menu_added_items);
        }
        void setTitle(String title) {
            TextView textViewTitle = view.findViewById(R.id.item_title);
            textViewTitle.setText(title);
        }
        void setImage(Context ctx, String photoUrl){
            ImageView itemImage = view.findViewById(R.id.item_image);
            Picasso.with(ctx).load(photoUrl).into(itemImage);
        }
        void setCategory(String category){
            TextView textViewCategory = view.findViewById(R.id.item_category);
            textViewCategory.setText(category);
        }
//        void setTime(String expireTime){
//            TextView textViewExpire = view.findViewById(R.id.item_time);
//            textViewExpire.setText(expireTime);
//        }
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
}
