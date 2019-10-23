package com.example.customslider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import static android.os.Build.VERSION.SDK_INT;



import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Slider.SliderChangeListener {

    private Slider mSlider = null;
    private TextView mTv = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    final static int PERMISSION_GRANTED = 1;
    final static int NO_ADAPTER=0;
    final static int USER_REQUEST=2;
    final static int PERMISSION_REQUEST = 0;
    final static int BT_ACTIVATION_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlider = findViewById(R.id.slider);
        mTv = findViewById(R.id.tv);
        mSlider.setSliderChangeListener(this);
    }


    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    public void onChange(float value) {
        String str = String.format("%d",(int)value);
    mTv.setText(str);
    }

    @Override
    public void onDoubleClick(float value) {
        String str = String.format("%d",(int)value);
        mTv.setText(str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (isBluetoothReady()) {
            // Il n'y a pas d'adaptateur BT !
            case NO_ADAPTER:
                Toast.makeText(this, "Pas d'adaptateur Bluetooth", Toast.LENGTH_LONG).show();
                finish();
                break;
            // La permission a été donnée pour la localisation fine
            case PERMISSION_GRANTED:
                onBluetoothActivationRequest();
                break;
            // la demande a été faite à l'utilisateur (retour par onRequestPermissionsResult...)
        }

    }

    /**
     * Teste l'état de l'adaptateur et des droits
     * @return indication du droit (0 : pas d'adaptateur, 1 : Permission ok, 2 : demandée)
     */
    private int isBluetoothReady() {
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            return NO_ADAPTER;
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                return USER_REQUEST;
            }
        }
        return PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Le Bluetooth doit être autorisé", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            onBluetoothActivationRequest();
        }
    }

    /**
     * Appelé sur retour d'activation du BT
     * @param requestCode : code de retour
     * @param resultCode : résultat du retour
     * @param data : données supplémentaires échangées
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BT_ACTIVATION_REQUEST){
            if (mBluetoothAdapter.isEnabled())
                Toast.makeText(this, "Le Bluetooth est activé", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Le Bluetooth n'est pas activé", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Appelé pour l'activation du BT
     */
    private void onBluetoothActivationRequest(){
        if(!mBluetoothAdapter.isEnabled()){
            Intent BTActivation = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BTActivation,BT_ACTIVATION_REQUEST);
        }
    }

}
