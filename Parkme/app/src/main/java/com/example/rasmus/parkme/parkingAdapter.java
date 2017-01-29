package com.example.rasmus.parkme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Rasmus on 2017-01-29.
 */

public class parkingAdapter extends BaseAdapter{

    Context context;
    ParkingLot[] data;
    private static LayoutInflater inflater = null;

    public parkingAdapter(Context context, ParkingLot[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.parking_lot_list_item, null);


        if (position % 2 == 1) {
            vi.setBackgroundColor(Color.parseColor("#9FA8DA"));

        } else {

            vi.setBackgroundColor(Color.WHITE);
        }

        TextView name_textView = (TextView) vi.findViewById(R.id.name_parking);
        TextView dictance_textView = (TextView) vi.findViewById(R.id.distance_to_parking);
        TextView price_textView = (TextView) vi.findViewById(R.id.price_of_parking);
        TextView lots_textView = (TextView) vi.findViewById(R.id.parking_lots);

        final ParkingLot parkingLot = data[position];


        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Search for restaurants in San Francisco
                Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q="+parkingLot.getName());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                final View parentView = parent;
                context.startActivity(mapIntent);
            }
        });


        name_textView.setText(parkingLot.getName());

        if(parkingLot.getCost() != null){
            dictance_textView.setText(parkingLot.getDistance()+" m");
        }else{
            dictance_textView.setText("");
        }

        if(parkingLot.getCost() != null){
            price_textView.setText(parkingLot.getCost());
        }else{
            price_textView.setText("");
        }

        if(parkingLot.getFreeSpaces() != -1){
            lots_textView.setTextColor(Color.GREEN);
            lots_textView.setText(parkingLot.getFreeSpaces()+" lediga platser");
        }else if(parkingLot.getSpaces() != -1){
            lots_textView.setTextColor(Color.BLACK);
            lots_textView.setText(parkingLot.getSpaces()+" parkeringar");
        }else{
            lots_textView.setText("");
        }

        return vi;
    }
}
