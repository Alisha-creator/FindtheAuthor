package alisha.digipodium.findtheauthor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private Button btnSearch;
    private TextView textBookName,textAuthor;
    private EditText editBookName;
    private ImageView book_img;

    public  void hideSystemUI(){
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        textBookName= findViewById(R.id.textBookName);
        editBookName= findViewById(R.id.editBookName);
        textAuthor= findViewById(R.id.textAuthorName);
        book_img = findViewById(R.id.book_img);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (methodManager!=null){
                    methodManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager!=null){
                    NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
                    if (networkinfo!=null && networkinfo.isConnected()){
                        searchBooks();
                        textAuthor.setText("Loading...");
                        textBookName.setText("Please Wait...");
                    }
                    else{
                        textBookName.setText("No Network Access! Check Your Connection Now...");
                        textAuthor.setText("");
                    }
                }
            }
        });
    }
    public void searchBooks(){
        String queryString =editBookName.getText().toString();
        if (queryString.length() > 2) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("query",queryString);
            getSupportLoaderManager().restartLoader(0,queryBundle,this);
            //new FetchBook(textAuthor, textBookName).execute(queryString);
        }else {
            editBookName.setError("Enter A Book Name");
            editBookName.requestFocus();
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String query = "";
        if (args != null){
            query = args.getString("query");
        }
        return new BookLoader(this,query);//our loader class
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //1.raw string data to json object
            //if curly braces then object and if square braces then json array
            JSONObject jsonObject = new JSONObject(data);

            //2.get book data array
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            //3.loop variables
            int i= 0;
            String book_img = null;
            String title = null;
            String authors = null;

            //4. look for results in array,exit when book and author are found or when all items are checked
            while (i < itemsArray.length() && (authors==null && title == null && book_img == null)){
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volume = book.getJSONObject("volumeInfo");
                try {
                    title = volume.getString("title");
                    authors = volume.getString("authors");
                    book_img = volume.getString("book_img");
                }catch(Exception e){
                    e.printStackTrace();
                }
                i++;
            }
            if(title!=null && authors !=null){
                textBookName.setText(title);
                textAuthor.setText(authors);
            }else {
                textBookName.setText("Unknown Book Name");
                textAuthor.setText("Unknown Author");
            }
        }catch (Exception e){
            e.printStackTrace();
            textBookName.setText("API error");
            textAuthor.setText("Please check Logcat");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    /*//create a Async task Class
    public class FetchBook extends AsyncTask<String,Void,String >{
        private WeakReference<TextView> textTitle;
        private WeakReference<TextView > textAuthor;

        //constructor
        public FetchBook(TextView textTitle,TextView textAuthor){
            this.textAuthor = new WeakReference<>(textAuthor);
            this.textTitle = new WeakReference<>(textTitle);
        }
        @Override
        protected String doInBackground(String... query) {
            //make a request to API Server
            return NetworkUtlis.getBookInfo(query[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Parse the Json data
            super.onPostExecute(result);
            try {
                //1.raw string data to json object
                //if curly braces then object and if square braces then json array
                JSONObject jsonObject = new JSONObject(result);

                //2.get book data array
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                //3.loop variables
                int i= 0;
                String title = null;
                String authors = null;

                //4. look for results in array,exit when book and author are found or when all items are checked
                while (i < itemsArray.length() && (authors==null && title == null)){
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volume = book.getJSONObject("volumeInfo");
                    try {
                        title = volume.getString("title");
                        authors = volume.getString("authors");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    i++;
                }
                if(title!=null && authors !=null){
                    textTitle.get().setText(title);
                    textAuthor.get().setText(authors);
                }else {
                    textTitle.get().setText("Unknown Book Name");
                    textAuthor.get().setText("Unknown Author");
                }
            }catch (Exception e){
                e.printStackTrace();
                textTitle.get().setText("API error");
                textAuthor.get().setText("Please check Logcat");
            }
        }
    }*/
}