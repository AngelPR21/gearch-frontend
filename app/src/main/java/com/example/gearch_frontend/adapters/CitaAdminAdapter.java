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
import com.example.gearch_frontend.api.models.Cita;
import com.example.gearch_frontend.api.models.enums.EstadoCita;

import java.util.List;

// Adaptador para mostrar la lista de citas del taller en el panel admin
// Incluye botones para cancelar o completar cada cita
public class CitaAdminAdapter extends RecyclerView.Adapter<CitaAdminAdapter.ViewHolder> {

    private List<Cita> citas;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onCancelarClickListener;
    private View.OnClickListener onCompletarClickListener;

    public CitaAdminAdapter(Context context, List<Cita> citas,
                            View.OnClickListener onCancelarClickListener,
                            View.OnClickListener onCompletarClickListener) {
        this.context = context;
        this.citas = citas;
        this.inflater = LayoutInflater.from(context);
        this.onCancelarClickListener = onCancelarClickListener;
        this.onCompletarClickListener = onCompletarClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cita_admin_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = citas.get(position);

        holder.tvFechaHora.setText(cita.getFechaHora());

        if (cita.getUsuario() != null) {
            holder.tvCliente.setText("Cliente: " + cita.getUsuario().getNombre() + " " + cita.getUsuario().getApellidos());
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

        holder.tvEstado.setText("Estado: " + cita.getEstado().toString());

        // Solo mostramos los botones si la cita esta confirmada
        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.COMPLETADA) {
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnCompletar.setVisibility(View.GONE);
        } else {
            holder.btnCancelar.setVisibility(View.VISIBLE);
            holder.btnCompletar.setVisibility(View.VISIBLE);
            holder.btnCancelar.setOnClickListener(onCancelarClickListener);
            holder.btnCompletar.setOnClickListener(onCompletarClickListener);
        }

        holder.setCita(cita);
    }

    @Override
    public int getItemCount() {
        return citas != null ? citas.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFechaHora, tvCliente, tvServicio, tvVehiculo, tvNotas, tvEstado;
        private Button btnCancelar, btnCompletar;
        private Cita cita;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvServicio = itemView.findViewById(R.id.tvServicio);
            tvVehiculo = itemView.findViewById(R.id.tvVehiculo);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
            btnCompletar = itemView.findViewById(R.id.btnCompletar);
        }

        public Cita getCita() { return cita; }
        public void setCita(Cita cita) { this.cita = cita; }
    }
}
