package com.example.nutripet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

// Adaptador para el RecyclerView de la pantalla principal que lista las mascotas del usuario
public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {
    private static final String TAG = "DEBUG_MASCOTA_ADAPTER";
    private List<Mascota> listaMascotas;
    private Context context;

    public MascotaAdapter(List<Mascota> listaMascotas, Context context) {
        this.listaMascotas = listaMascotas;
        this.context = context;
        Log.d(TAG, "Adapter inicializado con " + listaMascotas.size() + " mascotas");
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);
        Log.d(TAG, "Vinculando mascota: " + mascota.getNombre() + " en posición: " + position);

        int edad = calcularEdad(mascota.getFecha_nacimiento());
        holder.tvNombre.setText(mascota.getNombre() + " (" + edad + " años)");
        holder.tvMicrochip.setVisibility(View.GONE);

        String patologiasTexto = mascota.getNombrePatologia();

        // Lógica visual basada en si la mascota tiene patologías o está sana
        if (patologiasTexto == null || patologiasTexto.isEmpty() || patologiasTexto.equalsIgnoreCase("Sano")) {
            holder.tvDatos.setText("Estado: Sano / Ninguna");
            holder.tvDatos.setTextColor(Color.parseColor("#2E7D32")); // Verde
        } else {
            holder.tvDatos.setText("Patologías: " + patologiasTexto);
            holder.tvDatos.setTextColor(Color.parseColor("#D32F2F")); // Rojo
        }

        // Listener para abrir el detalle de la mascota seleccionada
        holder.ivOjo.setOnClickListener(v -> {
            Log.d(TAG, "Navegando a DetalleMascotaActivity para: " + mascota.getNombre());
            Intent intent = new Intent(context, DetalleMascotaActivity.class);
            intent.putExtra("MICROCHIP_MASCOTA", mascota.getMicrochip());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaMascotas.size();
    }

    public void updateList(List<Mascota> nuevaLista) {
        Log.d(TAG, "Actualizando lista de mascotas. Tamaño: " + nuevaLista.size());
        this.listaMascotas = nuevaLista;
        notifyDataSetChanged();
    }

    // Método auxiliar para calcular la edad según el año de nacimiento
    private int calcularEdad(String fechaNacimiento) {
        try {
            if (fechaNacimiento == null || fechaNacimiento.isEmpty()) return 0;
            String[] partes = fechaNacimiento.split("/");
            // Espera formato dd/mm/aaaa
            int anioNac = Integer.parseInt(partes[2].trim());

            Calendar hoy = Calendar.getInstance();
            int edad = hoy.get(Calendar.YEAR) - anioNac;
            return Math.max(0, edad);
        } catch (Exception e) {
            Log.e(TAG, "Error calculando edad para fecha: " + fechaNacimiento);
            return 0;
        }
    }

    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMicrochip, tvDatos;
        ImageView ivPerro, ivOjo;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreItem);
            tvMicrochip = itemView.findViewById(R.id.tvMicrochipItem);
            tvDatos = itemView.findViewById(R.id.tvDatosItem);
            ivPerro = itemView.findViewById(R.id.ivIconoPerro);
            ivOjo = itemView.findViewById(R.id.ivInspeccionarOjo);
        }
    }
}