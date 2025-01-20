package com.rene98c.bittyprice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rene98c.bittyprice.core.PersistentAppObject;
import com.rene98c.bittyprice.core.PortfolioItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity  {
    PersistentAppObject appObject;
    Button btn1,btn2;
    PortfolioViewAdapter adapter;
    RecyclerView recyclerView;
    ClickListener listener;
    List<PortfolioItem> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        super.setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        appObject = new PersistentAppObject(this.getBaseContext());


        SettingsActivity context = this;
        recyclerView
                = findViewById(
                R.id.recyclerView);
        SettingsActivity parent = this;
        listener = new ClickListener() {
            @Override
            public void click(int index){
                Intent intent = new Intent(context, PortfolioEditActivity.class);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        };
        items = new ArrayList<>();
        adapter  = new PortfolioViewAdapter(items, getApplication(), listener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        btn1 = findViewById(R.id.buttonAdd);

        btn1.setOnClickListener(v -> startActivity(new Intent(context, PortfolioEditActivity.class)));

        btn2 = findViewById(R.id.buttonRemove);
        btn2.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to clear all?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        int size = items.size();
                        items.clear();
                        appObject.writePortfolio(items);
                        adapter.notifyItemRangeRemoved(0,size);
                        Toast.makeText(this, "Portfolio cleared", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //just clear and add again, could be optimized, works well for now
        List<PortfolioItem> list = appObject.readPortfolio();
        int size = items.size();
        items.clear();
        adapter.notifyItemRangeRemoved(0,size);
        items.addAll(list);
        adapter.notifyItemRangeInserted(0,list.size());
    }

    private static class PortfolioItemViewHolder  extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemAmount;
        TextView itemPair;
        View view;

        PortfolioItemViewHolder(View itemView)
        {
            super(itemView);
            itemName
                    = itemView
                    .findViewById(R.id.portfolioAltCoinName);
            itemAmount
                    = itemView
                    .findViewById(R.id.portfolioItemTotal);
            itemPair
                    = itemView
                    .findViewById(R.id.portfolioItemPairName);
            view  = itemView;
        }
    }



    private static class ClickListener {
        public void click(int index){

        }
    }
    private static class PortfolioViewAdapter
            extends RecyclerView.Adapter<PortfolioItemViewHolder> {

        List<PortfolioItem> list;

        Context context;
        ClickListener listener;

        public PortfolioViewAdapter(List<PortfolioItem> list,
                                    Context context, ClickListener listener)
        {
            this.list = list;
            this.context = context;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PortfolioItemViewHolder
        onCreateViewHolder(ViewGroup parent,
                           int viewType)
        {

            Context context
                    = parent.getContext();
            LayoutInflater inflater
                    = LayoutInflater.from(context);

            // Inflate the layout

            View photoView
                    = inflater
                    .inflate(R.layout.currency_pair_item,
                            parent, false);

            return new PortfolioItemViewHolder(photoView);
        }

        @Override
        public void
        onBindViewHolder(final PortfolioItemViewHolder viewHolder,
                         final int position)
        {
            int index = viewHolder.getAdapterPosition();
            PortfolioItem item = list.get(position);
            String itemAmount = list.get(position).amount + "";
            viewHolder.itemName
                    .setText(item.name);
            viewHolder.itemAmount
                    .setText(itemAmount);
            viewHolder.itemPair
                    .setText(item.ticker);
            viewHolder.view.setOnClickListener(view -> listener.click(index));
        }

        @Override
        public int getItemCount()
        {
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(
                @NonNull RecyclerView recyclerView)
        {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }
}


