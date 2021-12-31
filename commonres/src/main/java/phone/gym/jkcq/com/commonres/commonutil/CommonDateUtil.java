package phone.gym.jkcq.com.commonres.commonutil;


public class CommonDateUtil {
    public static String formatOnePoint(double value) {
        String strNumber = String.format("%.1f", value);
        return strNumber;
    }

    public static double formatOnePointDouble(double value) {
        String strNumber = String.format("%.1f", value);
        return Double.parseDouble(strNumber);
    }

    public static String formatTwoStr(int number) {
        String strNumber = String.format("%02d", number);
        return strNumber;
    }

    public static String formatTwoPoint(double value) {
        String result = String.format("%.2f", Math.round(value * 100) / 100f);
        // String result2 = String.format("%.2f", 3.145);

    /*    DecimalFormat format = new DecimalFormat("0.00");
        String numberStr = format.format(value);*/
        return result;
    }

    //距离向下取整
    public static float formatFloor(float dis, boolean isfloor) {
        if (isfloor) {
            return (float) (Math.floor(dis / 10) * 10f) / 1000;

        } else {
            return dis / 1000;
        }

    }

    //华氏度与摄氏度转换
    //F=C×1.8+32
    //
    //C=(F-32)÷1.8
    public static float ftoc(float temp) {

        return 0.f;
    }

    //华氏度与摄氏度转换
    public static float ctof(float temp) {

        float fTemp = (temp * 1.8f) + 32;
        fTemp = (float) (Math.floor(fTemp * 100 / 10) / 10.0f);
        // Math.floor(temp *100) / 10f
        return fTemp;
    }

    public static String formatTwoPoint(float value) {
        String result = String.format("%.2f", value);
        // String result2 = String.format("%.2f", 3.145);

    /*    DecimalFormat format = new DecimalFormat("0.00");
        String numberStr = format.format(value);*/
        return result;
    }

    public static String formatTwoPointThree(double value) {
        String result = String.format("%.2f", Math.round(value * 1000) / 1000f);
     /*   DecimalFormat format = new DecimalFormat("0.000");
        String numberStr = format.format(value);*/
        return result;
    }

    public static String formatInterger(double value) {
        long data = Math.round(value);
       /* DecimalFormat format = new DecimalFormat("0");
        String numberStr = format.format(value);*/
        return data + "";
    }


    public static String getRemindMin(int time,String strhour,String strmin,String strsec){
        String timestr="";
        int sec = time % 60;

        int min = time / 60;

        int hour = min / 60;
        min=min%60;

        if (time>0&&time<60)
        {
            timestr =time+strsec;
        }else if(time>=60&&time<3600){
            timestr =min+strmin+sec+strsec;
            if (sec==0)
            {
                timestr =min+strmin;
            }
        }else if(time>=3600){
            timestr =hour+strhour+min+strmin+sec+strsec;
            if (time==3600){
                timestr=hour+strhour;
            }else{
                if (sec==0){
                    timestr=hour+strhour+min+strmin;
                }
            }
        }

        return timestr;
    }

}