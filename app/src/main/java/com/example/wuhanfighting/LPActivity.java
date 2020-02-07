package com.example.wuhanfighting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

public class LPActivity extends AppCompatActivity {

    private List<Record> recordList;
    private LinearLayout mContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lp);
        recordList = LitePal.findAll(Record.class);
        mContainer = findViewById(R.id.container);
        initView();
    }

    private void initView(){
        while(recordList.size()>10)
        {
            Record record = recordList.get(0);
            recordList.remove(0);
            record.delete();
        }
        for(int i = 0;i < recordList.size();i++){
            Record record = recordList.get(i);
            TextView timeView = new TextView(this);
            StringBuilder re = new StringBuilder();
            re.append(record.getTime());
            re.append("\n").append("确认病例总人数：").append(record.getTotalDiagnosed());
            re.append("\n").append("疑似病例总人数：").append(record.getTotalSuspect());
            re.append("\n").append("治愈病例总人数：").append(record.getTotalCured());
            re.append("\n").append("死亡病例总人数：").append(record.getTotalDeath());
            re.append("\n");
            timeView.setText(re);
            timeView.setTextSize(30);
            mContainer.addView(timeView);
        }
    }
}
