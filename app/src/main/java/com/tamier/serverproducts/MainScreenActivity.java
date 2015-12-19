package com.tamier.serverproducts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainScreenActivity extends Activity {

    Button mButtonViewProducts;
    Button mButtonAddProduct;
    Button mButtonDeleteProduct;
    private void init(){
        mButtonViewProducts=(Button)findViewById(R.id.btnViewproducts);
        mButtonAddProduct=(Button)findViewById(R.id.btnAddProduct);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        init();
        mButtonViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntentAllProducts = new Intent(MainScreenActivity.this, AllProductsActivity.class);
                startActivity(mIntentAllProducts);
            }
        });
        mButtonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntentAddProduct=new Intent(MainScreenActivity.this,NewProductActivity.class);
                startActivity(mIntentAddProduct);
            }
        });
    }

}
