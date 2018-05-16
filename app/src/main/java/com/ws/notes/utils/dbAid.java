package com.ws.notes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ws.notes.CalendarActivity;
import com.ws.notes.Note;
import com.ws.notes.NoteAdapter;
import com.ws.notes.widget.WidgetInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 存放各种操作方法的助手类
 * Created by KanModel on 2017/11/26.
 */

public abstract class dbAid {

    private static final String TAG = "AidClass";

    public static int pos = 0;

    /*Note相关*/

    /**
     * @param dbHelper 数据库操作类
     * @return Note类
     */
    public static Note addSQLNote(DatabaseHelper dbHelper) {
        return addSQLNote(dbHelper, "content", "title");
    }

    /**
     * @param dbHelper 数据库操作类
     * @param note     Note类
     */
    public static void addSQLNote(DatabaseHelper dbHelper, Note note) {
        addSQLNote(dbHelper, note.getContent(), note.getTitle());
    }

    /**
     * @param dbHelper 数据库操作类
     * @param content  内容
     * @param title    标题
     * @return 返回新添加的Note类
     */
    public static Note addSQLNote(DatabaseHelper dbHelper, String content, String title) {
        return addSQLNote(dbHelper, content, title, TimeAid.getNowTime(), TimeAid.getNowTime());
    }

    public static Note addSQLNote(DatabaseHelper dbHelper, String content, String title, long timeStamp, long lastChangedTimeStamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Note note;
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("title", title);
        values.put("time", timeStamp);
        values.put("lastChangedTime", lastChangedTimeStamp);
        db.insert("Note", null, values);
        Log.d(TAG, "addSQLNote: timestamp:" + timeStamp);
        //获取数据库最后一条信息
        Cursor cursor1 = db.rawQuery("select * from Note", null);
        if (cursor1.moveToLast()) {
            String logtime = cursor1.getString(cursor1.getColumnIndex("logtime"));
            long time = cursor1.getLong(cursor1.getColumnIndex("time"));
            long lastChangedTime = cursor1.getLong(cursor1.getColumnIndex("lastChangedTime"));
            note = new Note(title, content, logtime, time, lastChangedTime);
        } else {
            note = null;
        }
        cursor1.close();
        db.close();
        return note;
    }

