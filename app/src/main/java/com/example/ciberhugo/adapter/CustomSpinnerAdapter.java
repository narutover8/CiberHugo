package com.example.ciberhugo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.ciberhugo.fragment.LogsFragment;
import com.example.ciberhugo.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<LogsFragment> {

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull List<LogsFragment> items) {
        super(context, 0, items);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createCustomView(position, convertView, parent);
    }

    private View createCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.spinnerItemImage);

        LogsFragment item = getItem(position);

        if (item != null) {
            imageView.setImageResource(item.getImageResource());
        }

        return convertView;
    }
}