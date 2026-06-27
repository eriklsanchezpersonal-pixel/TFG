package com.example.nutripet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adaptador para gestionar la visualización de la lista de citas en el RecyclerView
public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {
    private static final String TAG = "DEBUG_CITA_ADAPTER"; // Tag para filtrar en Logcat
    private List<CitaMedica> listaCitas;
    private OnCitaClickListener listener;

    // Constructor simple
    public CitaAdapter(List<CitaMedica> listaCitas) {
        this.listaCitas = listaCitas;
    }

    // Interfaz para delegar la acción de borrar a la Activity
    public interface OnCitaClickListener {
        void onBorrarClick(CitaMedica cita);
    }

    // Constructor con listener para manejar el borrado
    public CitaAdapter(List<CitaMedica> listaCitas, OnCitaClickListener listener) {
        this.listaCitas = listaCitas;
        this.listener = listener;
        Log.d(TAG, "Adapter inicializado con " + (listaCitas != null ? listaCitas.size() : 0) + " elementos");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño XML definido para cada fila de cita
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CitaMedica cita = listaCitas.get(position);

        // Asignamos los datos a la vista
        holder.tvTitulo.setText(cita.titulo);
        holder.tvFechaHora.setText("Fecha: " + cita.fecha + " | Hora: " + cita.hora);
        Log.d(TAG, "Vinculando datos en posición " + position + ": " + cita.titulo);

        // Acción de borrar: notifica al listener (la Activity) que se ha hecho clic en el botón
        holder.btnBorrar.setOnClickListener(v -> {
            Log.d(TAG, "Botón borrar pulsado para cita: " + cita.titulo);
            if (listener != null) {
                listener.onBorrarClick(cita);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCitas != null ? listaCitas.size() : 0;
    }

    // ViewHolder para mantener las referencias de los elementos de la interfaz
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFechaHora;
        android.widget.ImageButton btnBorrar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }

    // Metodo para refrescar la lista de datos desde la Activity
    public void updateList(List<CitaMedica> nuevaLista) {
        Log.d(TAG, "Actualizando lista. Nuevos elementos: " + (nuevaLista != null ? nuevaLista.size() : 0));
        this.listaCitas = nuevaLista;
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }
}