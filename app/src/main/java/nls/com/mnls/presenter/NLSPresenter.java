package nls.com.mnls.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.StageListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;

import java.util.HashMap;

import nls.com.mnls.presenter.ivew.IAsrView;

/**
 * Created by huangyong on 2018/1/9.
 */

public class NLSPresenter{
    private static final String TAG = "NLSPresenter";
    private boolean isRecognizing = false;
    private Context context;
    private final NlsRequest mNlsRequest;
    private final NlsClient mNlsClient;
    private int sentenceId = 0;
    private IAsrView iAsrView;
    private HashMap<Integer,String> resultMap = new HashMap<Integer, String>();
    public NLSPresenter(Context context,IAsrView iAsrView) {
        this.context = context;
        this.iAsrView = iAsrView;
        String appkey = "nls-service-shurufa16khz"; //请设置文档中Appkey

        mNlsRequest = new NlsRequest();
        mNlsRequest.setAppkey(appkey);    //appkey请从 "快速开始" 帮助页面的appkey列表中获取
        mNlsRequest.setResponseMode("streaming");//流式为streaming,非流式为normal
        mNlsRequest.authorize("LTAIQU2Ib6TnwUhd", "wWqvL79xfeuVhvUh2vALiKtmVTP5QO"); //请替换为用户申请到的数加认证key和密钥

        /*设置热词相关属性*/
//        mNlsRequest.setVocabularyId("vocabid");
        /*设置热词相关属性*/

        NlsClient.openLog(true);
        NlsClient.configure(context.getApplicationContext()); //全局配置
        //实例化NlsClient
        mNlsClient = NlsClient.newInstance(context, mRecognizeListener, mStageListener, mNlsRequest);

        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长
    }

    public void stop() {
        isRecognizing = false;
        iAsrView.setResult("");
        mNlsClient.stop();
    }

    public void start() {
        isRecognizing = true;
        mNlsClient.start();
        iAsrView.setResult("正在录音，请稍后！");
        iAsrView.setStatus("录音中……");
    }


    private NlsListener mRecognizeListener = new NlsListener() {

        @Override
        public void onRecognizingResult(int status, NlsResponse result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    if (result!=null){
                        if(result.getResult()!=null) {
                            //获取句子id对应结果。
                            if (sentenceId != result.getSentenceId()) {
                                sentenceId = result.getSentenceId();
                            }
                            resultMap.put(sentenceId,result.getText());

                            Log.i("asr", "[demo] callback onRecognizResult :" + result.getResult().getText());
                           /* mResultEdit.setText(resultMap.get(sentenceId));
                            mFullEdit.setText(JSON.toJSONString(result.getResult()));*/
                           iAsrView.showResult(resultMap.get(sentenceId));
                        }
                    }else {
                        Log.i("asr", "[demo] callback onRecognizResult finish!" );
                       /* mResultEdit.setText("Recognize finish!");
                        mFullEdit.setText("Recognize finish!");*/
                    }
                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    Toast.makeText(context, "recognizer error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    Toast.makeText(context,"recording error", Toast.LENGTH_LONG).show();
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    Toast.makeText(context,"nothing", Toast.LENGTH_LONG).show();
                    break;
            }
            isRecognizing = false;
        }


    } ;

    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Log.d(TAG,"开始录音");
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            iAsrView.setResult("");
            mNlsClient.stop();
//            mStartButton.setText("开始 录音");
            Log.d(TAG,"结束录音");
            iAsrView.restore();
        }

        @Override
        public void onStartRecording(NlsClient recognizer) {
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecording(NlsClient recognizer) {
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }
    };
}
