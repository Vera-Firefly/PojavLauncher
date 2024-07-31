package net.kdt.pojavlaunch.profiles;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.graphics.ColorUtils;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.fragments.ProfileEditorFragment;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.spse.extended_view.ExtendedTextView;

/*
 * Adapter for listing launcher profiles in a Spinner
 */
public class ProfileAdapter extends BaseAdapter {
    private Map<String, MinecraftProfile> mProfiles;
    private final MinecraftProfile dummy = new MinecraftProfile();
    private List<String> mProfileList;
    private ProfileAdapterExtra[] mExtraEntires;
    private OnClick onClick;
    private ListView listView;

    public ProfileAdapter(ProfileAdapterExtra[] extraEntries) {
        reloadProfiles(extraEntries);
    }
    /*
     * Gets how much profiles are loaded in the adapter right now
     * @returns loaded profile count
     */
    @Override
    public int getCount() {
        return mProfileList.size() + mExtraEntires.length;
    }
    /*
     * Gets the profile at a given index
     * @param position index to retreive
     * @returns MinecraftProfile name or null
     */
    @Override
    public Object getItem(int position) {
        int profileListSize = mProfileList.size();
        int extraPosition = position - profileListSize;
        if(position < profileListSize){
            String profileName = mProfileList.get(position);
            if(mProfiles.containsKey(profileName)) return profileName;
        }else if(extraPosition >= 0 && extraPosition < mExtraEntires.length) {
            return mExtraEntires[extraPosition];
        }
        return null;
    }



    public int resolveProfileIndex(String name) {
        return mProfileList.indexOf(name);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        mProfiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        mProfileList = new ArrayList<>(Arrays.asList(mProfiles.keySet().toArray(new String[0])));
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_version_profile_layout, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.name);
            holder.edit = convertView.findViewById(R.id.edit);
            holder.delete = convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        View finalConvertView = convertView;
        convertView.setOnClickListener(v -> {
            listView.performItemClick(finalConvertView, position, getItemId(position));
        });
        setView(holder.textView, getItem(position), true);
        if (getItem(position) instanceof ProfileAdapterExtra) {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        holder.edit.setOnClickListener(v -> onClick.onClick(position));
        holder.delete.setOnClickListener(v -> {
            if (LauncherProfiles.mainProfileJson.profiles.size() > 1) {
                String key = getItem(position).toString();
                ProfileIconCache.dropIcon(key);
                LauncherProfiles.mainProfileJson.profiles.remove(key);
                LauncherProfiles.write();
                ExtraCore.setValue(ExtraConstants.REFRESH_VERSION_SPINNER, ProfileEditorFragment.DELETED_PROFILE);
            }
        });
        return convertView;
    }

    public void setViewProfile(View v, String nm, boolean displaySelection) {
        ExtendedTextView extendedTextView = (ExtendedTextView) v;

        MinecraftProfile minecraftProfile = mProfiles.get(nm);
        if(minecraftProfile == null) minecraftProfile = dummy;
        Drawable cachedIcon = ProfileIconCache.fetchIcon(v.getResources(), nm, minecraftProfile.icon);
        extendedTextView.setCompoundDrawablesRelative(cachedIcon, null, extendedTextView.getCompoundsDrawables()[2], null);

        if(Tools.isValidString(minecraftProfile.name))
            extendedTextView.setText(minecraftProfile.name);
        else
            extendedTextView.setText(R.string.unnamed);

        if(minecraftProfile.lastVersionId != null){
            if(minecraftProfile.lastVersionId.equalsIgnoreCase("latest-release")){
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), v.getContext().getText(R.string.profiles_latest_release)));
            } else if(minecraftProfile.lastVersionId.equalsIgnoreCase("latest-snapshot")){
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), v.getContext().getText(R.string.profiles_latest_snapshot)));
            } else {
                extendedTextView.setText( String.format("%s - %s", extendedTextView.getText(), minecraftProfile.lastVersionId));
            }

        } else extendedTextView.setText(extendedTextView.getText());

        // Set selected background if needed
        if(displaySelection){
            String selectedProfile = LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,"");
            extendedTextView.setBackgroundColor(selectedProfile.equals(nm) ? ColorUtils.setAlphaComponent(Color.WHITE,60) : Color.TRANSPARENT);
        }else extendedTextView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setViewExtra(View v, ProfileAdapterExtra extra) {
        ExtendedTextView extendedTextView = (ExtendedTextView) v;
        extendedTextView.setCompoundDrawablesRelative(extra.icon, null, extendedTextView.getCompoundsDrawables()[2], null);
        extendedTextView.setText(extra.name);
        extendedTextView.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setView(View v, Object object, boolean displaySelection) {
        if(object instanceof String) {
            setViewProfile(v, (String) object, displaySelection);
        }else if(object instanceof ProfileAdapterExtra) {
            setViewExtra(v, (ProfileAdapterExtra) object);
        }
    }

    /** Reload profiles from the file */
    public void reloadProfiles(){
        LauncherProfiles.load();
        mProfiles = new HashMap<>(LauncherProfiles.mainProfileJson.profiles);
        mProfileList = new ArrayList<>(Arrays.asList(mProfiles.keySet().toArray(new String[0])));
        notifyDataSetChanged();
    }

    /** Reload profiles from the file, with additional extra entries */
    public void reloadProfiles(ProfileAdapterExtra[] extraEntries) {
        if(extraEntries == null) mExtraEntires = new ProfileAdapterExtra[0];
        else mExtraEntires = extraEntries;
        this.reloadProfiles();
    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public interface OnClick {
        void onClick(int position);
    }

    private class ViewHolder {
        public ExtendedTextView textView;
        public Button edit;
        public Button delete;
    }
}
