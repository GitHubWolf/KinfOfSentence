package com.wolfinmotion.kingofsentence;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;


/**
 * A simple Fragment subclass.
 *
 */
public class OptionFragment extends Fragment implements View.OnClickListener {

    private Bundle mBundleConfig = new Bundle();
    public OptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);

        //onStart will be called whenever the fragment is shown to the user. We will get the latest config/sentence at this point.

        KingOfSentenceApplication app = (KingOfSentenceApplication)this.getActivity().getApplication();
        DBManager dbManager = app.getDBManager();

        //Retrieve config.
        dbManager.getConfig(mBundleConfig);

        //Show it in the UI.
        boolean showChinese = mBundleConfig.getBoolean("ShowChinese");
        RadioButton radioButtonShowChinese = (RadioButton)view.findViewById(R.id.radioButtonShowChinese);
        RadioButton radioButtonShowEnglish = (RadioButton)view.findViewById(R.id.radioButtonShowEnglish);
        radioButtonShowChinese.setChecked(showChinese);
        radioButtonShowEnglish.setChecked(!showChinese);

        radioButtonShowChinese.setOnClickListener(this);
        radioButtonShowEnglish.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()){
            case R.id.radioButtonShowChinese:{
                bundle.putBoolean("ShowChinese",true);
                break;
            }
            case R.id.radioButtonShowEnglish:{
                bundle.putBoolean("ShowChinese",false);
                break;
            }
        }

        KingOfSentenceApplication app = (KingOfSentenceApplication)this.getActivity().getApplication();
        DBManager dbManager = app.getDBManager();
        dbManager.setConfig(bundle);
    }
}
