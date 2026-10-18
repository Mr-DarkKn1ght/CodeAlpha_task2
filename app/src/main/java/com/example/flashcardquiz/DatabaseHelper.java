package com.example.flashcardquiz;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Base64;
import java.security.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context c) { super(c, "flashcards.db", null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,email TEXT UNIQUE,salt TEXT,hash TEXT)");
        db.execSQL("CREATE TABLE cards(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id INTEGER,question TEXT,answer TEXT)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV) {}
    private String hash(String password, byte[] salt) {
        try {
            PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,120000,256);
            byte[] result=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return Base64.encodeToString(result,Base64.NO_WRAP);
        } catch(Exception e){ throw new RuntimeException(e); }
    }
    public boolean register(String name,String email,String password) {
        try {
            byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
            ContentValues v=new ContentValues(); v.put("name",name); v.put("email",email.toLowerCase());
            v.put("salt",Base64.encodeToString(salt,Base64.NO_WRAP)); v.put("hash",hash(password,salt));
            getWritableDatabase().insertOrThrow("users",null,v); return true;
        } catch(Exception e){ return false; }
    }
    public int login(String email,String password) {
        Cursor c=getReadableDatabase().rawQuery("SELECT id,salt,hash FROM users WHERE email=?",
                new String[]{email.toLowerCase()});
        try {
            if(!c.moveToFirst()) return -1;
            byte[] salt=Base64.decode(c.getString(1),Base64.NO_WRAP);
            return hash(password,salt).equals(c.getString(2)) ? c.getInt(0) : -1;
        } finally { c.close(); }
    }
    public Cursor cards(int userId) {
        return getReadableDatabase().rawQuery("SELECT id,question,answer FROM cards WHERE user_id=? ORDER BY id",
                new String[]{String.valueOf(userId)});
    }
    public void saveCard(int userId,int id,String q,String a) {
        ContentValues v=new ContentValues(); v.put("question",q); v.put("answer",a);
        if(id<0){v.put("user_id",userId);getWritableDatabase().insert("cards",null,v);}
        else getWritableDatabase().update("cards",v,"id=? AND user_id=?",new String[]{""+id,""+userId});
    }
    public void deleteCard(int userId,int id) {
        getWritableDatabase().delete("cards","id=? AND user_id=?",new String[]{""+id,""+userId});
    }
}
