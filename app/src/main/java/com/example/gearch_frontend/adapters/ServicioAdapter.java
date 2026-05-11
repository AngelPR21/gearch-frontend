package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.api.models.Servicio;

import java.util.List;

// Adaptador para mostrar una lista de servicios en un RecyclerView
// Se usa en DetalleTallerActivity para mostrar los servicios del taller
public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ViewHolder> {

    private List<Servicio> servicios;
    private Context context;
    private LayoutInflater inflater;

    public ServicioAdapter(Context context, List<Servicio> servicios) {
        this.context = context;
        this.servicios = servicios;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.servicio_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);
        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescripcion.setText(servicio.getDescripcion());
        holder.tvPrecio.setText(servicio.getPrecio() + " €");
    }

    @Override
    public int getItemCount() {
        return servicios != null ? servicios.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvDescripcion, tvPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreServicio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionServicio);
            tvPrecio = itemView.findViewById(R.id.tvPrecioServicio);
        }
    }
}
