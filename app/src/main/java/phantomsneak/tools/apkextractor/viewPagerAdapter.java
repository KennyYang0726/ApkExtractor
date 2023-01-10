package phantomsneak.tools.apkextractor;

import android.os.Bundle;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewPagerAdapter extends FragmentPagerAdapter {

    public viewPagerAdapter(@NonNull FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment_List fragment_list = new Fragment_List();
        Bundle bundle = new Bundle();
        position = position + 1;
        bundle.putString("key", "" + position);
        fragment_list.setArguments(bundle);
        return fragment_list;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return LoginActivity.app_exist;
            case 1:
                return LoginActivity.app_delete;
        }
        return null;
    }
}
