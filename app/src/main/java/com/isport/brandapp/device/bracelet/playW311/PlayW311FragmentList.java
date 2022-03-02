package com.isport.brandapp.device.bracelet.playW311;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.device.bracelet.playW311.bean.PlayBean;
import com.isport.brandapp.util.UserAcacheUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import brandapp.isport.com.basicres.commonutil.AppUtil;
import brandapp.isport.com.basicres.commonutil.MessageEvent;
import brandapp.isport.com.basicres.service.observe.TodayObservable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class PlayW311FragmentList extends Fragment {

    private static final String TAG = "PlayW311FragmentList";

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;
    public static final int TYPE_FOUR = 3;
    private int type;

    ArrayList<PlayW311Fragment> fragments;

    List<PlayBean> playBeans;

    PlayBean playBean1 = null, playBean2 = null, playBean3 = null, playBean4 = null;

    private ViewPager viewPager;

    private FragmentStatePagerAdapter pagerAdapter;
    private int currentType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentType = getArguments().getInt(JkConfiguration.DEVICE, JkConfiguration.DeviceType.BRAND_W311);
        playBeans = UserAcacheUtil.getPlayBandImagelist(currentType);
        Logger.myLog("PlayW311FragmentList","currentType:" + currentType + "playBeans.size()" + playBeans.size() + "playBeans:" + playBeans);
        if (playBeans.size() == 4) {
            playBean1 = playBeans.get(0);
            playBean2 = playBeans.get(1);
            playBean3 = playBeans.get(2);
            playBean4 = playBeans.get(3);
        }
        TodayObservable.getInstance().cheackType(TYPE_ONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container,false);
        viewPager = (ViewPager) view;

        pagerAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                Fragment fragment = new PlayW311Fragment();
                Bundle bundle = new Bundle();
                String strBottom0 = "";
                if (position < playBeans.size()) {
                    if (AppUtil.isZh(getActivity())) {
                        bundle.putString("title", playBeans.get(position).getTitle1());
                        bundle.putString("content", playBeans.get(position).getTitle1Content1());
                        strBottom0 = playBeans.get(position).getTitle1Content2();
                    } else {
                        bundle.putString("title", playBeans.get(position).getTitleEn1());
                        bundle.putString("content", playBeans.get(position).getTitleEn1Content1());
                        strBottom0 = playBeans.get(position).getTitleEn1Content2();
                    }

                    if (currentType == JkConfiguration.DeviceType.WATCH_W516 || currentType == 70180) {
                        if (AppUtil.isZh(getActivity())) {
                            bundle.putString("strRes", playBeans.get(position).getUrl1());
                        } else {
                            bundle.putString("strRes", playBeans.get(position).getUrlEn1());
                        }
                    } else {
                        bundle.putString("strRes", playBeans.get(position).getUrlEn1());
                    }

                }
                if (TextUtils.isEmpty(strBottom0)) {
                    strBottom0 = "";
                }
                bundle.putInt("position", 0);
                bundle.putInt("currentType", currentType);
                bundle.putString("strbottom", strBottom0);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return playBeans.size();
            }
        };


        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TodayObservable.getInstance().cheackType(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
    }


}
