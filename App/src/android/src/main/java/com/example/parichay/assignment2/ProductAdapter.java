package com.example.parichay.assignment2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Parichay on 2/21/2016.
 */

//custom adapter used to display customised list
public class ProductAdapter extends BaseAdapter {
    private Context mcontext;
    private List<Product> mProductList;

    public ProductAdapter(Context mcontext, List<Product> mProductList) {
        this.mcontext = mcontext;
        this.mProductList = mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(mcontext,R.layout.item_product_list,null);
        TextView coursecode=(TextView)v.findViewById(R.id.course_code);
        TextView coursename=(TextView)v.findViewById(R.id.course_name);
        TextView courseltp=(TextView)v.findViewById(R.id.course_ltp);

        coursecode.setText(mProductList.get(position).getCode());
        coursename.setText(mProductList.get(position).getName());
        courseltp.setText(mProductList.get(position).getLtp());
        v.setTag(mProductList.get(position).getId());

        return v;
    }
}
