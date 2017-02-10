package com.lucentinsight.mclinicplus.activity;



import com.lucentinsight.mclinicplus.MCApplication;
import com.lucentinsight.mclinicplus.R;
import com.lucentinsight.mclinicplus.common.ImageUtil;
import com.lucentinsight.mclinicplus.service.ApplicationService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SplashActivity extends BaseActivity implements ApplicationService.UpdateDBServiceListener{

    private long startTime;

    @InjectView(R.id.splashStory)
    TextView tvSplashStory;

    @InjectView(R.id.logo_full_imageview)
    ImageView ivStoryImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_splash);
        if(getActionBar() != null) {
            getActionBar().hide();
        }
        ButterKnife.inject(this);
        MCApplication application = (MCApplication)getApplication();

        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 300, getResources().getDisplayMetrics());
        int width = getResources().getDisplayMetrics().widthPixels;

        ivStoryImage.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.shutterstock, width, height));
        if(application.isFirstTime()) {

        }
        else{
            tvSplashStory.setText("");
        }
        startTime = System.currentTimeMillis();

//		BackgroundSplashTask task = new BackgroundSplashTask(SplashActivity.this, this);
//		task.execute();
		
//		startLanding();

        //checking db and update database. upload status
        ApplicationService service = new ApplicationService((MCApplication)getApplication());
	    service.updateDB(this);
	}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}


    private void showMainActivity(){
        ((MCApplication)getApplication()).setFirstTime(false);
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onFinishedDBUpdate() {
        MCApplication application = (MCApplication)getApplication();

        if(application.isFirstTime()){
            long processTime = System.currentTimeMillis() - startTime;
            if(processTime >= 12000){
                showMainActivity();
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showMainActivity();
                    }
                }, 12000 - processTime);
            }
        }
        else{
            showMainActivity();
        }

    }

    @Override
    public void onUpdateProgress(int progress) {

    }
}
