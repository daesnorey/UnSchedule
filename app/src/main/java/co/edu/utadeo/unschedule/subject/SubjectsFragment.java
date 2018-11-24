package co.edu.utadeo.unschedule.subject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.edu.utadeo.unschedule.MainActivity;
import co.edu.utadeo.unschedule.R;
import co.edu.utadeo.unschedule.db.AppDataBase;
import co.edu.utadeo.unschedule.db.AppDataBaseAccess;
import co.edu.utadeo.unschedule.db.subject.Schedule;
import co.edu.utadeo.unschedule.db.subject.Subject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubjectsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubjectsFragment extends Fragment {

    public static final String TAG = "SubjectsFragment";

    private List<Subject> subjects;
    private boolean DEBUG = false;

    private OnFragmentInteractionListener mListener;

    private MainActivity.MainActivityListener mal;

    public SubjectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SubjectsFragment.
     */
    public static SubjectsFragment newInstance(MainActivity.MainActivityListener mal) {
        SubjectsFragment subjectsFragment = new SubjectsFragment();
        subjectsFragment.mal = mal;
        return subjectsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subject, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getSubjects();
        showSubjects();

        Objects.requireNonNull(getActivity())
                .findViewById(R.id.fab_add_subject)
                .setOnClickListener(v -> openEditFragment(-1));
    }

    private void getSubjects() {
        Callable<List<Subject>> task = () -> {
            AppDataBase db = AppDataBaseAccess.getInstance(getContext()).db();
            List<Subject> subjects = db.subjectDao().getAll();

            if (subjects.isEmpty() && DEBUG) {
                // sets dummy data
                subjects = getsDummyData(db);
            }

            return subjects;
        };

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<List<Subject>> future = executor.submit(task);

        Log.d("mainTag", "future done? " + future.isDone());

        try {
            this.subjects = future.get();
        } catch (InterruptedException | ExecutionException e) {
            this.subjects = null;
            Log.d("Error interrupted", e.getMessage(), e);
        }
    }

    /**
     *
     * @param id _
     * @return _
     */
    private Subject getSubject(final int id) {
        final Subject[] tmp = {null};
        AppDataBaseAccess.Executor<Subject> executor = (() ->
            AppDataBaseAccess.getInstance(getContext()).db().subjectDao().getById(id)
        );

        AppDataBaseAccess.execute(executor, subject -> tmp[0] = subject);

        return tmp[0];
    }

    private List<Subject> getsDummyData(AppDataBase db) {
        Log.d("task", "Empty subject");
        Subject subject_c = new Subject();
        subject_c.setSubjectName("Calculus");
        subject_c.setSubjectId(1);

        Schedule schedule_1 = new Schedule();
        schedule_1.setScheduleId(1);
        schedule_1.setSubjectId(1);
        schedule_1.setDayId(1);
        schedule_1.setPlace("M 26 505");
        schedule_1.setStartHour(18);
        schedule_1.setStartMinute(30);
        schedule_1.setEndHour(20);
        schedule_1.setEndMinute(30);

        Schedule schedule_2 = new Schedule();
        schedule_2.setScheduleId(2);
        schedule_2.setSubjectId(1);
        schedule_2.setDayId(1);
        schedule_2.setPlace("M 22 501");
        schedule_2.setStartHour(18);
        schedule_2.setStartMinute(30);
        schedule_2.setEndHour(20);
        schedule_2.setEndMinute(30);

        Subject subject_d = new Subject();
        subject_d.setSubjectName("Math");
        subject_d.setSubjectId(2);

        Subject subject_f = new Subject();
        subject_f.setSubjectName("Chemistry");
        subject_f.setSubjectId(3);

        db.subjectDao().insertAll(subject_c, subject_d, subject_f);
        db.scheduleDao().insertAll(schedule_1, schedule_2);
        return db.subjectDao().getAll();
    }

    private void showSubjects() {
        Log.d("tag", "showSubjects");
        RecyclerView rvMainFragment = Objects.requireNonNull(getActivity()).findViewById(R.id.rv_fragment_main);
        rvMainFragment.setHasFixedSize(true);

        LinearLayoutManager lym = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMainFragment.setLayoutManager(lym);

        CardViewSubjectDataAdapter.SubjectAdapterListener listener = ((v, position) -> {
            Log.d("listener", "clicked " + position);
            int subjectId = subjects.get(position).getSubjectId();
            openEditFragment(subjectId);
            rvMainFragment.setVisibility(View.GONE);
            if (mal != null) {
                mal.onFragmentChanged(v);
            }
        });

        RecyclerView.Adapter currentAdapter = new CardViewSubjectDataAdapter(this.subjects, listener);
        rvMainFragment.setAdapter(currentAdapter);
    }

    private void openEditFragment(int subjectId) {
        EditSubjectFragment secondFragment = EditSubjectFragment.newInstance(subjectId);
        secondFragment.setTargetFragment(this, 1);
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, secondFragment, EditSubjectFragment.TAG)
                .commit();
        getActivity().findViewById(R.id.fab_add_subject).setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", " Mainfragment: " + requestCode);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                this.getSubjects();
            } else if (resultCode == Activity.RESULT_FIRST_USER) {
                int id = data.getIntExtra("id", -1);
                if (id >= 0) {
                    Subject tmp = this.getSubject(id);
                    if (tmp != null) {
                        this.subjects.add(tmp);
                        RecyclerView rvMainFragment = Objects.requireNonNull(getActivity()).findViewById(R.id.rv_fragment_main);
                        rvMainFragment.getAdapter().notify();
                    }
                }
            }
            Objects.requireNonNull(getFragmentManager())
                    .beginTransaction()
                    .remove(getFragmentManager().findFragmentByTag(EditSubjectFragment.TAG))
                    .commit();
            RecyclerView rvMainFragment = Objects.requireNonNull(getActivity()).findViewById(R.id.rv_fragment_main);
            rvMainFragment.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.fab_add_subject).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
