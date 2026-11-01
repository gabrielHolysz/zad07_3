package com.example.form3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.form3.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spCrust;
    private RadioGroup rgSize;
    private CheckBox cbCheese, cbMushroom, cbHam;
    private SeekBar sbQuantity;
    private TextView tvQuantityValue, tvSummary;
    private EditText etAddress;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setUpCrustSpinner();
        setUpListeners();

        tvQuantityValue.setText(String.valueOf(sbQuantity.getProgress()));
    }

    private void initViews() {
        spCrust = findViewById(R.id.spCrust);
        rgSize = findViewById(R.id.rgSize);
        cbCheese = findViewById(R.id.cbCheese);
        cbMushroom = findViewById(R.id.cbMushroom);
        cbHam = findViewById(R.id.cbHam);
        sbQuantity = findViewById(R.id.sbQuantity);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        etAddress = findViewById(R.id.etAddress);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvSummary = findViewById(R.id.tvSummary);
    }

    private void setUpCrustSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.crusts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCrust.setAdapter(adapter);
    }

    private void setUpListeners() {

        sbQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int currentVal = progress < 1 ? 1 : progress;
                tvQuantityValue.setText(String.valueOf(currentVal));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                askForConfirmation();
            }
        });
    }

    private boolean validateForm() {

        if (spCrust.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_crust, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (rgSize.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_size, Toast.LENGTH_SHORT).show();
            return false;
        }


        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setError(getString(R.string.error_address));
            etAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void askForConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> submitOrder())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void submitOrder() {
        tvSummary.setText(buildSummary());

        Snackbar.make(findViewById(R.id.main), R.string.sent, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> tvSummary.setText(""))
                .show();
    }

    private String buildSummary() {
        String size = "";
        int selectedId = rgSize.getCheckedRadioButtonId();
        if (selectedId == R.id.rbSmall) size = getString(R.string.size_small);
        else if (selectedId == R.id.rbMedium) size = getString(R.string.size_medium);
        else if (selectedId == R.id.rbLarge) size = getString(R.string.size_large);

        List<String> extrasList = new ArrayList<>();
        if (cbCheese.isChecked()) extrasList.add(getString(R.string.extra_cheese));
        if (cbMushroom.isChecked()) extrasList.add(getString(R.string.extra_mushroom));
        if (cbHam.isChecked()) extrasList.add(getString(R.string.extra_ham));

        String extras = extrasList.isEmpty() ? "brak" : String.join(", ", extrasList);

        return getString(R.string.summary_format,
                spCrust.getSelectedItem().toString(),
                size,
                extras,
                Integer.parseInt(tvQuantityValue.getText().toString()),
                etAddress.getText().toString().trim()
        );
    }
}