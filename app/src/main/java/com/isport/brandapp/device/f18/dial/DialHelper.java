package com.isport.brandapp.device.f18.dial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.isport.brandapp.device.f18.view.GetImgForBitmapListener;

public class DialHelper {


    public static void getImageBitmap(Context context, Uri uri, GetImgForBitmapListener getImgForBitmapListener){

        Glide.with(context).asBitmap().load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                       if(getImgForBitmapListener != null)
                           getImgForBitmapListener.backImgBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable drawable) {

                    }
                });
    }
}
