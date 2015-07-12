package com.lalapetstudios.udacityprojects.spotifystreamer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.lalapetstudios.udacityprojects.spotifystreamer.adapters.SearchAdapter;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.ArtistContentProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.contentproviders.RecentSuggestionsProvider;
import com.lalapetstudios.udacityprojects.spotifystreamer.models.ResultItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g2ishan on 7/7/15.
 *
 * This activity controls the maint flow for the Custom Searchable behaviour. Its onCreate method
 * initialize the main UI components - such as the app bar and result list (RecyclerView). It will
 * be called through an intent and it's responses are also sent as intents
 */

public class SearchActivity extends AppCompatActivity {
    // CONSTANTS
    private static final String TAG = "SearchActivity";
    public static final int VOICE_RECOGNITION_CODE = 1;
    public static final String CLICKED_RESULT_ITEM = "clicked_result_item";

    // UI ELEMENTS
    private RecyclerView searchResultList;
    private EditText searchInput;
    private RelativeLayout voiceInput;
    private RelativeLayout dismissDialog;
    private ImageView micIcon;

    private String query;
    private String providerName;
    private String providerAuthority;
    private String searchableActivity;
    private Boolean isRecentSuggestionsProvider = Boolean.FALSE;
    private int currentapiVersion = 0;
    private RippleView dismissRippleView;
    private RippleView micRippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.custom_searchable);

        this.query = "";
        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        this.searchInput = (EditText) this.findViewById(R.id.custombar_text);
        this.voiceInput = (RelativeLayout) this.findViewById(R.id.custombar_mic_wrapper);
        this.dismissDialog = (RelativeLayout) this.findViewById(R.id.custombar_return_wrapper);
        this.micIcon = (ImageView) this.findViewById(R.id.custombar_mic);
        this.micIcon.setSelected(Boolean.FALSE);
        this.providerName = RecentSuggestionsProvider.class.getName();
        this.providerAuthority = RecentSuggestionsProvider.PROVIDER_AUTHORITY;

        this.currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            dismissRippleView = (RippleView)this.findViewById(R.id.dismiss_ripple_view);
            micRippleView = (RippleView)this.findViewById(R.id.mic_ripple_view);
        }

        // Initialize result list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);
        searchResultList.setItemAnimator(new DefaultItemAnimator());

        //TODO - see below if we uncomment below two line the initial search will not show the list Instead keep the third line
        //SearchAdapter adapter = new SearchAdapter(new ArrayList<ResultItem>());
        //searchResultList.setAdapter(adapter);
        if (isRecentSuggestionsProvider) {
            // Provider is descendant of SearchRecentSuggestionsProvider **/
            mapResultsFromRecentProviderToList();
        }

        //this.searchInput.setMaxLines(1);

        implementSearchTextListener();
        implementDismissListener();
        implementVoiceInputListener();

        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> infoList = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (infoList.size() == 0) {
            micIcon.setVisibility(View.INVISIBLE);
            if(currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
                micRippleView.setVisibility(View.INVISIBLE);
            }
        }


    }

    // Receives the intent with the speech-to-text information and sets it to the InputText
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VOICE_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(text.get(0));
                }
                break;
            }
        }
    }

    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (isRecentSuggestionsProvider) {
                        sendSearchIntent();
                    } else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(exampleView.getWindowToken(), 0);
                    }
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                query = searchInput.getText().toString();

                if (isRecentSuggestionsProvider) {
                    // Provider is descendant of SearchRecentSuggestionsProvider **/
                    mapResultsFromRecentProviderToList();
                } else {
                    // Provider is custom and shall follow the contract
                    mapResultsFromCustomProviderToList();
                }

                if (!"".equals(searchInput.getText().toString())) {
                    setClearTextIcon();
                } else {
                    setMicIcon();
                }
            }

            // DO NOTHING
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            // DO NOTHING
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    // Finishes this activity and goes back to the caller
    private void implementDismissListener () {
        if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            this.dismissDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            dismissRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    finish();
                }
            });
        }
    }

    // Implements speech-to-text
    private void implementVoiceInputListener () {
        if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            this.voiceInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showVoiceInput();
                }
            });
        } else {
            micRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    showVoiceInput();
                }
            });
        }
    }

    private void showVoiceInput() {
            if (micIcon.isSelected()) {
                searchInput.setText("");
                query = "";
                micIcon.setSelected(Boolean.FALSE);
                micIcon.setImageResource(R.drawable.ic_action_mic);
            } else {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

                SearchActivity.this.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
            }
    }

    // Given provider is descendant of SearchRecentSuggestionsProvider (column scheme differs)
    private void mapResultsFromRecentProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = queryRecentSuggestionsProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer headerIdx = results.getColumnIndex("display1");
                Integer subHeaderIdx = results.getColumnIndex("display2");
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                int i = 1;
                while (results.moveToNext()) {
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);
                    Integer leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(Integer.toString(i),header, subHeader, leftIcon, rightIcon);
                    resultList.add(aux);
                    i++;
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    private Cursor queryRecentSuggestionsProvider () {
        Uri uri = Uri.parse("content://".concat(providerAuthority.concat("/suggestions")));

        String[] selection = SearchRecentSuggestions.QUERIES_PROJECTION_1LINE;

        String[] selectionArgs = new String[] {"%" + query + "%"};

        return SearchActivity.this.getContentResolver().query(
                uri,
                selection,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Given provider is custom and must follow the column contract
    private void mapResultsFromCustomProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = results = queryCustomSuggestionProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer idIdx = results.getColumnIndex(ArtistContentProvider.ID);
                Integer headerIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                Integer subHeaderIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                while (results.moveToNext()) {
                    String id = results.getString(idIdx);
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);

                    Object leftIcon;
                    if(results.getType(leftIconIdx) == Cursor.FIELD_TYPE_INTEGER) {
                        leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    } else {
                        leftIcon = results.getString(leftIconIdx);
                    }

                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(id,header, subHeader, leftIcon, rightIcon);
                    resultList.add(aux);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    // Look for search suggestions using spotify artist custom content provider
    private Cursor queryCustomSuggestionProvider () {
        Uri uri = Uri.parse("content://".concat(ArtistContentProvider.PROVIDER_AUTHORITY).concat("/suggestions/").concat(query));

        String[] selection = {"display1"};
        String[] selectionArgs = new String[] {"%" + query + "%"};

        return SearchActivity.this.getContentResolver().query(
                uri,
                SearchRecentSuggestions.QUERIES_PROJECTION_1LINE,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Sends an intent with the typed query to the searchable Activity
    private void sendSearchIntent () {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, providerAuthority, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
        suggestions.saveRecentQuery(query, null);
        finish();
    }

    // Set X as the icon for the right icon in the app bar
    private void setClearTextIcon () {
        micIcon.setSelected(Boolean.TRUE);
        micIcon.setImageResource(R.drawable.ic_action_cancel);
        micIcon.invalidate();
    }

    // Set the micrphone icon as the right icon in the app bar
    private void setMicIcon () {
        micIcon.setSelected(Boolean.FALSE);
        micIcon.setImageResource(R.drawable.ic_action_mic);
        micIcon.invalidate();
    }

}
