package com.wolfinmotion.kingofsentence;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

	private DBManager mDBManager;

    private StudyFragment mStudyFragment;
    private ClassFragment mClassFragment;
    private OptionFragment mOptionFragment;
    private AboutFragment mAboutFragment;

    private View mSelectedView;//To avoid unnecessary refresh, we will save the current selected view.

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        FragmentManager fragmentManager = this.getFragmentManager();
        if(null != savedInstanceState){
            mStudyFragment = (StudyFragment)fragmentManager.findFragmentByTag("mStudyFragment");
            mClassFragment = (ClassFragment)fragmentManager.findFragmentByTag("mClassFragment");
            mOptionFragment = (OptionFragment)fragmentManager.findFragmentByTag("mOptionFragment");
            mAboutFragment = (AboutFragment)fragmentManager.findFragmentByTag("mAboutFragment");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        //Set the background.
        setBackground();

        View layoutStudy = (View)findViewById(R.id.layoutStudy);
        layoutStudy.setOnClickListener(this);

        View layoutClass = (View)findViewById(R.id.layoutClass);
        layoutClass.setOnClickListener(this);

        View layoutOption = (View)findViewById(R.id.layoutOption);
        layoutOption.setOnClickListener(this);

        View layoutAbout = (View)findViewById(R.id.layoutAbout);
        layoutAbout.setOnClickListener(this);

        //Get DBManager so that we can get read data from it.
        KingOfSentenceApplication app = (KingOfSentenceApplication)this.getApplication();
        mDBManager = app.getDBManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Show study view by default.
        onSelectTab(findViewById(R.id.layoutStudy));
    }

    private void setBackground(){

        //Randomly select a picture as the background.
        Random random = new Random();
        int backgroundIndex = random.nextInt(10);

        //The view holding the background.
        LinearLayout mainScreenLayout = (LinearLayout)findViewById((R.id.layoutMainScreen));

        //Get resource ID.
        int backgroundResourceId = this.getResources().getIdentifier("p"+String.valueOf(backgroundIndex), "drawable", this.getPackageName());

        //Set the background.
        //Drawable drawable = this.getResources().getDrawable(backgroundResourceId);
        mainScreenLayout.setBackgroundResource(backgroundResourceId);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_quit:
                finish();//Quit.
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        if(mSelectedView != view){
            //Select the view if it is not currently selected.
            onSelectTab(view);
        }
    }

    private void onSelectTab(View view){

        if(null != mSelectedView){
            //Update the text to black for previous selected item.
            TextView previousIconTextView = (TextView)mSelectedView.findViewById(R.id.textViewIconText);
            previousIconTextView.setTextColor(Color.rgb(0, 0, 0));
        }

        //Highlight current selected item.
        TextView currentIconTextView = (TextView)view.findViewById(R.id.textViewIconText);
        currentIconTextView.setTextColor(Color.rgb(255, 0, 0));

        //Set it as current selected view.So that we can avoid unnecessary refresh.
        mSelectedView = view;

        //Hide current fragment first.
        hideCurrentFragment();

        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;

        switch(view.getId()){
            case R.id.layoutStudy:{
                if(null == mStudyFragment){
                    //Create and add it into the fragment manager if it doesn't exist.
                    mStudyFragment = new StudyFragment();
                    fragmentTransaction.add(R.id.layoutContent, mStudyFragment, "mStudyFragment");
                }
                fragment = mStudyFragment;
                break;
            }
            case R.id.layoutClass:{
                if(null == mClassFragment){
                    //Create and add it into the fragment manager if it doesn't exist.
                    mClassFragment = new ClassFragment();
                    fragmentTransaction.add(R.id.layoutContent, mClassFragment, "mClassFragment");
                }
                fragment = mClassFragment;
                break;
            }
            case R.id.layoutOption:{
                if(null == mOptionFragment){
                    //Create and add it into the fragment manager if it doesn't exist.
                    mOptionFragment = new OptionFragment();
                    fragmentTransaction.add(R.id.layoutContent, mOptionFragment, "mOptionFragment");
                }
                fragment = mOptionFragment;
                break;
            }
            case R.id.layoutAbout:{
                if(null == mAboutFragment){
                    //Create and add it into the fragment manager if it doesn't exist.
                    mAboutFragment = new AboutFragment();
                    fragmentTransaction.add(R.id.layoutContent, mAboutFragment, "mAboutFragment");
                }
                fragment = mAboutFragment;
                break;
            }
            default:{
                //Nothing to do.
            }
        }

        //Show it.
        if(null != fragment){
            fragmentTransaction.show(fragment);
        }

        fragmentTransaction.commit();
    }

    private void hideCurrentFragment(){
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(null != mStudyFragment){
            fragmentTransaction.hide(mStudyFragment);
        }

        if(null != mClassFragment){
            fragmentTransaction.hide(mClassFragment);
        }

        if(null != mOptionFragment){
            fragmentTransaction.hide(mOptionFragment);
        }

        if(null != mAboutFragment){
            fragmentTransaction.hide(mAboutFragment);
        }
        fragmentTransaction.commit();
    }
}
