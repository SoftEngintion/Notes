package com.ws.notes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;
import com.ws.notes.receiver.AlarmReceiver;
import com.ws.notes.ui.TimeAndDatePickerDialog;
import com.ws.notes.utils.TimeAid;
import com.ws.notes.utils.Utils;
import com.ws.notes.utils.dbAid;
import com.ws.notes.widget.NoteAppWidget;

import java.util.Locale;

/**
 * 编辑便签的Activity
 */

public class EditActivity extends AppCompatActivity implements TimeAndDatePickerDialog.TimePickerDialogInterface {
    private static final String TAG = "EditActivity";

    private TimeAndDatePickerDialog dialog;

    private EditText titleET;
    private EditText contentET;
    private long time;
    private long lastChangedTime;
    boolean isNew;
    private Intent parentIntent;
    private String title;
    private String content;
    private int pos;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Log.d(TAG, "onCreate: start " + Utils.getVersionName(this));
        titleET = findViewById(R.id.editor_title);
        final LinearLayout mLinearLayout_add_desktop=findViewById(R.id.mLinearLayout_add_desktop);
        final LinearLayout mLinearLayout_add_time=findViewById(R.id.mLinearLayout_add_time);
        final SwitchButton switch_add_to_desktop=findViewById(R.id.SwitchButton_add_to_desktop);
        final SwitchButton switch_add_time=findViewById(R.id.SwitchButton_add_time);
        TextView timeTV = findViewById(R.id.editor_time);
        contentET = findViewById(R.id.editor_content);
        parentIntent = getIntent();
        title = parentIntent.getStringExtra("title");
        content = parentIntent.getStringExtra("content");
        pos = parentIntent.getIntExtra("pos", 0);
        titleET.setText(title);
        contentET.setText(content);
        final AppCompatButton appCompatButton=findViewById(R.id.mButton_yes);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNew) {
                    if(title.isEmpty())title=titleET.getText().toString();
                    if(content.isEmpty())content=contentET.getText().toString();
                    saveNewNote(title, content);
                    Toast.makeText(EditActivity.this,R.string.save_success,Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!contentET.getText().toString().isEmpty()&&!titleET.getText().toString().isEmpty()
                        &&mLinearLayout_add_desktop.getVisibility()==View.INVISIBLE
                        &&mLinearLayout_add_time.getVisibility()==View.INVISIBLE){
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                    if(isNew&&appCompatButton.getVisibility()==View.INVISIBLE)appCompatButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!contentET.getText().toString().isEmpty()&&!titleET.getText().toString().isEmpty()
                        &&mLinearLayout_add_desktop.getVisibility()==View.INVISIBLE
                        &&mLinearLayout_add_time.getVisibility()==View.INVISIBLE){
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                    if(isNew&&appCompatButton.getVisibility()==View.INVISIBLE)appCompatButton.setVisibility(View.VISIBLE);
                }
            }
        });
        contentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!contentET.getText().toString().isEmpty()&&!titleET.getText().toString().isEmpty()
                        &&mLinearLayout_add_desktop.getVisibility()==View.INVISIBLE
                        &&mLinearLayout_add_time.getVisibility()==View.INVISIBLE){
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                    if(isNew&&appCompatButton.getVisibility()==View.INVISIBLE)appCompatButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!contentET.getText().toString().isEmpty()&&!titleET.getText().toString().isEmpty()
                        &&mLinearLayout_add_desktop.getVisibility()==View.INVISIBLE
                        &&mLinearLayout_add_time.getVisibility()==View.INVISIBLE){
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                    if(isNew&&appCompatButton.getVisibility()==View.INVISIBLE)appCompatButton.setVisibility(View.VISIBLE);
                }
            }
        });
        switch_add_time.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    dialog = new TimeAndDatePickerDialog(EditActivity.this);
                    dialog.showDateAndTimePickerDialog();
                }
            }
        });
        switch_add_to_desktop.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    dbAid.pos = pos;
                    Toast.makeText(EditActivity.this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }
            }
        });
        time = parentIntent.getLongExtra("timeLong", 0);
        lastChangedTime = parentIntent.getLongExtra("lastChangedTime", 0);
        if (time == lastChangedTime) {
            timeTV.setText(parentIntent.getStringExtra("time"));
        } else {
            timeTV.setText(TimeAid.stampToDate(time) + getResources().getString(R.string.lastUpdate)+ TimeAid.stampToDate(lastChangedTime));
        }

        isNew = parentIntent.getBooleanExtra("isNew", false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        String title = titleET.getText().toString();
        String content = contentET.getText().toString();
        if (isNew) {
            if (content.equals("") && title.equals("")) {
                Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
            } else {
                saveNewNote(title, content);
            }
        } else {
            if (this.content.equals(content) && this.title.equals(title)) {
                if (MainActivity.getIsDebug()) {
                    Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                }
            } else {
                saveOriginalNote(title, content);
            }
            NoteAppWidget.updateWidget(this, time, title, content);
            MainActivity.getNoteAdapter().refreshAllDataForce();
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNew) {
            menu.setGroupVisible(R.id.edit_new_group, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(titleET.getText().toString().isEmpty()&&contentET.getText().toString().isEmpty())
            Toast.makeText(this,R.string.edit_title_hint,Toast.LENGTH_SHORT).show();
        else if(menu!=null)menu.setGroupVisible(R.id.edit_new_group,true);
        return super.onMenuOpened(featureId, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*返回按钮*/
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home");
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();
                if (isNew) {
                    if (content.equals("") && title.equals("")) {
                        Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
                    } else {
                        saveNewNote(title, content);
                    }
                } else {
                    if (this.content.equals(content) && this.title.equals(title)) {
                        if (MainActivity.getIsDebug()) {
                            Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.getNoteAdapter().refreshData(pos);
                    } else {
                        saveOriginalNote(title, content);
                        MainActivity.getNoteAdapter().refreshAllDataForce();
                        NoteAppWidget.updateWidget(this, time, title, content);
                    }
                }
                finish();
                return true;
            case R.id.add_to_desktop:
                dbAid.pos = pos;
                Toast.makeText(this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            case R.id.add_time:
                dialog = new TimeAndDatePickerDialog(this);
                dialog.showDateAndTimePickerDialog();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void saveNewNote(final String title, final String content) {
        final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("保存您的便笺");
        progressDialog.setMessage("正在保存...");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                lastChangedTime = TimeAid.getNowTime();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.cancel();
                progressDialog.dismiss();
                MainActivity.getNoteAdapter().addData(dbAid.addSQLNote(MainActivity.getDbHelper(), content, title, lastChangedTime, lastChangedTime));
                MainActivity.getRecyclerView().scrollToPosition(0);
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void saveOriginalNote(final String title, final String content) {
        final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("保存您的更改");
        progressDialog.setMessage("正在保存...");
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                lastChangedTime = TimeAid.getNowTime();
                int pos = parentIntent.getIntExtra("pos", 0);
                dbAid.updateSQLNote(title, content, time, pos, lastChangedTime);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.cancel();
                progressDialog.dismiss();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void positiveListener() {
        String dstStr = String.format(Locale.CHINA, "%d-%d-%d %d:%d:00", dialog.getYear(), dialog.getMonth(), dialog.getDay(), dialog.getHour(), dialog.getMinute());
        long dstTime = TimeAid.dateToStamp(dstStr);
        long nowTime = TimeAid.getNowTime();
        long dDay = TimeAid.getDiffDay(dstTime, nowTime);
        long dHour = TimeAid.getDiffHour(dstTime, nowTime);
        long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
        title = titleET.getText().toString();
        if (dDay > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dDay + "天后提醒你", Toast.LENGTH_SHORT).show();
            AlarmReceiver.setAlarm(this,dbAid.querySQLNote(MainActivity.getDbHelper(),dstTime).getId(), dDay * 60 * 24 + dHour * 60 + dMinute, title);
            dbAid.newSQLNotice(this, time, dstTime);
        } else if (dHour > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dHour + "小时后提醒你", Toast.LENGTH_SHORT).show();
            AlarmReceiver.setAlarm(this,dbAid.querySQLNote(MainActivity.getDbHelper(),dstTime).getId(), dDay * 60 * 24 + dHour * 60 + dMinute, title);
            dbAid.newSQLNotice(this, time, dstTime);
        } else if (dMinute > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dMinute + "分钟后提醒你", Toast.LENGTH_SHORT).show();
            dbAid.newSQLNotice(this, time, dstTime);
            AlarmReceiver.setAlarm(this, dbAid.querySQLNote(MainActivity.getDbHelper(),dstTime).getId(),dDay * 60 * 24 + dHour * 60 + dMinute, title);
        }else {
            Toast.makeText(this,R.string.setAlarm_error,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void negativeListener() {
    }
}
