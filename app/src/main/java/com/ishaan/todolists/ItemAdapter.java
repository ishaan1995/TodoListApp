package com.ishaan.todolists;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ishaan on 05/05/16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.Viewholder> {


    Context context;
    LayoutInflater inflater;
    ArrayList<Item> items = new ArrayList<>();



    public ItemAdapter(Context context,ArrayList<Item> items){
        this.items=items;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemCount() {
        return  items.size();
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_item, parent, false);
        Viewholder viewHolder = new Viewholder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {

        holder.Item.setText(items.get(position).getListItem());
        holder.dueTime.setText(items.get(position).getDateTime());
        if (items.get(position).getPlace().equals("Pick a Place")){
            holder.Place.setText("Home");
        }else {

            holder.Place.setText(items.get(position).getPlace());
        }

        if (!items.get(position).getColor().equals("#ffffff")){
            holder.relativeLayout.getBackground().setColorFilter(Color.parseColor(items.get(position).getColor()), PorterDuff.Mode.MULTIPLY);
        }
        else {
            holder.relativeLayout.getBackground().clearColorFilter();
        }

    }

    class Viewholder extends RecyclerView.ViewHolder{

        TextView Item,dueTime,Place;
        RelativeLayout relativeLayout;


        public Viewholder(View itemview){
            super(itemview);
            Item = (TextView) itemview.findViewById(R.id.item);
            dueTime = (TextView) itemview.findViewById(R.id.duetime);
            relativeLayout = (RelativeLayout) itemview.findViewById(R.id.relativeLayout);
            Place = (TextView) itemview.findViewById(R.id.place);
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Black.ttf");
            Typeface typeface2 = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Bold.ttf");
            Typeface typeface3 = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/Roboto-Light.ttf");
            Item.setTypeface(typeface2);
            dueTime.setTypeface(typeface3);
            //Place.setTypeface(typeface);

        }

    }
}
