package com.example.flashcardquiz;

import android.app.*; import android.os.*; import android.content.*; import android.widget.*;

public class RegisterActivity extends Activity {
    public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_register);
        DatabaseHelper db=new DatabaseHelper(this);
        EditText name=findViewById(R.id.name), email=findViewById(R.id.email), pass=findViewById(R.id.password), confirm=findViewById(R.id.confirm);
        findViewById(R.id.register).setOnClickListener(v->{
            String n=name.getText().toString().trim(), e=email.getText().toString().trim(), p=pass.getText().toString();
            if(n.isEmpty()||e.isEmpty()||p.length()<8){Toast.makeText(this,"Fill all fields; password must be 8+ characters",Toast.LENGTH_LONG).show();return;}
            if(!p.equals(confirm.getText().toString())){confirm.setError("Passwords do not match");return;}
            if(!db.register(n,e,p)){email.setError("Email already registered");return;}
            Toast.makeText(this,"Account created. Please log in.",Toast.LENGTH_SHORT).show();finish();
        });
    }
}
