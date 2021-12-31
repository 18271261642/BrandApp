package brandapp.isport.com.basicres.commonview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.isport.brandapp.basicres.R;

import java.util.ArrayList;

import bike.gymproject.viewlibray.pickerview.ArrayPickerView;
import bike.gymproject.viewlibray.pickerview.ArrayPickerView.ItemSelectedValue;
import bike.gymproject.viewlibray.pickerview.DatePickerView;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.mvp.BasePresenter;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * Created by huashao on 2017/10/30.
 */

public class UserDialogSetting extends BasePresenter<UserDialogView> implements
        ItemSelectedValue {
    UserDialogView dialogview;

    public UserDialogSetting(UserDialogView view) {
        this.dialogview = view;
    }

    private View mMenuView;
    private String localData[];
    private String localDataNoUnit[];
    private String genderDatas[] = new String[]{"男", "女"};
    private String heightData[] = new String[101]; // 身高
    private String heightDataNoUnit[] = new String[101]; // 身高不带单位
    private String weightData[] = new String[301];    // 体重
    private String weightDataNoUnit[] = new String[301];    // 体重不带单位
    private PopupWindow popupWindow;
    private String localChooseStr;
    private String selectType;
    private int showIndex;


    /**
     * @param context
     * @param view
     * @param type
     * @param lastData 上次的数据
     */
    public void popWindowSelect(Context context, View view, String type, String lastData, boolean isCyclic) {
//        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
//        int checkOp = appOpsManager.checkOp(AppOpsManager.OPSTR_CAMERA, android.os.Process.myUid(), getPackageName());
        this.selectType = type;
        switch (type) {
            case JkConfiguration.GymUserInfo.GENDER:
                genderDatas[0] = context.getString(R.string.gender_male);
                genderDatas[1] = context.getString(R.string.gender_female);
                localData = genderDatas;
                localDataNoUnit = genderDatas;
                for (int i = 0; i < genderDatas.length; i++) {

                    if (genderDatas[i].equals(lastData)) {
                        showIndex = i;
                    }
                }
                break;
            case JkConfiguration.GymUserInfo.HEIGHT:
                for (int j = 0; j < 101; j++) {
                    heightDataNoUnit[j] = String.valueOf(j + 140);
                    heightData[j] = j + 140 + " cm";
                    if (heightData[j].equals(lastData)) {
                        showIndex = j;
                    }
                }
                localData = heightData;
                localDataNoUnit = heightDataNoUnit;
                break;
            case JkConfiguration.GymUserInfo.WEIGHT:
                for (int j = 0; j < 301; j++) {
                    weightDataNoUnit[j] = String.valueOf(j);
                    weightData[j] = j + " kg";
                    if (weightData[j].equals(lastData)) {
                        showIndex = j;
                    }
                }
                localData = weightData;
                localDataNoUnit = weightDataNoUnit;
                break;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.app_pop_bottom_setting, null);
        TextView tv_determine = (TextView) mMenuView.findViewById(R.id.tv_determine);
        TextView tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        ArrayPickerView datePicker = (ArrayPickerView) mMenuView.findViewById(R.id.datePicker);

        datePicker.setData(localData);
        datePicker.setCyclic(isCyclic);
        datePicker.setItemOnclick(this);
        datePicker.setSelectItem(showIndex);
        /* qp_select_bottom.setOnValueChangedListener(this);
        qp_select_bottom
                .setDescendantFocusability(QNumberPickerBlackLine.FOCUS_BLOCK_DESCENDANTS);
        qp_select_bottom.setDisplayedValues(localData);
        qp_select_bottom.setMinValue(0);
        qp_select_bottom.setMaxValue(localData.length - 1);
        qp_select_bottom.setValue(showIndex);
        qp_select_bottom.setWrapSelectorWheel(false);
        qp_select_bottom.setNumberPickerDividerColor(qp_select_bottom);*/
        localChooseStr = localData[showIndex];
//        localChooseStr = localDataNoUnit[showIndex];
        popupWindow = new PopupWindow(context);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(mMenuView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.showAtLocation(view, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);

        if(mMenuView == null)
            return;
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (mMenuView == null) {
                    return false;
                }
                View pV = mMenuView.findViewById(R.id.pop_layout);
                if(pV == null )
                    return false;
                int height = pV != null ? pV.getTop() : 0;
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {

                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogview != null) {
                    dialogview.dataSetSuccess(selectType, localChooseStr);
                }
                Logger.e("tag", localChooseStr);
                popupWindow.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onItemSelectedValue(String str) {
        localChooseStr = str;
    }
 /*
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        localChooseStr = localDataNoUnit[newVal];

    }
    */


    //温度设置选择器
    private View mTmeapView;
    private PopupWindow popupWindowTemp;

    // 时间选择器

    public void setPopupWindowTemp(Context context, View view, int value) {
        try {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(inflater == null)
                return;
            mTmeapView = inflater.inflate(R.layout.pop_bottom_temp, null);
            TextView tv_determine = (TextView) mTmeapView.findViewById(R.id.tv_determine);
            TextView tv_cancel = (TextView) mTmeapView.findViewById(R.id.tv_cancel);
            final ArrayPickerView datePicker1 = (ArrayPickerView) mTmeapView.findViewById(R.id.datePicker1);
            final ArrayPickerView datePicker2 = (ArrayPickerView) mTmeapView.findViewById(R.id.datePicker2);
            final ArrayPickerView datePicker3 = (ArrayPickerView) mTmeapView.findViewById(R.id.datePicker3);
            final ArrayPickerView datePicker_point = (ArrayPickerView) mTmeapView.findViewById(R.id.datePicker_point);
            popupWindowTemp = new PopupWindow(context);
            popupWindowTemp.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindowTemp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popupWindowBirth.setHeight(DisplayUtils.dip2px(context, 150));
            popupWindowTemp.setContentView(mTmeapView);
            popupWindowTemp.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindowTemp.setOutsideTouchable(false);
            popupWindowTemp.setFocusable(true);
            popupWindowTemp.setAnimationStyle(R.style.popwin_anim_style);
            popupWindowTemp.showAtLocation(view, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);

            ArrayList<String> localData1 = new ArrayList<>();
            localData1.add("+");
            localData1.add("-");
            int showIndex1 = 0, showIndex2 = 0, showIndex3 = 0;

            if (value >= 0) {
                showIndex1 = 0;
            } else {
                showIndex1 = 1;
            }

            int currentValue = Math.abs(value);
            int yu = currentValue % 10;
            int ge = currentValue / 10;
            ArrayList<String> localData2 = new ArrayList<>();
            ArrayList<String> localData3 = new ArrayList<>();
            ArrayList<String> localDataPoint = new ArrayList<>();
            localDataPoint.add(".");
            for (int i = 0; i <= 9; i++) {

                if (i < 5) {
                    if (ge == i) {
                        showIndex2 = i;
                    }
                    localData2.add(i + "");
                }
                if (yu == i) {
                    showIndex3 = i;
                }
                localData3.add(i + "");
            }

            datePicker1.setData(localData1);
            datePicker1.setCyclic(false);
            datePicker1.setItemOnclick(this);
            datePicker1.setSelectItem(showIndex1);

            datePicker_point.setData(localDataPoint);
            datePicker_point.setCyclic(false);
            datePicker_point.setSelectItem(0);

            datePicker2.setData(localData2);
            datePicker2.setCyclic(false);
            datePicker2.setItemOnclick(this);
            datePicker2.setSelectItem(showIndex2);

            datePicker3.setData(localData3);
            datePicker3.setCyclic(false);
            datePicker3.setItemOnclick(this);
            datePicker3.setSelectItem(showIndex3);

            if(mTmeapView == null)
                return;
            mTmeapView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    View linView = mTmeapView.findViewById(R.id.pop_layout);
                    if(linView == null)
                        return false;
                    int height =linView.getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            popupWindowTemp.dismiss();
                        }
                    }
                    return true;
                }
            });

            tv_determine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                    if (dialogview != null) {
                        String value = datePicker1.getItem() + datePicker2.getItem() + datePicker3.getItem();
                        dialogview.dataSetSuccess("0", value);
                    }
                    popupWindowTemp.dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(popupWindowTemp != null)
                        popupWindowTemp.dismiss();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private View mMenuViewBirth;
    private PopupWindow popupWindowBirth;
    // 时间选择器

    public void setPopupWindow(Context context, View view, final String type, String defaultDay) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuViewBirth = inflater.inflate(R.layout.app_pop_date, null);
        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
        TextView tv_cancel = (TextView) mMenuViewBirth.findViewById(R.id.tv_cancel);
        final DatePickerView datePicker = (DatePickerView) mMenuViewBirth.findViewById(R.id.datePicker);
        if (type.equals("3")) {
            datePicker.setType(3);
        }
        datePicker.setDefaultItemAdapter(defaultDay);
        datePicker.setCyclic(false);
        popupWindowBirth = new PopupWindow(context);
        popupWindowBirth.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindowBirth.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindowBirth.setHeight(DisplayUtils.dip2px(context, 150));
        popupWindowBirth.setContentView(mMenuViewBirth);
        popupWindowBirth.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindowBirth.setOutsideTouchable(false);
        popupWindowBirth.setFocusable(true);
        popupWindowBirth.setAnimationStyle(R.style.popwin_anim_style);
        popupWindowBirth.showAtLocation(view, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);


        mMenuViewBirth.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(mMenuViewBirth == null)
                    return false;
                View linView = mMenuViewBirth.findViewById(R.id.pop_layout);
                if(linView == null)
                    return false;
                int height = linView.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        popupWindowBirth.dismiss();
                    }
                }
                return true;
            }
        });

        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calculationAgeAndConstellation(datePicker.getTime());
