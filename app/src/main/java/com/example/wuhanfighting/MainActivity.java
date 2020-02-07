package com.example.wuhanfighting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private LocationClient mLocationClient;
    private List<Province> provincesList = new ArrayList<>();
    private ProvinceAdapter adapter;
    private Province p;
    private Handler mHandler;
    private StringBuilder head  = new StringBuilder();
    private String time;
    private TextView locationText;
    private TextView headText;
    private TextView diagnosed;
    private TextView suspect;
    private TextView death;
    private TextView cured;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //初始化各控件
        init();
        //设置recycle
        initRecycle();
        //处理handle机制
        initHandler();
        //初始化地理位置
        initLocation();
        //访问接口获取数据
        String response = OkHttpRequest.sendRequestWithOkHttp();
        //解析获取的json数据
        pareJSONData(response);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.record:
                Intent intent = new Intent(MainActivity.this,LPActivity.class);
                startActivity(intent);
                break;
            case R.id.save:
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
                break;
            case R.id.refresh:
                Log.d("123",progressBar.getVisibility()+"");
                if(progressBar.getVisibility() == View.GONE)
                    progressBar.setVisibility(View.VISIBLE);
                Log.d("123",progressBar.getVisibility()+"");
                //访问接口获取数据
                String response = OkHttpRequest.sendRequestWithOkHttp();
                //解析获取的json数据
                pareJSONData(response);
                Toast.makeText(MainActivity.this,"已刷新！！",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void initLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }
    }
    private void init(){
        headText = (TextView)findViewById(R.id.head_view);
        locationText = (TextView)findViewById(R.id.location_view);
        diagnosed = (TextView) findViewById(R.id.diagnosed);
        suspect = (TextView) findViewById(R.id.suspect);
        death = (TextView)findViewById(R.id.death);
        cured = (TextView)findViewById(R.id.cured);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.bringToFront();
        progressBar.setVisibility(View.GONE);
    }
    private void initHandler(){
        //新建Handler对象
        mHandler = new Handler(){
            //handleMessage为处理消息的方法
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //更新UI
                if(msg.what == 1) {
                    headText.setText(head);
                    diagnosed.setText("\n已确诊总人数\n"+p.getTotalNumber()+"\n");
                    suspect.setText("\n疑似总人数\n"+p.getSuspectedNumber()+"\n");
                    death.setText("\n死亡总人数\n"+p.getDeathNumber()+"\n");
                    cured.setText("\n治愈总人数\n"+p.getCureNumber()+"\n");
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
                //保存
                else if(msg.what == 2){
                    Record re = LitePal.findLast(Record.class);
                    if(re != null && re.getTime().equals(time)){
                        Toast.makeText(MainActivity.this,"重复保存！！",Toast.LENGTH_SHORT).show();
                    }else {
                        new Record(time,p.getTotalNumber(),p.getSuspectedNumber(),p.getCureNumber(),p.getDeathNumber()).save();
                        Toast.makeText(MainActivity.this,"已保存当前数据！！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }
    private void initRecycle() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProvinceAdapter(provincesList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }
    private void pareJSONData(final String JSONData) {
        try {
            JSONObject rootObject = new JSONObject(JSONData);
            JSONObject secondObject = rootObject.getJSONObject("data");
            time = secondObject.getString("date");
            provincesList.clear();
            String totalDiagnosed = secondObject.getString("diagnosed");
            String totalSuspect = secondObject.getString("suspect");
            String totalCured = secondObject.getString("cured");
            String totalDeath = secondObject.getString("death");
            p = new Province("中国",totalDiagnosed,totalSuspect,totalCured,totalDeath);
            head = new StringBuilder();
            head.append(" 截至："+time+"\n 中国各地区疫情如下");
            //处理具体省份信息
            JSONArray jsonArray = secondObject.getJSONArray("list");
            provincesList.add(new Province("地区","确诊病例","疑似病例","治愈病例","死亡病例"));
            for (int i = 0; i < jsonArray.length(); i++) {
                String data = jsonArray.optString(i);
                //处理获得的字符串
                String arr[] = data.split(" ");
                String eare = "";
                String suspected = "-";
                String total = "0";
                String cure = "0";
                String death = "0";
                for(int j = 0;j < arr.length;j++)
                {
                    if(j == 0)
                    eare = arr[j];
                    else if(arr[j].equals("确诊"))
                    {
                        total = arr[j+1];
                        j++;
                    }
                    else if(arr[j].equals("例，疑似"))
                    {
                        suspected = arr[j+1];
                        j++;
                    }
                    else if (arr[j].equals("例，治愈"))
                    {
                        cure = arr[j+1];
                        j++;
                    }
                    else if(arr[j].equals("例，死亡"))
                    {
                        death = arr[j+1];
                        j++;
                    }
                }
                provincesList.add(new Province(eare,total,suspected,cure,death));
            }

            Message mes = new Message();
            mes.what = 1;
            mHandler.sendMessage(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void requestLocation(){
        initUpdata();
        mLocationClient.start();
    }
    //每五秒刷新一下当前位置
    private void initUpdata(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:if (grantResults.length > 0){
                for(int result : grantResults){
                    if(result != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"必须同意才能使用地理位置",Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                requestLocation();
            }else {
                Toast.makeText(this,"发生未知错误",Toast.LENGTH_LONG).show();
                finish();
            }
            break;
            default:break;
        }
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder loc = new StringBuilder();
            loc.append(" 您当前位置为：\n");
            loc.append(" 纬度：").append(bdLocation.getLatitude()).append(" ");
            loc.append("经度：").append(bdLocation.getLongitude()).append("\n");
            loc.append(" 国家：").append(bdLocation.getCountry()).append("   ");
            loc.append("省：").append(bdLocation.getProvince()).append("\n");
            loc.append(" 市级：").append(bdLocation.getCity()).append("   ");
            loc.append("区：").append(bdLocation.getDistrict()).append("\n");
            loc.append(" 街道：").append(bdLocation.getStreet());
            locationText.setText(loc);
        }
    }
}
