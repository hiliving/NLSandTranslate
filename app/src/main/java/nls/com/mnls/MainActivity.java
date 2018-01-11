package nls.com.mnls;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import nls.com.mnls.bean.TranInfo;
import nls.com.mnls.net.RetrofitClient;
import nls.com.mnls.presenter.NLSPresenter;
import nls.com.mnls.presenter.TranslatePresenter;
import nls.com.mnls.presenter.ivew.IAsrView;
import nls.com.mnls.presenter.ivew.ITranView;

public class MainActivity extends AppCompatActivity implements IAsrView, ITranView {

    private static boolean ISINSTANCE = false;
    private static boolean ISENTOZN = false;
    private EditText input;
    private TextView start;
    private String query;
    private TextView result;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (TextUtils.isEmpty(msg.getData().getString("RESULT"))) {
                return;
            }
            switch (msg.what){
                case 1:
                    result.setText("英译汉："+msg.getData().getString("RESULT"));
                    break;
                case 2:
                    result.setText("汉译英："+msg.getData().getString("RESULT"));
                    break;
            }

        }
    };
    private Button toggle;
    private Button statutoggle;
    private NLSPresenter presenter;
    private TranslatePresenter translatePresenter;
    private TextView instatu;
    private TextView tresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        start = findViewById(R.id.start);
        result = findViewById(R.id.result);
        toggle = findViewById(R.id.toggle);
        instatu = findViewById(R.id.instatu);
        tresult = findViewById(R.id.tresult);
        statutoggle = findViewById(R.id.instatutoggle);




        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    query = editable.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        presenter = new NLSPresenter(this,this);//语音识别方法
        translatePresenter = new TranslatePresenter(this,this);//翻译方法

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (ISENTOZN){
                            translatePresenter.getTransResult(query,ISENTOZN);
                        }else {
                            translatePresenter.getTransResult(query,ISENTOZN);
                        }

                    }
                }).start();

            }
        });
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ISENTOZN){
                    toggle.setText("汉译英");
                }else {
                    toggle.setText("英译汉");
                }
                ISENTOZN = !ISENTOZN;
            }
        });
        statutoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ISINSTANCE){
                    presenter.stop();
                }else {
                    presenter.start();
                }
                ISINSTANCE =!ISINSTANCE;
            }
        });

    }

    @Override
    public void setResult(String s) {

    }

    @Override
    public void restore() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                instatu.setText("已关闭");
            }
        },1000);
    }

    /**
     * 语音识别实时状态
     * @param s
     */
    @Override
    public void setStatus(String s) {
        instatu.setText(s);
    }

    /**
     * 语音识别结果
     *
     * 打算结合翻译一起，语音识别结果直接进行翻译
     * @param result
     */
    @Override
    public void showResult(String result) {
        tresult.setText("识别结果："+result);
        query = result;//将识别结果直接填写到翻译输入框
        input.setText(result);
    }

    @Override
    public void onTransResult(final String transResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Gson gson = new Gson();
                TranInfo tranInfo = gson.fromJson(transResult, TranInfo.class);
                Bundle bundle = new Bundle();
               try {
                   bundle.putString("RESULT",tranInfo.getTrans_result().get(0).getDst());
               }catch (Exception e){
                   e.printStackTrace();
               }
                message.setData(bundle);
                handler.sendMessage(message);
                if (ISENTOZN){
                    message.what=1;
                }else {
                    message.what=2;
                }
            }
        });
    }
}
