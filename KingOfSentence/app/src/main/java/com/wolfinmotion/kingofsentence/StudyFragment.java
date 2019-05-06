package com.wolfinmotion.kingofsentence;



import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple Fragment subclass.
 *
 */
public class StudyFragment extends Fragment{
    private Bundle mBundleSentence = new Bundle();//Save string data: "Chinese", "English".
    private Bundle mBundleConfig = new Bundle();//Save the boolean option "ShowChinese".

    private TextView mTextViewSentenceA;
    private TextView mTextViewSentenceB;

    private TextView mTextViewChapterInfo;

    private DBManager mDBManager;
    public StudyFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        mTextViewSentenceA = (TextView)view.findViewById(R.id.textViewSentenceA);
        mTextViewSentenceB = (TextView)view.findViewById(R.id.textViewSentenceB);
        mTextViewChapterInfo = (TextView)view.findViewById(R.id.textViewChapterInfo);


        //Clear the text.
        mTextViewSentenceA.setText(null);
        mTextViewSentenceA.setText(null);
        mTextViewChapterInfo.setText(null);

        KingOfSentenceApplication app = (KingOfSentenceApplication)this.getActivity().getApplication();
        mDBManager = app.getDBManager();

        //Retrieve data from DBManager.
        mDBManager.getCurrentSentence(mBundleSentence);

        //Retrieve config.
        mDBManager.getConfig(mBundleConfig);

        //Show current sentence.
        showCurrentSentence();

        //Show current chapter info.
        showChapterInfo();

        view.setOnTouchListener(new MyGesture());//Handle related gestures in this class.

        //Check and show user guide if this is the first time that this application runs.
        checkFirstRun();
        return view;
    }

    private void checkFirstRun(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        Boolean isTheFirstTime = true;
        String settingFirstRun = "FirstRun";
        if(null != sharedPreferences){
            isTheFirstTime = sharedPreferences.getBoolean(settingFirstRun,true);
        }

        //First time.We will present a user guide.
        if(isTheFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(settingFirstRun, false);
            editor.commit();

            //Present the user guide.
            showUserGuide();
        }
    }

    private void showUserGuide(){
        Dialog dialog = new UserGuideDialog(this.getActivity());
        dialog.show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            //The fragment is going to be presented to the user.We will retrieve latest config.

            //Retrieve data from DBManager.
            mDBManager.getCurrentSentence(mBundleSentence);

            //Retrieve config.
            mDBManager.getConfig(mBundleConfig);

            //Show current sentence.
            showCurrentSentence();

            //Show current chapter info.
            showChapterInfo();
        }
    }

    private void showCurrentSentence(){

        if(mBundleConfig.getBoolean("ShowChinese")){
            mTextViewSentenceA.setText(mBundleSentence.getString("Chinese"));
    }
    else{
        mTextViewSentenceA.setText(mBundleSentence.getString("English"));
    }

    //Clear SentenceB.
    mTextViewSentenceB.setText(null);
}

    private void getAnswer(){
        //Retrieve config.
        mDBManager.getConfig(mBundleConfig);

        //Show current answer.
        if(mBundleConfig.getBoolean("ShowChinese")){
            mTextViewSentenceB.setText(mBundleSentence.getString("English"));
        }
        else{
            mTextViewSentenceB.setText(mBundleSentence.getString("Chinese"));
        }

    }

    private void getNextSentence(){
        //Retrieve config.
        mDBManager.getConfig(mBundleConfig);

        //Get next one.
        if(!mDBManager.getNextSentence(mBundleSentence)){
            Toast.makeText(this.getActivity(), "Loop back to the first one. You may choose a new class.", Toast.LENGTH_SHORT).show();
        }

        //Show SentenceA.
        showCurrentSentence();
    }

    private void getPreviousSentence(){
        //Retrieve config.
        mDBManager.getConfig(mBundleConfig);

        //Get next one.
        if(!mDBManager.getPreviousSentence(mBundleSentence)){
            Toast.makeText(this.getActivity(), "You have reached the first item.", Toast.LENGTH_SHORT).show();
        }

        //Show SentenceA.
        showCurrentSentence();
    }

    private void showChapterInfo(){
        //Show the chapter info.
        mTextViewChapterInfo.setText(mDBManager.getCurrentChapterInfo());
    }
    private class MyGesture implements View.OnTouchListener,GestureDetector.OnGestureListener{

        private GestureDetector mGestureDetector = new GestureDetector(getActivity(),this);
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;//Return true otherwise other events will not be triggered.
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            //Toast.makeText(getActivity(),"onSingleTapUp", Toast.LENGTH_SHORT).show();

            getAnswer();//Show the answer.
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            if((motionEvent.getX() - motionEvent2.getX() ) > 100){
                //Flip left.
                //Toast.makeText(getActivity(),"To left", Toast.LENGTH_SHORT).show();

                //Get the next sentence.
                getNextSentence();
            }
            else if((motionEvent.getX() - motionEvent2.getX() ) < -100){
                //Flip right.
                //Toast.makeText(getActivity(),"To right", Toast.LENGTH_SHORT).show();
                getPreviousSentence();
            }
            else if((motionEvent.getY() - motionEvent2.getY() ) > 150){
                //Flip up.
                //Toast.makeText(getActivity(),"To up", Toast.LENGTH_SHORT).show();

                //Get the next class.
                mDBManager.switchToChapter(1);

                //Show current chapter info.
                showChapterInfo();

                //Show current sentence.
                getNextSentence();

                //Toast.makeText(getActivity(),chapterDescription, Toast.LENGTH_SHORT).show();
            }
            else if((motionEvent.getY() - motionEvent2.getY() ) < -150){
                //Flip down.
                //Toast.makeText(getActivity(),"To down", Toast.LENGTH_SHORT).show();
                 mDBManager.switchToChapter(-1);

                //Show current chapter info.
                showChapterInfo();

                //Show current sentence.
                getNextSentence();

                //Toast.makeText(getActivity(),chapterDescription, Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            //All touch events will be handled by by the GestureDetector,so that we can get notified by other onXXX events.
            return mGestureDetector.onTouchEvent(motionEvent);
        }
    }
}
