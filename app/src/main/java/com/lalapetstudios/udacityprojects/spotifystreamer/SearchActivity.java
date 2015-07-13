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
import android.support.design.widget.Snackbar;
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
import com.lalapetstudios.udacityprojects.spotifystreamer.util.OnAsyncTaskCompletedInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by g2ishan on 7/7/15.
 *
 * This activity controls the maint flow for the Custom Searchable behaviour. Its onCreate method
 * initialize the main UI components - such as the app bar and result list (RecyclerView). It will
 * be called through an intent and it's responses are also sent as intents
 */

public class SearchActivity extends AppCompatActivity implements OnAsyncTaskCompletedInterface {
    // CONSTANTS
    private static final String TAG = "SearchActivity";
    private static final String RESULT_ITEM_MODEL = "RESULT_ITEM_MODEL";
    private static final String RESULT_ITEM_MODEL_QUERY = "RESULT_ITEM_MODEL_QUERY";

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
    //private Boolean isRecentSuggestionsProvider = Boolean.FALSE;
    private int currentapiVersion = 0;
    private RippleView dismissRippleView;
    private RippleView micRippleView;
    ArrayList<ResultItem> mResultList;
    Bundle mSavedInstaneState;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.custom_searchable);
        this.mSavedInstaneState = savedInstanceState;

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

        rootView = findViewById(R.id.custom_searchable_wrapper);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESULT_ITEM_MODEL, mResultList);
        outState.putString(RESULT_ITEM_MODEL_QUERY, query);
    }

    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(exampleView.getWindowToken(), 0);
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                query = searchInput.getText().toString();

                    boolean usedFromBundle = false;
                    if (mSavedInstaneState != null) {
                        String previousQuery = mSavedInstaneState.getString(RESULT_ITEM_MODEL_QUERY);
                        if (previousQuery != null && previousQuery.trim().length() > 0) {
                            if (query != null && query.trim().length() > 0) {
                                if (previousQuery.trim().equals(query.trim())) {
                                    mResultList = mSavedInstaneState.getParcelableArrayList(RESULT_ITEM_MODEL);
                                    createSearchAdapter();
                                    usedFromBundle = true;
                                }
                            }
                        }

                    }
                    if (!usedFromBundle) {
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

    private void createSearchAdapter() {
        SearchAdapter adapter = new SearchAdapter(mResultList);
        searchResultList.setAdapter(adapter);
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

    // Given provider is custom and must follow the column contract
    private void mapResultsFromCustomProviderToList () {
        new AsyncTask<Void, Void, ArrayList>() {
            @Override
            protected void onPostExecute(ArrayList resultList) {
                mResultList = resultList;
                boolean errorFlag = resultList == null ? true : false;
                onAsyncTaskCompleted(errorFlag);
            }

            @Override
            protected ArrayList doInBackground(Void[] params) {
                Cursor results = null;
                try {
                    results = queryCustomSuggestionProvider();
                } catch(Exception ex) {
                    Log.e(TAG, "Issues in getting data from provider");
                    return null;
                }
                ArrayList<ResultItem> resultList = new ArrayList<>();

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

                    ResultItem aux = new ResultItem(id,header, subHeader, leftIcon, rightIcon, query);
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

    @Override
    public void onAsyncTaskCompleted(boolean pErrorFlag) {
        if(pErrorFlag) {
            Snackbar
                    .make(rootView, R.string.no_interent_connection_text, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            this.createSearchAdapter();
        }
    }
}
