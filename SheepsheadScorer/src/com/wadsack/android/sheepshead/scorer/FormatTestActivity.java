package com.wadsack.android.sheepshead.scorer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;

import java.util.IllegalFormatException;


/**
 * Author: Jeremy Wadsack
 */
public class FormatTestActivity extends GuiceActivity {

    @InjectView(R.id.format)
    private EditText formatText;

    @InjectView(R.id.value)
    private EditText valueText;

    @InjectView(R.id.done)
    private Button done;

    @InjectView(R.id.result)
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.format);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String result = "";
                try {
                    for (String s : valueText.getText().toString().split(","))
                        result += String.format(formatText.getText().toString(), Integer.parseInt(s)) + "\n";
                } catch( IllegalFormatException ex ) {
                    result = ex.getLocalizedMessage();
                } catch ( NumberFormatException ex ) {
                    result = ex.getLocalizedMessage();
                }
                resultView.setText(result);
            }
        });
    }
}
