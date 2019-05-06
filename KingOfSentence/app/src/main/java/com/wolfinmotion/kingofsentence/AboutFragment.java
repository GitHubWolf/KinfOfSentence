package com.wolfinmotion.kingofsentence;



import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

//import org.apache.http.util.EncodingUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * A simple Fragment subclass.
 *
 */
public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        getHelpText(view);
        return view;
    }

    private void getHelpText(View view){
        TextView textViewAbout = (TextView)view.findViewById(R.id.textViewAbout);
        textViewAbout.setMovementMethod(ScrollingMovementMethod.getInstance());

        //Read the help text from assets.
        byte[] helpText = new byte[1024];
        try{

            InputStream inputStream = this.getActivity().getResources().getAssets().open("help.txt");
            inputStream.read(helpText);
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        //Show help now.
        //textViewAbout.setText(EncodingUtils.getString(helpText, "UTF-8"));
        try{
            textViewAbout.setText(new String(helpText,"UTF-8"));
        }catch (UnsupportedEncodingException e){

        }
    }

}
