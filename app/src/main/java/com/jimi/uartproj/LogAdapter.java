package com.jimi.uartproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 类名: ${name}
 * 创建人: Liang GuoChang
 * 创建时间: ${date} ${time}
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class LogAdapter extends BaseAdapter {

    private List<String> logList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    ViewHOlder viewHOlder;

    public LogAdapter (Context Context,List<String> strings){
        this.mContext = Context;
        this.logList = strings;
        this.layoutInflater = LayoutInflater.from(Context);
    }

    @Override
    public int getCount() {
        return logList.size() > 0 ? logList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return logList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            viewHOlder = new ViewHOlder();
            view = layoutInflater.inflate(R.layout.log_list_item,null);
            viewHOlder.tv_log = view.findViewById(R.id.tv_log_item);
            view.setTag(viewHOlder);
        }else {
            viewHOlder = (ViewHOlder) view.getTag();
        }
        String log = logList.get(i);
        viewHOlder.tv_log.setText(log);
        return view;
    }

    class ViewHOlder{
        TextView tv_log;
    }

}
