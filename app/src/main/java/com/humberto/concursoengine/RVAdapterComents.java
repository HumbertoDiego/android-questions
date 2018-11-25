package com.humberto.concursoengine;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class RVAdapterComents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Utils.Coment> coments =new ArrayList<>();

    RVAdapterComents(List<Utils.Coment> coments){
        this.coments = coments;
    }

    RVAdapterComents(){

    }

    // classe q define os findViewById() methods,
    public class PersonViewHolder extends RecyclerView.ViewHolder {

        // UI Elements
        CardView cv;
        ImageView personPhoto;
        ImageView comentPhoto;
        TextView personName;
        TextView comentDate;
        TextView personComent;
        ToggleButton curtidas;
        TextView nrCurtidas;

        PersonViewHolder(View itemView) {
            super(itemView);
            this.cv = (CardView)itemView.findViewById(R.id.cv_coment);
            this.personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.comentPhoto = (ImageView) itemView.findViewById(R.id.coment_photo);
            this.personName = (TextView)itemView.findViewById(R.id.person_name);
            this.comentDate = (TextView) itemView.findViewById(R.id.coment_date);
            this.personComent = (TextView) itemView.findViewById(R.id.person_coment);
            this.nrCurtidas = (TextView) itemView.findViewById(R.id.curtir);

            this.curtidas = (ToggleButton) itemView.findViewById(R.id.curtir_toggle_button);
        }

    }


    // classe q define os findViewById() methods,
    public class UserViewHolder extends RecyclerView.ViewHolder {

        // UI Elements
        CardView cv;
        ImageView personPhoto;
        ImageView comentPhoto;
        TextView personName;
        TextView comentDate;
        TextView personComent;
        ToggleButton curtidas;
        TextView nrCurtidas;

        UserViewHolder(View itemView) {
            super(itemView);
            this.cv = (CardView)itemView.findViewById(R.id.cv_coment);
            this.personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.comentPhoto = (ImageView) itemView.findViewById(R.id.coment_photo);

            this.personName = (TextView)itemView.findViewById(R.id.person_name);
            this.comentDate = (TextView) itemView.findViewById(R.id.coment_date);
            this.personComent = (TextView) itemView.findViewById(R.id.person_coment);
            this.nrCurtidas = (TextView) itemView.findViewById(R.id.curtir);

            this.curtidas = (ToggleButton) itemView.findViewById(R.id.curtir_toggle_button);

        }

    }



    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // onCreateViewHolder : inflates the row layout
    // manages the findViewById() methods,
    // finding the views once and recycling them to avoid repeated calls.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v;
        if (i==0) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_coment, viewGroup, false);
            return new PersonViewHolder(v);
        } else{
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_coment_user, viewGroup, false);
            return new UserViewHolder(v);
        }

    }

    //onBindViewHolder : populate the rows
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        // i = posição dentro da arrayList
        final Utils.Coment coment = coments.get(i);
        if (holder.getItemViewType()==0) {
            final PersonViewHolder personViewHolder = (PersonViewHolder)holder;
            personViewHolder.personName.setText(coment.nome);
            personViewHolder.comentDate.setText(coment.date);
            personViewHolder.personPhoto.setImageBitmap(coment.userFoto);
            personViewHolder.comentPhoto.setImageBitmap(coment.comentFoto);
            personViewHolder.personComent.setText(coment.comentario);
            personViewHolder.nrCurtidas.setText(String.valueOf(coment.curtidas));
            personViewHolder.curtidas.setChecked(coment.liked);
            personViewHolder.curtidas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String count = (String) personViewHolder.nrCurtidas.getText();
                    String key;
                    int countint = Integer.parseInt(count);
                    if (isChecked){
                        countint++;
                        personViewHolder.nrCurtidas.setText(String.valueOf(countint));
                        key = "add";
                    }
                    else{
                        countint--;
                        personViewHolder.nrCurtidas.setText(String.valueOf(countint));
                        key = "del";
                    }
                    System.out.println(countint);
                    String coment_api_url = "http://humbertoalves.pythonanywhere.com/coments/"+ coment.currentUserEmail+"/"+ coment.comentId+"/"+key;
                    PutAsyncTask mlikessync = new PutAsyncTask(coment_api_url);
                    mlikessync.execute();
                }
            });

        } else{
            final UserViewHolder userViewHolder = (UserViewHolder)holder;
            userViewHolder.personName.setText(coment.nome);
            userViewHolder.comentDate.setText(coment.date);
            userViewHolder.personPhoto.setImageBitmap(coment.userFoto);
            userViewHolder.comentPhoto.setImageBitmap(coment.comentFoto);
            userViewHolder.comentPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    System.out.println("longclick");
                    return false;
                }
            });
            userViewHolder.personComent.setText(coment.comentario);
            userViewHolder.nrCurtidas.setText(String.valueOf(coment.curtidas));
            userViewHolder.curtidas.setChecked(coment.liked);
            userViewHolder.curtidas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String count = (String) userViewHolder.nrCurtidas.getText();
                    String key;
                    int countint = Integer.parseInt(count);
                    if(userViewHolder.curtidas.isChecked()){
                        countint++;
                        userViewHolder.nrCurtidas.setText(String.valueOf(countint));
                        key = "add";
                    }
                    else{
                        countint--;
                        userViewHolder.nrCurtidas.setText(String.valueOf(countint));
                        key = "del";
                    }
                    System.out.println(countint);
                    String coment_api_url = "http://humbertoalves.pythonanywhere.com/coments/"+ coment.currentUserEmail+"/"+ coment.comentId+"/"+key;
                    PutAsyncTask mlikessync = new PutAsyncTask(coment_api_url);
                    mlikessync.execute();
                }
            });
        }

    }

    @Override
    public int getItemCount() { return coments.size(); }

    @Override
    public int getItemViewType(int position) { return coments.get(position).getType_row(); }


    public void updateList(List<Utils.Coment> data, int position) {
        coments = data;
        notifyItemChanged(position);
    }

    public void addItem(Utils.Coment coment){
        coments.add(coment);
        notifyItemInserted(coments.size());
    }

}
