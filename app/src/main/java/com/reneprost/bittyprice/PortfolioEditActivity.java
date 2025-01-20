package com.rene98c.bittyprice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.rene98c.bittyprice.core.PersistentAppObject;
import com.rene98c.bittyprice.core.PortfolioItem;

import java.util.List;
import java.util.Objects;

public class PortfolioEditActivity extends AppCompatActivity {
    TextView symbolInput,amountInput,pairInput;
    List<PortfolioItem> portfolioItems;
    PersistentAppObject appObj;
    Button btnApply,btnRemove;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_pair_item_edit);
        super.setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findControls();
        appObj = new PersistentAppObject(this.getBaseContext());
        portfolioItems = appObj.readPortfolio();

        Intent i = getIntent();
        int itemIndex = i.getIntExtra("index",-1);
        boolean isEditing = itemIndex>-1;
        if(isEditing){
            prepareUIForEditing( itemIndex);
        }
        else{
            prepareUIForCreating();
        }
    }

    private  void findControls(){
         symbolInput = findViewById(R.id.tbSymbol);
         amountInput = findViewById(R.id.tbAmount);
         pairInput = findViewById(R.id.tbPair);
         btnApply = findViewById(R.id.btnApply);
        btnRemove =findViewById(R.id.btnDelete);
    }

    private  void prepareUIForCreating() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.new_portfolio_entry);
        btnApply.setText(R.string.add);
        btnRemove.setVisibility(View.INVISIBLE);

        btnApply.setOnClickListener(v -> {
            //add values and close
            PortfolioItem newItem = readItemFromUI();
            if(newItem!=null){
                portfolioItems.add(newItem);
                appObj.writePortfolio(portfolioItems);
                this.finish();
            }
        });
    }

    private  PortfolioItem readItemFromUI(){
        String symbolText=symbolInput.getText().toString().trim();
        String amountText=amountInput.getText().toString().trim();
        String pairText=pairInput.getText().toString().trim();
        if (TextUtils.isEmpty(symbolText)){
            symbolInput.setError("Please enter cryptocurrency name.");
        }
        else if (TextUtils.isEmpty(amountText)){
            symbolInput.setError("Please enter the amount.");
        }
        else if (TextUtils.isEmpty(pairText)){
            symbolInput.setError("Please enter the currency pair.");
        }
        else {
            //add
            return new PortfolioItem(symbolText,Float.parseFloat(amountText),pairText);
        }
        return  null;
    }

    private void prepareUIForEditing( int itemIndex) {
        btnApply.setText(R.string.apply);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.edit_portfolio_entry);

        PortfolioItem portfolioItem = portfolioItems.get(itemIndex);

        symbolInput.setText(portfolioItem.name);
        amountInput.setText(String.valueOf(portfolioItem.amount));
        pairInput.setText(portfolioItem.ticker);

        btnApply.setOnClickListener(v -> {
            //update values and close
            PortfolioItem editItem = readItemFromUI();
            if(editItem!=null){
                portfolioItems.set(itemIndex,editItem);
                appObj.writePortfolio(portfolioItems);
                this.finish();
            }
        });

        btnRemove.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this entry?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        portfolioItems.remove(itemIndex);
                        appObj.writePortfolio(portfolioItems);
                        this.finish();
                        Toast.makeText(this, "Entry removed", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();

        });
    }
}
