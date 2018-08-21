package com.wisebeartech.kidstimer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.os.CountDownTimer;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    public int counting;
    public int conMinStart;
    Button buttMulai, buttStop, buttReset;
    TextView countView;
    EditText inMenit;
    ImageView suppImage;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    private boolean isStopped = false;
    //Enable Admin Device Status
    public static final int RESULT_ENABLE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        //Set the Component Name
        compName = new ComponentName(this, MyAdmin.class);
        inMenit = (EditText) findViewById(R.id.inputMenit);
        buttMulai = (Button) findViewById(R.id.buttMulai);
        buttStop = (Button) findViewById(R.id.buttBatal);
        countView = (TextView) findViewById(R.id.textCount);
        buttReset = (Button) findViewById(R.id.buttReset);
        suppImage = (ImageView) findViewById(R.id.suppImage);

        //Check Device Admin Enabled
        boolean dactive = devicePolicyManager.isAdminActive(compName);

        //If Device Admin is Not Enabled
        if (!dactive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Why we need this permission");
            startActivityForResult(intent, RESULT_ENABLE);
        }

        //Initial State of the Buttons
        //buttStop.setEnabled(false);

        //Button Click Listener
        buttMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Disable Start Button After the first Click
                buttMulai.setEnabled(false);
                buttReset.setEnabled(false);
            //Get the value of Starting Minute
                String minStart = inMenit.getText().toString();
            //Convert String to Long to Minute
            //1000 millis = 1 Sec
                Long conMinStart = Long.parseLong(minStart)*60*1000;
                final Long numToDisp = conMinStart/1000;

            //Validation Part Goes Here

            //Countdown Function Here
                new CountDownTimer(conMinStart, 1000){
                    public void onTick(long millisUntilFinished){
                        if (isStopped) {
                            cancel();
                        }
                        else {
                            countView.setText(String.valueOf(numToDisp - counting));
                            counting++;
                        }
                    }
                    public  void onFinish(){
                        countView.setText("TimeUp");
                        devicePolicyManager.lockNow();
                        finish();
                        //buttMulai.setEnabled(true);
                        //buttReset.setEnabled(true);
                    }
                }.start();

            }
        });

        buttStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set isStopped True when the stop button clicked
                isStopped = true;
                buttMulai.setEnabled(false);
                buttReset.setEnabled(true);
                countView.setText("Aborted");

            }
        });

        buttReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        suppImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.wisebearproject.com"));
                startActivity(intent);
            }
        });


    }
}
