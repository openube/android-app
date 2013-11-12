package fr.gaulupeau.apps.Poche;

import java.util.ArrayList;
import static fr.gaulupeau.apps.Poche.ArticlesSQLiteOpenHelper.*;
import fr.gaulupeau.apps.InThePoche.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListArticles extends Activity {

    private ArrayList<Article> readArticlesInfo;
	private ListView readList;
	private SQLiteDatabase database;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		setupDB();
		setupList();
	}
	
    public void onResume() {
        super.onResume();
        setupList();
    }
    
    public void onDestroy() {
        super.onDestroy();
        database.close();
    }
	
	public void setupDB() {
		ArticlesSQLiteOpenHelper helper = new ArticlesSQLiteOpenHelper(this);
		database = helper.getWritableDatabase();
	}
	
	public void setupList() {
		readList = (ListView) findViewById(R.id.liste_articles);
        readArticlesInfo = new ArrayList<Article>();
        ReadingListAdapter ad = getAdapterQuery(ARCHIVE + "=0", readArticlesInfo);
        readList.setAdapter(ad);
        
        readList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(getBaseContext(), ReadArticle.class);
				i.putExtra("id", (String) readArticlesInfo.get(position).id);
				startActivity(i);
			}
        	
        });
	}
	
	public ReadingListAdapter getAdapterQuery(String filter, ArrayList<Article> articleInfo) {
		Log.e("getAdapterQuery", "running query");
		//String url, String domain, String id, String title, String content
		String[] getStrColumns = new String[] {ARTICLE_URL, ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, ARCHIVE};
		Cursor ac = database.query(
				ARTICLE_TABLE,
				getStrColumns,
				filter, null, null, null, null);
		ac.moveToFirst();
		if(!ac.isAfterLast()) {
			do {
				Article tempArticle = new Article(ac.getString(0),ac.getString(1),ac.getString(2),ac.getString(3),ac.getString(4));
				articleInfo.add(tempArticle);
			} while (ac.moveToNext());
		}
		ac.close();
		return new ReadingListAdapter(getBaseContext(), articleInfo);
	}
	
}
