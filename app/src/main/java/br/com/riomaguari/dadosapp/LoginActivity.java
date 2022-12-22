package br.com.riomaguari.dadosapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import br.com.riomaguari.dadosapp.R;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    final String LOG = "LoginActivity";
    Button btnLogin;
    EditText etUsername, etPassword;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        if (!habilitaLogin(checkPermission())) {
            requestPermission();
        }

    }
    @Override
    public void onClick(View v) {

        HashMap postData = new HashMap();
        String username, password;

        Common.registraMacAddress(getMacAddr());

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

/*        if (Common.macAddress.equals("fc:64:3a:ce:24:82")) {*/
            username = "Davi";
            password = "321";
/*        }
        if (Common.macAddress.equals("28:c2:1f:87:ea:6c")) {
            username = "Bruno";
            password = "789456";
        }*/

        postData.put("txtUsername", username);
        postData.put("txtPassword", password);

        PostResponseAsyncTask task1 = new PostResponseAsyncTask(LoginActivity.this, postData,
                new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Log.d(LOG, s);
                        if(s.contains("Sucesso")){

                            //registra o usuario
                            String[] myCodUsuario = s.split("_");
                            Common.registraUsuario(myCodUsuario[0]);

                            //só volto atualizando se inserir ok na detail
                            Common.ePraAtualizar = false;

                            Toast.makeText(LoginActivity.this, "Logado com Sucesso!", Toast.LENGTH_LONG).show();
                            Intent in = new Intent(LoginActivity.this, DadosActivity.class);

                            startActivity(in);

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Tente Novamente", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        if (Common.isLocal(this)) {
            task1.execute("http://192.168.1.244/ftperm/apps/CP/cp_login.php");
        }
        else {
            if (Common.isOnline(this)){
                task1.execute("http://riomaguari.dyndns.org:8082/ftperm/apps/CP/cp_login.php");
            }
            else {
                Toast.makeText(LoginActivity.this, "Sem conexão com a Internet!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && writeAccepted) {
                        Toast.makeText(LoginActivity.this, "Permissão concedida, Login liberado!", Toast.LENGTH_LONG).show();
                        habilitaLogin(true);
                    }
                    else {

                        Toast.makeText(LoginActivity.this, "Permissão não concedida, Login bloqueado!", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) ||
                                    shouldShowRequestPermissionRationale(CAMERA) ||
                                    shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("Voce tem que permitir todos os acessos para habilitar o Login!",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    public boolean habilitaLogin(Boolean habilitar) {
        btnLogin.setEnabled(habilitar);

        return habilitar;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

}
