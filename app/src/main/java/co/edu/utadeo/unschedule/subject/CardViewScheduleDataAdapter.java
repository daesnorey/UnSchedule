package co.edu.utadeo.unschedule.subject;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Handler;

import co.edu.utadeo.unschedule.R;
import co.edu.utadeo.unschedule.db.AppDataBaseAccess;
import co.edu.utadeo.unschedule.db.subject.Schedule;
import co.edu.utadeo.unschedule.services.GeneralUtil;

public class CardViewScheduleDataAdapter extends RecyclerView.Adapter<CardViewScheduleDataAdapter.ViewHolder> {

    private CardViewScheduleDataAdapter.ScheduleAdapterListener onClickListener;
    private List<Schedule> dataSet;
    private boolean enabled;
    private Context ctx;
    private static final String DATE_FORMAT = "hh:mm";
    private Activity activity;

    private ArrayAdapter<CharSequence> globalAdapter;

    public CardViewScheduleDataAdapter(List<Schedule> dataSet,
                                       Context ctx,
                                       ScheduleAdapterListener listener,
                                       boolean enabled, FragmentActivity activity) {
        this.ctx = ctx;
        this.activity = activity;
        this.dataSet = dataSet;
        this.onClickListener = listener;
        this.enabled = enabled;
        this.globalAdapter = ArrayAdapter.createFromResource(ctx, R.array.days, android.R.layout.simple_spinner_item);
        this.globalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Called when RecyclerView needs a new {@link CardViewSubjectDataAdapter.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(CardViewScheduleDataAdapter.ViewHolder, int,)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(CardViewScheduleDataAdapter.ViewHolder, int)
     */
    @Override
    public CardViewScheduleDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_schedule, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new CardViewScheduleDataAdapter.ViewHolder(itemLayoutView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link CardViewSubjectDataAdapter.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link CardViewSubjectDataAdapter.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(CardViewScheduleDataAdapter.ViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CardViewScheduleDataAdapter.ViewHolder holder, int position) {
        Schedule currentSchedule = dataSet.get(position);

        holder.sp_days.setAdapter(this.globalAdapter);
        holder.sp_days.setSelection(currentSchedule.getDayId());
        holder.sp_days.setGravity(Gravity.CENTER);

        holder.et_schedule_item_place.setText(currentSchedule.getPlace());
        holder.setScheduleId(currentSchedule.getScheduleId());

        holder.sp_days.setEnabled(enabled);
        holder.et_schedule_item_place.setEnabled(enabled);
        holder.et_schedule_item_start.setEnabled(enabled);
        holder.et_schedule_item_end.setEnabled(enabled);

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.HOUR, currentSchedule.getStartHour());
        myCalendar.set(Calendar.MINUTE, currentSchedule.getStartMinute());
        updateLabel(myCalendar, holder.et_schedule_item_start);

        myCalendar.set(Calendar.HOUR, currentSchedule.getEndHour());
        myCalendar.set(Calendar.MINUTE, currentSchedule.getEndMinute());
        updateLabel(myCalendar, holder.et_schedule_item_end);

        View.OnClickListener onClickListener = (v -> {
            GeneralUtil.toggleKeyBoard(activity,false);
            TimePickerDialog.OnTimeSetListener time = (TimePicker view, int hour, int minute) -> {
                myCalendar.set(Calendar.HOUR, hour);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabel(myCalendar, v);
                saveDateTime(v, currentSchedule, hour, minute);
            };
            String value = ((EditText) v).getText().toString();

            Calendar calendar = Calendar.getInstance();
            try {
                Date date = sdf.parse(value);
                calendar.setTime(date);
            } catch (ParseException e) {
                Log.e("ScheduleAdapter", e.getMessage(), e);
            }
            new TimePickerDialog(this.ctx, time, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
        });

        holder.et_schedule_item_start.setOnClickListener(onClickListener);
        holder.et_schedule_item_end.setOnClickListener(onClickListener);

        if (currentSchedule.getScheduleId() <= 0) {
            save(currentSchedule);
        }

        holder.et_schedule_item_place.addTextChangedListener(textChangedListener(currentSchedule));
        /*
         * holder.et_schedule_item_place.setOnFocusChangeListener(onFocusChangeListener(currentSchedule));
         */
        holder.sp_days.setOnItemSelectedListener(onItemSelectedListener(currentSchedule));
    }

    private TextWatcher textChangedListener(Schedule schedule) {
        final long DELAY = 1000; // in ms
        final Handler handler = new Handler();
        final Runnable[] runnable = {null};
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", s.toString() + (runnable[0] != null));
                if (runnable[0] != null) {
                    Log.d("onTextChanged", "Removes callback");
                    handler.removeCallbacks(runnable[0]);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                runnable[0] = () -> {
                    Log.d("afterTextChanged r[0]", "Inside runnable: " + s.toString());
                    schedule.setPlace(s.toString());
                    save(schedule);
                };
                handler.postDelayed(runnable[0], DELAY);
            }
        };
    }

    /**
     *
     * @param schedule current
     * @return event
     */
    private AdapterView.OnItemSelectedListener onItemSelectedListener(Schedule schedule) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDayId((int) id);
                save(schedule);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                schedule.setDayId(-1);
                save(schedule);
            }
        };
    }

    /**
     *
     * @param schedule s
     * @return s
     */
    private View.OnFocusChangeListener onFocusChangeListener(Schedule schedule) {
        return (v, hasFocus) -> {
            if (hasFocus) {
                return;
            }

            String value = ((EditText) v).getText().toString();
            Log.d("onFocusChangeListener", "value: " + value);
            switch (v.getId()) {
                case R.id.et_schedule_item_place:
                    schedule.setPlace(value);
                    break;
            }

            save(schedule);
        };
    }

    /**
     *
     * @param v view to update
     * @param schedule element schedule to save
     * @param hour hour of the day
     * @param minute minute of the hour
     */
    private void saveDateTime(@NonNull View v, Schedule schedule, int hour, int minute) {
        switch (v.getId()) {
            case R.id.et_schedule_item_start:
                schedule.setStartHour(hour);
                schedule.setStartMinute(minute);
                break;
            case R.id.et_schedule_item_end:
                schedule.setEndHour(hour);
                schedule.setEndMinute(minute);
                break;
        }
        save(schedule);
    }

    protected void save(Schedule schedule) {
        AppDataBaseAccess.Executor<Long> executor = () -> {
            try {
                if (schedule.getScheduleId() == 0) {
                    return AppDataBaseAccess.getInstance(ctx).db().scheduleDao().insert(schedule);
                } else {
                    AppDataBaseAccess.getInstance(ctx).db().scheduleDao().update(schedule);
                    return (long) schedule.getScheduleId();
                }
            } catch (Exception e) {
                Log.e("saveSchedule", e.getMessage(), e);
                return (long) Double.MIN_VALUE;
            }
        };

        AppDataBaseAccess.CallBack<Long> callBack = value -> {
            if (value == Double.MIN_VALUE) {
                Toast.makeText(ctx, "Error guardando item", Toast.LENGTH_LONG).show();
            } else {
                schedule.setScheduleId(value.intValue());
            }
        };

        AppDataBaseAccess.execute(executor, callBack);
    }

    /**
     *
     * @param calendar calendar instance
     * @param v view to update
     */
    private void updateLabel(Calendar calendar, View v) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        ((EditText) v).setText(sdf.format(calendar.getTime()));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Spinner sp_days;
        public EditText et_schedule_item_place;
        public TimePicker tp_schedule_item_start;
        public TimePicker tp_schedule_item_end;
        public EditText et_schedule_item_start;
        public EditText et_schedule_item_end;

        private int scheduleId;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            this.sp_days = itemLayoutView.findViewById(R.id.sp_schedule_item_day);
            this.et_schedule_item_place = itemLayoutView.findViewById(R.id.et_schedule_item_place);
            // this.tp_schedule_item_start = itemLayoutView.findViewById(R.id.tp_schedule_item_start);
            //this.tp_schedule_item_end = itemLayoutView.findViewById(R.id.tp_schedule_item_end);

            this.et_schedule_item_start = itemLayoutView.findViewById(R.id.et_schedule_item_start);
            this.et_schedule_item_end = itemLayoutView.findViewById(R.id.et_schedule_item_end);

        }

        public int getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(int scheduleId) {
            this.scheduleId = scheduleId;
        }
    }

    // Listener

    public interface ScheduleAdapterListener {
        void buttonEditScheduleClicked(View v, int position);
    }
}
