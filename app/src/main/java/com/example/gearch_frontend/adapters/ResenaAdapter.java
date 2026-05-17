package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.ResenaDetalleActivity;
import com.example.gearch_frontend.api.models.Resena;

import java.util.List;

// Adaptador para mostrar una lista de resenas en un RecyclerView
// Al pulsar una resena abre ResenaDetalleActivity con el detalle completo
public class ResenaAdapter extends RecyclerView.Adapter<ResenaAdapter.ViewHolder> {

    private List<Resena> resenas;
    private Context context;
    private LayoutInflater inflater;

    public ResenaAdapter(Context context, List<Resena> resenas) {
        this.context = context;
        this.resenas = resenas;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.resena_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resena resena = resenas.get(position);
        holder.tvPuntuacion.setText("⭐ " + resena.getPuntuacion() + "/5");
        holder.tvComentario.setText(resena.getComentario());
        //convertimos el formato ISO a "dd/MM/yyyy"
        String fechaFormateada = resena.getFecha();
        if (fechaFormateada != null && fechaFormateada.length() >= 10) {
            String[] partes = fechaFormateada.substring(0, 10).split("-");
            fechaFormateada = partes[2] + "/" + partes[1] + "/" + partes[0];
        }
        holder.tvFecha.setText(fechaFormateada);

        // Variable final para poder usarla dentro del lambda ya que se reasigna mas arriba y lambda no lo acepta
        final String fechaParaIntent = fechaFormateada;

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResenaDetalleActivity.class);
            intent.putExtra("puntuacion", resena.getPuntuacion());
            intent.putExtra("fecha", fechaParaIntent);
            intent.putExtra("comentario", resena.getComentario());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resenas != null ? resenas.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPuntuacion, tvComentario, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPuntuacion = itemView.findViewById(R.id.tvPuntuacion);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
