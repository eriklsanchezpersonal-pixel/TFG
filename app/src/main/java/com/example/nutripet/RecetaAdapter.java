package com.example.nutripet;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adaptador para el RecyclerView que muestra la lista de recetas disponibles o asignadas
public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {
    private static final String TAG = "DEBUG_RECETA_ADAPTER";
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
        Log.d(TAG, "RecetaAdapter inicializado. Modo asignación: " + esModoAsignacion);
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

        // Acción para ver detalles
        holder.btnInfo.setOnClickListener(v -> {
            Log.d(TAG, "Abriendo detalle de receta: " + receta.getNombre_receta());
            Intent intent = new Intent(v.getContext(), DetalleRecetaActivity.class);
            intent.putExtra("ID_RECETA", receta.getId_receta());
            v.getContext().startActivity(intent);
        });

        // Configuración de botones según el modo (Asignar/Eliminar)
        if (esModoAsignacion) {
            holder.btnAddReceta.setVisibility(View.VISIBLE);
            holder.btnAddReceta.setImageResource(R.drawable.ic_add);
            holder.btnAddReceta.setColorFilter(Color.parseColor("#2E7D32"));
            holder.btnAddReceta.setOnClickListener(v -> {
                Log.d(TAG, "Acción: Asignar receta " + receta.getNombre_receta());
                listener.onRecetaClick(receta, false);
            });
        } else if (mostrarBotonEliminar) {
            holder.btnAddReceta.setVisibility(View.VISIBLE);
            holder.btnAddReceta.setImageResource(R.drawable.ic_remove);
            holder.btnAddReceta.setColorFilter(Color.parseColor("#D32F2F"));
            holder.btnAddReceta.setOnClickListener(v -> {
                Log.d(TAG, "Acción: Eliminar receta " + receta.getNombre_receta());
                listener.onRecetaClick(receta, true);
            });
        } else {
            holder.btnAddReceta.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    public void actualizarLista(List<Receta> nuevaLista) {
        Log.d(TAG, "Refrescando lista de recetas. Nuevo tamaño: " + nuevaLista.size());
        this.listaRecetas.clear();
        this.listaRecetas.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTiempo;
        ImageView btnInfo;
        ImageButton btnAddReceta;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombreReceta);
            tvTiempo = itemView.findViewById(R.id.tvItemTiempoReceta);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            btnAddReceta = itemView.findViewById(R.id.btnAddReceta);
        }
    }
}