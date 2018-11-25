package com.humberto.concursoengine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    List <Utils.DownloadableDb> mValues =new ArrayList<Utils.DownloadableDb>();
    public static final Map<String, Utils.DownloadableDb> ITEM_MAP = new HashMap <String, Utils.DownloadableDb>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        inittalizeDownloadDBsData();

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void inittalizeDownloadDBsData() {
        Utils.DownloadableDb aux;

        aux = new Utils.DownloadableDb("1","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("1",aux);

        aux = new Utils.DownloadableDb("2","ENEM","10 anos de provas antigas de enem!",2);
        mValues.add(aux);
        ITEM_MAP.put("2",aux);

        aux = new Utils.DownloadableDb("3","ENEM","10 anos de provas antigas de enem!",3);
        mValues.add(aux);
        ITEM_MAP.put("3",aux);

        aux = new Utils.DownloadableDb("4","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("4",aux);

        aux = new Utils.DownloadableDb("1","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("1",aux);

        aux = new Utils.DownloadableDb("2","ENEM","10 anos de provas antigas de enem!",2);
        mValues.add(aux);
        ITEM_MAP.put("2",aux);

        aux = new Utils.DownloadableDb("3","ENEM","10 anos de provas antigas de enem!",3);
        mValues.add(aux);
        ITEM_MAP.put("3",aux);

        aux = new Utils.DownloadableDb("4","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("4",aux);

        aux = new Utils.DownloadableDb("1","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("1",aux);

        aux = new Utils.DownloadableDb("2","ENEM","10 anos de provas antigas de enem!",2);
        mValues.add(aux);
        ITEM_MAP.put("2",aux);

        aux = new Utils.DownloadableDb("3","ENEM","10 anos de provas antigas de enem!",3);
        mValues.add(aux);
        ITEM_MAP.put("3",aux);

        aux = new Utils.DownloadableDb("4","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("4",aux);

        aux = new Utils.DownloadableDb("1","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("1",aux);

        aux = new Utils.DownloadableDb("2","ENEM","10 anos de provas antigas de enem!",2);
        mValues.add(aux);
        ITEM_MAP.put("2",aux);

        aux = new Utils.DownloadableDb("3","ENEM","10 anos de provas antigas de enem!",3);
        mValues.add(aux);
        ITEM_MAP.put("3",aux);

        aux = new Utils.DownloadableDb("4","ENEM","10 anos de provas antigas de enem!",1);
        mValues.add(aux);
        ITEM_MAP.put("4",aux);


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mValues));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Utils.DownloadableDb> myValues;

        public SimpleItemRecyclerViewAdapter(List<Utils.DownloadableDb> items) {
            myValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row_download_dbs, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mOrdView.setText(myValues.get(position).ord);
            holder.mNameView.setText(myValues.get(position).nome);
            holder.mDbDescription.setText(myValues.get(position).desc);
            if (myValues.get(position).viewType==1){
                holder.downloadOuBuyButton.setText("R$ 1,00");
            }
            else if (myValues.get(position).viewType==2){
                holder.downloadOuBuyButton.setText("grátis");
            }
            else if (myValues.get(position).viewType==3){
                holder.mView.setBackgroundColor(getColor(android.R.color.holo_blue_dark));
                holder.downloadOuBuyButton.setText("grátis");
            }
            else if (myValues.get(position).viewType==4){
                holder.mView.setBackgroundColor(getColor(android.R.color.holo_blue_dark));
                holder.downloadOuBuyButton.setText("R$ 1,00");
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, myValues.get(position).ord);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, myValues.get(position).ord);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mOrdView;
            public final TextView mNameView;
            public final TextView mDbDescription;
            public final Button downloadOuBuyButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mOrdView = (TextView) view.findViewById(R.id.ord);
                mNameView = (TextView) view.findViewById(R.id.db_name);
                mDbDescription = (TextView) view.findViewById(R.id.db_desc);
                downloadOuBuyButton = (Button) view.findViewById(R.id.download_or_buy_button);
            }

        }
    }
}
