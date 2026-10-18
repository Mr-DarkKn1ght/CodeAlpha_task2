package com.example.flashcardquiz;

import android.app.*; import android.os.*; import android.database.*; import android.view.*; import android.widget.*; import java.util.*;

public class ManageCardsActivity extends Activity {
    DatabaseHelper db; int uid; LinearLayout list;
    public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_manage_cards);db=new DatabaseHelper(this);uid=getSharedPreferences("session",0).getInt("uid",-1);list=findViewById(R.id.cardList);
        findViewById(R.id.add).setOnClickListener(v->edit(-1,"",""));refresh();}
    void refresh(){list.removeAllViews();Cursor c=db.cards(uid);try{while(c.moveToNext()){int id=c.getInt(0);String q=c.getString(1),a=c.getString(2);
        TextView row=new TextView(this);row.setText(q+"\n"+a+"\n✎ Edit     🗑 Delete");row.setTextSize(16);row.setPadding(16,18,16,18);
        row.setOnClickListener(v->new AlertDialog.Builder(this).setItems(new String[]{"Edit","Delete"},(d,which)->{if(which==0)edit(id,q,a);else new AlertDialog.Builder(this).setMessage("Delete this card?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(x,y)->{db.deleteCard(uid,id);refresh();}).show();}).show());
        list.addView(row);View line=new View(this);line.setBackgroundColor(0xFFE0E0E0);line.setLayoutParams(new LinearLayout.LayoutParams(-1,1));list.addView(line);
    }}finally{c.close();}}
    void edit(int id,String q,String a){View form=getLayoutInflater().inflate(R.layout.dialog_flashcard,null);EditText qe=form.findViewById(R.id.q),ae=form.findViewById(R.id.a);qe.setText(q);ae.setText(a);
        new AlertDialog.Builder(this).setTitle(id<0?"Add flashcard":"Edit flashcard").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
            String question=qe.getText().toString().trim(),answer=ae.getText().toString().trim();
            if(question.isEmpty()||answer.isEmpty()){Toast.makeText(this,"Question and answer required",Toast.LENGTH_SHORT).show();return;}
            db.saveCard(uid,id,question,answer);refresh();}).show();}
}
