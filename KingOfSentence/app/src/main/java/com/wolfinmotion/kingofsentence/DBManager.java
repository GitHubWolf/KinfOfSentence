package com.wolfinmotion.kingofsentence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class DBManager {

	private Application mApplication;
	private String mDatabaseFileName;
	
	private SQLiteDatabase  mDatabase;
	
    private int mRepeatTimes;
	private int mClassIndex;
    private int mChapterIndex;
    private int mSentenceIndex;

    private long mTotalChapterCount;

    private String mChapterDescription;

    private boolean mShowChinese;//Show Chinese or show English.

    private Cursor mSentenceTable;

	public DBManager(Application application, String databaseFileName) {
		this.mApplication = application;
		this.mDatabaseFileName = databaseFileName;

		//Initialize the database.
		initialize();
	}

	// Check whether the database exists in the target folder.If it doesn't exist, make a copy to the target folder.
	private void initialize() {

		try {

			String targetDatabaseFullPath = mApplication.getFilesDir() + "/"+ mDatabaseFileName;
			File targetDatabaseFile = new File(targetDatabaseFullPath);// e.g. /data/data/com.wolfinmotion.kingofsentence/files/mDatabaseFileName
	
			if (!targetDatabaseFile.exists()) {
							
				// Target database file doesn't exist. We will copy the database from assets folder to the target file.
	
				//Try to create the folder first.
				File targetFolder = mApplication.getFilesDir();
				targetFolder.mkdirs();

				//To read in data.
				InputStream inputStream = mApplication.getAssets().open(mDatabaseFileName);

				//To write out data.
				FileOutputStream outputStream = new FileOutputStream(targetDatabaseFile);

				// Start to copy the data.
				byte[] fileBuffer = new byte[1024 * 5];
				int dataSize;

				while ((dataSize = inputStream.read(fileBuffer)) > 0) {
					outputStream.write(fileBuffer, 0, dataSize);
				}

				// Flush the output.
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			}
			
			//If no exception, DB shall be in position. Open it.
			mDatabase = SQLiteDatabase.openDatabase(targetDatabaseFullPath, null, SQLiteDatabase.OPEN_READWRITE);
			mDatabase.setLocale(Locale.CHINA);

            //Get total chapter count.
            mTotalChapterCount = getTotalChapterCount();

			//Read in useful information in advance.
			getOptionTable();
            selectChapter(0);
		}
		catch (IOException e) {
			// TODO Auto-generated ca
			// tch block
			e.printStackTrace();
		}
	}
	
	public Cursor getClassTable(){
		Cursor classTable = mDatabase.rawQuery("SELECT \"Index\" AS _id,ClassName FROM Class ", null);
/*
		while(classTable.moveToNext()){
			int index = classTable.getInt(0);
			String className = classTable.getString(1);
			
			System.out.println(
					"Index: " + index
					+ " ClassName:" + className);
			
		}
*/
		return classTable;
	}

    public Cursor getChapterTable(int classIndex){
        Cursor chapterTable = mDatabase.rawQuery("SELECT \"Index\" AS _id,ChapterName,ClassIndex FROM Chapter WHERE ClassIndex = ? ", new String[]{String.valueOf(classIndex)});
/*
        while(chapterTable.moveToNext()){
            String chapterName = chapterTable.getString(0);
            int index = chapterTable.getInt(1);


            System.out.println(
                    "Index: " + index
                     + " ChapterName:" + chapterName);

        }
*/
        return chapterTable;
    }

    public long getTotalChapterCount(){
        Cursor chapterCountCursor = mDatabase.rawQuery("SELECT count(*) FROM Chapter", null);
        long chapterCount = 0;
        while(chapterCountCursor.moveToNext()){
            chapterCount = chapterCountCursor.getLong(0);
            break;

        }

        return chapterCount;
    }

/*
    private Cursor getSentenceTable(int chapterIndex){
        Cursor sentenceTable = mDatabase.rawQuery("SELECT English, Chinese, \"Index\",ChapterIndex FROM Sentence WHERE ChapterIndex = ?", new String[]{String.valueOf(chapterIndex)});

        return sentenceTable;
    }
*/
	private void getOptionTable(){
		Cursor configTable =  mDatabase.rawQuery("SELECT * FROM Option", null);
		
		while(configTable.moveToNext()){
			mRepeatTimes = configTable.getInt(0);
			mClassIndex = configTable.getInt(1);
			mChapterIndex = configTable.getInt(2);
			mSentenceIndex = configTable.getInt(3);
			
			mShowChinese = (configTable.getInt(4) != 0);//Convert to boolean since the Cursor doesn't support getBoolean.
			
			System.out.println("Option values are:" + mRepeatTimes + " ," + mClassIndex + " ," + mChapterIndex + " ," + mSentenceIndex + " ,chinese " + mShowChinese);
			break;			
		}
		configTable.close();
	}

	public void getConfig(Bundle bundle) {
        //bundle.putInt("RepeatTimes", mRepeatTimes);
		bundle.putBoolean("ShowChinese", mShowChinese);
	}

    public void setConfig(Bundle bundle){
        //bundle.putInt("RepeatTimes", mRepeatTimes);
        mShowChinese = bundle.getBoolean("ShowChinese");

        int toShowChinese = mShowChinese?1:0;//Android API for SQLite doesn't support boolean very well.We will save it as integer.

        mDatabase.execSQL("UPDATE Option SET ShowChinese=?", new Object[]{toShowChinese});
    }

    public void getProgress(Bundle bundle){
        bundle.putInt("ClassIndex", mClassIndex);
        bundle.putInt("ChapterIndex", mChapterIndex);
        bundle.putInt("SentenceIndex", mSentenceIndex);
    }

    public void setProgress(int classIndex, int chapterIndex, int sentenceIndex){
        mClassIndex = classIndex;
        mChapterIndex = chapterIndex;
        mSentenceIndex = sentenceIndex;

        mDatabase.execSQL("UPDATE Option SET ClassIndex=?,ChapterIndex=?,SentenceIndex=?", new Object[]{classIndex, chapterIndex, sentenceIndex});

        //Class and chapter may have been updated, we need to get the latest sentence table.
        mSentenceTable.close();
        selectChapter(0);
    }

    public void getCurrentSentence(Bundle bundle){
        boolean found = false;
        int sentenceIndex = 0;

        //Move to the first first.
        mSentenceTable.moveToFirst();
        while(!mSentenceTable.isAfterLast()){
            String english = mSentenceTable.getString(0);
            String chinese = mSentenceTable.getString(1);
            sentenceIndex = mSentenceTable.getInt(2);

            if(sentenceIndex == mSentenceIndex){
                bundle.putString("English", english);
                bundle.putString("Chinese", chinese);
                bundle.putInt("SentenceIndex",sentenceIndex);
                found = true;
                break;
            }

            mSentenceTable.moveToNext();
        }

        if(!found){
            //Not found. We will read the first one.
            mSentenceTable.moveToFirst();

            //Do it now.
            readSentence(bundle);
        }
    }



    public boolean getNextSentence(Bundle bundle){
        if(mSentenceTable.moveToNext()){

            readSentence(bundle);

            return true;
        }
        else{
            //We have reached the end of the table.
            mSentenceTable.moveToFirst();

            //Read in data now.
            readSentence(bundle);

            return false;
        }
    }

    public boolean getPreviousSentence(Bundle bundle){
        if(mSentenceTable.moveToPrevious()){

            readSentence(bundle);

            return true;
        }
        else{
            return false;
        }
    }
    private void readSentence(Bundle bundle) {
        String english = mSentenceTable.getString(0);
        String chinese = mSentenceTable.getString(1);
        int sentenceIndex = mSentenceTable.getInt(2);

        mSentenceIndex = sentenceIndex;

        //Save the output.
        bundle.putString("English", english);
        bundle.putString("Chinese", chinese);
        bundle.putInt("SentenceIndex",sentenceIndex);

        //To save the new SentenceIndex.
        saveSentenceIndex();
    }

    private void saveSentenceIndex(){
        mDatabase.execSQL("UPDATE Option SET SentenceIndex=?", new Object[]{mSentenceIndex});
    }

    //Step will be either 1 ,0 OR  -1.
    public String switchToChapter(int step){

        selectChapter(step);

        return mChapterDescription;
    }

    public String getCurrentChapterInfo(){
        return mChapterDescription;
    }

    //Select current chapter(step is 0),, previous chapter(step is -1) or next chapter(step is 1).
    private void selectChapter(int step){

        //Chapter index starts from 1 and we also need to check whether overflow will happen, i.e. whether this chapter is the last one in the database table.

        if((1 == mChapterIndex)&&(-1 == step)){
            //Loop to the last one.
            mChapterIndex = (int)mTotalChapterCount;
        }
        else if((mTotalChapterCount == mChapterIndex)&&( 1 == step)){
            //Loop to the first one.
            mChapterIndex = 1;
        }
        else{
            mChapterIndex += step;
        }

        //Get class index for current chapter, also get the chapter name.
        Cursor chapterTable = mDatabase.rawQuery("SELECT ClassIndex,ChapterName FROM Chapter WHERE \"Index\" = ? ", new String[]{String.valueOf(mChapterIndex)});
        int classIndex = 0;
        String chapterName = null;
        while(chapterTable.moveToNext()){
            classIndex = chapterTable.getInt(0);
            chapterName = chapterTable.getString(1);
            break;
        }

        //Also update class index.
        mClassIndex = classIndex;

        //Get class name.
        Cursor classTable = mDatabase.rawQuery("SELECT ClassName FROM Class WHERE \"Index\" = ? ", new String[]{String.valueOf(classIndex)});
        String className = null;
        while(classTable.moveToNext()){
            className = classTable.getString(0);
            break;
        }

        //Form a description so that the user can know current chapter info.
        mChapterDescription = String.format("(%d)%s--%s",mChapterIndex,chapterName,className);

        //Cursor to save all the sentences within current chapter.
        mSentenceTable = mDatabase.rawQuery("SELECT English, Chinese, \"Index\",ChapterIndex FROM Sentence WHERE ChapterIndex = ?", new String[]{String.valueOf(mChapterIndex)});
    }


    public void close(){
		
		if(null != mDatabase){
			mDatabase.close();
		}
	}

}
