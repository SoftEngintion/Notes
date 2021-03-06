package com.ws.notes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ws.notes.ui.TimeAndDatePickerDialog;
import com.ws.notes.utils.TimeAid;

import java.lang.reflect.Method;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TimeAndDatePickerDialog.TimePickerDialogInterface {
    private static boolean isExit = false;
    private TimeAndDatePickerDialog dialog;
    @SuppressLint("HandlerLeak")
    private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_plan_black_24dp);
        drawable.setBounds(0, 0, 100, 100);
        Button mButton_plan = findViewById(R.id.mButton_plan);
        mButton_plan.setCompoundDrawables(null, drawable, null, null);
        mButton_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Calendar calendar= Calendar.getInstance();
//                final int[] HourOfDay = new int[]{calendar.get(java.util.Calendar.HOUR_OF_DAY)};
//                final int[] Minute = new int[]{calendar.get(java.util.Calendar.MINUTE)};
//                TimePickerDialog timePickerDialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        HourOfDay[0] =hourOfDay;
//                        Minute[0] =minute;
//                    }
//                }, HourOfDay[0],Minute[0],true);
//                timePickerDialog.setCancelable(true);
//                timePickerDialog.setTitle(R.string.edit_time_time);
//                DialogInterface.OnClickListener onClickListener= new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                Intent intent = new Intent(MainActivity.this,EditActivity.class);
//                                intent.putExtra("title", "");
//                                intent.putExtra("content", "");
//                                long timeStamp =TimeAid.getTimeStamp(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),HourOfDay[0],Minute[0]);
//                                intent.putExtra("time", TimeAid.stampToDate(timeStamp));
//                                intent.putExtra("timeLong", timeStamp);
//                                intent.putExtra("isNew", true);
//                                intent.putExtra("lastChangedTime", timeStamp);
//                                startActivity(intent);
//                            case DialogInterface.BUTTON_NEGATIVE:
//                            default:
//                                dialog.cancel();
//                                dialog.dismiss();
//                        }
//                    }
//                };
//                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.mButton_yes),onClickListener);
//                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.mButton_no),onClickListener);
//                timePickerDialog.show();
                dialog = new TimeAndDatePickerDialog(MainActivity.this);
                dialog.showDateAndTimePickerDialog();
            }
        });
        Button mButton_calendar = findViewById(R.id.mButton_calendar);
        drawable = getResources().getDrawable(R.drawable.ic_calendar);
        drawable.setBounds(0, 0, 64, 64);
        mButton_calendar.setCompoundDrawables(null, drawable, null, null);
        mButton_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarActivity.class));
            }
        });
        Button mButton_notes = findViewById(R.id.mButton_notes);
        drawable = getResources().getDrawable(R.drawable.ic_note);
        drawable.setBounds(0, 0, 90, 90);
        mButton_notes.setCompoundDrawables(null, drawable, null, null);
        mButton_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("content", "");
                long timeStamp = TimeAid.getNowTime();
                intent.putExtra("dstStr",TimeAid.stampToDate(timeStamp));
                intent.putExtra("time", TimeAid.stampToDate(timeStamp));
                intent.putExtra("timeLong", timeStamp);
                intent.putExtra("isNew", true);
                intent.putExtra("lastChangedTime", timeStamp);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (CalendarActivity.getIsDebug()) {
            menu.setGroupVisible(R.id.main_menu_debug, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 通过反射使图标与文字同时显示
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    @SuppressLint("PrivateApi") Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(this, R.string.NoExitTip, Toast.LENGTH_SHORT).show();
                // 利用handler延迟发送更改状态信息
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                Toast.makeText(this, R.string.ExitTip, Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.recycle_bin:
                startActivity(new Intent(MainActivity.this, RecycleBinActivity.class));
                return true;
            case R.id.main_menu_setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.main_menu_exit:
                finish();
                return true;
            case R.id.main_menu_about:
                /*启动关于应用*/
                startActivity(new Intent(MainActivity.this, AppAboutActivity.class));
                return true;
            case R.id.main_menu_help:
                startActivity(new Intent(this,HelpActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void positiveListener() {
        String dstStr = String.format(Locale.CHINA, "%d-%d-%d %d:%d:00", dialog.getYear(), dialog.getMonth(), dialog.getDay(), dialog.getHour(), dialog.getMinute());
        long timeStamp = TimeAid.dateToStamp(dstStr);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("title", "");
        intent.putExtra("content", "");
        intent.putExtra("time", TimeAid.stampToDate(timeStamp));
        intent.putExtra("timeLong", timeStamp);
        intent.putExtra("isNew", true);
        intent.putExtra("lastChangedTime", timeStamp);
        intent.putExtra("dstStr", dstStr);
        startActivity(intent);
    }

    @Override
    public void negativeListener() {
    }
}
