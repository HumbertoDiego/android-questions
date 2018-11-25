package com.humberto.concursoengine;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapterDatabases extends RecyclerView.Adapter<RVAdapterDatabases.DatabaseViewHolder>{

    List<Database> baralhos;
    Context ctx;
    private static RVAdapterBaralhos.ClickListener clickListener;

    RVAdapterDatabases(List<Database> baralhos,Context ctx){
        this.ctx = ctx;
        this.baralhos = baralhos;
    }

    public void setOnItemClickListener(RVAdapterBaralhos.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        public void itemClicked(View v, int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RVAdapterDatabases.DatabaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_database, viewGroup, false);
        RVAdapterDatabases.DatabaseViewHolder pvh = new RVAdapterDatabases.DatabaseViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVAdapterDatabases.DatabaseViewHolder baralhoViewHolder, int i) {
        baralhoViewHolder.dbTitle.setText(baralhos.get(i).title.replace(".db","").toUpperCase());
        baralhoViewHolder.dbDescriptor.setText(baralhos.get(i).descriptor);
        baralhoViewHolder.dbPhoto.setImageBitmap(baralhos.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return baralhos.size();
    }

    public class DatabaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView dbTitle;
        TextView dbDescriptor;
        ImageView dbPhoto;

        DatabaseViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            dbTitle = (TextView)itemView.findViewById(R.id.db_title);
            dbDescriptor = (TextView)itemView.findViewById(R.id.db_descriptor);
            dbPhoto = (ImageView)itemView.findViewById(R.id.db_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }
    }



}
