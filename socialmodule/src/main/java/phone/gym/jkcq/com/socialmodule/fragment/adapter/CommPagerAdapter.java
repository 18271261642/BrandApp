package phone.gym.jkcq.com.socialmodule.fragment.adapter;

import android.os.Parcelable;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * create by libo
 * create on 2020/5/19
 * description 公共viewPageradapter
 */
public class CommPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<? extends Fragment> items;
    private String[] mTitles;

    public CommPagerAdapter(FragmentManager fm, ArrayList< ? extends Fragment> items, String[] mTitles) {
        super(fm);
        this.items = items;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return items.size()==0 ? 0 :items.size();
    }

    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;

    }
    @Override
    public Parcelable saveState() {
        return null;
    }
}