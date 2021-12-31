package bike.gymproject.viewlibray.pickerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bike.gymproject.viewlibray.R;
import bike.gymproject.viewlibray.pickerview.adapter.ArrayWheelAdapter;
import bike.gymproject.viewlibray.pickerview.listener.OnItemSelectedListener;
import bike.gymproject.viewlibray.pickerview.view.WheelView;

/*
 *
 * classes : com.jkcq.gym.view.pickerview
 * @author 苗恒聚
 * V 1.0.0
 * Create at 2016/10/22 16:46
 */
public class ArrayPickerView extends LinearLayout {

    private WheelView arrayWheelView;
    private ItemSelectedValue itemSelectedValue;

    /**
     * 设置是否循环滚动
     */
    private boolean cyclic = true;

    private List<String> listSource = new ArrayList<>();

    public ArrayPickerView(Context context) {
        this(context, null);
    }

    public ArrayPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrayPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initBase(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArrayPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initBase(context, attrs, defStyleAttr);
    }

    private void initBase(Context context, AttributeSet attrs, int defStyleAttr) {
        initView();
        setListener();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_picker_array, this, true);

        arrayWheelView = (WheelView) findViewById(R.id.array_picker);
    }

    private void initData() {

    }

    public void setCyclic(boolean cyclic) {
        this.cyclic = cyclic;
        arrayWheelView.setCyclic(cyclic);
    }

    public void setSelectItem(int index) {
        arrayWheelView.setCurrentItem(index);
    }

    public void setData(String[] items) {
        listSource = new ArrayList<>();
        listSource.addAll(Arrays.asList(items));
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>((ArrayList<String>) listSource);
        arrayWheelView.setAdapter(arrayWheelAdapter);
    }

    public void setItemOnclick(ItemSelectedValue itemOnclick) {
        itemSelectedValue = itemOnclick;
    }

    public void setData(ArrayList<String> items) {
        listSource.clear();
        listSource.addAll(items);
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(items);
        arrayWheelView.setAdapter(arrayWheelAdapter);
    }

    private void setListener() {
        arrayWheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (itemSelectedValue != null) {
                    itemSelectedValue.onItemSelectedValue(getItem());
                }
            }
        });
    }


    public interface ItemSelectedValue {
        public void onItemSelectedValue(String str);
    }

    public int getCurrentItem() {
        return arrayWheelView.getCurrentItem();
    }

    public String getItem() {
        if (null == listSource || listSource.isEmpty()) {
            return null;
        }
        int position = arrayWheelView.getCurrentItem();
        if (position < 0 || position >= listSource.size()) {
            return null;
        }

        return listSource.get(position);
    }
}