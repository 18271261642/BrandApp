package brandapp.isport.com.basicres.commonnet.interceptor;

import android.net.ParseException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.isport.brandapp.basicres.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.UnknownHostException;

import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;
import retrofit2.HttpException;


public class ExceptionHandle {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int SUCESS = 2000;

    public static ResponeThrowable handleException(Throwable e) {
        ResponeThrowable ex;
        Log.e("MyLog", "错误信息==" + e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ResponeThrowable(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.message = UIUtils.getString(R.string.error_Connection_timeout);

            }
            if (ex.message.contains("Unable to resolve host")) {
                ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);
            }
            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                return ex;
            }
        } else if (e instanceof UnknownHostException) {

            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);

            Log.e("MyLog", " UnknownHostException 网络错误==" + ex.toString() + "!TextUtils.isEmpty(ex.message):" + !TextUtils.isEmpty(ex.message));
            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {

            } finally {
                return ex;
            }

        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ResponeThrowable(resultException, resultException.code);
            ex.message = resultException.message;
            if (ex.message.contains("Unable to resolve host")) {
                ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);
            }


            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {
                e.printStackTrace();
            } finally {
                return ex;
            }

        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ResponeThrowable(e, ERROR.PARSE_ERROR);
            ex.message = UIUtils.getString(R.string.parse_error);


            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                  //  ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {
                e.printStackTrace();
            } finally {
                return ex;
            }
        } else if (e instanceof java.net.ConnectException) {
            ex = new ResponeThrowable(e, ERROR.NETWORD_ERROR);
            ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);


            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {
                e.printStackTrace();
            } finally {
                return ex;
            }
        } else if (e instanceof javax.net.ssl.SSLException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = UIUtils.getString(R.string.error_Connection_timeout);


            try {
                ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.error_Connection_timeout));
            } catch (Exception exception) {

            } finally {
                return ex;
            }
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ResponeThrowable(e, ERROR.SSL_ERROR);
            ex.message = UIUtils.getString(R.string.net_err_verification);
            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {

            } finally {
                return ex;
            }

        } else if (e instanceof ConnectTimeoutException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = UIUtils.getString(R.string.error_Connection_timeout);
            //新增 当服务器出问题时取消弹窗,提示用户
            try {
                NetProgressObservable.getInstance().hide();
                ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.error_Connection_timeout));
            } catch (Exception exception) {

            } finally {
                return ex;
            }
        } else if (e instanceof java.net.SocketTimeoutException) {

            Logger.e("SocketTimeoutException  log");
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = UIUtils.getString(R.string.error_Connection_timeout);
            //新增 当服务器出问题时取消弹窗,提示用户
            NetProgressObservable.getInstance().hide();
            ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.error_Connection_timeout));
            return ex;
        } else if (e instanceof java.io.InterruptedIOException) {
            ex = new ResponeThrowable(e, ERROR.TIMEOUT_ERROR);
            ex.message = UIUtils.getString(R.string.error_Connection_timeout);
            //新增 当服务器出问题时取消弹窗,提示用户


            try {
                if (JkConfiguration.AppType == JkConfiguration.httpType) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), UIUtils.getString(R.string.error_Connection_timeout));
                }
            } catch (Exception exception) {

            } finally {
                return ex;
            }


        } else if (e instanceof FailException) {
            // Logger.e("FailException" + e.getMessage());
            ex = new ResponeThrowable(e, 1001);
            ex.message = e.getMessage();
            if (!TextUtils.isEmpty(e.getMessage())) {
                NetProgressObservable.getInstance().hide();
                ToastUtils.showToast(UIUtils.getContext(), ex.message);
            }
            return ex;
        } else if (e instanceof SuccessException) {
            ex = new ResponeThrowable(e, 2000);
            ex.message = e.getMessage();
            return ex;
        } else if (e instanceof ServerExceptions) {
            String message = ((ServerExceptions) e).message;
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);

            if (!TextUtils.isEmpty(message)) {
                ex.message = message;
            } else {
                ex.message = UIUtils.getString(R.string.net_err_unknown);
            }
            if (ex.message.contains("Unable to resolve host") || ex.message.contains("Failed to connect")) {
                ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);
            }
            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception excetion) {

            } finally {
                return ex;
            }


        } else {
            Exception resultException = (Exception) e;
            ex = new ResponeThrowable(e, ERROR.UNKNOWN);
            if (ex.message.contains("Unable to resolve host") || ex.message.contains("unable to resolve host") || ex.message.contains("Failed to connect") || ex.message.contains("failed to connect")) {
                ex.message = UIUtils.getString(R.string.common_please_check_that_your_network_is_connected);
            }
            if (ex.message.contains("没有访问权限！")) {
                ex.message = "";
            }
            if (TextUtils.isEmpty(e.getMessage())) {
                ex.message = UIUtils.getString(R.string.net_err_unknown);
            } else {
                String[] strs = e.getMessage().split(":");
                if (strs.length >= 2) {
                    ex.message = e.getMessage().split(":")[1];
                } else {
                    ex.message = e.getMessage();
                }
            }

            Log.e("MyLog", "网络错误==" + ex.toString());
            try {
                if (!TextUtils.isEmpty(ex.message)) {
                    NetProgressObservable.getInstance().hide();
                    ToastUtils.showToast(UIUtils.getContext(), ex.message);
                }
            } catch (Exception exception) {

            } finally {
                return ex;
            }


        }
    }


    /**
     * 约定异常
     */
    class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;
    }

    public static class ResponeThrowable extends Exception {
        @Override
        public String toString() {
            return "ResponeThrowable{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    '}';
        }

        public int code;
        public String message;

        public ResponeThrowable(Throwable throwable, int code) {
            super(throwable);
            this.code = code;

        }
    }

    public class ServerException extends RuntimeException {
        public int code;
        public String message;
    }
}