//                localUserChooseBirthday = datePicker.getTime();
                if (dialogview != null) {
                    dialogview.dataSetSuccess(type, datePicker.getTime());
                }
                popupWindowBirth.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowBirth.dismiss();
            }
        });
    }

//    public void setPopupWindow(Context context, View view, final String type, String defaultDay) {
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuViewBirth = inflater.inflate(R.layout.app_pop_date, null);
//        TextView tv_determine = (TextView) mMenuViewBirth.findViewById(R.id.tv_determine);
//        TextView tv_cancel = (TextView) mMenuViewBirth.findViewById(R.id.tv_cancel);
////        final DatePickerView datePicker = (DatePickerView) mMenuViewBirth.findViewById(R.id.datePicker);
////        if (type.equals("3")) {
////            datePicker.setType(3);
////        }
////        datePicker.setDefaultItemAdapter(defaultDay);
////        datePicker.setCyclic(false);
//        popupWindowBirth = new PopupWindow(context);
//        popupWindowBirth.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        popupWindowBirth.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //popupWindowBirth.setHeight(DisplayUtils.dip2px(context, 150));
//        popupWindowBirth.setContentView(mMenuViewBirth);
//        popupWindowBirth.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        popupWindowBirth.setOutsideTouchable(false);
//        popupWindowBirth.setFocusable(true);
//        popupWindowBirth.setAnimationStyle(R.style.popwin_anim_style);
//        popupWindowBirth.showAtLocation(view, Gravity.BOTTOM
//                | Gravity.CENTER_HORIZONTAL, 0, 0);
//        mMenuViewBirth.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int height = mMenuViewBirth.findViewById(R.id.pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        popupWindowBirth.dismiss();
//                    }
//                }
//                return true;
//            }
//        });
//
//        tv_determine.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                calculationAgeAndConstellation(datePicker.getTime());
////                localUserChooseBirthday = datePicker.getTime();
//                if (isViewAttached()) {
////                    mActView.get().dataSetSuccess(type, datePicker.getTime());
//                }
//                popupWindowBirth.dismiss();
//            }
//        });
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindowBirth.dismiss();
//            }
//        });
//    }




   /* @Override
    public void saveUserBaseInfoSuccess() {

        if (isViewAttached()) {
            mActView.get().saveUserBaseInfoSuccess();
        }
    }
*/

}
