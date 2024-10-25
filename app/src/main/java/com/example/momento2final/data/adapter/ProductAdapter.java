package com.example.momento2final.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momento2final.R;
import com.example.momento2final.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Verificar si el nombre del producto no es nulo antes de asignarlo al TextView
        if (product.getName() != null && !product.getName().isEmpty()) {
            holder.tvProductName.setText(product.getName());
        } else {
            holder.tvProductName.setText("Nombre no disponible");
        }

        // Asignar el precio del producto
        holder.tvProductPrice.setText(String.valueOf(product.getPrice()));
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.txtProductName);
            tvProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }


}
