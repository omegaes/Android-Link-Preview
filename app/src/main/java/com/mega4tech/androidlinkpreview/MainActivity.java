package com.mega4tech.androidlinkpreview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mega4tech.linkpreview.GetLinkPreviewListener;
import com.mega4tech.linkpreview.LinkPreview;
import com.mega4tech.linkpreview.LinkUtil;

public class MainActivity extends AppCompatActivity {

    private LinearLayout activityMain;
    private EditText linkEt;
    private Button linkFetchBtn;
    private RelativeLayout previewGroup;
    private ImageView imgPreviewIv;
    private TextView titleTv;
    private TextView descTv;
    private TextView siteTv;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        linkFetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                previewGroup.setVisibility(View.GONE);
                LinkUtil.getInstance().getLinkPreview(MainActivity.this, linkEt.getText().toString(), new GetLinkPreviewListener() {
                    @Override
                    public void onSuccess(final LinkPreview link) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                previewGroup.setVisibility(View.VISIBLE);
                                titleTv.setText(link.getTitle() != null ? link.getTitle() : "" );

                                descTv.setText(link.getDescription() != null ? link.getDescription() : "" );
                                siteTv.setText(link.getSiteName() != null ? link.getSiteName() : "" );
                                previewGroup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getLink()));
                                        startActivity(browserIntent);
                                    }
                                });
                                if(link.getImageFile() != null)
                                    Glide.with(MainActivity.this).load(link.getImageFile()).into(imgPreviewIv);
                                else
                                    Glide.with(MainActivity.this).load(R.mipmap.ic_launcher).into(imgPreviewIv);
                            }
                        });

                    }

                    @Override
                    public void onFailed(final Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                previewGroup.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });
    }

    private void initView() {
        activityMain = (LinearLayout) findViewById(R.id.activity_main);
        linkEt = (EditText) findViewById(R.id.link_et);
        linkFetchBtn = (Button) findViewById(R.id.link_fetch_btn);
        previewGroup = (RelativeLayout) findViewById(R.id.preview_group);
        imgPreviewIv = (ImageView) findViewById(R.id.img_preview_iv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        descTv = (TextView) findViewById(R.id.desc_tv);
        siteTv = (TextView) findViewById(R.id.site_tv);
        progress = (ProgressBar) findViewById(R.id.progress);
    }
}
