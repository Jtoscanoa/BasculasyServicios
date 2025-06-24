package com.puropoo.proyectobys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RequestAdapter extends BaseAdapter {

    private Context context;
    private List<Request> requests;

    public RequestAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        }

        Request request = requests.get(position);

        TextView tvServiceType = convertView.findViewById(R.id.tvServiceType);
        TextView tvServiceDate = convertView.findViewById(R.id.tvServiceDate);
        TextView tvServiceTime = convertView.findViewById(R.id.tvServiceTime);

        tvServiceType.setText(request.getServiceType());
        tvServiceDate.setText(request.getServiceDate());
        tvServiceTime.setText(request.getServiceTime());

        return convertView;
    }
}
