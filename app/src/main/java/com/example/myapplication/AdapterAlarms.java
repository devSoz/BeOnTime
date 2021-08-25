package com.example.myapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.Database.DatabaseInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class AdapterAlarms extends BaseAdapter {

    Context context;
    List<EventData> alarmList = new ArrayList<>();
    TextView tvName, tvDate, tvTime;
    Switch swNotify;
    Button btnCancel;
    ListView listView;
    private CreateDatabase createDb;
    private DatabaseInterface dao;


    public AdapterAlarms(List<EventData> alarmList, Context context) {
        this.context = context;
        this.alarmList = alarmList;
        //getEventData();
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int i) {
        return alarmList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return alarmList.get(i).getId();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        //String.format("%02d:%02d"
        final View V = LayoutInflater.from(context).inflate(R.layout.alarm_list, viewGroup, false);
        ((TextView) V.findViewById(R.id.tvAName)).setText(alarmList.get(i).getEventName());
        long milliseconds = alarmList.get(i).getTime();
        SimpleDateFormat simple = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");

        Date result = new Date(milliseconds);


        ((TextView) V.findViewById(R.id.tvADate)).setText(String.valueOf( simple.format(result)));
       // ((TextView) V.findViewById(R.id.tvATime)).setText(String.valueOf( alarmList.get(i).getTime()));
       /* if (alarmList.get(i).getNotify())
            ((Switch) V.findViewById(R.id.swNotify)).setChecked(true);
        else
            ((Switch) V.findViewById(R.id.swNotify)).setChecked(false);*/
        btnCancel = (Button) V.findViewById(R.id.btnCancel);
        //listView = view.findViewById(R.id.listview);
        btnCancel.setTag(i);
        (btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //View parentRow = ((View) v.getParent());
                //ListView l = (ListView) parentRow.getParent();
                //Integer pos = l.getPositionForView(parentRow);
                int pos = (int) v.getTag();
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to cancel the alarm?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Integer uniqueId = alarmList.get(pos).getId();
                                deleteAlarm(uniqueId, pos);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        return V;
    }

    public void deleteAlarm(Integer uniqueId, int position)
    {
        createDb = CreateDatabase.getInstance(context);
        dao = createDb.Dao();
        dao.deleteById(uniqueId);
        alarmList.remove(position);
        Intent intent = new Intent(context, AlarmRingClass.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, uniqueId, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pi);
        PendingIntent pi2 = PendingIntent.getBroadcast(context, uniqueId+10000, intent, 0);
        am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pi2);
        notifyDataSetChanged();
        //AdapterAlarms.notifyDataSetChanged();
    }

}
