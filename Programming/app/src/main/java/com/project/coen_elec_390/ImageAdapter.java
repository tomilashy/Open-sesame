package com.project.coen_elec_390;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<ImageInfo> listImageInfos;

    public ImageAdapter(Context context, List<ImageInfo> imageInfos) {
        mContext = context;
        listImageInfos = imageInfos;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImageInfo uploadCurrent = listImageInfos.get(position);
        holder.textViewName.setText(uploadCurrent.getImageName());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .fit()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listImageInfos.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
