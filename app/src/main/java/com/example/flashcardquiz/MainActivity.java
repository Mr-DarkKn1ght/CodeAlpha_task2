package com.example.flashcardquiz;

import android.app.*; import android.os.*; import android.content.*; import android.database.*; import android.view.*; import android.widget.*; import java.util.*;

public class MainActivity extends Activity {
    DatabaseHelper db; int uid,index=0; ArrayList<Integer> ids=new ArrayList<>(); ArrayList<String> qs=new ArrayList<>(),as=new ArrayList<>();
    TextView question,answer,count; Button show;
    public void onCreate(Bundle b){super.onCreate(b);uid=getSharedPreferences("session",0).getInt("uid",-1);
        if(uid<0){startActivity(new Intent(this,LoginActivity.class));finish();return;}
        setContentView(R.layout.activity_main);db=new DatabaseHelper(this);
        question=findViewById(R.id.question);answer=findViewById(R.id.answer);count=findViewById(R.id.count);show=findViewById(R.id.showAnswer);
        findViewById(R.id.previous).setOnClickListener(v->{if(index>0){index--;render();}});
        findViewById(R.id.next).setOnClickListener(v->{if(index<qs.size()-1){index++;render();}});
        show.setOnClickListener(v->{answer.setVisibility(answer.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);show.setText(answer.getVisibility()==View.VISIBLE?"Hide Answer":"Show Answer");});
        findViewById(R.id.manage).setOnClickListener(v->startActivity(new Intent(this,ManageCardsActivity.class)));
        findViewById(R.id.logout).setOnClickListener(v->{getSharedPreferences("session",0).edit().clear().apply();startActivity(new Intent(this,LoginActivity.class));finish();});
        load();
    }
    void load(){ids.clear();qs.clear();as.clear();Cursor c=db.cards(uid);try{while(c.moveToNext()){ids.add(c.getInt(0));qs.add(c.getString(1));as.add(c.getString(2));}}finally{c.close();}
        if(index>=qs.size())index=Math.max(0,qs.size()-1);render();}
    void render(){boolean has=!qs.isEmpty();question.setText(has?qs.get(index):"No flashcards yet. Tap Manage Cards to add one.");
        answer.setText(has?as.get(index):"");answer.setVisibility(View.GONE);show.setVisibility(has?View.VISIBLE:View.GONE);show.setText("Show Answer");
        count.setText(has?(index+1)+" / "+qs.size():"0 cards");findViewById(R.id.previous).setEnabled(index>0);findViewById(R.id.next).setEnabled(index<qs.size()-1);}
    protected void onResume(){super.onResume();if(db!=null)load();}
}
