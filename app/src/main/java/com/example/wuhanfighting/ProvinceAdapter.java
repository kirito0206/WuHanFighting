package com.example.wuhanfighting;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ViewHolder>{

    private List<Province> provinceList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        View provinceView;
        TextView eare;
        TextView suspected;
        TextView total;
        TextView cure;
        TextView death;

        public ViewHolder(View v){
            super(v);
            provinceView = v;
            eare = (TextView) v.findViewById(R.id.eare);
            suspected = (TextView) v.findViewById(R.id.suspected_number);
            total = (TextView) v.findViewById(R.id.total_number);
            cure = (TextView) v.findViewById(R.id.cure_number);
            death = (TextView) v.findViewById(R.id.death_number);
        }
    }

    public ProvinceAdapter(List<Province> pList){
        provinceList = pList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.province_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.provinceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取view对应的位置
                int position=holder.getLayoutPosition();
                if(position == 0 || !provinceList.get(1).getProvince().equals("湖北"))
                    return;
                Intent intent = new Intent(MyApplication.getContext(),DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("province",provinceList.get(position).getProvince());
                MyApplication.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Province province = provinceList.get(position);
        holder.eare.setText(province.getProvince());
        holder.suspected.setText((province.getSuspectedNumber()));
        holder.cure.setText(province.getCureNumber());
        holder.total.setText((province.getTotalNumber()));
        holder.death.setText(province.getDeathNumber());
    }

    @Override
    public int getItemCount() {
        return provinceList.size();
    }
}
