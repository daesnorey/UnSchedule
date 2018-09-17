package co.edu.utadeo.unschedule.subject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import co.edu.utadeo.unschedule.R;
import co.edu.utadeo.unschedule.db.subject.Schedule;

public class CardViewScheduleDataAdapter extends RecyclerView.Adapter<CardViewScheduleDataAdapter.ViewHolder> {

    private CardViewScheduleDataAdapter.ScheduleAdapterListener onClickListener;
    private List<Schedule> dataSet;

    private ArrayAdapter<CharSequence> globalAdapter;

    public CardViewScheduleDataAdapter(List<Schedule> dataSet, Context ctx, CardViewScheduleDataAdapter.ScheduleAdapterListener listener) {
        this.dataSet = dataSet;
        this.onClickListener = listener;
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
                R.layout.item_schedule, null);
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
        holder.sp_days.setEnabled(false);

        holder.et_schedule_item_start.setText(currentSchedule.getStartDate() + "");
        holder.et_schedule_item_end.setText(currentSchedule.getEndTime() + "");
        holder.et_schedule_item_place.setText(currentSchedule.getPlace());
        holder.setScheduleId(currentSchedule.getScheduleId());
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
        public EditText et_schedule_item_start;
        public EditText et_schedule_item_end;

        private int scheduleId;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            this.sp_days = itemLayoutView.findViewById(R.id.sp_schedule_item_day);
            this.et_schedule_item_place = itemLayoutView.findViewById(R.id.et_schedule_item_place);
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
