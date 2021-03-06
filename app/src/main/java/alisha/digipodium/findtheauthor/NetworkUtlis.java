package alisha.digipodium.findtheauthor;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtlis {
    public static final String TAG = NetworkUtlis.class.getSimpleName();//logic of network
    //https://www.googleapis.com/books/v1/volumes?=40&orderBy=newest&q=Choas HTTP/1.1

    private static final String BOOK_API_BASE = "https://www.googleapis.com/books/v1/volumes?";
    private static final String MAX_RESULTS = "maxResults";
    private static final String QUERY_PARAM = "q";
    private static final String PRINT_TYPE = "printType";
    private static final String YOUR_API_KEY = "";

    static String getBookInfo(String query){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONData = null;
        try{
            //todo get data from API
            //connect to server
            Uri buildUri = Uri.parse(BOOK_API_BASE).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,query)
                    .appendQueryParameter(MAX_RESULTS,"10")
                    .appendQueryParameter(PRINT_TYPE,"books")
                    .build();
            URL requestUrl = new URL(buildUri.toString());
            urlConnection = (HttpURLConnection)requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //get data from server as byte stream
            InputStream input = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder builder  =new StringBuilder();

            //convert data into readable format
            String line;
            while ((line= reader.readLine())!=null){
                builder.append(line);
                builder.append("\n");
            }
            if (builder.length()==0){
                return null;
            }
            bookJSONData = builder.toString();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            //connection closing
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if (reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        //Log.d(TAG,bookJSONData);
        return bookJSONData;
    }
}
