package com.packruler.carmaintenance.ui.utilities;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.packruler.carmaintenance.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

/**
 * Created by Packruler on 5/28/15.
 */
public class MaterialPopupHandler implements Menu {
    private final String TAG = getClass().getName();

    private AlertDialog dialog;
    private PopupMenu popupMenu;
    private View clickCatch;

    public MaterialPopupHandler(RelativeLayout parent, MaterialEditText materialEditText) {
        Log.v(TAG, "Load");
        clickCatch = LayoutInflater.from(materialEditText.getContext()).inflate(R.layout.dialog_button_overlay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_RIGHT, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_LEFT, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_TOP, materialEditText.getId());
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");
                popupMenu.show();
            }
        };
        clickCatch.setOnClickListener(onClickListener);

        parent.addView(clickCatch, params);

        popupMenu = new PopupMenu(parent.getContext(), materialEditText);
    }

    public void setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener listener) {
        popupMenu.setOnMenuItemClickListener(listener);
    }

    @Override
    public MenuItem add(CharSequence title) {
        return popupMenu.getMenu().add(title);
    }

    @Override
    public MenuItem add(int titleRes) {
        return popupMenu.getMenu().add(titleRes);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return popupMenu.getMenu().add(groupId, itemId, order, title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return popupMenu.getMenu().add(groupId, itemId, order, titleRes);
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        return popupMenu.getMenu().addSubMenu(title);
    }

    @Override
    public SubMenu addSubMenu(int titleRes) {
        return popupMenu.getMenu().addSubMenu(titleRes);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return popupMenu.getMenu().addSubMenu(groupId, itemId, order, title);
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return popupMenu.getMenu().addSubMenu(groupId, itemId, order, titleRes);
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        return popupMenu.getMenu().addIntentOptions(groupId, itemId, order, caller, specifics, intent, flags, outSpecificItems);
    }

    @Override
    public void removeItem(int id) {
        popupMenu.getMenu().removeItem(id);
    }

    @Override
    public void removeGroup(int groupId) {
        popupMenu.getMenu().removeGroup(groupId);
    }

    @Override
    public void clear() {
        popupMenu.getMenu().clear();
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        popupMenu.getMenu().setGroupCheckable(group, checkable, exclusive);
    }

    @Override
    public void setGroupVisible(int group, boolean visible) {
        popupMenu.getMenu().setGroupVisible(group, visible);
    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {
        popupMenu.getMenu().setGroupEnabled(group, enabled);
    }

    @Override
    public boolean hasVisibleItems() {
        return popupMenu.getMenu().hasVisibleItems();
    }

    @Override
    public MenuItem findItem(int id) {
        return popupMenu.getMenu().findItem(id);
    }

    @Override
    public int size() {
        return popupMenu.getMenu().size();
    }

    @Override
    public MenuItem getItem(int index) {
        return popupMenu.getMenu().getItem(index);
    }

    @Override
    public void close() {
        popupMenu.getMenu().close();
    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return popupMenu.getMenu().performShortcut(keyCode, event, flags);
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return popupMenu.getMenu().isShortcutKey(keyCode, event);
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return popupMenu.getMenu().performIdentifierAction(id, flags);
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {
        popupMenu.getMenu().setQwertyMode(isQwerty);
    }

    public void setList(List<String> list) {
        clear();
        addList(list);
    }

    public void setList(int arrayId) {
        for (String item : clickCatch.getContext().getResources().getStringArray(arrayId)) {
            popupMenu.getMenu().add(item);
        }
    }

    public void addList(List<String> list) {
        for (Object item : list) {
            add((CharSequence) item);
        }
    }
}
