package com.example.nutripet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> listaMascotas;
    private Context context;

    public MascotaAdapter(List<Mascota> listaMascotas, Context context) {
        this.listaMascotas = listaMascotas;
        this.context = context;
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

        //Calcular la edad exacta
        int edad = calcularEdad(mascota.getFecha_nacimiento());

        //Inyectamos Nombre y Edad
        holder.tvNombre.setText(mascota.getNombre() + " (" + edad + " años)");

        //Ocultamos el campo del microchip
        holder.tvMicrochip.setVisibility(View.GONE);

        //Cargamos la patología con colores
        String patologia = mascota.getNombrePatologia();
        holder.tvDatos.setText("Patología: " + (patologia != null ? patologia : "Sano / Ninguna"));

        if (patologia != null && (patologia.contains("Sano") || patologia.contains("Ninguna"))) {
            holder.tvDatos.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvDatos.setTextColor(Color.parseColor("#D32F2F"));
        }


        holder.ivOjo.setOnClickListener(v -> {
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
        this.listaMascotas = nuevaLista;
        notifyDataSetChanged();
    }

    private int calcularEdad(String fechaNacimiento) {
        try {
            if (fechaNacimiento == null || fechaNacimiento.isEmpty()) return 0;
            String[] partes = fechaNacimiento.split("/");
            int diaNac = Integer.parseInt(partes[0].trim());
            int mesNac = Integer.parseInt(partes[1].trim());
            int anioNac = Integer.parseInt(partes[2].trim());

            Calendar hoy = Calendar.getInstance();
            int anioActual = hoy.get(Calendar.YEAR);
            int mesActual = hoy.get(Calendar.MONTH) + 1;
            int diaActual = hoy.get(Calendar.DAY_OF_MONTH);

            int edad = anioActual - anioNac;
            if (mesActual < mesNac || (mesActual == mesNac && diaActual < diaNac)) {
                edad--;
            }
            return Math.max(0, edad);
        } catch (Exception e) {
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