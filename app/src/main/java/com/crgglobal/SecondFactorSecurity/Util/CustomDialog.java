package com.crgglobal.SecondFactorSecurity.Util;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crgglobal.SecondFactorSecurity.R;


public class CustomDialog extends Dialog {
    public myOnClickListener myListener;
    String title;
    String message;
    String negativeText;
    String positiveText;

    public CustomDialog(String title, String message, String negativeText, String positiveText, Context context, myOnClickListener myclick) {
        super(context);
        this.title = title;
        this.message = message;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.myListener = myclick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(title);


        TextView messageText = (TextView) findViewById(R.id.txt);
        messageText.setText(message);

        TextView positiveButton = (TextView) findViewById(R.id.positive_button);
        positiveButton.setText(positiveText);

        RelativeLayout rlNegativeButton = (RelativeLayout) findViewById(R.id.rlNegativeButton);
      /*  TextView negativeButton = (TextView) findViewById(R.id.negative_button);
        if (negativeText.equals("")) {
            rlNegativeButton.setVisibility(View.GONE);
        }
        else{
            negativeButton.setText(negativeText);
        }*/


        positiveButton.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                myListener.onButtonClick(); // I am giving the click to the
                // interface function which we need
                // to implements where we call this
                // class
                dismiss();
            }
        });

/*        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
    }

    // This is my interface //
    public interface myOnClickListener {
        void onButtonClick();
    }

}
