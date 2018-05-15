package com.ws.notes;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.ws.notes.utils.DatabaseHelper;
import com.ws.notes.utils.PreferenceManager;
import com.ws.notes.utils.RecyclerViewClickListener;
import com.ws.notes.utils.TimeAid;
import com.ws.notes.utils.Utils;
import com.ws.notes.utils.WrapContentLinearLayoutManager;
import com.ws.notes.utils.dbAid;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * 主要Activity
 */

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    public static SwipeMenuRecyclerView recyclerView;
    private DatabaseHelper dbHelper=dbAid.getDbHelper(this);
    private List<Note> noteList = new ArrayList<>();
    private static NoteAdapter noteAdapter;
    private PreferenceManager preferences;
    private static LinearLayout emptyView;
    private static TextView emptyTV;
    private static Context context;
    private FloatingActionButton mFloatingActionButton;
    private CalendarLayout calendarLayout;
    static boolean isDebug = false;
    public android.app.ActionBar actionBar;
    private static boolean isExit = false;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    @SuppressLint("HandlerLeak")
    private static final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        context = this;
        initComponent();
        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void initComponent() {
        /*组件初始化*/
        actionBar = getActionBar();
        preferences = new PreferenceManager(this.getApplicationContext());
        emptyView = findViewById(R.id.empty_view);
        emptyTV = findViewById(R.id.empty_view_text);
        emptyTV.setTypeface(Utils.getFontAwesome(getApplicationContext()));
        mFloatingActionButton=findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this,EditActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("content", "");
                long timeStamp = TimeAid.getNowTime();
                intent.putExtra("time", TimeAid.stampToDate(timeStamp));
                intent.putExtra("timeLong", timeStamp);
                intent.putExtra("isNew", true);
                intent.putExtra("lastChangedTime", timeStamp);
                startActivity(intent);
            }
        });
        calendarLayout=findViewById(R.id.calendarLayout);
        calendarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarLayout.isExpand())
                    calendarLayout.shrink();
                else calendarLayout.expand();
            }
        });
        CalendarView calendarView=findViewById(R.id.calendarView);
        calendarView.setOnDateLongClickListener(new CalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClick(final Calendar calendar) {
                Toast.makeText(CalendarActivity.this,calendar.getYear()+"yue"+calendar.getMonth(),Toast.LENGTH_LONG).show();
                java.util.Calendar calendar1= java.util.Calendar.getInstance();
                final int[] HourOfDay = new int[]{calendar1.get(java.util.Calendar.HOUR_OF_DAY)};
                final int[] Minute = new int[]{calendar1.get(java.util.Calendar.MINUTE)};
                TimePickerDialog timePickerDialog=new TimePickerDialog(CalendarActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        HourOfDay[0] =hourOfDay;
                        Minute[0] =minute;
                    }
                }, HourOfDay[0],Minute[0],true);
                timePickerDialog.setCancelable(true);
                timePickerDialog.setTitle(R.string.edit_time_time);
                DialogInterface.OnClickListener onClickListener= new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(CalendarActivity.this,EditActivity.class);
                                intent.putExtra("title", "");
                                intent.putExtra("content", "");
                                long timeStamp =TimeAid.getTimeStamp(calendar.getYear(),calendar.getMonth()-1,calendar.getDay(),HourOfDay[0],Minute[0]);
                                intent.putExtra("time", TimeAid.stampToDate(timeStamp));
                                intent.putExtra("timeLong", timeStamp);
                                intent.putExtra("isNew", true);
                                intent.putExtra("lastChangedTime", timeStamp);
                                startActivity(intent);
                            case DialogInterface.BUTTON_NEGATIVE:
                            default:
                                dialog.cancel();
                                dialog.dismiss();
                        }
                    }
                };
                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.mButton_yes),onClickListener);
                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.mButton_no),onClickListener);
                timePickerDialog.show();
            }
        });
        calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(final Calendar calendar, boolean isClick) {
                if(isClick) {
                    if(!noteList.isEmpty())noteList.clear();
                    Log.i(TAG, "onDateSelected: year"+calendar.getYear()+"Month:"+calendar.getMonth()+"day:"+calendar.getDay());
                    noteList=dbAid.querySQLNotes(dbAid.getDbHelper(CalendarActivity.this),calendar.getYear(),calendar.getMonth()-1,calendar.getDay());
                    Log.d(TAG, "onDateSelected: "+noteList.size()+"date:");
                    noteAdapter.removeNoteList();
                    noteAdapter.setNotes(noteList);
                    noteAdapter.notifyDataSetChanged();
                    noteAdapter.refreshAllData();
                }
            }
        });
        mWaveSwipeRefreshLayout = findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                if(noteAdapter.getItemCount()!=0)recyclerView.scrollToPosition(0);
                if (mFloatingActionButton != null  && mFloatingActionButton.getVisibility() == View.GONE) {
                    Animator animator =  ObjectAnimator.ofFloat(mFloatingActionButton,"translationY",100f,0f);
                    animator.setDuration(500);
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                    animator.start();
                }
                new Task().execute();
            }
        });
        /*判断是否是debug模式*/
        isDebug = preferences.getDebug();
        Log.d(TAG, "onCreate: isDebug " + isDebug);
        if (isDebug) {
            Toast.makeText(this, "isDebug:" + isDebug + "\n当前版本名称:" + Utils.getVersionName(this) +
                    "\n当前版本号" + Utils.getVersionCode(this), Toast.LENGTH_SHORT).show();
            setTitle(getResources().getString(R.string.app_name) + "[Debug模式]");
            Log.d(TAG, "onCreate: DatabaseDir: " + getDatabasePath("Note.db").getAbsolutePath());
        }
    }

    /**
     * 初始画RecyclerView
     */
    private void initRecyclerView() {

        /*sql数据库初始化*/
        if(!noteList.isEmpty())noteList.clear();
        noteList=dbAid.initNotes(dbHelper);
        /*RecyclerView初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        recyclerView.setLongPressDragEnabled(true); // 拖拽排序，默认关闭。
        recyclerView.setItemViewSwipeEnabled(true); // 侧划删除，默认关闭。
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);//设置Note集合
        /*设置RecyclerView内容字体大小*/
        NoteAdapter.setTitleFontSize(preferences.getFontTitleSize());
        NoteAdapter.setTimeFontSize(preferences.getFontTimeSize());
        NoteAdapter.setContentFontSize(preferences.getFontContextSize());
        recyclerView.setOnItemMoveListener(mItemMoveListener);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isFabAnimg;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ){
                    if (!isFabAnimg && mFloatingActionButton != null && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                        Animator animator = ObjectAnimator.ofFloat(mFloatingActionButton, "translationY", 0f, 100f);
                        animator.setDuration(500);
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                isFabAnimg = true;
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isFabAnimg = false;
                                mFloatingActionButton.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                isFabAnimg = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.start();
                    }
                }else{
                    if (mFloatingActionButton != null && !isFabAnimg && mFloatingActionButton.getVisibility() == View.GONE) {
                        Animator animator =  ObjectAnimator.ofFloat(mFloatingActionButton,"translationY",100f,0f);
                        animator.setDuration(500);
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                isFabAnimg = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isFabAnimg = false;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                isFabAnimg = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        mFloatingActionButton.setVisibility(View.VISIBLE);
                        animator.start();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(this, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Note note = NoteAdapter.getNotes().get(position);
                    Log.d(TAG, "onClick: Content:" + note.getContent() + "\nTitle:" +
                            note.getTitle() + "\nTime:" + note.getLogTime() + "\n" +
                            "\nTimeLong:" + note.getTime() + "\n"+"Pos:" + position);
                    Intent intent = new Intent(CalendarActivity.this,EditActivity.class);
                    intent.putExtra("id",note.getId());
                    intent.putExtra("pos", position);
                    intent.putExtra("title", note.getTitle());
                    intent.putExtra("content", note.getContent());
                    intent.putExtra("time", TimeAid.stampToDate(note.getTime()));
                    intent.putExtra("timeLong", TimeAid.stampToDate(note.getTime()));
                    intent.putExtra("lastChangedTime", note.getLastChangedTime());
                    view.getContext().startActivity(intent);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(CalendarActivity.this, "这个便笺好像并不存在哦~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final NormalDialog normalDialog=new NormalDialog(CalendarActivity.this);
                normalDialog.style(NormalDialog.STYLE_TWO);
                normalDialog.title(getString(R.string.Notes_delete_tip));
                normalDialog.content(getString(R.string.Notes_delete));
                normalDialog.setOnBtnClickL(new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        normalDialog.cancel();
                        normalDialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        long time = NoteAdapter.getNotes().get(position).getTime();
                        dbAid.deleteSQLNote(CalendarActivity.this,time);
                        noteAdapter.removeData(position);
                        normalDialog.cancel();
                        normalDialog.dismiss();
                    }
                });
                normalDialog.show();
            }
        }));
        checkEmpty();
    }
    @SuppressLint("StaticFieldLeak")
    private class Task extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }

        @Override protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(CalendarActivity.this,R.string.Refresh,Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }
    }

    public static void checkEmpty(){
        if (noteAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:break;
            default:super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        if (isDebug) {
            setTitle(getResources().getString(R.string.app_name) + "[Debug模式]");
        } else {
            setTitle(getResources().getString(R.string.app_name));
        }
//        noteAdapter.notifyDataSetChanged();
        checkEmpty();
//        noteAdapter.refreshAllDataForce();
        for (Note note : NoteAdapter.getNotes()) {
            Log.d(TAG, "onResume: note " + note.getTitle());
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(CalendarActivity.this,MainActivity.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }


    /**
     * @return NoteAdapter
     */
    public static NoteAdapter getNoteAdapter() {
        return noteAdapter;
    }

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public static boolean getIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        CalendarActivity.isDebug = isDebug;
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = 400;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(CalendarActivity.this)
//                    .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
                    .setImage(R.drawable.ic_launcher_foreground) // 图标。
                    .setWidth(width) // 宽度。
                    .setHeight(height); // 高度。
            swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(CalendarActivity.this)
                    .setText("删除") // 文字。
                    .setBackgroundColor(Color.RED)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.

            // 上面的菜单哪边不要菜单就不要添加。
        }
    };

    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();
            if(fromPosition>=0&&toPosition>=0&&fromPosition<noteList.size()&&toPosition<noteList.size()) {
                Collections.swap(noteList, fromPosition, toPosition);
                noteAdapter.notifyItemMoved(fromPosition, toPosition);
            }
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int adapterPosition = srcHolder.getAdapterPosition();
            // Item被侧滑删除时，删除数据，并更新adapter。
            long time = NoteAdapter.getNotes().get(adapterPosition).getTime();
            dbAid.deleteSQLNote(CalendarActivity.this,time);
            Toast.makeText(CalendarActivity.this, "你删除了一条便笺，你可以在回收站中彻底删除或恢复", Toast.LENGTH_SHORT).show();
            noteAdapter.removeData(adapterPosition);
            Log.d(TAG, "onItemDismiss: pos : " + adapterPosition);
            checkEmpty();
        }
    };

    SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            Toast.makeText(CalendarActivity.this, "删除POS" + adapterPosition, Toast.LENGTH_SHORT).show();
            long time = NoteAdapter.getNotes().get(adapterPosition).getTime();
            dbAid.deleteSQLNote(CalendarActivity.this,time);
            noteAdapter.removeData(adapterPosition);
        }
    };
}
