package squad.testutor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText regEmail = (EditText) findViewById(R.id.regEmail);
        final EditText regUsername = (EditText) findViewById(R.id.regUsername);
        final EditText regPassword = (EditText) findViewById(R.id.regPassword);
        final EditText regPassword2 = (EditText) findViewById(R.id.regPassword2);

        final Button bRegister = (Button) findViewById(R.id.register);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = regEmail.getText().toString();
                final String password = regPassword.getText().toString();
                final String password2 = regPassword2.getText().toString();

                View focusView = null;

                if (!password.equals(password2)) {
                    regPassword2.setError("Passwords do not match");
                    focusView = regPassword2;
                    focusView.requestFocus();
                } else {
                    ArrayList<String> arr = LoginActivity.userHelper(email, password);
                    String cancel = arr.get(0);
                    String code = arr.get(1);

                    if (cancel.contains("true")) {
                        if (cancel.contains("Email")) {
                            regEmail.setError("Email is invalid");
                            focusView = regEmail;
                        } else if (cancel.contains("Password")) {
                            regPassword.setError(code);
                            focusView = regPassword;
                        }
                        focusView.requestFocus();
                    } else {
                        LoginActivity.mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(LoginActivity.TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, R.string.newUserFailed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
