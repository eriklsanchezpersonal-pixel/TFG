package com.example.nutripet;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private final List<Receta> listaRecetas;
    private final OnRecetaClickListener listener;
    private final boolean esModoAsignacion;
    private final boolean mostrarBotonEliminar;
    public interface OnRecetaClickListener {
        void onRecetaClick(Receta receta, boolean esEliminar);
    }

    public RecetaAdapter(List<Receta> listaRecetas, OnRecetaClickListener listener, boolean esModoAsignacion, boolean mostrarBotonEliminar) {
        this.listaRecetas = listaRecetas;
        this.listener = listener;
        this.esModoAsignacion = esModoAsignacion;
        this.mostrarBotonEliminar = mostrarBotonEliminar;
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
        holder.tvTiempo.setText("Tiempo: " + receta.getTiempo_preparacion() + " min");

        holder.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleRecetaActivity.class);
            intent.putExtra("ID_RECETA", receta.getId_receta());
            v.getContext().startActivity(intent);
        });

        if (esModoAsignacion) {
            holder.btnAddReceta.setVisibility(View.VISIBLE);
            holder.btnAddReceta.setImageResource(R.drawable.ic_add);
            holder.btnAddReceta.setColorFilter(Color.parseColor("#2E7D32"));
            holder.btnAddReceta.setOnClickListener(v -> listener.onRecetaClick(receta, false));
        } else if (mostrarBotonEliminar) {
            holder.btnAddReceta.setVisibility(View.VISIBLE);
            holder.btnAddReceta.setImageResource(R.drawable.ic_remove);
            holder.btnAddReceta.setColorFilter(Color.parseColor("#D32F2F"));
            holder.btnAddReceta.setOnClickListener(v -> listener.onRecetaClick(receta, true));
        } else {
            holder.btnAddReceta.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    // Metodo para refrescar la lista
    public void actualizarLista(List<Receta> nuevaLista) {
        this.listaRecetas.clear();
        this.listaRecetas.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTiempo, tvInstrucciones;
        ImageView btnInfo;
        ImageButton btnAddReceta;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombreReceta);
            tvTiempo = itemView.findViewById(R.id.tvItemTiempoReceta);
            tvInstrucciones = itemView.findViewById(R.id.tvIngredientes);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnAddReceta = itemView.findViewById(R.id.btnAddReceta);
        }
    }
}