package squad.testutor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText regEmail = (EditText) findViewById(R.id.regEmail);
        final EditText regUsername = (EditText) findViewById(R.id.regEmail);
        final EditText regPassword = (EditText) findViewById(R.id.regEmail);
        final EditText regPassword2 = (EditText) findViewById(R.id.regEmail);

        final Button bRegister = (Button) findViewById(R.id.register);

    }
}
