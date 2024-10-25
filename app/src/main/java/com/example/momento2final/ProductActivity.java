package com.example.momento2final;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.momento2final.data.adapter.ProductAdapter;
import com.example.momento2final.data.dao.ProductDao;
import com.example.momento2final.data.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    private EditText etIdProducto, etNameProduct, etPrice;
    private Button btnCreate, btnUpdate, btnDelete, btnRead;
    private RecyclerView rvProductList;

    private ProductDao productDao;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Inicializar vistas
        etIdProducto = findViewById(R.id.EtIdProducto);
        etNameProduct = findViewById(R.id.EtNameProduct);
        etPrice = findViewById(R.id.EtPrice);
        btnCreate = findViewById(R.id.btnCreate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnRead = findViewById(R.id.btnRead);  // Botón de búsqueda
        rvProductList = findViewById(R.id.rvProductList);

        // Inicializar Firestore y ProductDao
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        productDao = new ProductDao(db);

        // Configurar RecyclerView
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList);
        rvProductList.setAdapter(productAdapter);

        // Cargar productos automáticamente
        loadProducts();

        // Establecer listeners para los botones
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProduct();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Suponiendo que tienes un EditText llamado etIdInput para ingresar el ID
                String productId = etIdProducto.getText().toString().trim(); // Obtiene el texto y elimina espacios

                if (!productId.isEmpty()) { // Verifica que no esté vacío
                    loadProductById(productId); // Llama a la función con el ID del producto
                } else {
                    Toast.makeText(ProductActivity.this, "Por favor, ingresa un ID válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Establecer listener para el campo ID
        etIdProducto.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String id = etIdProducto.getText().toString().trim();
                if (!id.isEmpty()) {
                    loadProductById(id); // Cargar el producto por ID
                }
            }
        });
    }

    private void loadProducts() {
        productDao.getAll(new OnSuccessListener<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                if (products != null) {
                    productList.clear();
                    productList.addAll(products);
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductActivity.this, "No hay productos disponibles.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadProductById(String id) {
        productDao.getById(id, new OnSuccessListener<Product>() {
            @Override
            public void onSuccess(Product product) {
                if (product != null) {
                    etIdProducto.setText(product.getId());
                    etPrice.setText(String.valueOf(product.getPrice())); // Llenar automáticamente el precio
                    etNameProduct.setText(product.getName()); // Llenar automáticamente el nombre
                } else {
                    Toast.makeText(ProductActivity.this, "Producto no encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createProduct() {
        String name = etNameProduct.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        Product newProduct = new Product(name, (float) price);

        productDao.insert(newProduct, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String documentId) {
                Log.d(TAG, "Producto insertado con ID: " + documentId);
                loadProducts(); // Cargar productos nuevamente para actualizar la lista
                Toast.makeText(ProductActivity.this, "Producto creado con éxito.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        String id = etIdProducto.getText().toString();
        String name = etNameProduct.getText().toString();
        String priceStr = etPrice.getText().toString();

        if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        Product updatedProduct = new Product(name, (float) price);

        productDao.update(id, updatedProduct, new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success) {
                    Log.d(TAG, "Producto actualizado correctamente.");
                    loadProducts(); // Cargar productos nuevamente para actualizar la lista
                    Toast.makeText(ProductActivity.this, "Producto actualizado con éxito.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Error al actualizar producto.");
                    Toast.makeText(ProductActivity.this, "Error al actualizar producto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteProduct() {
        String id = etIdProducto.getText().toString();
        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el ID del producto.", Toast.LENGTH_SHORT).show();
            return;
        }

        productDao.delete(id, new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success) {
                    Log.d(TAG, "Producto eliminado correctamente.");
                    loadProducts(); // Cargar productos nuevamente para actualizar la lista
                    Toast.makeText(ProductActivity.this, "Producto eliminado correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Error al eliminar producto.");
                    Toast.makeText(ProductActivity.this, "Error al eliminar producto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
