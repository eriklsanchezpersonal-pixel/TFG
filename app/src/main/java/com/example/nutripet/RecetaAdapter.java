package com.example.nutripet;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // 🌟 Importante
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private final List<Receta> listaRecetas;
    private final OnRecetaClickListener listener;

    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta);
    }

    public RecetaAdapter(List<Receta> listaRecetas, OnRecetaClickListener listener) {
        this.listaRecetas = listaRecetas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta receta = listaRecetas.get(position);
        holder.tvNombre.setText(receta.getNombre_receta());
        // Asumiendo que también quieres mostrar estos campos si existen en tu objeto Receta
        holder.tvTiempo.setText("Tiempo: " + receta.getTiempo_preparacion() + " min");

        //Clic en el botón de info -> Abre detalle
        if (holder.btnInfo != null) {
            holder.btnInfo.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DetalleRecetaActivity.class);
                intent.putExtra("ID_RECETA", receta.getId_receta());
                v.getContext().startActivity(intent);
            });
        }

        //Clic en la fila completa (Asignar)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecetaClick(receta);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTiempo, tvInstrucciones;
        ImageView btnInfo;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombreReceta);
            tvTiempo = itemView.findViewById(R.id.tvItemTiempoReceta);
            tvInstrucciones = itemView.findViewById(R.id.tvItemInstrucciones);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }
}