    /**
     * @param title           标题
     * @param content         内容
     * @param time            时间戳
     * @param pos             在RecyclerView中的位置
     * @param lastChangedTime 最后更改的时间戳
     */
    public static void updateSQLNote(Context context,int id,String title, String content, long time, int pos, long lastChangedTime) {
        SQLiteDatabase db = getDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("lastChangedTime", lastChangedTime);
        db.update("Note", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        NoteAdapter.getNotes().get(pos).setTitle(title);
        NoteAdapter.getNotes().get(pos).setContent(content);
        NoteAdapter.getNotes().get(pos).setLastChangedTime(lastChangedTime);
        CalendarActivity.getNoteAdapter().refreshData(pos);
    }

    /**
     * 根据时间戳搜索数据库中的内容设置isDeleted为1代表删除
     *
     * @param time 时间戳
     */
    public static void deleteSQLNote(Context context,long time) {
        SQLiteDatabase db = getDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        db.close();
    }

    public static void setSQLNote(Context context,long time, int isDeleted) {
        SQLiteDatabase db = getDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", isDeleted);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        db.close();
    }

    /**
     * 清空数据库
     */
    public static void deleteSQLNoteForced(Context context) {
        SQLiteDatabase db = getDbHelper(context).getWritableDatabase();
        db.delete("Note", "time > ?", new String[]{"0"});
        db.close();
    }

    /**
     * 根据时间删除
     */
    public static void deleteSQLNoteForced(Context context,long time) {
        SQLiteDatabase db = getDbHelper(context).getWritableDatabase();
        db.delete("Note", "time = ?", new String[]{String.valueOf(time)});
        db.close();
    }
    public static List<Note> querySQLNotes(DatabaseHelper dbHelper, int year ,int month,int day) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String title = null, content = null,logtime=null;
        int id,isDeleted;
        long lastChangedTime,time;
        List<Note>noteList=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,month,day,0,0,0);
        Log.i("TAG","calendar:"+calendar.getTime().getTime());
        long before=calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        long after=calendar.getTimeInMillis();
        Log.i(TAG, "querySQLNotes: before:"+before+"after:"+after+"\n");
        Cursor cursor=db.rawQuery("select * from Note where time>=? and time <?",new String[]{String.valueOf(before),String.valueOf(after)});
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                id = cursor.getInt(cursor.getColumnIndex("id"));
                content = cursor.getString(cursor.getColumnIndex("content"));
                title = cursor.getString(cursor.getColumnIndex("title"));
                isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                time = cursor.getLong(cursor.getColumnIndex("time"));
                lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDeleted == 0) {
                    noteList.add(new Note(id,title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }
    public static List<Note> initNotes(DatabaseHelper dbHelper) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDeleted == 0) {
                    noteList.add(0, new Note(id,title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public static void initNotes(DatabaseHelper dbHelper, List<Note> noteList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        noteList.clear();
        Cursor cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDeleted == 0) {
                    noteList.add(0, new Note(id,title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public static Note querySQLNote(DatabaseHelper dbHelper, long time) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String title = "", content = "";
        int isDeleted;
        Cursor cursor = db.query("Note", null, "time like ?", new String[]{String.valueOf(time)}, null, null, null);
        if (cursor.moveToLast()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
        } else {
            isDeleted = 1;
        }
        cursor.close();
        db.close();
        if (isDeleted == 1) {
            return new Note(title+"此便签可能以删除,请您手动删除", "", TimeAid.getNowTime());
        }
        return new Note(title, content, time);
    }


    /*Widget相关*/
    public static WidgetInfo querySQLWidget(DatabaseHelper dbHelper, long time) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int widgetID, isDeleted;
        Cursor cursor = db.query("widget", null, "time like ?", new String[]{String.valueOf(time)}, null, null, null);
        if (cursor.moveToLast()) {
            widgetID = cursor.getInt(cursor.getColumnIndex("widgetID"));
            isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
        } else {
            widgetID = isDeleted = 1;
        }
        cursor.close();
        db.close();
        if (isDeleted == 1) {
            return null;
        }
        return new WidgetInfo(time, widgetID, querySQLNote(dbHelper, time));
    }

    public static WidgetInfo querySQLWidget(Context context, long time) {
        return querySQLWidget(getDbHelper(context), time);
    }

    public static void addSQLWidget(DatabaseHelper dbHelper, long time, int appWidgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("widgetID", appWidgetId);
        db.insert("widget", null, values);
        db.close();
    }

    public static void deleteSQLWidget(DatabaseHelper dbHelper, int widgetID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("widget", values, "widgetID = ?", new String[]{String.valueOf(widgetID)});
        db.close();
    }

    /*notice相关*/


    public static void addSQLNotice(DatabaseHelper dbHelper, long time, long dstTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("dstTime", dstTime);
        db.insert("notice", null, values);
        db.close();
    }

    public static void addSQLNotice(Context context, long time, long dstTime) {
        addSQLNotice(getDbHelper(context), time, dstTime);
    }

    public static void updateSQLNotice(DatabaseHelper dbHelper, long time, long dstTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("dstTime", dstTime);
        db.update("notice", values, "time = ?", new String[]{String.valueOf(time)});
        db.close();
    }

    public static void updateSQLNotice(DatabaseHelper dbHelper, long time, int done) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("isDone", done);
        db.update("notice", values, "time = ?", new String[]{String.valueOf(time)});
        db.close();
    }

    public static void updateSQLNotice(Context context, long time, long dstTime) {
        updateSQLNotice(getDbHelper(context), time, dstTime);
    }

    public static long querySQLNotice(DatabaseHelper dbHelper, long time) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long dstTime;
        int isDone;
        Cursor cursor = db.query("notice", null, "time like ?", new String[]{String.valueOf(time)}, null, null, null);
        if (cursor.moveToLast()) {
            isDone = cursor.getInt(cursor.getColumnIndex("isDone"));
            dstTime = cursor.getLong(cursor.getColumnIndex("dstTime"));
        } else {
            isDone = 1;
            dstTime = 0;
        }
        cursor.close();
        db.close();
        if (isDone == 1) {
            return 0;
        }
        return dstTime;
    }

    public static long querySQLNotice(Context context, long time) {
        return querySQLNotice(getDbHelper(context), time);
    }

    public static void newSQLNotice(Context context, long time, long dstTime) {
        DatabaseHelper dbHelper = getDbHelper(context);
        long oldDstTime = querySQLNotice(dbHelper, time);
        if (oldDstTime == 0) {
            //不存就添加
            addSQLNotice(dbHelper, time, dstTime);
        } else {
            updateSQLNotice(dbHelper, time, dstTime);
        }
    }

    public static void setSQLNoticeDone(Context context, long time, int done) {
        updateSQLNotice(getDbHelper(context), time, done);
    }


    public static DatabaseHelper getDbHelper(Context context) {
        return new DatabaseHelper(context, "Note.db", null, 12);
    }

}
