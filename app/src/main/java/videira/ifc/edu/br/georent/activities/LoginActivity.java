package videira.ifc.edu.br.georent.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import videira.ifc.edu.br.georent.R;
import videira.ifc.edu.br.georent.interfaces.Bind;
import videira.ifc.edu.br.georent.models.User;

public class LoginActivity extends AppCompatActivity implements Bind<User> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Set TextView logo font */
        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        final TextView tvLogo = (TextView) findViewById(R.id.tv_logo);
        final Button btRegister = (Button) findViewById(R.id.bt_register);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/source-sans-pro.regular.ttf");
        etEmail.setTypeface(typeFace);
        etPassword.setTypeface(typeFace);
        tvLogo.setTypeface(typeFace);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        assert btRegister != null;
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, UserRegisterActivity.class);
                startActivity(i);
            }
        });
    }

    /*************************************************************************
     **                            SERVIÇO                                  **
     *************************************************************************/

    @Override
    public void doLoad() {

    }

    @Override
    public void doSingleBind(User result) {

    }

    @Override
    public void doMultipleBind(List<User> results) {

    }

    @Override
    public void doError(Exception ex) {

    }
}
