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
import com.example.gearch_frontend.api.models.DisponibilidadTaller;

import java.util.List;

/*
 * Adaptador para mostrar el horario semanal del taller en el panel admin.
 */
public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.ViewHolder> {

    private List<DisponibilidadTaller> horarios;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onEliminarClickListener;

    public HorarioAdapter(Context context, List<DisponibilidadTaller> horarios, View.OnClickListener onEliminarClickListener) {
        this.context = context;
        this.horarios = horarios;
        this.inflater = LayoutInflater.from(context);
        this.onEliminarClickListener = onEliminarClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.horario_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisponibilidadTaller horario = horarios.get(position);

        holder.tvDia.setText(horario.getDiaSemana().toString());
        // Mostramos solo HH:mm de cada hora
        String inicio = horario.getHoraInicio().length() >= 5 ? horario.getHoraInicio().substring(0, 5) : horario.getHoraInicio();
        String fin = horario.getHoraFin().length() >= 5 ? horario.getHoraFin().substring(0, 5) : horario.getHoraFin();
        holder.tvHorario.setText(inicio + " - " + fin + " (cada " + horario.getIntervaloMinutos() + " min)");
        holder.btnEliminar.setOnClickListener(onEliminarClickListener);
        holder.setHorario(horario);
    }

    @Override
    public int getItemCount() {
        return horarios != null ? horarios.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDia, tvHorario;
        private Button btnEliminar;
        private DisponibilidadTaller horario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tvDia);
            tvHorario = itemView.findViewById(R.id.tvHorario);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public DisponibilidadTaller getHorario() { return horario; }
        public void setHorario(DisponibilidadTaller horario) { this.horario = horario; }
    }
}