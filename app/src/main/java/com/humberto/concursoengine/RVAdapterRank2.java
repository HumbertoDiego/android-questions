package com.humberto.concursoengine;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapterRank2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView personOrd;
        TextView personName;
        TextView personExp;
        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv2);
            personOrd = (TextView)itemView.findViewById(R.id.ord);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personExp = (TextView) itemView.findViewById(R.id.person_exp);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        CardView cv5;
        TextView personOrd5;
        TextView personName5;
        TextView personExp5;
        ImageView personPhoto5;

        UserViewHolder(View itemView) {
            super(itemView);
            cv5 = (CardView)itemView.findViewById(R.id.cv5);
            personOrd5 = (TextView)itemView.findViewById(R.id.ord);
            personName5 = (TextView)itemView.findViewById(R.id.person_name);
            personExp5 = (TextView) itemView.findViewById(R.id.person_exp);
            personPhoto5 = (ImageView)itemView.findViewById(R.id.person_photo);
        }

        public void setContent(Bitmap bit) {
            personPhoto5.setImageBitmap(bit);
        }
    }



    public class TitleViewHolder extends RecyclerView.ViewHolder{

        TextView rankTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            rankTitle = (TextView) itemView.findViewById(R.id.rank_title);
        }
    }






    List<Utils.Person> persons;

    RVAdapterRank2(List<Utils.Person> persons){
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v;
        if(i==1)
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_rank_title, viewGroup, false);
            return new TitleViewHolder(v);

        }else if(i==2){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_rank_user, viewGroup, false);
            return new UserViewHolder(v);
        }
        else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_rank, viewGroup, false);
            return new PersonViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        Utils.Person pessoa = persons.get(i);

        // item type =1 => card_row_rank_title
        if(holder.getItemViewType()==1){
            final TitleViewHolder titleViewHolder = (TitleViewHolder)holder;
            titleViewHolder.rankTitle.setText(pessoa.getNome());
        }
        // item type =2 => card_row_rank_user
        else if (holder.getItemViewType()==2){
            ((UserViewHolder)holder).personOrd5.setText(pessoa.ord);
            ((UserViewHolder)holder).personName5.setText(pessoa.nome);
            ((UserViewHolder)holder).personPhoto5.setImageBitmap(pessoa.photoId);
            ((UserViewHolder)holder).personExp5.setText(pessoa.nivExp);


        }
        else{
            ((PersonViewHolder)holder).personOrd.setText(pessoa.ord);
            ((PersonViewHolder)holder).personName.setText(pessoa.nome);
            ((PersonViewHolder)holder).personPhoto.setImageBitmap(pessoa.photoId);
            ((PersonViewHolder)holder).personExp.setText(pessoa.nivExp);
        }
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public int getItemViewType(int position) {
        return persons.get(position).getType_row();
    }


    public void updateList(List<Utils.Person> data, int position) {
        persons = data;
//        notifyDataSetChanged();
        notifyItemChanged(position);
    }

}
