package brandapp.isport.com.basicres.commonutil;

import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户信息检测公共类
 *
 * @author ck
 */
public class InfoUtil {
    // 检查手机号码是否存在
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(18[0-9])|(19[8,9])|(17[0,1,3,5-9]))\\d{8}$
        String phone = "^1[345789]\\d{9}$";

        String tmpP = "^((13[0-9])|(14[5,7,9])|(15[^4,\\\\D])|(18[0-9])|(19[0-9])|(17[0,1,3,5-9]))\\\\d{8}$";

        // String phone = "^((13[0-9])|(14[5,7,9])|(15[^4,\\\\D])|(18[0-9])|(19[8,9])|(17[0,1,3,5-9]))\\\\d{8}$";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // 检测是否为数字
    public static boolean isNumber(String number) {
        boolean result = number.matches("[0-9]+");
        return result;
    }

    public static boolean isEmail(String strEmail) {
        //String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        String strPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }


    // 判断密码是否格式正确
    public static boolean isPassword(String password) {
        boolean isValid = false;
        CharSequence inputStr = password;
        // String phone = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,20}$";
        String phone = "^(?=.*[0-9].*)(?=.*[a-z].*).{6,20}$";
        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;

    }

    /**
     * 公式:身高*0.6 height 必须传入用户身高
     *
     * @return 步长
     */
    public static Integer targetStepDistance(int height, String gender) {
        int stepDistance;
        if (gender.equals("Male")) {
            stepDistance = (int) (height * 0.6);
        } else {
            stepDistance = (int) (height * 0.475);
        }
        return stepDistance;
    }

    /**
     * 卡路里(Kcal) = 步数 * [(体重(公斤）-13.63636) * 0.000693 + 0.00495] * (步速 / 130)
     *
     * @param step
     * @param weight
     * @param stepSpeed
     * @return
     */
    public static Double targetCal(int step, int weight, double stepSpeed) {
        double cal = step
                * (((weight - 13.63636) * 0.000693 + 0.00495) * (stepSpeed / 130));
        BigDecimal b = new BigDecimal(cal);
        double calTwoDecimal = b.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return calTwoDecimal;
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}