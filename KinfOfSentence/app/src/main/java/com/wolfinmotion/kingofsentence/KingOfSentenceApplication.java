package com.wolfinmotion.kingofsentence;

import android.app.Application;

public class KingOfSentenceApplication extends Application {
	private DBManager mDBManager;

	public DBManager getDBManager(){
		if(null == mDBManager)
		{
			mDBManager = new DBManager(this, "KingOfSentence.s3db");
		}
		return mDBManager;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		
		mDBManager.close();
	}
	
	
}
