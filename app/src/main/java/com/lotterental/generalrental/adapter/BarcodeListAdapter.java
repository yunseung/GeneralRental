package com.lotterental.generalrental.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lotterental.generalrental.R;
import com.lotterental.generalrental.item.BarcodeListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jonathan on 2017-07-07.
 */

public class BarcodeListAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<BarcodeListItem> listViewItemList = new ArrayList<BarcodeListItem>() ;

    private Button bt_remove;

    private Context mContext;


    // ListViewAdapter의 생성자
    public BarcodeListAdapter(Context context) {
        mContext = context;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_scanner_code, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
//        final EditText tv_barcode = (EditText) convertView.findViewById(R.id.tv_barcode) ;
        TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num) ;

        final ViewHolder holder = new ViewHolder();
        holder.tv_barcode = (EditText) convertView.findViewById(R.id.tv_barcode) ;


        holder.bt_edit = (TextView)convertView.findViewById(R.id.bt_edit);
        holder.bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.tv_barcode.isEnabled())
                {
//                    listViewItemList.set(position);
                    holder.bt_edit.setText("수정");
                    holder.bt_edit.setTextColor(mContext.getResources().getColor(R.color.blue_text));

                    BarcodeListItem item = new BarcodeListItem();
                    item = listViewItemList.get(position);
                    item.setBarcode(holder.tv_barcode.getText().toString());
                    listViewItemList.set(position, item);
                    holder.tv_barcode.setEnabled(false);
                }
                else
                {
                    holder.bt_edit.setText("완료");
                    holder.bt_edit.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tv_barcode.setEnabled(true);
                }



            }
        });

        bt_remove = (Button)convertView.findViewById(R.id.bt_remove);
        bt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listViewItemList.remove(position);
                notifyDataSetChanged();

//                if(mContext instanceof ContinuousCaptureActivity){
//                    ((ContinuousCaptureActivity)mContext).setBarcodeText();
//                    ((ContinuousCaptureActivity)mContext).setBarcodeScanResume();
//                }

            }
        });

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        BarcodeListItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        holder.tv_barcode.setText(listViewItem.getBarcode());
        tv_num.setText(listViewItem.getNum());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public ArrayList<BarcodeListItem> getList()
    {
        return listViewItemList;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String barcode, String num) {
        BarcodeListItem item = new BarcodeListItem();

//        item.setIcon(icon);
        item.setBarcode(barcode);
        item.setNum(num);

        listViewItemList.add(item);


        Collections.sort(listViewItemList, new Comparator<BarcodeListItem>() {
            @Override
            public int compare(BarcodeListItem t1, BarcodeListItem t2) {

                int one = Integer.parseInt(t1.getNum());
                int two = Integer.parseInt(t2.getNum());

                if (one >= two) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });


        notifyDataSetChanged();

    }


    public boolean hasBarcode(String barcode)
    {

        for(int i = 0 ; i < listViewItemList.size() ; i++ )
        {
            if(barcode.equals(listViewItemList.get(i).getBarcode()))
            {
                return true;
            }

        }

        return false;

    }



    static class  ViewHolder {

        public EditText tv_barcode;
        public TextView bt_edit;
    }




}
