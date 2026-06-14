package com.example.nutripet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> listaMascotas;
    private Context context;

    // Constructor
    public MascotaAdapter(List<Mascota> listaMascotas, Context context) {
        this.listaMascotas = listaMascotas;
        this.context = context;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Enlazamos con el XML que creamos en el paso 1
        View view = LayoutInflater.from(context).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);

        // Inyectamos los datos reales de Room en los TextViews
        holder.tvNombre.setText(mascota.getNombre());
        holder.tvMicrochip.setText("Microchip: " + mascota.getMicrochip());
        holder.tvDatos.setText("Actividad: " + mascota.getNivel_actividad() + " | Peso: " + mascota.getPeso_actual() + "kg");
    }

    @Override
    public int getItemCount() {
        return listaMascotas.size();
    }

    // 🌟 Este método limpia la lista visual y dibuja la nueva cuando volvemos del alta
    public void updateList(List<Mascota> nuevaLista) {
        this.listaMascotas = nuevaLista;
        notifyDataSetChanged();
    }

    // Clase interna para mapear los elementos visuales de la tarjeta
    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMicrochip, tvDatos;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreItem);
            tvMicrochip = itemView.findViewById(R.id.tvMicrochipItem);
            tvDatos = itemView.findViewById(R.id.tvDatosItem);
        }
    }
}