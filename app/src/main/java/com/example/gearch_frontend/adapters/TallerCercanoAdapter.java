package com.example.gearch_frontend.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gearch_frontend.R;
import com.example.gearch_frontend.api.models.Taller;

import java.util.List;

// Adaptador para mostrar la lista de talleres cercanos en el RecyclerView vertical
// A diferencia de TallerAdapter muestra la foto del taller y es mas ancho
public class TallerCercanoAdapter extends RecyclerView.Adapter<TallerCercanoAdapter.ViewHolder> {

    private List<Taller> talleres;
    private Context context;
    private LayoutInflater inflater;
    private View.OnClickListener onClickListener;

    public TallerCercanoAdapter(Context context, List<Taller> talleres, View.OnClickListener onClickListener) {
        this.context = context;
        this.talleres = talleres;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.taller_cercano_viewholder, parent, false);
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

    //Esto es porque no pude implementar las fotos de perfil de los talleres

//        // Si el taller tiene foto la mostramos, si no dejamos la imagen por defecto del XML
//        if (taller.getFotoPerfil() != null) {
//            holder.ivFoto.setImageBitmap(BitmapFactory.decodeByteArray(
//                    taller.getFotoPerfil(), 0, taller.getFotoPerfil().length));
//        }
    }

    @Override
    public int getItemCount() {
        return talleres != null ? talleres.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre, tvDireccion;
        private ImageView ivFoto;
        private Taller taller;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreTaller);
            tvDireccion = itemView.findViewById(R.id.tvDireccionTaller);
            ivFoto = itemView.findViewById(R.id.ivFotoTaller);
        }

        public void setTaller(Taller taller) { this.taller = taller; }
        public Taller getTaller() { return taller; }
    }
}