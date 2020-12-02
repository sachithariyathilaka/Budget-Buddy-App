package com.budget.buddy.moneytracking.Adapters;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.budget.buddy.moneytracking.Activities.DetailActivity;
import com.budget.buddy.moneytracking.Entities.PlannedItem;
import com.budget.buddy.moneytracking.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PlannedItemAdapter extends RecyclerView.Adapter<PlannedItemAdapter.ViewHolder> {

    private List<PlannedItem> plannedItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtAmount;
        public TextView txtDate;
        public ImageView iconitem;

        //planned
        public TextView txtRepeat;
        public TextView txtOccurrence;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.planned_itemlist_title);
            txtAmount = (TextView) v.findViewById(R.id.planned_itemlist_amount);
            iconitem = (ImageView) v.findViewById(R.id.planned_itemlist_icon);

            //planned
            txtRepeat = (TextView) v.findViewById(R.id.planned_itemlist_repeat);
            txtDate = (TextView) v.findViewById(R.id.planned_itemlist_date);
            txtOccurrence = (TextView) v.findViewById(R.id.planned_itemlist_occurrence);
        }
    }

    public void add(int position, PlannedItem item) {
        plannedItems.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();

    }

    public void remove(int position) {
        plannedItems.remove(position);
        notifyItemRemoved(position);
    }


    public PlannedItemAdapter(List<PlannedItem> myDataset) {
        plannedItems = myDataset;
    }


    @Override
    public PlannedItemAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.planned_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final View.OnClickListener titleListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("planned_item", plannedItems.get(position));
                intent.putExtra("planned", true);
                v.getContext().startActivity(intent);
                notifyDataSetChanged();

            }
        };
        holder.layout.setOnClickListener(titleListener);


        String name = plannedItems.get(holder.getAdapterPosition()).getName();
        double d_amount = plannedItems.get(holder.getAdapterPosition()).getAmount();
        DecimalFormat df = new DecimalFormat("#.00");
        String amount = df.format(d_amount);
        String occurrence = plannedItems.get(holder.getAdapterPosition()).getOccurrence();
        String repeat = plannedItems.get(holder.getAdapterPosition()).getRepeat().toString();

        Date d = (plannedItems.get(holder.getAdapterPosition()).getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        holder.txtTitle.setText(name);
        holder.txtAmount.setText(amount + "Rs");
        holder.txtDate.setText(sdf.format(d.getTime()));
        holder.txtRepeat.setText(repeat);
        holder.txtOccurrence.setText(occurrence);


        if (plannedItems.get(holder.getAdapterPosition()).getAmount() > 0) {
            holder.iconitem.setImageResource(R.drawable.thumb_up);
        } else {
            holder.iconitem.setImageResource(R.drawable.thumb_down);
        }


    }


    @Override
    public int getItemCount() {
        return plannedItems.size();
    }

}