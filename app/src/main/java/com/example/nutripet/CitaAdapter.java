package com.example.nutripet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private List<CitaMedica> listaCitas;

    public CitaAdapter(List<CitaMedica> listaCitas) {
        this.listaCitas = listaCitas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CitaMedica cita = listaCitas.get(position);
        holder.tvTitulo.setText(cita.titulo);
        holder.tvFecha.setText(cita.fecha + " - " + cita.hora);
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(android.R.id.text1);
            tvFecha = itemView.findViewById(android.R.id.text2);
        }
    }
}