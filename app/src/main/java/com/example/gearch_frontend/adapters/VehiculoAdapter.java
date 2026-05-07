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
import com.example.gearch_frontend.api.models.Vehiculo;

import java.util.List;

/*
 * Adaptador para mostrar la lista de vehículos del usuario en un RecyclerView.
 */
public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.ViewHolder> {

    private List<Vehiculo> vehiculos;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onEliminarClickListener;

    public VehiculoAdapter(Context context, List<Vehiculo> vehiculos, View.OnClickListener onEliminarClickListener) {
        this.context = context;
        this.vehiculos = vehiculos;
        this.inflater = LayoutInflater.from(context);
        this.onEliminarClickListener = onEliminarClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vehiculo_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehiculo vehiculo = vehiculos.get(position);

        holder.tvMarcaModelo.setText(vehiculo.getMarca() + " " + vehiculo.getModelo());
        holder.tvMatricula.setText(vehiculo.getMatricula());
        holder.btnEliminar.setOnClickListener(onEliminarClickListener);
        holder.setVehiculo(vehiculo);
    }

    @Override
    public int getItemCount() {
        return vehiculos != null ? vehiculos.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMarcaModelo, tvMatricula;
        private Button btnEliminar;
        private Vehiculo vehiculo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarcaModelo = itemView.findViewById(R.id.tvMarcaModelo);
            tvMatricula = itemView.findViewById(R.id.tvMatricula);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public Vehiculo getVehiculo() { return vehiculo; }
        public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    }
}