package com.example.momento2final.data.dao;

import android.util.Log;

import com.example.momento2final.data.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDao {
    private final FirebaseFirestore db;
    private static final String TAG = "ProductDao";
    private static final String COLLECTION_NAME = "Products";

    public ProductDao(FirebaseFirestore db) {
        this.db = db;
    }

    public  void insert(Product product, OnSuccessListener<String> listener){
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", product.getName());
        productData.put("price", product.getPrice());

        db.collection(COLLECTION_NAME)
                .add(productData)
                .addOnSuccessListener(documentReference ->  {
                    Log.d(TAG, "onSuccess:" + documentReference.getId());
                    listener.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: ", e);
                    listener.onSuccess(null);

                });
    }


    public void update(String id, Product product, OnSuccessListener<Boolean> listener){
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", product.getName());
        productData.put("price", product.getPrice());

        db.collection(COLLECTION_NAME)
                .document(id)
                .update(productData)
                .addOnSuccessListener(unused -> listener.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    listener.onSuccess(false);
                });
    }

    public void delete(String id, OnSuccessListener<Boolean> listener){
        db.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    listener.onSuccess(false);
                });
    }
    public void getById(String id, OnSuccessListener<Product> listener){
        db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            Product product = document.toObject(Product.class);
                            listener.onSuccess(product);
                        }else{
                            listener.onSuccess(null);
                        }
                    }else{
                        Log.e(TAG, "onComplete: ", task.getException());
                        listener.onSuccess(null);
                    }
                });
    }

    public void getAll(OnSuccessListener<List<Product>> listener){
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Product> productList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Product product = documentSnapshot.toObject(Product.class);
                            productList.add(product);
                        }
                        listener.onSuccess(productList);
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    listener.onSuccess(null);

                });
    }
    public void getByName(String name, OnSuccessListener<List<Product>> listener) {
        db.collection(COLLECTION_NAME)
                .whereEqualTo("NameProduct", name)
                .limit(1) // Asegúrate de limitar a 1 para obtener solo un producto
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0); // Solo el primero
                        Product product = documentSnapshot.toObject(Product.class);
                        listener.onSuccess((List<Product>) product);
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    listener.onSuccess(null);
                });
    }

}