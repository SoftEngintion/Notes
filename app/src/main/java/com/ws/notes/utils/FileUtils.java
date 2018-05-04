package com.ws.notes.utils;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ws.notes.R;
import com.ws.notes.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * 文件操作相关函数集
 */

public abstract class FileUtils {
    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        copy(inStream, outStream);
    }

    public static void copy(InputStream inStream, File dst) throws IOException {
        FileOutputStream outStream = new FileOutputStream(dst);
        copy(inStream, outStream);
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int numBytes;
        byte[] buffer = new byte[1024];

        while ((numBytes = in.read(buffer)) != -1)
            out.write(buffer, 0, numBytes);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String saveDatabaseCopy(Context context, File dir)
            throws IOException {
        SimpleDateFormat dateFormat = DateFormats.getBackupDateFormat();
        String date = dateFormat.format(TimeAid.getNowTime());
        String format = "%s/Note Backup %s.db";
        String filename = String.format(format, dir.getAbsolutePath(), date);

        File db = getDatabaseFile(context);
        File dbCopy = new File(filename);
        FileUtils.copy(db, dbCopy);

        return dbCopy.getAbsolutePath();
    }

    @NonNull
    public static File getDatabaseFile(Context context) {
        String databaseFilename = "Note.db";
        String root = context.getFilesDir().getPath();

        String format = "%s/../databases/%s";
        String filename = String.format(format, root, databaseFilename);

        return new File(filename);
    }

    public static void showSendFileScreen(final SettingsActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File foder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/***");//声明存储位置
            if (!foder.exists()) {//判断文件夹是否存在，如不存在就重新创建
                foder.mkdirs();
            }
            final File file =new File(activity.getApplication().getDatabasePath("Note.d"), "backup_Notes.pdf");
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getResources().getString(R.string.saving_backup));
            new AsyncTask<Context, Integer, Void>() {
                @Override
                protected void onPreExecute() {
                    progressDialog.setTitle(R.string.save_backup);
                    progressDialog.show();
                    super.onPreExecute();
                }
                @Override
                protected Void doInBackground(Context... contexts) {
                    backup_tables(activity,file);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(activity,R.string.export_success,Toast.LENGTH_SHORT).show();
                    super.onPostExecute(aVoid);
                }
            }.execute(activity);
        }else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            Intent chooser = Intent.createChooser(intent, activity.getResources().getString(R.string.save_backup));
            activity.startActivity(chooser);
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void backup_tables(Context context, File file) {// 利用模板生成pdf
        PrintAttributes attributes=new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("1","print",1200,1200))
                    .setMinMargins(new PrintAttributes.Margins(0,0,0,0))
                    .build();
        PrintedPdfDocument pdfDocument=new PrintedPdfDocument(context,attributes);
        PrintedPdfDocument.Page page=pdfDocument.startPage(0);
        Canvas canvas=page.getCanvas();
        int titleBaseLine=72;
        int LeftMargin=54;
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(36);
        Cursor cursor = dbAid.getDbHelper(context).getReadableDatabase().
                    query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            /*
            * 打印表头*/
            for(int i=0,size=cursor.getColumnCount(),x=LeftMargin,y=titleBaseLine;i<size;++i) {
                canvas.drawText(cursor.getColumnName(i)+"   |",x,y,paint);
                y+=cursor.getColumnName(i).length()+5;
            }
            /*
            * 打印表信息*/
            int x=LeftMargin,y=titleBaseLine;
            do {
                for(int i=0,size=cursor.getColumnCount();i<size;++i){
                    canvas.drawText(cursor.getString(i),x,y,paint);
                    y+=cursor.getString(i).length()+5;
                }
                x+=5;
            } while (cursor.moveToNext());
        }
        if(cursor.isClosed())cursor.close();
        paint.setColor(Color.BLUE);
        canvas.drawRect(100,100,172,172,paint);
        pdfDocument.finishPage(page);//结束页
        try {
            FileOutputStream outputStream=new FileOutputStream(file);
            pdfDocument.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
