package com.project.coen_elec_390;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<Profile> profiles;

    public ProfileAdapter(Context context, ArrayList<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public ProfileAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileAdapter.ImageViewHolder holder, int index) {
        Profile profile = profiles.get(index);
        holder.usernameTextView.setText(profile.getUsername());
        holder.emailTextView.setText(profile.getEmail());
        Picasso.with(context)
                .load(profile.getImageUrl())
                .fit()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView emailTextView;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            usernameTextView = itemView.findViewById(R.id.text_view_username);
            emailTextView = itemView.findViewById(R.id.text_view_email);
            imageView = itemView.findViewById(R.id.image_view_profile_image);
        }
    }

}
