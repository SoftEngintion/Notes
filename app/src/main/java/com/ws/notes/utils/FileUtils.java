package com.ws.notes.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ws.notes.R;
import com.ws.notes.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import static android.support.v4.content.FileProvider.getUriForFile;

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

    public static void showSendFileScreen(@NonNull String archiveFilename, SettingsActivity activity) {
        File file = new File(archiveFilename);
        Uri fileUri = getUriForFile(activity, "com.ws.notes", file);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            File foder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/***");//声明存储位置
            if (!foder.exists()) {//判断文件夹是否存在，如不存在就重新创建
                foder.mkdirs();
            }
//            fillTemplate(file,fileUri);
            Toast.makeText(activity.getApplicationContext(),R.string.export_success,Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            Intent chooser = Intent.createChooser(intent, activity.getResources().getString(R.string.save_backup));
            activity.startActivity(chooser);
        }
    }
//    public static void fillTemplate(File file,Uri uri) {// 利用模板生成pdf
//            // 模板路径
////            String templatePath = "D:/Temp/pdf/pdf-template-form.pdf";
//            // 生成的新文件路径
////            String fileName = StringExtend.format("itext-template-{0}.pdf", DateExtend.getDate("yyyyMMddHHmmss"));
////            String newPDFPath = PathExtend.Combine("D:/Temp/pdf/", fileName);
//            try {
//                //Initialize PDF document
//                PdfDocument pdf = new PdfDocument(new PdfWriter(file));
//                PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
////                Map<String, PdfFormField> fields = form.getFormFields();
////                //处理中文问题
////                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
////                int i = 0;
////                java.util.Iterator<String> it = fields.keySet().iterator();
////                while (it.hasNext()) {
////                    //获取文本域名称
////                    String name = it.next().toString();
////                    //填充文本域
////                    fields.get(name).setValue(str[i++]).setFont(font).setFontSize(12);
////                    System.out.println(name);
////                }
//                form.flattenFields();//设置表单域不可编辑
//                pdf.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
}
