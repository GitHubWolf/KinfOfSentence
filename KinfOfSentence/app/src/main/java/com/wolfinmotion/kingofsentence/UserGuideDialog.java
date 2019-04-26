package com.wolfinmotion.kingofsentence;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class UserGuideDialog extends Dialog{

    public UserGuideDialog(Context context) {
        super(context, R.style.TransparentDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_guide_cn);

        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutUserGuide);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
