//Activity Class to handle language selection operations

package sandy.android.assistant.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;

import com.akexorcist.localizationactivity.core.LanguageSetting;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;

import sandy.android.assistant.R;

public class ChangeLanguageActivity extends LocalizationActivity {
    Button button_en;
    Button button_tr;
    Button button_save;
    Button button_cancel;

    Button selectedLang;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        button_en = findViewById(R.id.button_en);       //initialize view attributes
        button_tr = findViewById(R.id.button_tr);
        button_save = findViewById(R.id.button_save_lang);
        button_cancel = findViewById(R.id.button_cancel_lang);

        button_en.setOnClickListener(l -> {                //if english button is selected, change the selection color around button and set selectedLang to english
            button_en.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            button_en.setTextColor(getResources().getColor(R.color.white));

            button_tr.setBackgroundColor(getResources().getColor(R.color.white));
            button_tr.setTextColor(getResources().getColor(R.color.black));

            selectedLang = button_en;
        });

        button_tr.setOnClickListener(l -> {                   //if turkish button is selected, change the selection color around button and set selectedLang to turkish
            button_tr.setBackgroundColor(getResources().getColor(R.color.darkBlue));
            button_tr.setTextColor(getResources().getColor(R.color.white));

            button_en.setBackgroundColor(getResources().getColor(R.color.white));
            button_en.setTextColor(getResources().getColor(R.color.black));

            selectedLang = button_tr;
        });

        button_save.setOnClickListener(l -> {         //onClick listener for change language save button
            if (selectedLang == null) {
                setResult(RESULT_CANCELED);         //finish activity and return cancel if no language is selected
                finish();
                return;
            } else if (selectedLang.equals(button_en)) {
                setLanguage("en");                          //if english button is selected, change App language to English
            } else if (selectedLang.equals(button_tr)) {        //if turkish button is selected, change App language to Turkish
                setLanguage("tr");
            }
            setResult(RESULT_OK);                    //return ok to MainActivity.java
            finish();
        });

        button_cancel.setOnClickListener(l -> {           //onClick listener for cancel button
            setResult(RESULT_CANCELED);             //finish activity and return cancel if cancel button is clicked.
            finish();
        });
    }
}
