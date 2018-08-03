package com.fincoapps.servizone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.android.volley.RequestQueue;
import com.fincoapps.servizone.activities.BaseActivity;
import com.fincoapps.servizone.adapters.ReviewsAdapter;
import com.fincoapps.servizone.models.ReviewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.out;

public class ReviewsActivity extends BaseActivity {

    RequestQueue queue;

    ListView listview;
    ListView mlistview;
    ReviewsAdapter adapter;
    private AlertDialog alertDialog;
    public Type collectionType;

    List<ReviewModel.Data> feedsList = new ArrayList<>();
    ReviewModel feedsModel;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private TextView textView;
    String expertID;

    @BindView(R.id.txtLoadMore)
    TextView txtLoadMore;
    private ReviewModel reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
ButterKnife.bind(this);
        //        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        listview = (ListView) findViewById(R.id.reviewlist);
        adapter = new ReviewsAdapter(feedsList, this);
        listview.setAdapter(adapter);
        //        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //            @Override
        //            public void onRefresh() {LayoutInflater inflater = LayoutInflater.from(getContext());
        //                getReviewsFromServer();
        //            }
        //        });
        //        textView = (TextView) findViewById(R.id.noreviewyet);

        Intent i = getIntent();
        expertID = i.getStringExtra("expert_id");


        getReviewsFromServer(com.fincoapps.servizone.utils.Request.api + "/reviews?");
    }


    @OnClick(R.id.txtLoadMore)
    void onLoadMoreClick(View v){
        getReviewsFromServer(reviews.next_page_url+"&");
    }


    public void getReviewsFromServer(String url) {
        loader.show();
        Form form = new Form()
                .add("expert_id", expertID);
        out.println(url+form.toString());
        Bridge.get(url+form.toString())
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                        loader.hide();
                        if (e != null) {
                            int reason = e.reason();
                            out.println("================ ERROR ================");

                            switch (e.reason()) {
                                case BridgeException.REASON_REQUEST_CANCELLED: {
                                    notification.setMessage("Request was canceled \n Retry or Check your Connection");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_TIMEOUT: {
                                    notification.setMessage("Network timed out, try again");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_FAILED: {
                                    notification.setMessage("Network Error \n request failed, try again");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_RESPONSE_UNSUCCESSFUL: {
                                    notification.setMessage("Server error, try again later");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_RESPONSE_UNPARSEABLE: {
                                    notification.setMessage("Server error, try again later");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_RESPONSE_IOERROR: {
                                    notification.setMessage("Server error try again later");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_RESPONSE_VALIDATOR_FALSE:
                                case BridgeException.REASON_RESPONSE_VALIDATOR_ERROR:
                                    notification.setMessage("Invalid details, check your details");
                                    notification.setAnchor(listview);
                                    notification.show();
                                    break;
                            }

                        } else {
                            out.println(response + "00000000000000000");
                            reviews = new ReviewModel();
                                    reviews = gson.fromJson(response.asString(), ReviewModel.class);
                            feedsList.addAll(reviews.data);
                            adapter.notifyDataSetChanged();

                            if(reviews.next_page_url == null){
                                txtLoadMore.setVisibility(View.GONE);
                            }
                            else {
                                txtLoadMore.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
}