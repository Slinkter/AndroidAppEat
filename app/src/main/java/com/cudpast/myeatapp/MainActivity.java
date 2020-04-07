package com.cudpast.myeatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cudpast.myeatapp.Commom.Common;
import com.cudpast.myeatapp.Model.UserModel;
import com.cudpast.myeatapp.Remote.ICloudFunctions;
import com.cudpast.myeatapp.Remote.RetrofitCloudClient;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    //facebook kit
    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    public AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ICloudFunctions cloudFunctions;
    private DatabaseReference usersRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null) {
            firebaseAuth.removeAuthStateListener(listener);
            compositeDisposable.clear();
        }
        super.onStop();
    }


    private void init() {

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        usersRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        cloudFunctions = RetrofitCloudClient.getInstance().create(ICloudFunctions.class);
        // preguntar si el usuario esta logeado
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                phoneLogin();
            } else {
                checkUserFromFirebase(user);
            }
        };
    }
    //------->
    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), APP_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "failed to sign in ! ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //------->
    private void checkUserFromFirebase(FirebaseUser user) {
        dialog.show();
        usersRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(MainActivity.this, "Usuarios Registro", Toast.LENGTH_SHORT).show();
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            goToHomeActivity(userModel);
                        } else {
                            Toast.makeText(MainActivity.this, "Usuario no Registrado", Toast.LENGTH_SHORT).show();
                            showRegisterDialog(user);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "lo", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Registro");
        builder.setMessage("Ingrese su datos");
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText edt_name = itemView.findViewById(R.id.edt_name);
        EditText edt_address = itemView.findViewById(R.id.edt_address);
        EditText edt_phone = itemView.findViewById(R.id.edt_phone);
        // set
        edt_phone.setText(user.getPhoneNumber());
        //
        builder.setView(itemView);
        builder.setNegativeButton("Salir", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton("Registrar", (dialog, which) -> {
            if (TextUtils.isEmpty(edt_name.getText().toString())) {
                Toast.makeText(MainActivity.this, "Ingresar nombre", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (TextUtils.isEmpty(edt_address.getText().toString())) {
                Toast.makeText(MainActivity.this, "ingresar direccion", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            UserModel userModel = new UserModel();
            userModel.setUid(user.getUid());
            userModel.setName(edt_name.getText().toString());
            userModel.setAddress(edt_address.getText().toString());
            userModel.setPhone(edt_phone.getText().toString());

            usersRef
                    .child(user.getUid())
                    .setValue(userModel)
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Existo", Toast.LENGTH_SHORT).show();
                        goToHomeActivity(userModel);
                    });

        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToHomeActivity(UserModel userRef) {
        Common.currentUser = userRef;
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }


}
