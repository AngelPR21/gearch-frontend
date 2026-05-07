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

/*
 * Adaptador para mostrar la lista de citas del usuario en un RecyclerView.
 */
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

        if (cita.getTaller() != null) {
            holder.tvTaller.setText(cita.getTaller().getNombre());
        }

        if (cita.getServicio() != null) {
            holder.tvServicio.setText(cita.getServicio().getNombre());
        }

        if (cita.getNotas() != null && !cita.getNotas().isEmpty()) {
            holder.tvNotas.setText(cita.getNotas());
        } else {
            holder.tvNotas.setVisibility(View.GONE);
        }

        // Solo mostramos el botón si la cita no está ya cancelada o completada
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            holder.btnCancelar.setVisibility(View.GONE);
        } else {
            holder.btnCancelar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setOnClickListener(v -> {
                api.actualizarEstadoCita(cita.getId(), "CANCELADA").enqueue(new Callback<Cita>() {
                    @Override
                    public void onResponse(Call<Cita> call, Response<Cita> response) {
                        if (response.isSuccessful()) {
                            cita.setEstado(EstadoCita.CANCELADA);
                            int pos = holder.getAdapterPosition();
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
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
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

        private TextView tvFechaHora, tvTaller, tvServicio, tvNotas;
        private Button btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvTaller = itemView.findViewById(R.id.tvTaller);
            tvServicio = itemView.findViewById(R.id.tvServicio);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
        }
    }
}