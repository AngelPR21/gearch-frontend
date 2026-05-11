package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.api.models.Servicio;

import java.util.List;

// Adaptador para mostrar la lista de servicios del taller en el panel admin
// Incluye un boton para eliminar cada servicio
public class ServicioAdminAdapter extends RecyclerView.Adapter<ServicioAdminAdapter.ViewHolder> {

    private List<Servicio> servicios;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onEliminarClickListener;

    public ServicioAdminAdapter(Context context, List<Servicio> servicios, View.OnClickListener onEliminarClickListener) {
        this.context = context;
        this.servicios = servicios;
        this.inflater = LayoutInflater.from(context);
        this.onEliminarClickListener = onEliminarClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.servicio_admin_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);

        holder.tvNombre.setText(servicio.getNombre());
        holder.tvDescripcion.setText(servicio.getDescripcion());
        holder.tvPrecio.setText(servicio.getPrecio() + " €");
        holder.btnEliminar.setOnClickListener(onEliminarClickListener);
        holder.setServicio(servicio);
    }

    @Override
    public int getItemCount() {
        return servicios != null ? servicios.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvDescripcion, tvPrecio;
        private Button btnEliminar;
        private Servicio servicio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public Servicio getServicio() { return servicio; }
        public void setServicio(Servicio servicio) { this.servicio = servicio; }
    }
}
