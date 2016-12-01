package videira.ifc.edu.br.georent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import videira.ifc.edu.br.georent.R;
import videira.ifc.edu.br.georent.interfaces.Bind;
import videira.ifc.edu.br.georent.models.User;
import videira.ifc.edu.br.georent.repositories.UserRepository;

public class LoginActivity extends AppCompatActivity implements Bind<User> {

    UserRepository mUserRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Set TextView logo font */
        final EditText etEmail = (EditText) findViewById(R.id.et_email);
        final EditText etPassword = (EditText) findViewById(R.id.et_password);
        final TextView tvLogo = (TextView) findViewById(R.id.tv_logo);
        final Button btLogin = (Button) findViewById(R.id.bt_login);
        final Button btRegister = (Button) findViewById(R.id.bt_register);

        mUserRepository = new UserRepository(this,this);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/source-sans-pro.regular.ttf");
        etEmail.setTypeface(typeFace);
        etPassword.setTypeface(typeFace);
        tvLogo.setTypeface(typeFace);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOG", "tentou logar");
                User u = new User();
                u.setEmail(etEmail.getText().toString());
                u.setPassword(etPassword.getText().toString());
                mUserRepository.login(u);
            }
        });

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
        if(result != null){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);

            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("idUser", String.valueOf(result.getIdUser()));
            editor.commit();
        }
    }

    @Override
    public void doMultipleBind(List<User> results) {

    }

    @Override
    public void doError(Exception ex) {

    }
}
