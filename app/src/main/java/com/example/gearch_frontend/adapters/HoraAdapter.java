package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;

import java.util.List;

/*
 * Adaptador para mostrar las horas disponibles en un RecyclerView en grid.
 * Al pulsar una hora la marca en azul y guarda la selección.
 */
public class HoraAdapter extends RecyclerView.Adapter<HoraAdapter.ViewHolder> {

    private List<String> horas;
    private Context context;
    private LayoutInflater inflater;
    private int posicionSeleccionada = -1; // -1 = ninguna hora seleccionada

    public HoraAdapter(Context context, List<String> horas) {
        this.context = context;
        this.horas = horas;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hora_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String hora = horas.get(position);

        // El backend devuelve "HH:mm:ss", mostramos solo "HH:mm"
        String horaCorta = hora.length() >= 5 ? hora.substring(0, 5) : hora;
        holder.tvHora.setText(horaCorta);

        // Marcamos la hora seleccionada con color diferente
        if (position == posicionSeleccionada) {
            holder.linearLayout.setBackgroundColor(0xFF1976D2);
            holder.tvHora.setTextColor(0xFFFFFFFF);
        } else {
            holder.linearLayout.setBackgroundColor(0xFFFFFFFF);
            holder.tvHora.setTextColor(0xFF000000);
        }

        holder.itemView.setOnClickListener(v -> {
            int anteriorSeleccionada = posicionSeleccionada;
            posicionSeleccionada = holder.getAdapterPosition();
            // Solo refrescamos las dos posiciones afectadas para no redibujar todo
            notifyItemChanged(anteriorSeleccionada);
            notifyItemChanged(posicionSeleccionada);
        });
    }

    @Override
    public int getItemCount() {
        return horas != null ? horas.size() : 0;
    }

    // Reemplaza la lista de horas y resetea la selección
    public void actualizarHoras(List<String> nuevasHoras) {
        this.horas = nuevasHoras;
        posicionSeleccionada = -1;
        notifyDataSetChanged();
    }

    // Devuelve la hora seleccionada completa "HH:mm:ss", o null si no hay ninguna
    public String getHoraSeleccionada() {
        if (posicionSeleccionada == -1) return null;
        return horas.get(posicionSeleccionada);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHora);
            linearLayout = (LinearLayout) itemView;
        }
    }
}