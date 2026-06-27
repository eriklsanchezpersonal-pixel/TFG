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
    private OnCitaClickListener listener;
    public CitaAdapter(List<CitaMedica> listaCitas) {
        this.listaCitas = listaCitas;
    }
    public interface OnCitaClickListener {
        void onBorrarClick(CitaMedica cita);
    }

    public CitaAdapter(List<CitaMedica> listaCitas, OnCitaClickListener listener) {
        this.listaCitas = listaCitas;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CitaMedica cita = listaCitas.get(position);
        holder.tvTitulo.setText(cita.titulo);
        holder.tvFechaHora.setText("Fecha: " + cita.fecha + " | Hora: " + cita.hora);

        // Acción de borrar
        holder.btnBorrar.setOnClickListener(v -> listener.onBorrarClick(cita));
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFechaHora;
        android.widget.ImageButton btnBorrar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            btnBorrar = itemView.findViewById(R.id.btnBorrar); // Asegúrate de añadir este ID en XML
        }
    }

    public void updateList(List<CitaMedica> nuevaLista) {
        this.listaCitas = nuevaLista;
        notifyDataSetChanged();
    }
}