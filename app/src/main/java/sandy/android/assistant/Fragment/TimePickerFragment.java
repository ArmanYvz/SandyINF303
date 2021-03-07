//TimePickerFragment class that creates a time picker dialog.

package sandy.android.assistant.Fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.*;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    private Integer hour;       //timepicker fragment variables
    private Integer minute;

    public TimePickerFragment() {

    }

    public TimePickerFragment(Integer hour, Integer minute) {        //timepicker fragment constructor
        this.hour = hour;
        this.minute = minute;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {     //timepicker fragment onCreate dialog

        if (hour == null || minute == null) {
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);          //set hour minute to selected time value
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));

        return timePickerDialog;
    }

}
