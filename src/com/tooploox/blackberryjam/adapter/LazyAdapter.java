package com.tooploox.blackberryjam.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tooploox.blackberryjam.R;
import com.tooploox.blackberryjam.data.ListItemData;

public class LazyAdapter extends ArrayAdapter<ListItemData> {

    private Activity activity;
    private Vector<ListItemData> data;
    private static LayoutInflater inflater = null;

    // public ImageLoader imageLoader;

    public LazyAdapter(Activity a, Vector<ListItemData> d) {
        super(a, R.id.productList, R.id.text, d);
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public ListItemData getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) vi = inflater.inflate(R.layout.item, null);

        TextView text = (TextView) vi.findViewById(R.id.text);;
        TextView price = (TextView) vi.findViewById(R.id.price);
        TextView noItems = (TextView) vi.findViewById(R.id.tvNoItems);
        TextView prdValue = (TextView) vi.findViewById(R.id.tvProductValue);
        ImageView image = (ImageView) vi.findViewById(R.id.image);


        text.setText(data.get(position).getCaption());
        price.setText(data.get(position).getPrice());
        noItems.setText(String.valueOf(data.get(position).getNoItems()));
        prdValue.setText(data.get(position).getProductValue() + ".00 PLN");
        // TODO: add price tag display.
        image.setImageBitmap(data.get(position).getImage());
        return vi;
    }
}
