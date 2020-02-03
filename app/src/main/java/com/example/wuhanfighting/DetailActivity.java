package com.example.wuhanfighting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private String province;
    private Handler mHandler;
    private TextView provinceHead;
    private ProvinceAdapter adapter;
    private List<Province> eareList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        province = getIntent().getStringExtra("province");
        //初始化控件
        init();
        //初始化handler
        initHandler();
        //解析对应省份的数据
        pareJSONData(province);
    }

    private void init(){
        provinceHead = (TextView)findViewById(R.id.province_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detail_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProvinceAdapter(eareList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }
    private void initHandler(){
        //新建Handler对象
        mHandler = new Handler(){
            //handleMessage为处理消息的方法
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    provinceHead.setText(" "+province+"省的疫情状况如下：");
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void pareJSONData(final String province) {
        try {
            JSONObject rootObject = new JSONObject(OkHttpRequest.getResponseData());
            JSONObject secondObject = rootObject.getJSONObject("data");
            //处理具体省份信息
            eareList.add(new Province("地区","确诊病例","疑似病例","治愈病例","死亡病例"));
            JSONArray jsonArray = secondObject.getJSONArray("area");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("provinceName");
                if(province.equals(name))
                {
                    JSONArray cityArray = jsonObject.getJSONArray("cities");
                    for (int k = 0; k < cityArray.length(); k++) {
                        JSONObject dataObject = cityArray.getJSONObject(k);
                        String eare = dataObject.getString("cityName");
                        String suspected = dataObject.getString("suspectedCount");
                        if(suspected.equals("0"))
                            suspected = "-";
                        String total = dataObject.getString("confirmedCount");
                        String cure = dataObject.getString("curedCount");
                        String death = dataObject.getString("deadCount");
                        eareList.add(new Province(eare,total,suspected,cure,death));
                    }
                    break;
                }
            }

            Message mes = new Message();
            mes.what = 1;
            mHandler.sendMessage(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
