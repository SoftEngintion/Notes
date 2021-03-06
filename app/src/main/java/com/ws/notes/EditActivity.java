package com.ws.notes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
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

/**
 * 编辑便签的Activity
 */

public class EditActivity extends AppCompatActivity implements TimeAndDatePickerDialog.TimePickerDialogInterface {
    private static final String TAG = "EditActivity";
    boolean isNew;
    private EditText titleET;
    private EditText contentET;
    private long time;
    private long lastChangedTime;
    private Intent parentIntent;
    private String title;
    private String content;
    private int pos;
    private boolean is_add_time;
    private boolean is_add_desktop;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Log.d(TAG, "onCreate: start " + Utils.getVersionName(this));
        titleET = findViewById(R.id.editor_title);
        final LinearLayout mLinearLayout_add_desktop = findViewById(R.id.mLinearLayout_add_desktop);
        final LinearLayout mLinearLayout_add_time = findViewById(R.id.mLinearLayout_add_time);
        final SwitchButton switch_add_to_desktop = findViewById(R.id.SwitchButton_add_to_desktop);
        final SwitchButton switch_add_time = findViewById(R.id.SwitchButton_add_time);
        final AppCompatButton appCompatButton = findViewById(R.id.mButton_yes);
        TextView timeTV = findViewById(R.id.editor_time);
        contentET = findViewById(R.id.editor_content);
        parentIntent = getIntent();
        title = parentIntent.getStringExtra("title");
        content = parentIntent.getStringExtra("content");
        pos = parentIntent.getIntExtra("pos", 0);
        isNew = parentIntent.getBooleanExtra("isNew", false);
        is_add_desktop=parentIntent.getBooleanExtra("is_add_desktop",false);
        is_add_time=parentIntent.getBooleanExtra("is_add_time",false);
        titleET.setText(title);
        contentET.setText(content);
        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!contentET.getText().toString().isEmpty() && !titleET.getText().toString().isEmpty()
                        && mLinearLayout_add_desktop.getVisibility() == View.INVISIBLE
                        && mLinearLayout_add_time.getVisibility() == View.INVISIBLE) {
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                    if (!isNew && appCompatButton.getVisibility() == View.INVISIBLE)
                        appCompatButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!contentET.getText().toString().isEmpty() && !titleET.getText().toString().isEmpty()
                        && mLinearLayout_add_desktop.getVisibility() == View.INVISIBLE
                        && mLinearLayout_add_time.getVisibility() == View.INVISIBLE) {
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                }
            }
        });
        contentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!contentET.getText().toString().isEmpty() && !titleET.getText().toString().isEmpty()
                        && mLinearLayout_add_desktop.getVisibility() == View.INVISIBLE
                        && mLinearLayout_add_time.getVisibility() == View.INVISIBLE) {
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!contentET.getText().toString().isEmpty() && !titleET.getText().toString().isEmpty()
                        && mLinearLayout_add_desktop.getVisibility() == View.INVISIBLE
                        && mLinearLayout_add_time.getVisibility() == View.INVISIBLE) {
                    mLinearLayout_add_desktop.setVisibility(View.VISIBLE);
                    mLinearLayout_add_time.setVisibility(View.VISIBLE);
                }
            }
        });
        switch_add_time.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    is_add_time=true;
                    String dstStr = parentIntent.getStringExtra("dstStr");
                    long dstTime = TimeAid.dateToStamp(dstStr);
                    long nowTime = TimeAid.getNowTime();
                    long dDay = TimeAid.getDiffDay(dstTime, nowTime);
                    long dHour = TimeAid.getDiffHour(dstTime, nowTime);
                    long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
                    if (dDay > 0) {
                        Toast.makeText(EditActivity.this, "你设定了提醒时间 :" + dstStr + "\n将于" + dDay + "天后提醒你", Toast.LENGTH_SHORT).show();
                    } else if (dHour > 0) {
                        Toast.makeText(EditActivity.this, "你设定了提醒时间 :" + dstStr + "\n将于" + dHour + "小时后提醒你", Toast.LENGTH_SHORT).show();
                    } else if (dMinute > 0) {
                        Toast.makeText(EditActivity.this, "你设定了提醒时间 :" + dstStr + "\n将于" + dMinute + "分钟后提醒你", Toast.LENGTH_SHORT).show();
                    } else {
                        is_add_time=false;
                        Toast.makeText(EditActivity.this, R.string.setAlarm_error, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    is_add_time=false;
                }
            }
        });
        switch_add_to_desktop.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                is_add_desktop = isChecked;
            }
        });
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String title_temp = titleET.getText().toString();
                String content_temp = contentET.getText().toString();
                if (title_temp.isEmpty() || content_temp.isEmpty()) {
                    Toast.makeText(EditActivity.this, R.string.empty_note_no_save, Toast.LENGTH_LONG).show();
                } else {
                    if (isNew) {
                        saveNewNote(title_temp, content_temp,time);
                        Toast.makeText(EditActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                    } else {
                        if (content.equals(content_temp) && title.equals(title_temp)) {
                            Toast.makeText(EditActivity.this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                        } else {
                            saveOriginalNote(title_temp, content_temp);
                        }
                        NoteAppWidget.updateWidget(EditActivity.this, time, title_temp, content_temp);
                        CalendarActivity.getNoteAdapter().refreshAllDataForce();
                    }
                    if(is_add_time){
                        String dstStr = parentIntent.getStringExtra("dstStr");
                        title = titleET.getText().toString();
                        long dstTime = TimeAid.dateToStamp(dstStr);
                        long nowTime = TimeAid.getNowTime();
                        long dDay = TimeAid.getDiffDay(dstTime, nowTime);
                        long dHour = TimeAid.getDiffHour(dstTime, nowTime);
                        long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
                        AlarmReceiver.setAlarm(EditActivity.this, dbAid.querySQLNote(dbAid.getDbHelper(EditActivity.this), dstTime).getId(), dDay * 60 * 24 + dHour * 60 + dMinute, title);
                        dbAid.newSQLNotice(EditActivity.this, time, dstTime);
                    }
                    if(is_add_desktop){
                        dbAid.pos = pos;
                        Toast.makeText(EditActivity.this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(Intent.ACTION_MAIN);
                        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        home.addCategory(Intent.CATEGORY_HOME);
                        startActivity(home);
                    }
                }
                finish();
            }
        });
        time = parentIntent.getLongExtra("timeLong", TimeAid.getNowTime());
        lastChangedTime = parentIntent.getLongExtra("lastChangedTime", TimeAid.getNowTime());
        if (time == lastChangedTime) {
            timeTV.setText(parentIntent.getStringExtra("time"));
        } else {
            timeTV.setText(TimeAid.stampToDate(time) + getResources().getString(R.string.lastUpdate) + TimeAid.stampToDate(lastChangedTime));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        String title_temp = titleET.getText().toString();
        String content_temp = contentET.getText().toString();
        if (title_temp.isEmpty() || content_temp.isEmpty()) {
            Toast.makeText(EditActivity.this, R.string.empty_note_no_save, Toast.LENGTH_LONG).show();
        } else {
            if (isNew) {
                saveNewNote(title_temp, content_temp,time);
                Toast.makeText(EditActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
            } else {
                if (content.equals(content_temp) && title.equals(title_temp)) {
                    Toast.makeText(EditActivity.this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                } else {
                    saveOriginalNote(title_temp, content_temp);
                }
                NoteAppWidget.updateWidget(EditActivity.this, time, title_temp, content_temp);
                CalendarActivity.getNoteAdapter().refreshAllDataForce();
            }
            if(is_add_time){
                String dstStr = parentIntent.getStringExtra("dstStr");
                title = titleET.getText().toString();
                long dstTime = TimeAid.dateToStamp(dstStr);
                long nowTime = TimeAid.getNowTime();
                long dDay = TimeAid.getDiffDay(dstTime, nowTime);
                long dHour = TimeAid.getDiffHour(dstTime, nowTime);
                long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
                AlarmReceiver.setAlarm(EditActivity.this, dbAid.querySQLNote(dbAid.getDbHelper(EditActivity.this), dstTime).getId(), dDay * 60 * 24 + dHour * 60 + dMinute, title);
                dbAid.newSQLNotice(EditActivity.this, time, dstTime);
            }
            if(is_add_desktop){
                dbAid.pos = pos;
                Toast.makeText(EditActivity.this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
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
        if (titleET.getText().toString().isEmpty() && contentET.getText().toString().isEmpty())
            Toast.makeText(this, R.string.edit_title_hint, Toast.LENGTH_SHORT).show();
        else if (menu != null) menu.setGroupVisible(R.id.edit_new_group, true);
        return super.onMenuOpened(featureId, menu);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*返回按钮*/
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home");
                String title_temp = titleET.getText().toString();
                String content_temp = contentET.getText().toString();
                if (title_temp.isEmpty() || content_temp.isEmpty()) {
                    Toast.makeText(EditActivity.this, R.string.empty_note_no_save, Toast.LENGTH_LONG).show();
                } else {
                    if (isNew) {
                        saveNewNote(title_temp, content_temp,time);
                        Toast.makeText(EditActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                    } else {
                        if (content.equals(content_temp) && title.equals(title_temp)) {
                            Toast.makeText(EditActivity.this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                        } else {
                            saveOriginalNote(title_temp, content_temp);
                        }
                        NoteAppWidget.updateWidget(EditActivity.this, time, title_temp, content_temp);
                        CalendarActivity.getNoteAdapter().refreshAllDataForce();
                    }
                    if(is_add_time){
                        String dstStr = parentIntent.getStringExtra("dstStr");
                        title = titleET.getText().toString();
                        long dstTime = TimeAid.dateToStamp(dstStr);
                        long nowTime = TimeAid.getNowTime();
                        long dDay = TimeAid.getDiffDay(dstTime, nowTime);
                        long dHour = TimeAid.getDiffHour(dstTime, nowTime);
                        long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
                        AlarmReceiver.setAlarm(EditActivity.this, dbAid.querySQLNote(dbAid.getDbHelper(EditActivity.this), dstTime).getId(), dDay * 60 * 24 + dHour * 60 + dMinute, title);
                        dbAid.newSQLNotice(EditActivity.this, time, dstTime);
                    }
                    if(is_add_desktop){
                        dbAid.pos = pos;
                        Toast.makeText(EditActivity.this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(Intent.ACTION_MAIN);
                        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        home.addCategory(Intent.CATEGORY_HOME);
                        startActivity(home);
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
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewNote(final String title, final String content,long time) {
        lastChangedTime = time;
        if (CalendarActivity.getNoteAdapter() != null)
            CalendarActivity.getNoteAdapter().addData(dbAid.addSQLNote(dbAid.getDbHelper(this), content, title, time, lastChangedTime));
        if (CalendarActivity.getRecyclerView() != null)
            CalendarActivity.getRecyclerView().scrollToPosition(0);
    }

    private void saveOriginalNote(final String title, final String content) {
        lastChangedTime = TimeAid.getNowTime();
        int pos = parentIntent.getIntExtra("pos", 0);
        dbAid.updateSQLNote(this, parentIntent.getIntExtra("id", 0), title, content, time, pos, lastChangedTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void positiveListener() {
    }

    @Override
    public void negativeListener() {
    }
}
