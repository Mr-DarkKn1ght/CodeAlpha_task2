package com.example.flashcardquiz;

import android.app.*; import android.os.*; import android.content.*; import android.widget.*;

public class LoginActivity extends Activity {
    DatabaseHelper db; EditText email,password;
    public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_login);db=new DatabaseHelper(this);
        email=findViewById(R.id.email);password=findViewById(R.id.password);
        findViewById(R.id.login).setOnClickListener(v->{
            int id=db.login(email.getText().toString().trim(),password.getText().toString());
            if(id<0) Toast.makeText(this,"Invalid email or password",Toast.LENGTH_SHORT).show();
            else {getSharedPreferences("session",0).edit().putInt("uid",id).apply();
                startActivity(new Intent(this,MainActivity.class));finish();}
        });
        findViewById(R.id.toRegister).setOnClickListener(v->startActivity(new Intent(this,RegisterActivity.class)));
    }
}
