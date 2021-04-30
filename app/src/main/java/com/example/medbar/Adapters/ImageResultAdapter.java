package com.example.medbar.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.medbar.Objects.ImageResult;
import com.example.medbar.R;
import com.example.medbar.ViewLabtests;
import com.example.medbar.ViewResults;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageResultAdapter extends RecyclerView.Adapter<com.example.medbar.Adapters.ImageResultAdapter.MyViewHolder> {

    ArrayList<ImageResult> list;
    Context context;


    public ImageResultAdapter(ArrayList<ImageResult> list, Context context) {

        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public com.example.medbar.Adapters.ImageResultAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_holder, parent, false);
        return new com.example.medbar.Adapters.ImageResultAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.medbar.Adapters.ImageResultAdapter.MyViewHolder holder, final int i) {

        Glide.with(context).load(list.get(i).getLink()).into(holder.imageBtn);

        holder.displayFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayFullImage(list.get(i).getLink());


            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBtn;
        Button displayFull;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBtn = itemView.findViewById(R.id.imageView6);
            displayFull = itemView.findViewById(R.id.displayFull);


        }
    }


    public void displayFullImage(String link) {


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_display_fullimage, null, false);

            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.setContentView(view);
            final Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);



           ImageView image = dialog.findViewById(R.id.imageFullSize);
            Glide.with(context).load(link).into(image);

            ImageButton dismiss = (ImageButton) dialog.findViewById(R.id.dismissView);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }
            });
            dialog.show();
    }


}
