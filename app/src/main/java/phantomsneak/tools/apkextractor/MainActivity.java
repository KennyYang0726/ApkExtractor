package phantomsneak.tools.apkextractor;

import android.Manifest;
import android.animation.*;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.provider.Settings;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.LinearLayout;
import androidx.annotation.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;


public class MainActivity extends AppCompatActivity {

	private Toolbar toolbar;
	private TabLayout tabLayout;
	private DrawerLayout drawerLayout;
	private ViewPager viewPager;
	private LinearLayout linearLayout;
	private viewPagerAdapter viewPagerAdapter;
	private AlertDialog.Builder about;
	private AlertDialog.Builder dialog;
	//private EditText editText;

	private String app_version;
	private boolean quit;

	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
				||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		viewPager = findViewById(R.id.ViewPager);
		viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager());
		tabLayout = findViewById(R.id.TabLayout);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.setAdapter(viewPagerAdapter);
		linearLayout = findViewById(R.id.bannerAd);
		drawerLayout = findViewById(R.id._drawer);
		ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
		drawerLayout.addDrawerListener(toogle);
		toogle.syncState();
		//editText = findViewById(R.id.EditText);
		dialog = new AlertDialog.Builder(this);


		about = new AlertDialog.Builder(this, R.style.Dialog);
/*
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Fragment_List fragment_list = new Fragment_List();
				Bundle bundle0 = new Bundle();
				bundle0.putString("edttext", editText.getText().toString());
				fragment_list.setArguments(bundle0);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
 */
		
	}
	
	private void initializeLogic() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				ManageAllFiles();
			}
		}
		try {
			android.content.pm.PackageInfo pinfo = getPackageManager().getPackageInfo("phantomsneak.tools.apkextractor", android.content.pm.PackageManager.GET_ACTIVITIES);
			app_version = pinfo.versionName;
		}catch (Exception e){

		}
		AdView banner1 = new AdView(MainActivity.this);
		banner1.setAdSize(AdSize.BANNER);
		banner1.setAdUnitId("ca-app-pub-3897977034034314/1209563365");
		AdRequest arbanner1 = new AdRequest.Builder().build();
		banner1.loadAd(arbanner1);
		LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		linearLayout.addView(banner1,p1);
		//BroadCast();
	}

	@Override
	public void onBackPressed() {
		if (quit == false) {//詢問退出程序
			showmessage(LoginActivity.exit_text);
			new Timer(true).schedule(new TimerTask() {//啟動定時任務
				@Override
				public void run() {
					quit = false;//重置退出標示
				}
			}, 2000);//2秒後執行run()方法
			quit = true;
		} else {//確認退出應用
			super.onBackPressed();
			finishAffinity();
		}
	}


	/**使選項內Icon與文字並存*/
	private CharSequence menuIconWithText(Drawable r, String title) {
		r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
		SpannableString sb = new SpannableString("    " + title);
		ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
		sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sb;
	}
	/**程式中新增MenuItem選項*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**itemId為稍後判斷點擊事件要用的*/
		menu.add(0,0,0,menuIconWithText(getDrawable(R.drawable.youtube),LoginActivity.yt));
		menu.add(0,1,1,menuIconWithText(getDrawable(R.drawable.more_apps),LoginActivity.more_apps));
		//menu.add(0,1,1,LoginActivity.more_apps);
		menu.add(0,2,2,menuIconWithText(getDrawable(R.drawable.gmail),LoginActivity.feedback));
		menu.add(0,3,3,menuIconWithText(getDrawable(R.drawable.about),LoginActivity.about));
		return super.onCreateOptionsMenu(menu);
	}
	/**此處為設置點擊事件*/
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		/**取得Item的ItemId，判斷點擊到哪個元件*/
		switch (item.getItemId()){
			case 0:
				Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCrBaHnJwilrSZ87hGU4AVfg"));
				startActivity(browserintent);
				break;
			case 1:
				showmessage("not finished yet");
				break;
			case 2:
				@SuppressLint("IntentReset")
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"yangkenny0726@gmail.com"});
				intent.putExtra(Intent.EXTRA_SUBJECT, LoginActivity.EXTRA_SUBJECT);
				//intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
				startActivity(Intent.createChooser(intent, LoginActivity.createChooser));
				break;
			case 3:
				about.setTitle("ApkExtractor");
				about.setIcon(R.drawable.app_icon);
				about.setMessage(LoginActivity.dialog_message + app_version);
				about.setPositiveButton("OK", null);
				about.create().show();
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void ManageAllFiles() {
		dialog.setTitle(LoginActivity.hint_title);
		dialog.setMessage(LoginActivity.hint_message);
		dialog.setCancelable(false);
		dialog.setNegativeButton(LoginActivity.hint_cancel, null);
		dialog.setPositiveButton(LoginActivity.hint_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				Intent allfile = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
				allfile.setData(Uri.parse("package:" + getPackageName()));
				startActivity(allfile);
			}
		});
		dialog.create().show();
	}

	private void showmessage(String s){
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

}
