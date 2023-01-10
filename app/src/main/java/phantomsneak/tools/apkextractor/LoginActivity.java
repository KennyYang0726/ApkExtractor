package phantomsneak.tools.apkextractor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private String languages;

    public static String hint_title, hint_message, hint_cancel, hint_ok;

    public static String yt, more_apps, feedback, about;

    public static String EXTRA_SUBJECT, createChooser;
    
    public static String dialog_message;
    public static String exit_text;
    
    public static String app_exist, app_delete;
    //
    public static String info_message_packagename, info_message_ver, info_message_size, info_message_installation_time, info_message_last_update_time, info_message_installation_source, info_extract_btn, other_installation_source;

    public static String extracting, extrct_success_title, extrct_success_message, extrct_success_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        languages = Locale.getDefault().toLanguageTag();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
                LoginActivity.this.overridePendingTransition(0, 0);
                if (languages.contains("zh")){ //中文
                    hint_title = "提示";
                    hint_message = "為了能提取出Apk，安卓11以上須請求「所有檔案存取權」，點擊 去授權 將跳轉至設定畫面";
                    hint_cancel = "取消";
                    hint_ok = "去授權";
                    yt = "Youtube 頻道";
                    more_apps = "更多 Apps";
                    feedback = "回饋信";
                    about = "關於";
                    EXTRA_SUBJECT = "關於 ApkExtractor 我想說...";
                    createChooser = "請選擇 Gmail 寄送回饋";
                    dialog_message = "提取已安裝的App，支援提取Apks\n開發者：幻影潛行\nApp版本：";
                    exit_text = "再按一次退出應用";
                    app_exist = "用戶應用";
                    app_delete = "系統應用";
                    //
                    info_message_packagename = "包名：";
                    info_message_ver = "版本名稱：";
                    info_message_size = "安裝包大小：";
                    info_extract_btn = "提取";
                    info_message_installation_time = "安裝時間：";
                    info_message_last_update_time = "最後更新時間：";
                    info_message_installation_source = "安裝來源：";
                    other_installation_source = "第三方套件安裝程式";
                    //
                    extracting = "提取中...";
                    extrct_success_title = "提示";
                    extrct_success_message = "文件已保存至：";
                    extrct_success_btn = "關閉";

                } else if (languages.contains("ja")){ //日文
                    hint_title = "ヒント";
                    hint_message = "Apk を抽出できるようにするために、Android 11 以降では「すべてのファイル アクセス権」を要求する必要があります。クリックして承認すると、設定画面にジャンプします";
                    hint_cancel = "取消";
                    hint_ok = "許されました";
                    yt = "YTチャンネル";
                    more_apps = "より多くのApps";
                    feedback = "フィードバック メール";
                    about = "だいたい";
                    EXTRA_SUBJECT = "ApkExtractor について、私は言いたい...";
                    createChooser = "フィードバックを送信するには Gmail を選択してください";
                    dialog_message = "Apk を抽出し、Apks の抽出をサポートします\n開発者：幻影潛行\nApp version：";
                    exit_text = "もう一度押すと終了します";
                    app_exist = "ユーザーのApp";
                    app_delete = "システムのApp";
                    //
                    info_message_packagename = "パッケージ名：";
                    info_message_ver = "Appバージョン：";
                    info_message_size = "Appサイズ：";
                    info_extract_btn = "エキス";
                    info_message_installation_time = "取り付け時間：";
                    info_message_last_update_time = "最終更新時間：";
                    info_message_installation_source = "インストール ソース：";
                    other_installation_source = "サードパーティのパッケージ インストーラー";
                    //
                    extracting = "抽出しています...";
                    extrct_success_title = "ヒント";
                    extrct_success_message = "ファイルの保存先：";
                    extrct_success_btn = "OK";

                } else{ //英文
                    hint_title = "Info";
                    hint_message = "In order to extract apk, please manually grant \"All Files Access\" permission, click OK to enter the setting interface.";
                    hint_cancel = "cancel";
                    hint_ok = "OK";
                    yt = "Youtube Channel";
                    more_apps = "More Apps";
                    feedback = "FeedBack Mail";
                    about = "About";
                    EXTRA_SUBJECT = "About ApkExtractor, I wanna say...";
                    createChooser = "Please choose Gmail to send feedback";
                    dialog_message = "Extract apps, support extracting Apks.\nDeveloper by: Phantom Sneak\nApp version:";
                    exit_text = "Press again to exit";
                    app_exist = "User App";
                    app_delete = "System App";
                    //
                    info_message_packagename = "PackageName: ";
                    info_message_ver = "App Version: ";
                    info_message_size = "Apk size: ";
                    info_extract_btn = "Extract";
                    info_message_installation_time = "Installation Time: ";
                    info_message_last_update_time = "Last Updated: ";
                    info_message_installation_source = "Update Source: ";
                    other_installation_source = "Third-Party Package Installer";
                    //
                    extracting = "Extracting...";
                    extrct_success_title = "Info";
                    extrct_success_message = "The file has been saved to: ";
                    extrct_success_btn = "Close";

                }
            }
        }, 2000);
    }
}