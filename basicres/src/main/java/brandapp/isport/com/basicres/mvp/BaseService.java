
package brandapp.isport.com.basicres.mvp;

import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import brandapp.isport.com.basicres.commonutil.StringUtil;

/**
 * ClassName:BaseService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2017年2月17日 下午3:38:24 <br/>
 *
 * @author Administrator
 */
public abstract class BaseService<V extends BaseView, P extends BasePresenter<V>> extends Service
        implements BaseView {

    protected final String LTAG = BaseService.this.getClass().getSimpleName();

    // Persenter类的实例。
    public P mSerPersenter;
    public Handler handler;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
        mSerPersenter = createPresenter();
        if (mSerPersenter != null) {
            // BasePersenter类的方法。主要用于将View用弱引用赋值给P层的View对象
            mSerPersenter.attach(this,(V) this);
        }
        handler = new Handler(Looper.getMainLooper());
    }

    // 子类实现，具体类型创建具体P层对象。
    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        if (mSerPersenter != null) {
            // BasePersenter类的方法。主要用于将View的引用清除。
            mSerPersenter.detach();
        }
        super.onDestroy();
    }

    @Override
    public void onRespondError(String message) {
        showToast(message);
    }

    public void showToast(final String toast) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (StringUtil.isBlank(toast)||toast.contains("没有访问权限！")){
                    return;
                }
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}