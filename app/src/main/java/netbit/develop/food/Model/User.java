package netbit.develop.food.Model;

import android.net.Uri;

public class User {

    private String id;
    private String email;
    private Uri photoUrl;
    private String name;

    public User(){

    }

    public User(String id, String email, Uri photoUrl,String name){

        this.id = id;
        this.email = email;
        this.photoUrl = photoUrl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }
}
