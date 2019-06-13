package netbit.develop.food.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import netbit.develop.food.Model.User;
import netbit.develop.food.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        User user = mUsers.get(i);
        viewHolder.username.setText(user.getName());
        Glide.with(mContext).load(user.getPhotoUrl()).into(viewHolder.profileImage);
//        if(user.getPhotoUrl().equals("default")){
//            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
//        }else{
//            Glide.with(mContext).load(user.getPhotoUrl()).into(viewHolder.profileImage);
//        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profileImage;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username_chat);
            profileImage = itemView.findViewById(R.id.profile_image_chat);

        }
    }

}
