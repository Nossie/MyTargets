package de.dreier.mytargets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class TargetOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRAINING = "TRAINING";
    public static final String TRAINING_ID = "_id";
    public static final String TRAINING_TITLE = "title";
    public static final String TRAINING_DATE = "datum";

    private static final String TABLE_ROUND = "ROUND";
    public static final String RUNDE_ID = "_id";
    public static final String RUNDE_TRAINING = "training";
    public static final String RUNDE_INDOOR = "indoor";
    public static final String RUNDE_DISTANCE = "distance";
    public static final String RUNDE_UNIT = "unit";
    public static final String RUNDE_BOW = "bow";
    public static final String RUNDE_PPP = "ppp";
    public static final String RUNDE_TARGET = "target";

    private static final String TABLE_PASSE = "PASSE";
    public static final String PASSE_ID = "_id";
    public static final String PASSE_ROUND = "round";

    private static final String TABLE_SHOOT = "SHOOT";
    public static final String SHOOT_ID = "_id";
    public static final String SHOOT_PASSE = "passe";
    public static final String SHOOT_ZONE = "points";

    private static final String TABLE_BOW = "BOW";
    public static final String BOW_ID = "_id";
    public static final String BOW_NAME = "name";
    public static final String BOW_BRAND = "brand";
    public static final String BOW_TYPE = "type";
    public static final String BOW_SIZE = "size";
    public static final String BOW_HEIGHT = "hight";
    public static final String BOW_TILLER = "tiller";
    public static final String BOW_DESCRIPTION = "description";
    public static final String BOW_THUMBNAIL = "thumbnail";
    public static final String BOW_IMAGE = "image";

    private static final String TABLE_BOW_IMAGE = "BOW_IMAGE";
    public static final String BOW_IMAGE_ID = "_id";
    public static final String BOW_IMAGE_DATA = "data";


    private static final String TABLE_VISIER = "VISIER";
    public static final String VISIER_ID = "_id";
    public static final String VISIER_BOW = "bow";
    public static final String VISIER_DISTANCE = "distance";
    public static final String VISIER_SETTING = "setting";

    private static final String CREATE_TABLE_BOW =
            "CREATE TABLE "+ TABLE_BOW +" ( "+
                    BOW_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    BOW_NAME +" TEXT,"+
                    BOW_BRAND +" TEXT,"+
                    BOW_TYPE +" INTEGER,"+
                    BOW_SIZE +" INTEGER,"+
                    BOW_HEIGHT +" TEXT,"+
                    BOW_TILLER +" TEXT,"+
                    BOW_DESCRIPTION +" TEXT,"+
                    BOW_THUMBNAIL +" BLOB,"+
                    BOW_IMAGE+" INTEGER REFERENCES "+TABLE_BOW_IMAGE+" ON DELETE SET NULL);";

    private static final String CREATE_TABLE_BOW_IMAGE =
            "CREATE TABLE "+ TABLE_BOW_IMAGE +" ( "+
                    BOW_IMAGE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    BOW_IMAGE_DATA +" BLOB);";

    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE "+TABLE_VISIER+" ( "+
                    VISIER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    VISIER_BOW +" REFERENCES "+ TABLE_BOW +" ON DELETE CASCADE,"+
                    VISIER_DISTANCE+" INTEGER,"+
                    VISIER_SETTING+" TEXT);";

    private static final String CREATE_TABLE_TRAINING =
            "CREATE TABLE "+TABLE_TRAINING+" ( "+
                    TRAINING_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TRAINING_DATE +" INTEGER,"+
                    TRAINING_TITLE+" TEXT);";

    private static final String CREATE_TABLE_ROUND =
            "CREATE TABLE "+ TABLE_ROUND +" (" +
                    RUNDE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    RUNDE_INDOOR+" BOOLEAN,"+
                    RUNDE_DISTANCE+" INTEGER," +
                    RUNDE_UNIT +" TEXT," +
                    RUNDE_PPP+" INTEGER,"+
                    RUNDE_TARGET +" INTEGER,"+
                    RUNDE_BOW +" REFERENCES "+ TABLE_BOW +" ON DELETE SET NULL,"+
                    RUNDE_TRAINING+" INTEGER REFERENCES "+TABLE_TRAINING+" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_PASSE =
            "CREATE TABLE "+TABLE_PASSE+" (" +
                    PASSE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    PASSE_ROUND +" INTEGER REFERENCES "+ TABLE_ROUND +" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_SHOOT =
            "CREATE TABLE "+TABLE_SHOOT+" (" +
                    SHOOT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    SHOOT_PASSE+" INTEGER REFERENCES "+TABLE_PASSE+" ON DELETE CASCADE,"+
                    SHOOT_ZONE +" INTEGER);";

    TargetOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOW);
        db.execSQL(CREATE_TABLE_BOW_IMAGE);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_TRAINING);
        db.execSQL(CREATE_TABLE_ROUND);
        db.execSQL(CREATE_TABLE_PASSE);
        db.execSQL(CREATE_TABLE_SHOOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRAINING);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ROUND);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PASSE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SHOOT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BOW);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BOW_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_VISIER);
        onCreate(db);
    }

    public Cursor getTrainings() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_TRAINING, null, null, null, null, null, TRAINING_DATE+" ASC");
    }

    public Cursor getRunden(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+training};
        return db.query(TABLE_ROUND,null,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
    }

    public Cursor getPasses(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+round};
        return db.query(TABLE_PASSE,null, PASSE_ROUND +"=?",args,null,null,PASSE_ID+" ASC");
    }

    public Cursor getBows() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BOW,null,null,null,null,null, BOW_ID+" ASC");
    }

    public long addPasseToRound(long round, int[] points) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSE_ROUND, round);
        long insertId = db.insert(TABLE_PASSE, null, values);
        for(int point:points) {
            values = new ContentValues();
            values.put(SHOOT_PASSE, insertId);
            values.put(SHOOT_ZONE, point);
            db.insert(TABLE_SHOOT, null, values);
        }
        db.close();
        return insertId;
    }

    public long newTraining() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRAINING_DATE, System.currentTimeMillis());
        values.put(TRAINING_TITLE, "Training");
        long insertId = db.insert(TABLE_TRAINING, null, values);
        db.close();
        return insertId;
    }

    public long newRound(long training, int distance, String unit, boolean indoor, int ppp, int target, long bow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RUNDE_DISTANCE, distance);
        values.put(RUNDE_UNIT, unit);
        values.put(RUNDE_INDOOR, indoor);
        values.put(RUNDE_TARGET, target);
        values.put(RUNDE_BOW, bow);
        values.put(RUNDE_PPP, ppp);
        values.put(RUNDE_TRAINING, training);
        long round = db.insert(TABLE_ROUND, null, values);
        db.close();
        return round;
    }

    public int[] getPasse(long round, int passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {""+round};
        Cursor res1 = db.query(TABLE_PASSE,cols1, PASSE_ROUND +"=?",args1,null,null,PASSE_ID+" ASC");
        if(!res1.moveToPosition(passe-1))
            return null;
        long passeId = res1.getLong(0);
        String[] cols2 = {SHOOT_ZONE};
        String[] args2 = {""+passeId};
        Cursor res = db.query(TABLE_SHOOT,cols2,SHOOT_PASSE+"=?",args2,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        int[] points = new int[count];
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            points[i] = res.getInt(0);
            res.moveToNext();
        }
        res.close();
        db.close();
        return points;
    }

    public Training getTraining(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {TRAINING_ID,TRAINING_TITLE,TRAINING_DATE};
        String[] args = {""+training};
        Cursor res = db.query(TABLE_TRAINING,cols,TRAINING_ID+"=?",args,null,null,null);
        res.moveToFirst();

        Training tr = new Training();
        tr.id = res.getLong(0);
        tr.title = res.getString(1);
        tr.date = new Date(res.getLong(2));
        res.close();
        db.close();
        return tr;
    }

    public int[] getPasse(long passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {SHOOT_ID, SHOOT_ZONE};
        String[] args = {""+passe};
        Cursor res = db.query(TABLE_SHOOT,cols,SHOOT_PASSE+"=?",args,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        int[] points = new int[count];
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            points[i] = res.getInt(1);
            res.moveToNext();
        }
        res.close();
        db.close();
        return points;
    }

    public Round getRound(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {RUNDE_PPP,RUNDE_TRAINING,RUNDE_TARGET,RUNDE_INDOOR,RUNDE_DISTANCE,RUNDE_UNIT,RUNDE_BOW};
        String[] args = {""+round};
        Cursor res = db.query(TABLE_ROUND,cols,RUNDE_ID+"=?",args,null,null,null);
        res.moveToFirst();
        Round r = new Round();
        r.ppp = res.getInt(0);
        r.training = res.getLong(1);
        r.target = res.getInt(2);
        r.indoor = res.getInt(3)!=0;
        r.distance = ""+NewRoundActivity.distanceValues[res.getInt(4)]+res.getString(5);
        r.distanceInd = res.getInt(4);
        r.bow = res.getInt(6);
        res.close();
        db.close();
        return r;
    }

    public int getRoundPoints(long round, int tar) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+round};
        Cursor res = db.rawQuery("SELECT s."+ SHOOT_ZONE +
                " FROM "+TABLE_PASSE+" p, "+TABLE_SHOOT+" s"+
                " WHERE p."+ PASSE_ROUND +"=? AND s."+SHOOT_PASSE+"=p."+PASSE_ID,args);
        res.moveToFirst();
        int[] target = TargetView.target_points[tar];
        int sum = 0;
        for(int i=0;i<res.getCount();i++) {
            int zone = res.getInt(0);
            if(zone>-1)
                sum+=target[zone];
            res.moveToNext();
        }
        res.close();
        db.close();
        return sum;
    }

    public int getRoundInd(long training, long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {RUNDE_ID};
        String[] args = {""+training};
        Cursor res = db.query(TABLE_ROUND,cols,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
        res.moveToFirst();
        for(int i=1;i<res.getCount()+1;i++) {
            if(round==res.getLong(0)) {
                res.close();
                db.close();
                return i;
            }
            res.moveToNext();
        }
        res.close();
        db.close();
        return 0;
    }

    public void deletePasses(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_PASSE,PASSE_ID+"=?",args);
        }
        db.close();
    }

    public void deleteRounds(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_ROUND,RUNDE_ID+"=?",args);
        }
        db.close();
    }

    public void deleteTrainings(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_TRAINING,TRAINING_ID+"=?",args);
        }
        db.close();
    }

    public void deleteBows(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_BOW, BOW_ID +"=?",args);
        }
        db.close();
    }

    public void updatePasse(long round, int passe, int[] zones) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {""+round};
        Cursor res1 = db.query(TABLE_PASSE,cols1, PASSE_ROUND +"=?",args1,null,null,PASSE_ID+" ASC");
        if(!res1.moveToPosition(passe-1))
            return;
        long passeId = res1.getLong(0);
        String[] cols2 = {SHOOT_ID};
        String[] args2 = {""+passeId};
        Cursor res = db.query(TABLE_SHOOT,cols2,SHOOT_PASSE+"=?",args2,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            String[] args3 = {""+res.getLong(0)};
            ContentValues values = new ContentValues();
            values.put(SHOOT_ZONE,zones[i]);
            db.update(TABLE_SHOOT,values,SHOOT_ID+"=?",args3);
            res.moveToNext();
        }
        res.close();
        db.close();
    }

    public long updateBow(long bowId, long imageId, String name, int bowType, String marke, String size, String height, String tiller, String desc, Bitmap thumb, Bitmap img) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        byte[] imageData = getBitmapAsByteArray(img);
        values.put(BOW_IMAGE_DATA,imageData);
        if(imageId==-1) {
            imageId = db.insert(TABLE_BOW_IMAGE,null,values);
        } else {
            String[] args = {"" + imageId};
            db.update(TABLE_BOW_IMAGE,values,BOW_IMAGE_ID+"=?",args);
        }
        values.clear();
        values.put(BOW_NAME,name);
        values.put(BOW_TYPE,bowType);
        values.put(BOW_BRAND,marke);
        values.put(BOW_SIZE,size);
        values.put(BOW_HEIGHT,height);
        values.put(BOW_TILLER,tiller);
        values.put(BOW_DESCRIPTION,desc);
        imageData = getBitmapAsByteArray(thumb);
        values.put(BOW_THUMBNAIL,imageData);
        values.put(BOW_IMAGE,imageId);
        if(bowId==-1) {
            bowId = db.insert(TABLE_BOW,null,values);
        } else {
            String[] args = {"" + bowId};
            db.update(TABLE_BOW,values,BOW_ID+"=?",args);
        }
        return bowId;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Bow getBow(long bowId, boolean small) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {BOW_NAME,BOW_TYPE, BOW_BRAND,BOW_SIZE,BOW_HEIGHT,BOW_TILLER,BOW_DESCRIPTION,BOW_THUMBNAIL,BOW_IMAGE};
        String[] args = {""+bowId};
        Cursor res = db.query(TABLE_BOW,cols,BOW_ID+"=?",args,null,null,null);
        Bow bow = null;
        if(res.moveToFirst()) {
            bow = new Bow();
            bow.id = bowId;
            bow.name = res.getString(0);
            bow.type = res.getInt(1);
            bow.brand = res.getString(2);
            bow.size = res.getString(3);
            bow.height = res.getString(4);
            bow.tiller = res.getString(5);
            bow.description = res.getString(6);
            if(small) {
                byte[] data = res.getBlob(7);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                res.close();
            } else {
                bow.imageId = res.getLong(8);
                res.close();
                String[] cols2 = {BOW_IMAGE_DATA};
                String[] args2 = {""+bow.imageId};
                res = db.query(TABLE_BOW_IMAGE,cols2,BOW_IMAGE_ID+"=?",args2,null,null,null);
                res.moveToFirst();
                byte[] data = res.getBlob(0);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                res.close();
            }
        }
        return bow;
    }

    public void updateSightSettings(long bowId, ArrayList<EditBowActivity.SightSetting> sightSettingsList) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {"" + bowId};
        db.delete(TABLE_VISIER,VISIER_BOW+"=?",args);
        for(EditBowActivity.SightSetting set:sightSettingsList) {
            ContentValues values = new ContentValues();
            values.put(VISIER_DISTANCE,set.distanceInd);
            values.put(VISIER_SETTING,set.value);
            db.insert(TABLE_VISIER,null,values);
        }

    }

    public String getSetting(long bowId, int distInd) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {VISIER_SETTING};
        String[] args = {""+bowId,""+distInd};
        Cursor res = db.query(TABLE_VISIER,cols,VISIER_BOW+"=? AND "+VISIER_DISTANCE+"=?",args,null,null,null);
        String s = "";
        if(res.moveToFirst()) {
            s = res.getString(0);
        }
        res.close();
        db.close();
        return s;
    }

    public ArrayList<EditBowActivity.SightSetting> getSettings(long bowId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {VISIER_DISTANCE,VISIER_SETTING};
        String[] args = {""+bowId};
        Cursor res = db.query(TABLE_VISIER,cols,VISIER_BOW+"=?",args,null,null,null);
        String s = null;
        res.moveToFirst();
        ArrayList<EditBowActivity.SightSetting> list = new ArrayList<EditBowActivity.SightSetting>();
        for(int i=0;i<res.getCount();i++) {
            EditBowActivity.SightSetting set = new EditBowActivity.SightSetting();
            set.distanceInd = res.getInt(0);
            set.value = res.getString(1);
            list.add(set);
            res.moveToNext();
        }
        res.close();
        db.close();
        return list;
    }

    public class Bow {
        long id;
        long imageId;
        String name;
        int type;
        String brand;
        String size;
        String height;
        String tiller;
        String description;
        Bitmap image;
    }

    public class Training {
        long id;
        String title;
        Date date;
    }

    public class Round {
        int ppp;
        int target;
        long training;
        public boolean indoor;
        public String distance;
        public int bow;
        public int distanceInd;
    }
}