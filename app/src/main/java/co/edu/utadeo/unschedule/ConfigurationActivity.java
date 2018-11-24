package co.edu.utadeo.unschedule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.persistence.room.OnConflictStrategy;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.utadeo.unschedule.db.AppDataBaseAccess;
import co.edu.utadeo.unschedule.db.subject.AcademicTerm;
import co.edu.utadeo.unschedule.services.GeneralUtil;

public class ConfigurationActivity extends Activity {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private ArrayList<View> focusables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Button saveButton = findViewById(R.id.bt_save_activity_conf);
        saveButton.setOnClickListener(v -> {
            if (allFieldsFilled()) {
                saveAcademicTerm(v);
            }
        });

        Calendar myCalendar = Calendar.getInstance();
        View.OnClickListener onClickListener = v -> {
            datesListener(v, myCalendar);
            GeneralUtil.toggleKeyBoard(this, false);
        };

        EditText et_start_date = findViewById(R.id.et_start_date_activity_conf);
        et_start_date.setOnClickListener(onClickListener);
        et_start_date.setInputType(InputType.TYPE_NULL);
        EditText et_end_date = findViewById(R.id.et_end_date_activity_conf);
        et_end_date.setOnClickListener(onClickListener);
        et_end_date.setInputType(InputType.TYPE_NULL);

        EditText et_name = findViewById(R.id.et_name_activity_conf);

        if (et_name.requestFocus()) {
            GeneralUtil.toggleKeyBoard(this, true);
        }

        focusables.add(et_start_date);
        focusables.add(et_end_date);
        focusables.add(et_name);
    }

    private void datesListener(View v, Calendar myCalendar) {
        DatePickerDialog.OnDateSetListener date = (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(myCalendar, v);
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * @param v current view
     */
    private void saveAcademicTerm(View v) {
        v.setEnabled(false);
        AcademicTerm academicTerm = new AcademicTerm();
        academicTerm.setName(getValueString(R.id.et_name_activity_conf, true));
        academicTerm.setStartDate(getValueDateLong(R.id.et_start_date_activity_conf));
        academicTerm.setEndDate(getValueDateLong(R.id.et_end_date_activity_conf));

        AppDataBaseAccess.execute(() -> {
            try {
                int next = AppDataBaseAccess.getInstance(this).db().academicTermDao().next();

                academicTerm.setId(next);
                long result = AppDataBaseAccess.getInstance(this).db().academicTermDao().insert(academicTerm);
                if (result == OnConflictStrategy.ABORT) {
                    Log.e("ConfActivity", "Insertion aborted");
                    return false;
                }
            } catch (Exception e) {
                Log.e("ConfActivity", e.getMessage(), e);
                return e.getMessage();
            }

            return true;
        }, value -> {
            if (value instanceof Boolean) {
                setResult(RESULT_OK);
                finish();
            } else if (value instanceof String) {
                Toast.makeText(this, "Error interno " + value, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error desconocido", Toast.LENGTH_SHORT).show();
            }
            v.setEnabled(true);
        });
    }

    private void updateLabel(Calendar calendar, View v) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        ((EditText) v).setText(sdf.format(calendar.getTime()));
    }

    /**
     * @return returns boolean value
     */
    private boolean allFieldsFilled() {
        List<View> views = focusables; // v.getRootView().getFocusables(View.FOCUSABLES_ALL);
        boolean isValid = true;

        for (View view : views) {
            if (view instanceof EditText) {
                EditText et = ((EditText) view);
                Editable ed = et.getText();
                if (ed == null || ed.toString().isEmpty()) {
                    Drawable customErrorDrawable = getResources().getDrawable(android.R.drawable.ic_dialog_alert);
                    customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());
                    et.setError("Campo obligatorio", customErrorDrawable);
                    et.requestFocus();
                    isValid = false;
                } else {
                    et.setError(null);
                }
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor llene los malditos datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        String start_id = getResources().getString(R.string.term_start_date);
        String end_id = getResources().getString(R.string.term_end_date);
        long current_date = new Date().getTime();
        long start_value = getValueDateLong(R.id.et_start_date_activity_conf);
        long end_value = getValueDateLong(R.id.et_end_date_activity_conf);

        String msg = null;
        if (start_value > end_value) {
            msg = String.format("El valor de %s no puede ser mayor al de %s", end_id, start_id);
        } else if (start_value > current_date) {
            msg = String.format("El valor de %s no puede ser mayor a la fecha actual", start_id);
        } else if (end_value < current_date) {
            msg = String.format("El valor de %s no puede ser menor a la fecha actual", end_id);
        }

        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * @param id resource
     * @return string value from view
     */
    private String getValueString(@IdRes int id, boolean capitalize) {
        String value = getValueString(id);
        if (!capitalize) {
            return value;
        }

        String[] words = value.split("\\s");
        StringBuffer sb = new StringBuffer();
        for (String word : words) {
            sb.append(word.length() < 2 ? word : word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * @param id resource
     * @return string value from view
     */
    private String getValueString(@IdRes int id) {
        return ((EditText) findViewById(id)).getText().toString();
    }

    /**
     * @param id resource
     * @return {@link java.util.Date} as timestamp
     */
    private long getValueDateLong(@IdRes int id) {
        String value = getValueString(id, false);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            Date date = sdf.parse(value);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("ConfActivity", e.getMessage(), e);
        }

        return -1;
    }
}
