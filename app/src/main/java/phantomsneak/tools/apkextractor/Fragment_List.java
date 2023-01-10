package phantomsneak.tools.apkextractor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.outputstream.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Fragment_List extends Fragment {

    private ListView listView;
    List<MainData> dataList = new ArrayList<>();
    List<MainData> dataList2 = new ArrayList<>();
    private ArrayList<String> get_apks = new ArrayList<>();
    private ArrayList<String> get_apks2 = new ArrayList<>();
    private ProgressDialog prog;

    private String data;
    //private String edittext;

    private boolean apk; //true 即 apk，false 即 apks
    private String app_name;
    private String app_package_name;
    private String app_ver;
    private String apk_path;
    private long apk_size;
    private String apk_size_R;
    private String installation_source;
    private String installation_time;
    private String last_update_time;
    private double B = 1024;
    private double K = B*B;
    private double M = K*B;
    private double G = M*B;
    private int cnt;
    private boolean InerstitialLoaded;
    private InterstitialAd mInterstitialAd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.listfragment, container, false);
        listView = v.findViewById(R.id.listview);
        LoadInerstitialAd();
        //edittext = getArguments().getString("edttext");
        data = getArguments().getString("key");

        setListView();

        return v;
    }

    private void setListView(){
        if (data.contains("1")){ //用戶應用
            PackageManager manager = getActivity().getPackageManager();
            List<ApplicationInfo> infoList = manager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo info : infoList){

                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    //非系統應用
                    MainData data = new MainData();
                    data.setName(info.loadLabel(manager).toString());
                    data.setPackagename(info.packageName);
                    data.setLogo(info.loadIcon(manager));
                    dataList.add(data);
                }
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> param1, View param2, int param3, long param4) {
                    final int position = param3;
                    MainData data = dataList.get(position);
                    app_package_name = data.getPackagename();
                    app_name = data.getName();

                    try {
                        String source = getActivity().getPackageManager().getInstallerPackageName(app_package_name);
                        String installation_source_packagename = source;
                        android.content.pm.PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(app_package_name, PackageManager.GET_ACTIVITIES);
                        app_ver = pinfo.versionName;
                        //有些可能是apks
                        String apk_located = pinfo.applicationInfo.sourceDir;
                        int apk_index = apk_located.lastIndexOf("/");
                        apk_path = apk_located.substring(0, apk_index+1);
                        get_apks.clear();
                        get_apks2.clear();
                        int cnt = 0;
                        FileUtil.listDir(apk_path, get_apks);
                        FileUtil.listDir(apk_path, get_apks2);
                        for (int i = 0; i < get_apks2.size(); i++){
                            if (!get_apks2.get(i).endsWith(".apk")){
                                get_apks.remove(cnt);
                            } else{
                                cnt++;
                            }
                        }
                        if (get_apks.size() != 1){ //apks
                            apk = false;
                        } else { //apk
                            apk = true;
                        }

                        java.io.File file = new java.io.File(apk_located);
                        apk_size = file.length();
                        if (apk_size < B){
                            apk_size_R = String.valueOf(apk_size)+ " B";
                        } else if(apk_size < K){
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/B) + " K";
                        } else if(apk_size < M){
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/K)+ " M";
                        } else {
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/M)+ " G";
                        }

                        long firstInstalledTime = pinfo.firstInstallTime;
                        long lastInstalledTime = pinfo.lastUpdateTime;
                        Calendar calendar = Calendar.getInstance();
                        TimeZone tz = TimeZone.getDefault();
                        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        java.util.Date first_install = new java.util.Date((long)firstInstalledTime);
                        java.util.Date last_update = new java.util.Date((long)lastInstalledTime);
                        installation_time = sdf.format(first_install);
                        last_update_time = sdf.format(last_update);
                        //將 installation_source 從包名轉為應用名
                        PackageManager packageManager= getActivity().getApplicationContext().getPackageManager();
                        //可能是其他安裝程式 ex MT, SAI, ADB...
                        try{
                            installation_source = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(installation_source_packagename, PackageManager.GET_META_DATA));
                        } catch (Exception e){
                            installation_source = LoginActivity.other_installation_source;
                        }
                    } catch (PackageManager.NameNotFoundException e) {

                    }

                    AlertDialog info = new AlertDialog.Builder(getActivity())
                            .create();
                    info.setIcon(data.getLogo()) ;
                    info.setTitle(app_name);
                    info.setMessage(
                            LoginActivity.info_message_packagename + app_package_name + "\n"
                            + LoginActivity.info_message_ver + app_ver + "\n"
                            + LoginActivity.info_message_size + apk_size_R + "\n"
                            + LoginActivity.info_message_installation_time + installation_time + "\n"
                            + LoginActivity.info_message_last_update_time + last_update_time + "\n"
                            + LoginActivity.info_message_installation_source + installation_source + "\n");
                    info.setButton(LoginActivity.info_extract_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //版本名稱包含"/"
                            if (app_ver.contains("/")){
                                app_ver = app_ver.replaceAll("/","_");
                            }
                            //開始
                            cnt++;
                            if (cnt % 3 == 0){ //Ad
                                if (InerstitialLoaded){
                                    mInterstitialAd.show(getActivity());
                                    ReloadInterstitialAd();
                                }
                            }
                            ProgressBar_Show(LoginActivity.extracting);
                            new BackgroundTaskClass(getActivity()){
                                @Override
                                public void doInBackground() {
                                    Looper.prepare();
                                    if(apk) { //提取 Apk
                                        try {
                                            FileUtil.copyFile(getActivity().getPackageManager().getApplicationInfo(app_package_name, 0).publicSourceDir, "/storage/emulated/0/" + app_name + "_" + app_ver + ".apk");
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    } else { //apks
                                        FileUtil.makeDir("/storage/emulated/0/AAAAAAA幫我開直播tmp");
                                        for (int i = 0; i < get_apks.size(); i++) {
                                            FileUtil.copyFile(get_apks.get(i), "/storage/emulated/0/AAAAAAA幫我開直播tmp/" + Uri.parse(get_apks.get(i)).getLastPathSegment());
                                        }
                                        //zip4j
                                        get_apks2.clear();
                                        FileUtil.listDir("/storage/emulated/0/AAAAAAA幫我開直播tmp", get_apks2);
                                        for (String vb : get_apks2) {
                                            try {
                                                try {
                                                    java.lang.Thread.sleep(50);
                                                } catch (java.lang.InterruptedException exc) {
                                                }
                                                net.lingala.zip4j.model.ZipParameters zipParameters = new net.lingala.zip4j.model.ZipParameters();
                                                zipParameters.setCompressionLevel(CompressionLevel.FAST);
                                                zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                                                net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile("/storage/emulated/0/" + app_name + "_" + app_ver + ".apks");
                                                if (FileUtil.isDirectory(vb)) {
                                                    zipFile.addFolder(new java.io.File(vb), zipParameters);
                                                } else {
                                                    zipFile.addFile(new java.io.File(vb), zipParameters);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        FileUtil.deleteFile("/storage/emulated/0/AAAAAAA幫我開直播tmp");
                                    }
                                }
                                @Override
                                public void onPostExecute(){
                                    //完成
                                    ProgressBar_Dismiss();
                                    if(apk) {
                                        AlertDialog.Builder extrct_success = new AlertDialog.Builder(getActivity());
                                        extrct_success.setTitle(LoginActivity.extrct_success_title)
                                                .setMessage(LoginActivity.extrct_success_message + "/storage/emulated/0/" + app_name + "_" + app_ver + ".apk")
                                                .setCancelable(true)
                                                .setPositiveButton(LoginActivity.extrct_success_btn, null);
                                        extrct_success.create().show();

                                    } else {
                                        AlertDialog.Builder extrct_success = new AlertDialog.Builder(getActivity());
                                        extrct_success.setTitle(LoginActivity.extrct_success_title)
                                                .setMessage(LoginActivity.extrct_success_message + "/storage/emulated/0/" + app_name + "_" + app_ver + ".apks")
                                                .setCancelable(true)
                                                .setPositiveButton(LoginActivity.extrct_success_btn, null);
                                        extrct_success.create().show();
                                    }
                                }
                            }.execute();
                            //結束
                        }
                    });
                    info.show();

                }
            });

            listView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return dataList.size();
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = getLayoutInflater().inflate(R.layout.listitem, null);

                    MainData data = dataList.get(position);
                    ImageView imageView = view.findViewById(R.id.image_logo);
                    TextView appname = view.findViewById(R.id.name);
                    TextView pmname = view.findViewById(R.id.pmname);

                    imageView.setImageDrawable(data.getLogo());
                    appname.setText(data.getName());
                    pmname.setText(data.getPackagename());

                    return view;
                }
            });

        } else {
            PackageManager manager = getActivity().getPackageManager();
            List<ApplicationInfo> infoList = manager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo info : infoList){

                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1){
                    //系統應用
                    MainData data2 = new MainData();
                    data2.setName(info.loadLabel(manager).toString());
                    data2.setPackagename(info.packageName);
                    data2.setLogo(info.loadIcon(manager));
                    dataList2.add(data2);
                }
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> param1, View param2, int param3, long param4) {
                    final int position = param3;
                    MainData data2 = dataList2.get(position);
                    app_package_name = data2.getPackagename();
                    app_name = data2.getName();

                    try {
                        String source = getActivity().getPackageManager().getInstallerPackageName(app_package_name);
                        String installation_source_packagename = source;
                        android.content.pm.PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(app_package_name, PackageManager.GET_ACTIVITIES);
                        app_ver = pinfo.versionName;
                        //有些可能是apks
                        String apk_located = pinfo.applicationInfo.sourceDir;
                        int apk_index = apk_located.lastIndexOf("/");
                        apk_path = apk_located.substring(0, apk_index+1);
                        get_apks.clear();
                        get_apks2.clear();
                        int cnt = 0;
                        FileUtil.listDir(apk_path, get_apks);
                        FileUtil.listDir(apk_path, get_apks2);
                        for (int i = 0; i < get_apks2.size(); i++){
                            if (!get_apks2.get(i).endsWith(".apk")){
                                get_apks.remove(cnt);
                            } else{
                                cnt++;
                            }
                        }
                        if (get_apks.size() != 1){ //apks
                            apk = false;
                        } else { //apk
                            apk = true;
                        }

                        java.io.File file = new java.io.File(apk_located);
                        apk_size = file.length();
                        if (apk_size < B){
                            apk_size_R = String.valueOf(apk_size)+ " B";
                        } else if(apk_size < K){
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/B) + " K";
                        } else if(apk_size < M){
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/K)+ " M";
                        } else {
                            apk_size_R = new DecimalFormat("0.00").format(apk_size/M)+ " G";
                        }

                        long firstInstalledTime = pinfo.firstInstallTime;
                        long lastInstalledTime = pinfo.lastUpdateTime;
                        Calendar calendar = Calendar.getInstance();
                        TimeZone tz = TimeZone.getDefault();
                        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        java.util.Date first_install = new java.util.Date((long)firstInstalledTime);
                        java.util.Date last_update = new java.util.Date((long)lastInstalledTime);
                        installation_time = sdf.format(first_install);
                        last_update_time = sdf.format(last_update);
                        //將 installation_source 從包名轉為應用名
                        PackageManager packageManager= getActivity().getApplicationContext().getPackageManager();
                        //可能是其他安裝程式 ex MT, SAI, ADB...
                        try{
                            installation_source = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(installation_source_packagename, PackageManager.GET_META_DATA));
                        } catch (Exception e){
                            installation_source = LoginActivity.other_installation_source;
                        }
                    } catch (PackageManager.NameNotFoundException e) {

                    }

                    AlertDialog info = new AlertDialog.Builder(getActivity())
                            .create();
                    info.setIcon(data2.getLogo()) ;
                    info.setTitle(app_name);
                    info.setMessage(
                            LoginActivity.info_message_packagename + app_package_name + "\n"
                                    + LoginActivity.info_message_ver + app_ver + "\n"
                                    + LoginActivity.info_message_size + apk_size_R + "\n"
                                    + LoginActivity.info_message_installation_time + installation_time + "\n"
                                    + LoginActivity.info_message_last_update_time + last_update_time + "\n"
                                    + LoginActivity.info_message_installation_source + installation_source + "\n");
                    info.setButton(LoginActivity.info_extract_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //版本名稱包含"/"
                            if (app_ver.contains("/")){
                                app_ver = app_ver.replaceAll("/","_");
                            }
                            //開始
                            cnt++;
                            if (cnt % 3 == 0){ //Ad
                                if (InerstitialLoaded){
                                    mInterstitialAd.show(getActivity());
                                    ReloadInterstitialAd();
                                }
                            }
                            ProgressBar_Show(LoginActivity.extracting);
                            new BackgroundTaskClass(getActivity()){
                                @Override
                                public void doInBackground() {
                                    Looper.prepare();
                                    if(apk) { //提取 Apk
                                        try {
                                            FileUtil.copyFile(getActivity().getPackageManager().getApplicationInfo(app_package_name, 0).publicSourceDir, "/storage/emulated/0/" + app_name + "_" + app_ver + ".apk");
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    } else { //apks
                                        FileUtil.makeDir("/storage/emulated/0/AAAAAAA幫我開直播tmp");
                                        for (int i = 0; i < get_apks.size(); i++) {
                                            FileUtil.copyFile(get_apks.get(i), "/storage/emulated/0/AAAAAAA幫我開直播tmp/" + Uri.parse(get_apks.get(i)).getLastPathSegment());
                                        }
                                        //zip4j
                                        get_apks2.clear();
                                        FileUtil.listDir("/storage/emulated/0/AAAAAAA幫我開直播tmp", get_apks2);
                                        for (String vb : get_apks2) {
                                            try {
                                                try {
                                                    java.lang.Thread.sleep(50);
                                                } catch (java.lang.InterruptedException exc) {
                                                }
                                                net.lingala.zip4j.model.ZipParameters zipParameters = new net.lingala.zip4j.model.ZipParameters();
                                                zipParameters.setCompressionLevel(CompressionLevel.FAST);
                                                zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
                                                net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile("/storage/emulated/0/" + app_name + "_" + app_ver + ".apks");
                                                if (FileUtil.isDirectory(vb)) {
                                                    zipFile.addFolder(new java.io.File(vb), zipParameters);
                                                } else {
                                                    zipFile.addFile(new java.io.File(vb), zipParameters);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        FileUtil.deleteFile("/storage/emulated/0/AAAAAAA幫我開直播tmp");
                                    }
                                }
                                @Override
                                public void onPostExecute(){
                                    //完成
                                    ProgressBar_Dismiss();
                                    if(apk) {
                                        AlertDialog.Builder extrct_success = new AlertDialog.Builder(getActivity());
                                        extrct_success.setTitle(LoginActivity.extrct_success_title)
                                                .setMessage(LoginActivity.extrct_success_message + "/storage/emulated/0/" + app_name + "_" + app_ver + ".apk")
                                                .setCancelable(true)
                                                .setPositiveButton(LoginActivity.extrct_success_btn, null);
                                        extrct_success.create().show();

                                    } else {
                                        AlertDialog.Builder extrct_success = new AlertDialog.Builder(getActivity());
                                        extrct_success.setTitle(LoginActivity.extrct_success_title)
                                                .setMessage(LoginActivity.extrct_success_message + "/storage/emulated/0/" + app_name + "_" + app_ver + ".apks")
                                                .setCancelable(true)
                                                .setPositiveButton(LoginActivity.extrct_success_btn, null);
                                        extrct_success.create().show();
                                    }
                                }
                            }.execute();
                            //結束
                        }
                    });
                    info.show();

                }
            });

            listView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return dataList2.size();
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = getLayoutInflater().inflate(R.layout.listitem, null);

                    MainData data2 = dataList2.get(position);
                    ImageView imageView = view.findViewById(R.id.image_logo);
                    TextView appname = view.findViewById(R.id.name);
                    TextView pmname = view.findViewById(R.id.pmname);

                    imageView.setImageDrawable(data2.getLogo());
                    appname.setText(data2.getName());
                    pmname.setText(data2.getPackagename());

                    return view;
                }
            });
        }
    }

    private void refresh(){

    }


    public static abstract class BackgroundTaskClass {

        private Activity activity;
        public BackgroundTaskClass(Context activity) {
            this.activity = (Activity) activity;
        }

        private void startBackground() {
            new Thread(new Runnable() {
                public void run() {

                    doInBackground();
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }
        public void execute(){
            startBackground();
        }

        public abstract void doInBackground();
        public abstract void onPostExecute();

    }

    private void ProgressBar_Show(String title){
        prog = new ProgressDialog(getActivity());
        prog.setMax(100);
        prog.setMessage(title);
        prog.setIndeterminate(true);
        prog.setCancelable(false);
        prog.setCanceledOnTouchOutside(false);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog.show();
    }
    private void ProgressBar_Dismiss(){
        if (prog != null){
            prog.dismiss();
        }
    }

    private void LoadInerstitialAd(){
       AdRequest inreq = new AdRequest.Builder().build();
        InterstitialAd.load(getActivity(), "ca-app-pub-3897977034034314/2804897827", inreq, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                InerstitialLoaded = true;
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                InerstitialLoaded = false;
                LoadInerstitialAd();
            }
        });
    }

    private void ReloadInterstitialAd() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                //
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                //Toast.makeText(getActivity().getApplicationContext(), "Ad Dismiss", Toast.LENGTH_SHORT).show();
                LoadInerstitialAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                //
            }

            @Override
            public void onAdImpression() {
                //
            }

            @Override
            public void onAdShowedFullScreenContent() {
                //
            }
        });
    }
}