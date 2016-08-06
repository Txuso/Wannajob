package com.example.txuso.wannajob.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.txuso.wannajob.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guillermo V2M on 08/03/2016.
 */
public class OverlapMessageActivity extends Activity {

    private final static String ARG_MESSAGE = "message";
    private final static String ARG_IMAGE = "image";
    private final static String ARG_LAYOUT = "layout";
    private final static String ARG_DELAY = "delay";
    public final static int DEFAULT_DELAY = 2;

    @Bind(R.id.iv_overlap_message)
    ImageView ivOverlapMessage;
    @Bind(R.id.tv_overlap_message)
    TextView tvOverlapMessage;
    @Bind(R.id.layout_overlap_message)
    FrameLayout layoutOverlapMessage;

    private String mMessage;
    private int mImageResId;
    private int delaySeconds;
    private int mLayoutResId;

    public static Intent newIntent(Context context, String message, @DrawableRes int imageResId, int delayToClose) {
        return newIntent(context, -1, message, imageResId, delayToClose);
    }

    public static Intent newIntent(Context context, @LayoutRes int layoutResId) {
        return newIntent(context, layoutResId, null, -1, -1);
    }

    private static Intent newIntent(Context context, @LayoutRes int layoutResId, String message, @DrawableRes int imageResId, int delayToClose) {
        Intent intent = new Intent(context, OverlapMessageActivity.class);
        intent.putExtra(ARG_LAYOUT, layoutResId);
        intent.putExtra(ARG_MESSAGE, message);
        intent.putExtra(ARG_IMAGE, imageResId);
        intent.putExtra(ARG_DELAY, delayToClose);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mMessage = getIntent().getExtras().getString(ARG_MESSAGE,"");
            mImageResId = getIntent().getExtras().getInt(ARG_IMAGE, -1);
            mLayoutResId = getIntent().getExtras().getInt(ARG_LAYOUT, -1);
            delaySeconds = getIntent().getExtras().getInt(ARG_DELAY, -1);
        }

        initViews();
        if(delaySeconds>0) initTimer();
    }

    private void initViews(){
        if(mLayoutResId>0){
            setContentView(mLayoutResId);
            try {
                View view = (View) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            }catch(Exception e){
                finish();
            }
        }else {
            setContentView(R.layout.activity_overlap_message);
            ButterKnife.bind(this);
            if (!TextUtils.isEmpty(mMessage)) tvOverlapMessage.setText(mMessage);
            if (mImageResId > 0) ivOverlapMessage.setImageResource(mImageResId);
        }
    }

    private void initTimer(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, delaySeconds*3000);
    }

}