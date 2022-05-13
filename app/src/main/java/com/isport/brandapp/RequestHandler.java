package com.isport.brandapp;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.JsonSyntaxException;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.NullBodyException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.request.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2019/12/07
 *    desc   : 请求处理类
 */
public final class RequestHandler implements IRequestHandler {

    private final Application mApplication;

    public RequestHandler(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public Object requestSucceed(@NonNull HttpRequest<?> httpRequest, @NonNull Response response,
                                 @NonNull Type type) throws Exception {
        if (Response.class.equals(type)) {
            return response;
        }

        if (!response.isSuccessful()) {
            // 返回响应异常
            throw new ResponseException("exception" + ", responseCode: " +
                    response.code() + ", message: " + response.message(), response);
        }

        if (Headers.class.equals(type)) {
            return response.headers();
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new NullBodyException("请求body为空!");
        }

        if (ResponseBody.class.equals(type)) {
            return body;
        }

        // 如果是用数组接收，判断一下是不是用 byte[] 类型进行接收的
        if(type instanceof GenericArrayType) {
            Type genericComponentType = ((GenericArrayType) type).getGenericComponentType();
            if (byte.class.equals(genericComponentType)) {
                return body.bytes();
            }
        }

        if (InputStream.class.equals(type)) {
            return body.byteStream();
        }

        if (Bitmap.class.equals(type)) {
            return BitmapFactory.decodeStream(body.byteStream());
        }

        String text;
        try {
            text = body.string();
        } catch (IOException e) {
            // 返回结果读取异常
            throw new DataException("读取结果异常", e);
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text);

        if (String.class.equals(type)) {
            return text;
        }

        final Object result;

        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type);
        } catch (JsonSyntaxException e) {

            requestFail(httpRequest,e);
            // 返回结果读取异常
            throw new DataException("读取结果异常", e);
        }
//
//        if (result instanceof HttpData) {
//            HttpData<?> model = (HttpData<?>) result;
//            if (model.isRequestSucceed()) {
//                // 代表执行成功
//                return result;
//            }
//
//            if (model.isTokenFailure()) {
//
//
//
//                return ((HttpData<?>) result);
//                // 代表登录失效，需要重新登录
//               // throw new TokenException(mApplication.getString(R.string.http_token_error));
//            }
//
//            if(model.isNo200Code()){
//
//                return ((HttpData<?>) result).getMessage();
//            }
//
//            // 打印这个 Json 或者文本
//
//
//            // 代表执行失败
//            throw new ResultException(model.getMessage(), model);
//        }
        return result;
    }

    @NonNull
    @Override
    public Exception requestFail(@NonNull HttpRequest<?> httpRequest, @NonNull Exception e) {
        if (e instanceof HttpException) {
//            if (e instanceof TokenException) {
//                // 登录信息失效，跳转到登录页
//
//            }
            return e;
        }

//        if (e instanceof SocketTimeoutException) {
//            return new TimeoutException(mApplication.getString(R.string.http_server_out_time), e);
//        }
//
//        if (e instanceof UnknownHostException) {
//            NetworkInfo info = ((ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
//            // 判断网络是否连接
//            if (info != null && info.isConnected()) {
//                // 有连接就是服务器的问题
//                return new ServerException(mApplication.getString(R.string.http_server_error), e);
//            }
//            // 没有连接就是网络异常
//            return new NetworkException(mApplication.getString(R.string.http_network_error), e);
//        }
//
//        if (e instanceof IOException) {
//            return new CancelException(mApplication.getString(R.string.http_request_cancel), e);
//        }

        return new HttpException(e.getMessage(), e);
    }

    @Nullable
    @Override
    public Object readCache(@NonNull HttpRequest<?> httpRequest, @NonNull Type type, long cacheTime) {
//        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
//        String cacheValue = HttpCacheManager.getMmkv().getString(cacheKey, null);
//        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
//            return null;
//        }
//        EasyLog.printLog(httpRequest, "----- readCache cacheKey -----");
//        EasyLog.printJson(httpRequest, cacheKey);
//        EasyLog.printLog(httpRequest, "----- readCache cacheValue -----");
//        EasyLog.printJson(httpRequest, cacheValue);
//        return GsonFactory.getSingletonGson().fromJson(cacheValue, type);

        return null;
    }

    @Override
    public boolean writeCache(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Object result) {
//        String cacheKey = HttpCacheManager.generateCacheKey(httpRequest);
//        String cacheValue = GsonFactory.getSingletonGson().toJson(result);
//        if (cacheValue == null || "".equals(cacheValue) || "{}".equals(cacheValue)) {
//            return false;
//        }
//        EasyLog.printLog(httpRequest, "----- writeCache cacheKey -----");
//        EasyLog.printJson(httpRequest, cacheKey);
//        EasyLog.printLog(httpRequest, "----- writeCache cacheValue -----");
//        EasyLog.printJson(httpRequest, cacheValue);
//        return HttpCacheManager.getMmkv().putString(cacheKey, cacheValue).commit();
        return false;
    }

    @Override
    public void clearCache() {
//        HttpCacheManager.getMmkv().clearMemoryCache();
//        HttpCacheManager.getMmkv().clearAll();
    }
}