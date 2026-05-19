package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.api.ApiClient;
import com.example.gearch_frontend.api.ApiService;
import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.enums.EstadoCita;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adaptador para mostrar la lista de citas del usuario en un RecyclerView
public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {

    private List<Cita> citas;
    private Context context;
    private LayoutInflater inflater;
    private ApiService api;

    public CitaAdapter(Context context, List<Cita> citas) {
        this.context = context;
        this.citas = citas;
        this.inflater = LayoutInflater.from(context);
        this.api = ApiClient.getClient().create(ApiService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cita_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = citas.get(position);

        holder.tvFechaHora.setText(cita.getFechaHora());
        holder.tvEstado.setText("Estado: " + cita.getEstado().toString());

        if (cita.getTaller() != null) {
            holder.tvTaller.setText(cita.getTaller().getNombre());
        }

        if (cita.getServicio() != null) {
            holder.tvServicio.setText("Servicio: " + cita.getServicio().getNombre());
        }

        // El vehiculo es opcional, si no tiene se oculta el TextView
        if (cita.getVehiculo() != null) {
            holder.tvVehiculo.setText("Coche: " + cita.getVehiculo().getMarca() + " " + cita.getVehiculo().getModelo() + " - " + cita.getVehiculo().getMatricula());
        } else {
            holder.tvVehiculo.setVisibility(View.GONE);
        }

        if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
            holder.tvNotas.setText("Nota: " + cita.getNotas());
        } else {
            holder.tvNotas.setVisibility(View.GONE);
        }

        // Solo mostramos el boton cancelar si la cita no esta ya cancelada o completada
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            holder.btnCancelar.setVisibility(View.GONE);
        } else {
            holder.btnCancelar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setOnClickListener(v -> {
                api.cancelarCita(cita.getId()).enqueue(new Callback<Cita>() {
                    @Override
                    public void onResponse(Call<Cita> call, Response<Cita> response) {
                        if (response.isSuccessful()) {
                            cita.setEstado(EstadoCita.CANCELADA);
                            // getBindingAdapterPosition en lugar de getAdapterPosition (deprecated)
                            int pos = holder.getBindingAdapterPosition();
                            if (pos != RecyclerView.NO_ID) {
                                notifyItemChanged(pos);
                            }
                            Toast.makeText(context, "Cita cancelada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al cancelar la cita", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cita> call, Throwable t) {
                        Toast.makeText(context, "Error de conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return citas != null ? citas.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFechaHora, tvTaller, tvServicio, tvVehiculo, tvNotas, tvEstado;
        private Button btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvTaller = itemView.findViewById(R.id.tvTaller);
            tvServicio = itemView.findViewById(R.id.tvServicio);
            tvVehiculo = itemView.findViewById(R.id.tvVehiculo);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
        }
    }
}
