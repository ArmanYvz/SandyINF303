//DatePickerFragment class that creates a Date Picker dialog.

package sandy.android.assistant.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

    private Integer year;       //datepicker fragment variables
    private Integer month;
    private Integer day;

    public DatePickerFragment() {

    }

    public DatePickerFragment(Integer year, Integer month, Integer day) {        //datepicker fragment constructor
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {         //onCreateDialog action

        if (year == null || month == null || day == null) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);         //set year month day values to selected date
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);     //disables past date selections from date picker

        return datePickerDialog;
    }
}
