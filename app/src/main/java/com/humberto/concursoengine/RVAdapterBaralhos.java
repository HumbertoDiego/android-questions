package com.humberto.concursoengine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RVAdapterBaralhos extends RecyclerView.Adapter<RVAdapterBaralhos.BaralhoViewHolder> {

    List<Utils.Baralho> baralhos;
    Context ctx;
    private static ClickListener clickListener;

    RVAdapterBaralhos(List<Utils.Baralho> baralhos,Context ctx){
        this.ctx = ctx;
        this.baralhos = baralhos;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
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
    public BaralhoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_baralho, viewGroup, false);
        BaralhoViewHolder pvh = new BaralhoViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(BaralhoViewHolder baralhoViewHolder, int i) {
        baralhoViewHolder.baralhoName.setText(baralhos.get(i).name);
        baralhoViewHolder.baralhoData.setText(baralhos.get(i).age);
        baralhoViewHolder.baralhoPhoto.setImageBitmap(baralhos.get(i).photoId);
        baralhoViewHolder.baralhobadgeNew.setText(baralhos.get(i).badgeNew);
        baralhoViewHolder.baralhobadgeRev.setText(baralhos.get(i).badgeRev);
    }

    @Override
    public int getItemCount() {
        return baralhos.size();
    }

    public class BaralhoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView baralhoName;
        TextView baralhoData;
        TextView baralhobadgeNew;
        TextView baralhobadgeRev;
        ImageView baralhoPhoto;

        BaralhoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            baralhoName = (TextView)itemView.findViewById(R.id.person_name);
            baralhoData = (TextView)itemView.findViewById(R.id.baralho_data);
            baralhoPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            baralhobadgeNew = (TextView)itemView.findViewById(R.id.badge_new);
            baralhobadgeRev = (TextView)itemView.findViewById(R.id.badge_rev);
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
