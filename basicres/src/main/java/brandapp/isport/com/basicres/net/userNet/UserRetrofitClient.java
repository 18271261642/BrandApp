package brandapp.isport.com.basicres.net.userNet;


import com.google.gson.Gson;

import java.io.File;

import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.commonnet.net.PostBody;
import brandapp.isport.com.basicres.commonnet.net.RxScheduler;
import brandapp.isport.com.basicres.entry.bean.UpdatePhotoBean;
import brandapp.isport.com.basicres.net.CommonRetrofitClient;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @author azheng
 * @date 2018/4/17.
 * GitHub：https://github.com/RookieExaminer
 * email：wei.azheng@foxmail.com
 * description：
 */
public class UserRetrofitClient extends CommonRetrofitClient {

    private static volatile UserRetrofitClient instance;

    public UserRetrofitClient() {
    }

    public static UserRetrofitClient getInstance() {
        if (instance == null) {
            synchronized (CommonRetrofitClient.class) {
                if (instance == null) {
                    instance = new UserRetrofitClient();
                }
            }
        }
        return instance;
    }

    public Observable<?> gettOss() {
//        return getRetrofit().create(UserAPIService.class).getAliToken().compose
//                (RxScheduler.Obs_io_main()).compose(transformer);

        return getRetrofitNoToken().create(UserAPIService.class).getAliToken().compose
                (RxScheduler.Obs_io_main()).compose(transformer);
    }

    public Observable<?> post(PostBody body) {
        RequestBody requestBody = null;
        if (body.data != null) {
            Gson gson = new Gson();
            requestBody = RequestBody.create(MediaType.parse("application/json"), gson.toJson(body.data));
        }
        Observable observable = null;
        switch (body.type) {
            case JkConfiguration.RequstType.GET_USER_FRIEND_RELATION:
                observable = getRetrofit().create(UserAPIService.class).getFriendRelation(requestBody);
                break;
            case JkConfiguration.RequstType.GET_USERINFO:
                observable = getRetrofit().create(UserAPIService.class).getUserInfo(requestBody);
                break;
            case JkConfiguration.RequstType.EDITBASICINFO:
                observable = getRetrofit().create(UserAPIService.class).updateUserInfo(requestBody);
                break;
        }
        return observable.compose
                (RxScheduler.Obs_io_main()).compose(transformer);
    }


}
