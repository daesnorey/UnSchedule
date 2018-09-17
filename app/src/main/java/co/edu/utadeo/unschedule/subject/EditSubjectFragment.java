package co.edu.utadeo.unschedule.subject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
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

public class EditSubjectFragment extends Fragment {

    public static final String TAG = "EditSubjectFragment";
    private static final String ARG_SUBJECT_ID = "subjectId";

    protected int subjectId;
    protected Subject currentSubject;
    protected List<Schedule> currentSchedules;

    public EditSubjectFragment() {
        // Required empty public constructor
    }

    public static EditSubjectFragment newInstance(@NonNull int subjectId) {
        Bundle args = new Bundle();
        args.putInt(ARG_SUBJECT_ID, subjectId);

        EditSubjectFragment fragment = new EditSubjectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subjectId = getArguments().getInt(ARG_SUBJECT_ID);

            AppDataBase db = AppDataBaseAccess.getInstance(getContext()).db();
            Callable<Subject> subjectTask = () -> {
                Subject subject = db.subjectDao().getById(subjectId);
                return subject;
            };

            Callable<List<Schedule>> schedulesTask = () -> {
                List<Schedule> schedules = db.scheduleDao().loadAllBySubjectId(subjectId);
                return schedules;
            };

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
            this.currentSubject = new Subject();
            this.currentSchedules = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_subject_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (this.currentSubject != null) {
            ((EditText) view.findViewById(R.id.et_fragment_subject_name)).setText(this.currentSubject.getSubjectName());
        }

        if (this.currentSchedules != null) {
            Log.d("tag", "showSubjects");
            RecyclerView rvSubjectFragment = getActivity().findViewById(R.id.rv_fragment_subject);
            rvSubjectFragment.setHasFixedSize(true);

            LinearLayoutManager lym = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvSubjectFragment.setLayoutManager(lym);

            RecyclerView.Adapter currentAdapter = new CardViewScheduleDataAdapter(this.currentSchedules, getContext(),null);
            rvSubjectFragment.setAdapter(currentAdapter);
        }

        /*
        view.findViewById(R.id.bt_secod).setOnClickListener(v -> {
            Log.d("Click", " Secondfragment: ");
            //getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK, null);
            getTargetFragment().getActivity().setResult(Activity.RESULT_OK);
        });
        */
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
