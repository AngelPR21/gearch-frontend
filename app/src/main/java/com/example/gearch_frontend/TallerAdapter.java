package com.example.gearch_frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

/*
 * Adaptador para mostrar una lista de talleres en un RecyclerView.
 * Muestra el nombre y la dirección de cada taller.
 */
public class TallerAdapter extends RecyclerView.Adapter<TallerAdapter.ViewHolder> {

    private List<Taller> talleres;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    /*
     * Constructor del adaptador.
     *
     * @param context Contexto de la aplicación o actividad.
     * @param talleres Lista de talleres a mostrar.
     * @param onClickListener Listener para manejar clicks en los ítems.
     */
    public TallerAdapter(Context context, List<Taller> talleres, View.OnClickListener onClickListener) {
        this.context = context;
        this.talleres = talleres;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    // Crea un nuevo ViewHolder y asigna el layout para cada ítem del RecyclerView.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.taller_viewholder, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    // Asigna los datos del taller en la posición dada al ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Taller taller = talleres.get(position);
        holder.tvNombre.setText(taller.getNombre());
        holder.tvDireccion.setText(taller.getDireccion());
        holder.setTaller(taller);
        holder.itemView.setOnClickListener(onClickListener);
    }

    // Devuelve el número total de talleres en la lista.
    @Override
    public int getItemCount() {
        return talleres != null ? talleres.size() : 0;
    }

    // Clase interna que representa el ViewHolder para cada ítem del RecyclerView.
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvDireccion;
        private Taller taller;

        // Constructor del ViewHolder
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTaller);
            tvDireccion = itemView.findViewById(R.id.tvDireccionTaller);
        }

        // Establece el taller asociado a este ViewHolder.
        public void setTaller(Taller taller) {
            this.taller = taller;
        }

        // Devuelve el taller asociado a este ViewHolder.
        public Taller getTaller() {
            return taller;
        }
    }
}