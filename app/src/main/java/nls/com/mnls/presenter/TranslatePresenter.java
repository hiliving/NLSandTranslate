package nls.com.mnls.presenter;

import android.content.Context;

import nls.com.mnls.TransApi;
import nls.com.mnls.presenter.ivew.ITranView;

/**
 * Created by huangyong on 2018/1/9.
 */

public class TranslatePresenter {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20180109000113218";
    private static final String SECURITY_KEY = "pZEAl8xq7xdHbYRXk4wr";
    private Context context;
    private ITranView iTranView;
    private TransApi api;
    public TranslatePresenter(Context context, ITranView iTranView) {
        this.context = context;
        this.iTranView = iTranView;
        api = new TransApi(APP_ID, SECURITY_KEY);
    }

    public void getTransResult(String src,boolean isEn){
        if (isEn){
            iTranView.onTransResult( api.getTransResult(src, "auto", "zh"));
        }else {
            iTranView.onTransResult( api.getTransResult(src, "auto", "en"));
        }
    }
}
