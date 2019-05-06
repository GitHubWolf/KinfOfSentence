package com.wolfinmotion.kingofsentence;



import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple Fragment subclass.
 *
 */
public class ClassFragment extends Fragment {
    private DBManager mDBManager;
    private ListView mListViewClass;
    private ListView mListViewChapter;
    private Bundle mBundleProgress = new Bundle();

    //To keep track of the selected items. We are going to change the color for the selected item.
    private View mViewClassSelected;
    private View mViewChapterSelected;

    //To save current selected ClassIndex and ChapterIndex.
    private int mCurrentClassIndex;
    private int mCurrentChapterIndex;

    private int mCurrentChapterPosition;

    public ClassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        KingOfSentenceApplication app = (KingOfSentenceApplication)this.getActivity().getApplication();
        mDBManager = app.getDBManager();
        mDBManager.getProgress(mBundleProgress);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class, container, false);

        mListViewClass = (ListView)view.findViewById(R.id.listViewClass);
        mListViewChapter = (ListView)view.findViewById(R.id.listViewChapter);

        SimpleCursorAdapter cursorAdapterClass = new SimpleCursorAdapter(
                                                this.getActivity(),
                                                R.layout.class_row,
                                                mDBManager.getClassTable(),
                                                new String[]{"_id","ClassName"},
                                                new int[]{R.id.textViewClassIndex,R.id.textViewClassName },
                                                0);

        mListViewClass.setAdapter(cursorAdapterClass);

        mListViewClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,//The AdapterView where the click happened
                                    View selectedView,//The view within the AdapterView that was clicked
                                    int position,//The position of the view in the adapter
                                    long rowId //The row id of the item that was clicked
            ) {
                onSelectClass(selectedView, position);
            }
        });
        mListViewClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedView, int position, long rowId) {
                onSelectClass(selectedView, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("No class selected.");
            }
        });

        mListViewChapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,//The AdapterView where the click happened
                                    View selectedView,//The view within the AdapterView that was clicked
                                    int position,//The position of the view in the adapter
                                    long rowId //The row id of the item that was clicked
            ) {
                onSelectChapter(selectedView, position);
            }
        });
        mListViewChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View selectedView, int position, long rowId) {
                onSelectChapter(selectedView, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("No chapter selected.");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Select current class.Note that the adapter will be executed asynchronously, we will need to do so, so that we will not get ZERO from getChildCount.
        mListViewClass.setSelection(mBundleProgress.getInt("ClassIndex") - 1);
        mListViewClass.post(new Runnable() {
            @Override
            public void run() {
                selectClass(mBundleProgress.getInt("ClassIndex") - 1);
            }
        });

    }

    private void onSelectClass(View selectedView, int position){
        //To read Class Index and then show all the chapters.

        if(null == selectedView){
            //Toast.makeText(getActivity(),"position is " + position, Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            selectClass(position);
        }

    }
    private void onSelectChapter(View selectedView, int position){
        if(null == selectedView){
            //Toast.makeText(getActivity(),"position is " + position, Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            selectChapter(position);
        }
    }

    private void selectClass(int position){

        //Clear previous selected item's background color.
        if(null != mViewClassSelected ){
            mViewClassSelected.setBackgroundColor(Color.TRANSPARENT);
        }

        //Get the view so that we can change its background color.
        //View selectedView = mListViewClass.getAdapter().getView(position, null, null);//It can't be used to update the background.
        /*
        if((position < mListViewClass.getFirstVisiblePosition() ) || (position > mListViewClass.getLastVisiblePosition())){
            mListViewClass.setSelected(true);
            mListViewClass.setSelection(position);//So that getChildAt will not return ZERO.
        }
        */
        //int firstPos = mListViewClass.getFirstVisiblePosition();
        //int lastPost = mListViewClass.getLastVisiblePosition();
        //int childCount = mListViewClass.getChildCount();
        View selectedView = mListViewClass.getChildAt(position - mListViewClass.getFirstVisiblePosition());//getChildAt will only get the children that are currently visible in the list!!!!!!!

        //System.out.println("Position is " + position + " total count " + mListViewClass.getCount());
        if(null != selectedView){
            selectedView.setBackgroundColor(Color.YELLOW);
            mViewClassSelected = selectedView;
        }
        else{
            System.out.println("No child found." + position);
        }

        //Query the chapters from DB and show it in the list view.
        SimpleCursorAdapter cursorAdapterChapter = new SimpleCursorAdapter(
                this.getActivity(),
                R.layout.chapter_row,
                mDBManager.getChapterTable(position + 1),//ClassIndex is position + 1.
                new String[]{"_id","ChapterName"},
                new int[]{R.id.textViewChapterIndex,R.id.textViewChapterName },
                0);
        mListViewChapter.setAdapter(cursorAdapterChapter);

        //Save ClassIndex.
        mCurrentClassIndex = position + 1;

        //To select the default chapter.
        //Set the selection.
        mCurrentChapterPosition = getChapterPosition(mBundleProgress.getInt("ChapterIndex"));
        mListViewChapter.setSelection(mCurrentChapterPosition);

        mListViewChapter.post(new Runnable() {
            @Override
            public void run() {
                selectChapter(mCurrentChapterPosition);
            }
        });

    }

    private void selectChapter(int position){
        if(null != mViewChapterSelected ){
            mViewChapterSelected.setBackgroundColor(Color.TRANSPARENT);
        }

        /*
        if((position < mListViewChapter.getFirstVisiblePosition() ) || (position > mListViewChapter.getLastVisiblePosition())){
            mListViewChapter.setSelected(true);
            mListViewChapter.setSelection(position);//So that getChildAt will not return ZERO.
        }*/

        View selectedView = mListViewChapter.getChildAt(position - mListViewChapter.getFirstVisiblePosition());//getChildAt will only get the children that are currently visible in the list!!!!!!!

        if(null != selectedView){
            selectedView.setBackgroundColor(Color.GREEN);
            mViewChapterSelected = selectedView;
        }

        //Get ChapterIndex from position.
        Cursor cursor = (Cursor)mListViewChapter.getItemAtPosition(position);
        mCurrentChapterIndex = cursor.getInt(0);//Get ChapterIndex.

        //Save the progress.
        mDBManager.setProgress(mCurrentClassIndex, mCurrentChapterIndex, mBundleProgress.getInt("SentenceIndex"));
    }

    //Get position from ChapterIndex.
    private int getChapterPosition(int chapterIndex){
        int position = 0;
        int count = mListViewChapter.getAdapter().getCount();

        //System.out.println("getChapterPosition chapterIndex "+chapterIndex + "  count " + count);
        for(int i = 0;i < count; i++){
            //Compare the chapter index.If it is what we are looking for, select it.
            Cursor myCursor = (Cursor)mListViewChapter.getItemAtPosition(i);
            int chapterIndexAssociated = myCursor.getInt(0);

            //System.out.println("chapterIndexAssociated "+chapterIndexAssociated);

            if(chapterIndexAssociated == chapterIndex){
                position = i;
                break;
            }
        }

        return position;
    }
}
