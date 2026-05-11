package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

// Adaptador para mostrar una lista de talleres en un RecyclerView
// Se usa tanto en el RecyclerView horizontal de recientes como en el vertical de cercanos
public class TallerAdapter extends RecyclerView.Adapter<TallerAdapter.ViewHolder> {

    private List<Taller> talleres;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    public TallerAdapter(Context context, List<Taller> talleres, View.OnClickListener onClickListener) {
        this.context = context;
        this.talleres = talleres;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.taller_viewholder, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Taller taller = talleres.get(position);
        holder.tvNombre.setText(taller.getNombre());
        holder.tvDireccion.setText(taller.getDireccion());
        holder.setTaller(taller);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return talleres != null ? talleres.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvDireccion;
        private Taller taller;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTaller);
            tvDireccion = itemView.findViewById(R.id.tvDireccionTaller);
        }

        public void setTaller(Taller taller) { this.taller = taller; }
        public Taller getTaller() { return taller; }
    }
}
