package com.isport.brandapp.device.bracelet.playW311;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.brandapp.R;

import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseFragment;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class PlayW311Fragment extends BaseFragment {
    TextView tvTitle, tvContent, tvBottom;
    ImageView ivPic;

    String title, content, strBottem;
    int positon;
    String strUrl;
    private int deviceType;

    @Override
    protected int getLayoutId() {
        return R.layout.app_fragment_play_w311_layout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        strUrl = getArguments().getString("strRes");
        content = getArguments().getString("content");
        strBottem = getArguments().getString("strbottom");
        deviceType = getArguments().getInt("currentType", JkConfiguration.DeviceType.WATCH_W516);
        positon = getArguments().getInt("position", 0);
    }

    @Override
    protected void initView(View view) {

        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_conttent);
        tvBottom = view.findViewById(R.id.tv_bottom);
        ivPic = view.findViewById(R.id.iv_pic);
        ivPic.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {

        tvBottom.setText(strBottem);
        tvContent.setText(content);
        tvTitle.setText(title);
        if (positon == 3) {
            try {
                LoadImageUtil.getInstance().loadPlayer(getActivity(), strUrl, ivPic);
            } catch (Exception e) {

            }
            // ivPic.setImageResource(res);
        } else {
            try {
                if (deviceType == JkConfiguration.DeviceType.WATCH_W516) {
                    LoadImageUtil.getInstance().loadPlayer(getActivity(), strUrl, ivPic);
                } else {
                    LoadImageUtil.getInstance().loadGif(getActivity(), strUrl, ivPic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //  GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
            //   gifDrawable.start();
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initImmersionBar() {

    }
}
