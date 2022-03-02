package brandapp.isport.com.basicres.commonutil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.isport.brandapp.basicres.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseApp;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by Bravo  图片加载的基类。
 */
public class LoadImageUtil {

    private static LoadImageUtil instance;

    public static LoadImageUtil getInstance() {
        if (null == instance) {
            synchronized (LoadImageUtil.class) {
                if (null == instance) {
                    instance = new LoadImageUtil();
                }
            }
        }
        return instance;
    }

    public void loadCirc(Context ctx, String url, final ImageView iv) {
        if (ctx == null) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop().placeholder(R.drawable.icon_def).error(R.drawable.icon_def).dontAnimate();
            Glide.with(ctx).load(url).apply(options).into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void loadCover(Context ctx, String url, final ImageView iv) {
        if (ctx == null) {
            return;
        }
        try {
            Logger.e("loadCover", "url=" + url);
            Glide.with(ctx).asBitmap().load(url).addListener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    int imageWidth = resource.getWidth();
                    int imageHeight = resource.getHeight();

                    Logger.e("loadCover", "imageWidth=" + imageWidth + ",imageHeight=" + imageHeight);

                    // int height = ScreenUtils.getScreenWidth() * imageHeight / imageWidth;
                    ViewGroup.LayoutParams para = iv.getLayoutParams();
                    if (para != null) {
                        if (imageWidth >= imageHeight) {
                            RequestOptions options = new RequestOptions();
                            options.centerInside().dontAnimate();
                            Glide.with(BaseApp.getApp()).load(url).apply(options).into(iv);
                            /*para.height = imageHeight;
                            para.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            iv.setLayoutParams(para);*/
                        } else {
                            RequestOptions options = new RequestOptions();
                            options.centerCrop().dontAnimate();
                            Glide.with(BaseApp.getApp()).load(url).apply(options).into(iv);
                         /*   para.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            para.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            iv.setLayoutParams(para);*/
                        }

                    }

                }
            });
           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.icon_defs)
                    .error(R.drawable.icon_defs)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {

        }

    }

    public void loadCover(Context ctx, String url, final ImageView iv, int height, int with) {
        if (ctx == null) {
            return;
        }
        try {

            //MultiTransformation mation4 = new MultiTransformation(new BlurTransformation(90));
           /* RequestOptions options3 = new RequestOptions();
            options3.centerCrop().bi;*/

            /*RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .bitmapTransform(mation4);*/

           /* Glide.with(ctx)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(mation4).override(with, height))
                    // .apply(RequestOptions.bitmapTransform(mation4))
                    .into(iv);
*/
            Glide.with(ctx)
                    .load(url).apply(new RequestOptions()
                    .transform(new GlideBlurTransformation(ctx))).into(iv);


            /*RequestOptions options = new RequestOptions();
            options.centerCrop().dontAnimate();
            Glide.with(ctx).load(url).apply(options).into(iv);*/

           /* Glide.with(this).load(R.drawable.demo)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(imageView)*/


           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception ex) {

        }
    }

    public void loadPlayer(Context ctx, String url, final ImageView iv) {
        if (ctx == null) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop().placeholder(R.drawable.bg_no_play).error(R.drawable.bg_no_play).dontAnimate();
            Glide.with(ctx).load(url).apply(options).into(iv);
           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.bg_no_play)
                    .error(R.drawable.bg_no_play)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCirc(Context ctx, int url, final ImageView iv, int roundCirc) {
        if (ctx == null) {
            return;
        }
        try {


            /**
             * 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
             */
            MultiTransformation mation5 = new MultiTransformation(
                    new RoundedCornersTransformation(roundCirc, 0, RoundedCornersTransformation.CornerType.ALL));
            RequestOptions myOptions = new RequestOptions().transform(new GlideCerterTransformation(ctx, roundCirc));

            Glide.with(ctx)
                    .load(url)
                    .apply(myOptions
                            // 圆角图片
                    ).into(iv);
            // .apply(RequestOptions.bitmapTransform(mation5).centerCrop())


           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.icon_defs)
                    .transform(new CenterCrop(ctx), new GlideRoundTransform(ctx, roundCirc))
                    .error(R.drawable.icon_defs)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCircs(Context ctx, String url, final ImageView iv, int roundCirc, int res) {
        if (ctx == null) {
            return;
        }
        try {


            /**
             * 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
             */
            MultiTransformation mation5 = new MultiTransformation(
                    new RoundedCornersTransformation(roundCirc, 0, RoundedCornersTransformation.CornerType.ALL));
            RequestOptions myOptions = new RequestOptions().transform(new GlideCerterTransformation(ctx, roundCirc));

            Glide.with(ctx)
                    .load(url)
                    .apply(myOptions
                            // 圆角图片
                    ).error(res).placeholder(res).into(iv);
            // .apply(RequestOptions.bitmapTransform(mation5).centerCrop())


           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.icon_defs)
                    .transform(new CenterCrop(ctx), new GlideRoundTransform(ctx, roundCirc))
                    .error(R.drawable.icon_defs)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {

        }
    }

    public void loadCircs(Context ctx, String url, final ImageView iv, int roundCirc) {
        if (ctx == null) {
            return;
        }
        try {


            /**
             * 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
             */
            MultiTransformation mation5 = new MultiTransformation(
                    new RoundedCornersTransformation(roundCirc, 0, RoundedCornersTransformation.CornerType.ALL));
            RequestOptions myOptions = new RequestOptions().transform(new GlideCerterTransformation(ctx, roundCirc));

            Glide.with(ctx)
                    .load(url)
                    .apply(myOptions
                            // 圆角图片
                    ).into(iv);
            // .apply(RequestOptions.bitmapTransform(mation5).centerCrop())


           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.icon_defs)
                    .transform(new CenterCrop(ctx), new GlideRoundTransform(ctx, roundCirc))
                    .error(R.drawable.icon_defs)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {

        }
    }

    public void loadCirc(Context ctx, File url, final ImageView iv, int roundCirc) {
        if (ctx == null) {
            return;
        }
        try {


            /**
             * 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
             */
            MultiTransformation mation5 = new MultiTransformation(
                    new RoundedCornersTransformation(roundCirc, 0, RoundedCornersTransformation.CornerType.ALL));
            RequestOptions myOptions = new RequestOptions().transform(new GlideCerterTransformation(ctx, roundCirc));

            Glide.with(ctx)
                    .load(url)
                    .apply(myOptions
                            // 圆角图片
                    ).into(iv);
            // .apply(RequestOptions.bitmapTransform(mation5).centerCrop())


           /* Glide.with(ctx).load(url)
                    .centerCrop()
                    .placeholder(R.drawable.icon_defs)
                    .transform(new CenterCrop(ctx), new GlideRoundTransform(ctx, roundCirc))
                    .error(R.drawable.icon_defs)
                    .dontAnimate()
                    *//*   .skipMemoryCache(false)
                       .priority(Priority.LOW)
                       .diskCacheStrategy(DiskCacheStrategy.ALL)*//*
                    .into(iv);
               *//* .into(new SimpleTarget<GlideDrawable>() { // 加上这段代码 可以解决
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        iv.setImageDrawable(resource); //显示图片
                    }
                });*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isDestroy(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @Description: 加载本地文件方式
     */
    public static void displayImagePath(Context activity, String path, final ImageView imageView) {
        if (isDestroy((Activity) activity)) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(activity).
                    load("file://" + path).apply(options)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadCirc(Context ctx, String url, ImageView iv, int errorResId) {
        if (isDestroy((Activity) ctx)) {
            return;
        }
        try {
            iv.setTag(iv.getId(), url);
            RequestOptions options = new RequestOptions();
            options.centerCrop()
                    .placeholder(errorResId)
                    .error(errorResId)
                    .dontAnimate();
            Glide.with(ctx)
                    .load(url)
                    .apply(options)
                    .into(iv);
        } catch (Exception e) {

        }
    }

    public void loadCirc(Context ctx, String url, ImageView iv, int placeholderResId, int errorResId) {
        if (isDestroy((Activity) ctx)) {
            return;
        }
        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop()
                    .placeholder(errorResId)
                    .error(errorResId)
                    .dontAnimate();
            Glide.with(ctx)
                    .load(url)
                    .apply(options)
                    .into(iv);
        } catch (Exception e) {

        }
    }


    public void loadGif(Context ctx, String url, final ImageView imageView) {
        if (isDestroy((Activity) ctx)) {
            return;
        }
        try {

            RequestOptions options = new RequestOptions();
            options
                    .placeholder(R.drawable.bg_no_play)
                    .error(R.drawable.bg_no_play)
            ;
            Glide.with(ctx)
                    .load(url)
                    .apply(options)
                    .into(imageView);

            //  Glide.with(ctx).load(url).placeholder(R.drawable.bg_no_play).error(R.drawable.bg_no_play).into(new GlideDrawableImageViewTarget(imageView, 100)); //加载一次
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadGif(Context ctx, int url, final ImageView imageView) {
        if (ctx == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(ctx)
                .load(url)
                .apply(options)
                .into(imageView);

      /*  Glide.with(ctx).load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable)resource).setLoopCount(1);
                }
                return false;
            }
        }).into(imageView);*/

        // Glide.with(ctx).load(url).into(new GlideDrawableImageViewTarget(imageView, 1)); //加载一次
       /* Glide.with(ctx).asGif().load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable) resource).setLoopCount(1);
                }
                return false;
            }

        }).into(iv);*/
    }

    public void loadGifHr(Context ctx, int url, final ImageView imageView) {
        if (ctx == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        Glide.with(ctx)
                .load(url)
                .apply(options)
                .into(imageView);

      /*  Glide.with(ctx).load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable)resource).setLoopCount(1);
                }
                return false;
            }
        }).into(imageView);*/

        // Glide.with(ctx).load(url).into(new GlideDrawableImageViewTarget(imageView, 1)); //加载一次
       /* Glide.with(ctx).asGif().load(url).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable) resource).setLoopCount(1);
                }
                return false;
            }

        }).into(iv);*/
    }


}