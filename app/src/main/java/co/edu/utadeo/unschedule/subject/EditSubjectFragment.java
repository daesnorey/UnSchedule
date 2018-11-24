package co.edu.utadeo.unschedule.subject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import co.edu.utadeo.unschedule.R;
import co.edu.utadeo.unschedule.db.AppDataBase;
import co.edu.utadeo.unschedule.db.AppDataBaseAccess;
import co.edu.utadeo.unschedule.db.subject.Schedule;
import co.edu.utadeo.unschedule.db.subject.Subject;
import co.edu.utadeo.unschedule.services.GeneralUtil;
import co.edu.utadeo.unschedule.services.ScheduleService;

public class EditSubjectFragment extends Fragment {

    public static final String TAG = "EditSubjectFragment";
    private static final String ARG_SUBJECT_ID = "subjectId";

    protected int subjectId;
    protected Subject currentSubject;
    protected List<Schedule> currentSchedules;
    protected boolean isNew = false;

    public EditSubjectFragment() {
        // Required empty public constructor
    }

    public static EditSubjectFragment newInstance(int subjectId) {
        Bundle args = new Bundle();
        args.putInt(ARG_SUBJECT_ID, subjectId);

        EditSubjectFragment fragment = new EditSubjectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && (subjectId = getArguments().getInt(ARG_SUBJECT_ID)) > 0) {

            AppDataBase db = AppDataBaseAccess.getInstance(getContext()).db();
            Callable<Subject> subjectTask = (() ->
                    db.subjectDao().getById(subjectId)
            );

            Callable<List<Schedule>> schedulesTask = (() ->
                    db.scheduleDao().loadAllBySubjectId(subjectId)
            );

            ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Future<Subject> subjectFuture = executor.submit(subjectTask);
            Future<List<Schedule>> schedulesFuture = executor.submit(schedulesTask);

            try {
                this.currentSubject = subjectFuture.get(1, TimeUnit.SECONDS);
                this.currentSchedules = schedulesFuture.get(1, TimeUnit.SECONDS);
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                Log.d("EdtSubFragment", e.getMessage(), e);
            }
        } else {
            currentSubject = new Subject();
            currentSchedules = new ArrayList<>();
            isNew = true;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subject_edit, container, false);
    }

    private View.OnFocusChangeListener onFocusChangeListener() {
        return (v, hasFocus) -> {
            if (!hasFocus) {
                save(v.getRootView());
            }
        };
    }

    private void save(View v) {
        EditText et_subject_name = v.findViewById(R.id.et_fragment_subject_name);
        String subject_name = et_subject_name.getText().toString();
        if (subject_name.trim().isEmpty()) {
            return;
        }

        boolean insert = subjectId == -1;
        currentSubject.setSubjectName(subject_name);

        AppDataBase db = AppDataBaseAccess.getInstance(getContext()).db();
        try {
            Callable<Long> runnable;
            if (insert) {
                currentSubject.setAcademyTermId(ScheduleService.currentAcademicTerm.getId());
                runnable = () -> db.subjectDao().insert(currentSubject);
            } else {
                runnable = () -> {
                    db.subjectDao().update(currentSubject);
                    return Long.MIN_VALUE;
                };
            }
            ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            Future<Long> future = executor.submit(runnable);
            long response = future.get(1, TimeUnit.SECONDS);

            if (response != Long.MIN_VALUE) {
                subjectId = (int) response;
                currentSubject.setSubjectId(subjectId);
                toggleButtons(false);

                Intent intent = new Intent();
                intent.putExtra("id", subjectId);
                Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(),
                        Activity.RESULT_FIRST_USER, intent);
            }

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.d("SaveSubFragment", e.getMessage(), e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (this.currentSubject != null) {
            ((EditText) view.findViewById(R.id.et_fragment_subject_name)).setText(this.currentSubject.getSubjectName());
        }

        if (this.currentSchedules != null) {
            Log.d("tag", "showSubjects");
            RecyclerView rvSubjectFragment = Objects.requireNonNull(getActivity()).findViewById(R.id.rv_fragment_subject);
            rvSubjectFragment.setHasFixedSize(true);
            LinearLayoutManager lym = new LinearLayoutManager(getContext());
            rvSubjectFragment.setLayoutManager(lym);
            RecyclerView.Adapter currentAdapter = new CardViewScheduleDataAdapter(this.currentSchedules, getContext(), null, true, getActivity());
            rvSubjectFragment.setAdapter(currentAdapter);
        }

        toggleButtons(true);
    }

    /**
     *
     */
    protected void toggleButtons(boolean setEvents) {
        Button bt_cancel = Objects.requireNonNull(getActivity()).findViewById(R.id.bt_fragment_subject_cancel);
        Button bt_save = Objects.requireNonNull(getActivity()).findViewById(R.id.bt_fragment_subject_save);
        FloatingActionButton fab_add = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_add_edit_subject_fragment);
        EditText et_subject_name = Objects.requireNonNull(getActivity()).findViewById(R.id.et_fragment_subject_name);

        if (this.subjectId >= 0) {
            bt_save.setVisibility(View.GONE);
            bt_cancel.setText(R.string.goback);
            et_subject_name.setOnFocusChangeListener(this.onFocusChangeListener());
            fab_add.setEnabled(true);
        } else {
            bt_cancel.setText(R.string.cancel);
            bt_save.setVisibility(View.VISIBLE);
            bt_save.setOnClickListener(v -> {
                currentSubject = new Subject();
                save(v.getRootView());
                GeneralUtil.toggleKeyBoard(getActivity(), false);
                et_subject_name.clearFocus();
            });
            fab_add.setEnabled(false);
            if (et_subject_name.requestFocus()) {
                GeneralUtil.toggleKeyBoard(getActivity(), true);
            }
        }

        if (setEvents) {
            bt_cancel.setOnClickListener(v -> {
                Log.d("Cancel button clicked", " Second fragment: ");

                int response = isNew ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
                Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), response, null);
            });

            fab_add.setOnClickListener(v -> {
                Log.d("Fab clicked", " Second fragment: ");
                this.currentSchedules.add(new Schedule(subjectId));
                RecyclerView rvSubjectFragment = getActivity().findViewById(R.id.rv_fragment_subject);
                rvSubjectFragment.getAdapter().notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearView();
    }

    protected void clearView() {

    }

}
