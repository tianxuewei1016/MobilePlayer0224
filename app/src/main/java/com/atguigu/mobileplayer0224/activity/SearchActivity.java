package com.atguigu.mobileplayer0224.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.adapter.SearchAdapter;
import com.atguigu.mobileplayer0224.bean.SearchBean;
import com.atguigu.mobileplayer0224.utils.JsonParser;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

    @InjectView(R.id.et_sousuo)
    EditText etSousuo;
    @InjectView(R.id.iv_voice)
    ImageView ivVoice;
    @InjectView(R.id.tv_go)
    TextView tvGo;
    @InjectView(R.id.lv)
    ListView lv;
    @InjectView(R.id.activity_search)
    LinearLayout activitySearch;

    private SearchAdapter adapter;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public static final String NET_SEARCH_URL = "http://hot.news.cntv.cn/index.php?controller=list&action=searchList&sort=date&n=20&wd=";
    private String url;
    private List<SearchBean.ItemsBean> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
    }


    @OnClick({R.id.iv_voice, R.id.tv_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_voice:
                //语音数据
                showVoiceDialog();
                break;
            case R.id.tv_go:
                Toast.makeText(SearchActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                toSearch();
                break;
        }
    }

    private void toSearch() {
        //1.得到输入宽的内容
        String trim = etSousuo.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            //2.拼接
            url = NET_SEARCH_URL + trim;
            //3.联网请求
            getDataFromNet(url);
        } else {
            Toast.makeText(SearchActivity.this, "请输入您要搜索的内容", Toast.LENGTH_SHORT).show();
        }

    }

    private void getDataFromNet(String url) {
        final RequestParams request = new RequestParams(url);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "xUtils联网成功==" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析数据
     *
     * @param json
     */
    private void processData(String json) {
        SearchBean searchBean = new Gson().fromJson(json, SearchBean.class);
        datas = searchBean.getItems();
        if (datas != null && datas.size() > 0) {
            adapter = new SearchAdapter(this, datas);
            lv.setAdapter(adapter);
        }
    }

    private void showVoiceDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String resultString = recognizerResult.getResultString();
            Log.e("TAG", "onResult--resultString==" + resultString);
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("TAG", "onError==" + speechError.getMessage());
        }
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i == ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        String stri = resultBuffer.toString();
        stri = stri.replace("。", "");

        etSousuo.setText(stri);
        etSousuo.setSelection(etSousuo.length());
    }
}
