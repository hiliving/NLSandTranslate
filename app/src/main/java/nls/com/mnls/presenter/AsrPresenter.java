package nls.com.mnls.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.StageListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;

import java.util.HashMap;

import nls.com.mnls.presenter.ivew.IasrView;

/**
 * Created by Huangyong on 2018/1/11.
 */

public class AsrPresenter {
    private Context context;
    private boolean isRecognizing = false;
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    private HashMap<Integer,String> resultMap = new HashMap<Integer, String>();
    private int sentenceId = 0;
    private IasrView iasrView;
    public AsrPresenter(Context context,IasrView iasrView) {
        this.context = context;
        this.iasrView = iasrView;
//        String appkey = "nls-service-en"; //请设置文档中Appkey
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
        mNlsClient = NlsClient.newInstance(context, mRecognizeListener, mStageListener,mNlsRequest);                          //实例化NlsClient

        mNlsClient.setMaxRecordTime(60000);  //设置最长语音
        mNlsClient.setMaxStallTime(1000);    //设置最短语音
        mNlsClient.setMinRecordTime(500);    //设置最大录音中断时间
        mNlsClient.setRecordAutoStop(false);  //设置VAD
        mNlsClient.setMinVoiceValueInterval(200); //设置音量回调时长
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
                         //   mResultEdit.setText(resultMap.get(sentenceId));
                        //    mFullEdit.setText(JSON.toJSONString(result.getResult()));
                            iasrView.setRecResult(resultMap.get(sentenceId));
                        }
                    }else {
                        Log.i("asr", "[demo] callback onRecognizResult finish!" );
                      //  mResultEdit.setText("Recognize finish!");
                      //  mFullEdit.setText("Recognize finish!");
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
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
          //  mResultEdit.setText("");
            mNlsClient.stop();
           // mStartButton.setText("开始 录音");
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

    public void start() {
        isRecognizing = true;
        iasrView.setStatu("正在录音，请稍候！");
        mNlsClient.start();
        iasrView.setIsRecord("录音中。。。");
    }

    public void stop() {
        isRecognizing = false;
        iasrView.setStatu("");
        mNlsClient.stop();
        iasrView.setIsRecord("开始 录音");
    }
}
