package com.packruler.materialdialogbutton;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Packruler on 5/29/15.
 */
public class MaterialDialogButton extends RelativeLayout {
    private final String TAG = getClass().getName();
    private MaterialEditText editText;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ListAdapter adapter;

    public MaterialDialogButton(Context context) {
        this(context, null);
    }

    public MaterialDialogButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        editText = new MaterialEditText(context, attrs);
        this.isInEditMode();
    }

    public MaterialDialogButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        editText = new MaterialEditText(context, attrs, defStyleAttr);
        this.isInEditMode();
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        editText = new MaterialEditText(context, attrs, defStyleAttr);
        this.addView(editText);
        View clickCatch = LayoutInflater.from(context).inflate(R.layout.dialog_click_catch, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, editText.getId());
        params.addRule(RelativeLayout.ALIGN_RIGHT, editText.getId());
        params.addRule(RelativeLayout.ALIGN_LEFT, editText.getId());
        params.addRule(RelativeLayout.ALIGN_TOP, editText.getId());
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");
                if (dialog != null)
                    dialog.show();
            }
        };
        builder = new AlertDialog.Builder(context);
        if (editText.getHint().length() > 0)
            builder.setTitle(editText.getHint());
    }

//    public class DialogBuilder {
//        AlertDialog.Builder builder;
//
//        public DialogBuilder(Context context) {
//            builder = new AlertDialog.Builder(context);
//        }
//
//        public DialogBuilder(Context context, int theme) {
//            builder = new AlertDialog.Builder(context, theme);
//        }
//
//        /**
//         * Returns a {@link Context} with the appropriate theme for dialogs created by this
//         * Builder.
//         * Applications should use this Context for obtaining LayoutInflaters for inflating views
//         * that will be used in the resulting dialogs, as it will cause views to be inflated with
//         * the correct theme.
//         *
//         * @return A Context for built Dialogs.
//         */
//        public Context getContext() {
//            return builder.getContext();
//        }
//
//        /**
//         * Set the title using the given resource id.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setTitle(int titleId) {
//            builder.setTitle(titleId);
//            return this;
//        }
//
//        /**
//         * Set the title displayed in the {@link Dialog}.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setTitle(CharSequence title) {
//            builder.setTitle(title);
//            return this;
//        }
//
//        /**
//         * Set the title using the custom view {@code customTitleView}. The
//         * methods {@link #setTitle(int)} and {@link #setIcon(int)} should be
//         * sufficient for most titles, but this is provided if the title needs
//         * more customization. Using this will replace the title and icon set
//         * via the other methods.
//         *
//         * @param customTitleView
//         *         The custom view to use as the title.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setCustomTitle(View customTitleView) {
//            builder.setCustomTitle(customTitleView);
//            return this;
//        }
//
//        /**
//         * Set the message to display using the given resource id.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setMessage(int messageId) {
//            builder.setMessage(messageId);
//            return this;
//        }
//
//        /**
//         * Set the message to display.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setMessage(CharSequence message) {
//            builder.setMessage(message);
//            return this;
//        }
//
//        /**
//         * Set the resource id of the {@link Drawable} to be used in the title.
//         * <p/>
//         * Takes precedence over values set using {@link #setIcon(Drawable)}.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setIcon(int iconId) {
//            builder.setIcon(iconId);
//            return this;
//        }
//
//        /**
//         * Set the {@link Drawable} to be used in the title.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setIcon(Drawable icon) {
//            builder.setIcon(icon);
//            return this;
//        }
//
//        /**
//         * Set an icon as supplied by a theme attribute. e.g.
//         * {@link android.R.attr#alertDialogIcon}.
//         * <p/>
//         * Takes precedence over values set using {@link #setIcon(int)} or
//         * {@link #setIcon(Drawable)}.
//         *
//         * @param attrId
//         *         ID of a theme attribute that points to a drawable resource.
//         */
//        public DialogBuilder setIconAttribute(int attrId) {
//            builder.setIconAttribute(attrId);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the positive button of the dialog is pressed.
//         *
//         * @param textId
//         *         The resource id of the text to display in the positive button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
//            builder.setPositiveButton(textId, listener);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the positive button of the dialog is pressed.
//         *
//         * @param text
//         *         The text to display in the positive button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
//            builder.setPositiveButton(text, listener);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the negative button of the dialog is pressed.
//         *
//         * @param textId
//         *         The resource id of the text to display in the negative button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
//            builder.setNegativeButton(textId, listener);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the negative button of the dialog is pressed.
//         *
//         * @param text
//         *         The text to display in the negative button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
//            builder.setNegativeButton(text, listener);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the neutral button of the dialog is pressed.
//         *
//         * @param textId
//         *         The resource id of the text to display in the neutral button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setNeutralButton(int textId, final DialogInterface.OnClickListener listener) {
//            builder.setNeutralButton(textId, listener);
//            return this;
//        }
//
//        /**
//         * Set a listener to be invoked when the neutral button of the dialog is pressed.
//         *
//         * @param text
//         *         The text to display in the neutral button
//         * @param listener
//         *         The {@link DialogInterface.OnClickListener} to use.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
//            builder.setNeutralButton(text, listener);
//            return this;
//        }
//
//        /**
//         * Sets whether the dialog is cancelable or not.  Default is true.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setCancelable(boolean cancelable) {
//            builder.setCancelable(cancelable);
//            return this;
//        }
//
//        /**
//         * Sets the callback that will be called if the dialog is canceled.
//         * <p/>
//         * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
//         * being canceled or one of the supplied choices being selected.
//         * If you are interested in listening for all cases where the dialog is dismissed
//         * and not just when it is canceled, see
//         * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
//         * setOnDismissListener}.</p>
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         *
//         * @see #setCancelable(boolean)
//         * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
//         */
//        public DialogBuilder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
//            builder.setOnCancelListener(onCancelListener);
//            return this;
//        }
//
//        /**
//         * Sets the callback that will be called when the dialog is dismissed for any reason.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
//            builder.setOnDismissListener(onDismissListener);
//            return this;
//        }
//
//        /**
//         * Sets the callback that will be called if a key is dispatched to the dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
//            builder.setOnKeyListener(onKeyListener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the
//         * selected item via the supplied listener. This should be an array type i.e. R.array.foo
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setItems(int itemsId, final DialogInterface.OnClickListener listener) {
//            builder.setItems(itemsId, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the
//         * selected item via the supplied listener.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
//            builder.setItems(items, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items, which are supplied by the given {@link ListAdapter}, to be
//         * displayed in the dialog as the content, you will be notified of the
//         * selected item via the supplied listener.
//         *
//         * @param adapter
//         *         The {@link ListAdapter} to supply the list of items
//         * @param listener
//         *         The listener that will be called when an item is clicked.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setAdapter(final ListAdapter adapter, final DialogInterface.OnClickListener listener) {
//            builder.setAdapter(adapter, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items, which are supplied by the given {@link Cursor}, to be
//         * displayed in the dialog as the content, you will be notified of the
//         * selected item via the supplied listener.
//         *
//         * @param cursor
//         *         The {@link Cursor} to supply the list of items
//         * @param listener
//         *         The listener that will be called when an item is clicked.
//         * @param labelColumn
//         *         The column name on the cursor containing the string to display
//         *         in the label.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setCursor(final Cursor cursor, final DialogInterface.OnClickListener listener,
//                                       String labelColumn) {
//            builder.setCursor(cursor, listener, labelColumn);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content,
//         * you will be notified of the selected item via the supplied listener.
//         * This should be an array type, e.g. R.array.foo. The list will have
//         * a check mark displayed to the right of the text for each checked
//         * item. Clicking on an item in the list will not dismiss the dialog.
//         * Clicking on a button will dismiss the dialog.
//         *
//         * @param itemsId
//         *         the resource id of an array i.e. R.array.foo
//         * @param checkedItems
//         *         specifies which items are checked. It should be null in which case
//         *         no
//         *         items are checked. If non null it must be exactly the same length as
//         *         the array of
//         *         items.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
//                                                 final DialogInterface.OnMultiChoiceClickListener listener) {
//            builder.setMultiChoiceItems(itemsId, checkedItems, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content,
//         * you will be notified of the selected item via the supplied listener.
//         * The list will have a check mark displayed to the right of the text
//         * for each checked item. Clicking on an item in the list will not
//         * dismiss the dialog. Clicking on a button will dismiss the dialog.
//         *
//         * @param items
//         *         the text of the items to be displayed in the list.
//         * @param checkedItems
//         *         specifies which items are checked. It should be null in which case
//         *         no
//         *         items are checked. If non null it must be exactly the same length as
//         *         the array of
//         *         items.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
//                                                 final DialogInterface.OnMultiChoiceClickListener listener) {
//            builder.setMultiChoiceItems(items, checkedItems, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content,
//         * you will be notified of the selected item via the supplied listener.
//         * The list will have a check mark displayed to the right of the text
//         * for each checked item. Clicking on an item in the list will not
//         * dismiss the dialog. Clicking on a button will dismiss the dialog.
//         *
//         * @param cursor
//         *         the cursor used to provide the items.
//         * @param isCheckedColumn
//         *         specifies the column name on the cursor to use to determine
//         *         whether a checkbox is checked or not. It must return an integer
//         *         value where 1
//         *         means checked and 0 means unchecked.
//         * @param labelColumn
//         *         The column name on the cursor containing the string to display in
//         *         the
//         *         label.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not
//         *         be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss
//         *         the dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setMultiChoiceItems(Cursor cursor, String isCheckedColumn,
//                                                 String labelColumn,
//                                                 final DialogInterface.OnMultiChoiceClickListener listener) {
//            builder.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the selected item via the supplied listener. This should be an array type i.e.
//         * R.array.foo The list will have a check mark displayed to the right of the text for the
//         * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
//         * button will dismiss the dialog.
//         *
//         * @param itemsId
//         *         the resource id of an array i.e. R.array.foo
//         * @param checkedItem
//         *         specifies which item is checked. If -1 no items are checked.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setSingleChoiceItems(int itemsId, int checkedItem,
//                                                  final DialogInterface.OnClickListener listener) {
//            builder.setSingleChoiceItems(itemsId, checkedItem, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the selected item via the supplied listener. The list will have a check mark displayed
//         * to
//         * the right of the text for the checked item. Clicking on an item in the list will not
//         * dismiss the dialog. Clicking on a button will dismiss the dialog.
//         *
//         * @param cursor
//         *         the cursor to retrieve the items from.
//         * @param checkedItem
//         *         specifies which item is checked. If -1 no items are checked.
//         * @param labelColumn
//         *         The column name on the cursor containing the string to display in the
//         *         label.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
//                                                  final DialogInterface.OnClickListener listener) {
//            builder.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the selected item via the supplied listener. The list will have a check mark displayed
//         * to
//         * the right of the text for the checked item. Clicking on an item in the list will not
//         * dismiss the dialog. Clicking on a button will dismiss the dialog.
//         *
//         * @param items
//         *         the items to be displayed.
//         * @param checkedItem
//         *         specifies which item is checked. If -1 no items are checked.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setSingleChoiceItems(CharSequence[] items, int checkedItem,
//                                                  final DialogInterface.OnClickListener listener) {
//            builder.setSingleChoiceItems(items, checkedItem, listener);
//            return this;
//        }
//
//        /**
//         * Set a list of items to be displayed in the dialog as the content, you will be notified
//         * of
//         * the selected item via the supplied listener. The list will have a check mark displayed
//         * to
//         * the right of the text for the checked item. Clicking on an item in the list will not
//         * dismiss the dialog. Clicking on a button will dismiss the dialog.
//         *
//         * @param adapter
//         *         The {@link ListAdapter} to supply the list of items
//         * @param checkedItem
//         *         specifies which item is checked. If -1 no items are checked.
//         * @param listener
//         *         notified when an item on the list is clicked. The dialog will not be
//         *         dismissed when an item is clicked. It will only be dismissed if
//         *         clicked on a
//         *         button, if no buttons are supplied it's up to the user to dismiss the
//         *         dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setSingleChoiceItems(ListAdapter adapter, int checkedItem,
//                                                  final DialogInterface.OnClickListener listener) {
//            builder.setSingleChoiceItems(adapter, checkedItem, listener);
//            return this;
//        }
//
//        /**
//         * Sets a listener to be invoked when an item in the list is selected.
//         *
//         * @param listener
//         *         The listener to be invoked.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         *
//         * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
//         */
//        public DialogBuilder setOnItemSelectedListener(
//                final AdapterView.OnItemSelectedListener listener) {
//            builder.setOnItemSelectedListener(listener);
//            return this;
//        }
//
//        /**
//         * Set a custom view resource to be the contents of the Dialog. The
//         * resource will be inflated, adding all top-level views to the screen.
//         *
//         * @param layoutResId
//         *         Resource ID to be inflated.
//         *
//         * @return This Builder object to allow for chaining of calls to set
//         * methods
//         */
//        public DialogBuilder setView(int layoutResId) {
//            builder.setView(layoutResId);
//            return this;
//        }
//
//        /**
//         * Set a custom view to be the contents of the Dialog. If the supplied view is an instance
//         * of a {@link ListView} the light background will be used.
//         *
//         * @param view
//         *         The view to use as the contents of the Dialog.
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setView(View view) {
//            builder.setView(view);
//            return this;
//        }
//
//        /**
//         * Set a custom view to be the contents of the Dialog, specifying the
//         * spacing to appear around that view. If the supplied view is an
//         * instance of a {@link ListView} the light background will be used.
//         *
//         * @param view
//         *         The view to use as the contents of the Dialog.
//         * @param viewSpacingLeft
//         *         Spacing between the left edge of the view and
//         *         the dialog frame
//         * @param viewSpacingTop
//         *         Spacing between the top edge of the view and
//         *         the dialog frame
//         * @param viewSpacingRight
//         *         Spacing between the right edge of the view
//         *         and the dialog frame
//         * @param viewSpacingBottom
//         *         Spacing between the bottom edge of the view
//         *         and the dialog frame
//         *
//         * @return This Builder object to allow for chaining of calls to set
//         * methods
//         * <p/>
//         * <p/>
//         * This is currently hidden because it seems like people should just
//         * be able to put padding around the view.
//         *
//         * @hide
//         */
//        public DialogBuilder setView(View view, int viewSpacingLeft, int viewSpacingTop,
//                                     int viewSpacingRight, int viewSpacingBottom) {
//            builder.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
//            return this;
//        }
//
//        /**
//         * Sets the Dialog to use the inverse background, regardless of what the
//         * contents is.
//         *
//         * @param useInverseBackground
//         *         Whether to use the inverse background
//         *
//         * @return This Builder object to allow for chaining of calls to set methods
//         */
//        public DialogBuilder setInverseBackgroundForced(boolean useInverseBackground) {
//            builder.setInverseBackgroundForced(useInverseBackground);
//            return this;
//        }
//
//        /**
//         * @hide
//         */
//        public DialogBuilder setRecycleOnMeasureEnabled(boolean enabled) {
//            builder.setRecycleOnMeasureEnabled(enabled);
//            return this;
//        }
//
//
//        /**
//         * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
//         * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
//         * before displaying the dialog. Use {@link #show()} if you don't have any other processing
//         * to do and want this to be created and displayed.
//         */
//        public AlertDialog create() {
//            dialog = builder.create();
//            return dialog;
//        }
//
//        /**
//         * Creates a {@link AlertDialog} with the arguments supplied to this builder and
//         * {@link Dialog#show()}'s the dialog.
//         */
//        public AlertDialog show() {
//            create();
//            dialog.show();
//            return dialog;
//        }
//    }

    public AlertDialog.Builder getBuilder() {
        return builder;
    }

    public Editable getText() {
        return editText.getText();
    }

    public void setText(CharSequence text) {
        editText.setText(text);
    }

    public void setFloatingLabel(int mode) {
        editText.setFloatingLabel(mode);
    }

    public void setPrimaryColor(int color) {
        editText.setPrimaryColor(color);
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    /**
     * Set the title using the given resource id.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setTitle(int titleId) {
        builder.setTitle(titleId);
        return this;
    }

    /**
     * Set the title displayed in the {@link Dialog}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }

    /**
     * Set the title using the custom view {@code customTitleView}. The
     * methods {@link #setTitle(int)} and {@link #setIcon(int)} should be
     * sufficient for most titles, but this is provided if the title needs
     * more customization. Using this will replace the title and icon set
     * via the other methods.
     *
     * @param customTitleView
     *         The custom view to use as the title.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setCustomTitle(View customTitleView) {
        builder.setCustomTitle(customTitleView);
        return this;
    }

    /**
     * Set the message to display using the given resource id.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setMessage(int messageId) {
        builder.setMessage(messageId);
        return this;
    }

    /**
     * Set the message to display.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setMessage(CharSequence message) {
        builder.setMessage(message);
        return this;
    }

    /**
     * Set the resource id of the {@link Drawable} to be used in the title.
     * <p/>
     * Takes precedence over values set using {@link #setIcon(Drawable)}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setIcon(int iconId) {
        builder.setIcon(iconId);
        return this;
    }

    /**
     * Set the {@link Drawable} to be used in the title.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setIcon(Drawable icon) {
        builder.setIcon(icon);
        return this;
    }

    /**
     * Set an icon as supplied by a theme attribute. e.g.
     * {@link android.R.attr#alertDialogIcon}.
     * <p/>
     * Takes precedence over values set using {@link #setIcon(int)} or
     * {@link #setIcon(Drawable)}.
     *
     * @param attrId
     *         ID of a theme attribute that points to a drawable resource.
     */
    public MaterialDialogButton setIconAttribute(int attrId) {
        builder.setIconAttribute(attrId);
        return this;
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param textId
     *         The resource id of the text to display in the positive button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(textId, listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param text
     *         The text to display in the positive button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(text, listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param textId
     *         The resource id of the text to display in the negative button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(textId, listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param text
     *         The text to display in the negative button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(text, listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param textId
     *         The resource id of the text to display in the neutral button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setNeutralButton(int textId, final DialogInterface.OnClickListener listener) {
        builder.setNeutralButton(textId, listener);
        return this;
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param text
     *         The text to display in the neutral button
     * @param listener
     *         The {@link DialogInterface.OnClickListener} to use.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        builder.setNeutralButton(text, listener);
        return this;
    }

    /**
     * Sets whether the dialog is cancelable or not.  Default is true.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    /**
     * Sets the callback that will be called if the dialog is canceled.
     * <p/>
     * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
     * being canceled or one of the supplied choices being selected.
     * If you are interested in listening for all cases where the dialog is dismissed
     * and not just when it is canceled, see
     * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
     * setOnDismissListener}.</p>
     *
     * @return This Builder object to allow for chaining of calls to set methods
     *
     * @see #setCancelable(boolean)
     * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
     */
    public MaterialDialogButton setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        builder.setOnCancelListener(onCancelListener);
        return this;
    }

    /**
     * Sets the callback that will be called when the dialog is dismissed for any reason.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        builder.setOnDismissListener(onDismissListener);
        return this;
    }

    /**
     * Sets the callback that will be called if a key is dispatched to the dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        builder.setOnKeyListener(onKeyListener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the
     * selected item via the supplied listener. This should be an array type i.e. R.array.foo
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setItems(int itemsId, final DialogInterface.OnClickListener listener) {
        builder.setItems(itemsId, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the
     * selected item via the supplied listener.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
        builder.setItems(items, listener);
        return this;
    }

    /**
     * Set a list of items, which are supplied by the given {@link ListAdapter}, to be
     * displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener.
     *
     * @param adapter
     *         The {@link ListAdapter} to supply the list of items
     * @param listener
     *         The listener that will be called when an item is clicked.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setAdapter(final ListAdapter adapter, final DialogInterface.OnClickListener listener) {
        this.adapter = adapter;
        builder.setAdapter(adapter, listener);
        return this;
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    /**
     * Set a list of items, which are supplied by the given {@link Cursor}, to be
     * displayed in the dialog as the content, you will be notified of the
     * selected item via the supplied listener.
     *
     * @param cursor
     *         The {@link Cursor} to supply the list of items
     * @param listener
     *         The listener that will be called when an item is clicked.
     * @param labelColumn
     *         The column name on the cursor containing the string to display
     *         in the label.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setCursor(final Cursor cursor, final DialogInterface.OnClickListener listener,
                                          String labelColumn) {
        builder.setCursor(cursor, listener, labelColumn);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content,
     * you will be notified of the selected item via the supplied listener.
     * This should be an array type, e.g. R.array.foo. The list will have
     * a check mark displayed to the right of the text for each checked
     * item. Clicking on an item in the list will not dismiss the dialog.
     * Clicking on a button will dismiss the dialog.
     *
     * @param itemsId
     *         the resource id of an array i.e. R.array.foo
     * @param checkedItems
     *         specifies which items are checked. It should be null in which case
     *         no
     *         items are checked. If non null it must be exactly the same length as
     *         the array of
     *         items.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                                                    final DialogInterface.OnMultiChoiceClickListener listener) {
        builder.setMultiChoiceItems(itemsId, checkedItems, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content,
     * you will be notified of the selected item via the supplied listener.
     * The list will have a check mark displayed to the right of the text
     * for each checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param items
     *         the text of the items to be displayed in the list.
     * @param checkedItems
     *         specifies which items are checked. It should be null in which case
     *         no
     *         items are checked. If non null it must be exactly the same length as
     *         the array of
     *         items.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                                    final DialogInterface.OnMultiChoiceClickListener listener) {
        builder.setMultiChoiceItems(items, checkedItems, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content,
     * you will be notified of the selected item via the supplied listener.
     * The list will have a check mark displayed to the right of the text
     * for each checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param cursor
     *         the cursor used to provide the items.
     * @param isCheckedColumn
     *         specifies the column name on the cursor to use to determine
     *         whether a checkbox is checked or not. It must return an integer
     *         value where 1
     *         means checked and 0 means unchecked.
     * @param labelColumn
     *         The column name on the cursor containing the string to display in
     *         the
     *         label.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not
     *         be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss
     *         the dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setMultiChoiceItems(Cursor cursor, String isCheckedColumn,
                                                    String labelColumn,
                                                    final DialogInterface.OnMultiChoiceClickListener listener) {
        builder.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the selected item via the supplied listener. This should be an array type i.e.
     * R.array.foo The list will have a check mark displayed to the right of the text for the
     * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
     * button will dismiss the dialog.
     *
     * @param itemsId
     *         the resource id of an array i.e. R.array.foo
     * @param checkedItem
     *         specifies which item is checked. If -1 no items are checked.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setSingleChoiceItems(int itemsId, int checkedItem,
                                                     final DialogInterface.OnClickListener listener) {
        builder.setSingleChoiceItems(itemsId, checkedItem, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the selected item via the supplied listener. The list will have a check mark displayed
     * to
     * the right of the text for the checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param cursor
     *         the cursor to retrieve the items from.
     * @param checkedItem
     *         specifies which item is checked. If -1 no items are checked.
     * @param labelColumn
     *         The column name on the cursor containing the string to display in the
     *         label.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
                                                     final DialogInterface.OnClickListener listener) {
        builder.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the selected item via the supplied listener. The list will have a check mark displayed
     * to
     * the right of the text for the checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param items
     *         the items to be displayed.
     * @param checkedItem
     *         specifies which item is checked. If -1 no items are checked.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setSingleChoiceItems(CharSequence[] items, int checkedItem,
                                                     final DialogInterface.OnClickListener listener) {
        builder.setSingleChoiceItems(items, checkedItem, listener);
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified
     * of
     * the selected item via the supplied listener. The list will have a check mark displayed
     * to
     * the right of the text for the checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param adapter
     *         The {@link ListAdapter} to supply the list of items
     * @param checkedItem
     *         specifies which item is checked. If -1 no items are checked.
     * @param listener
     *         notified when an item on the list is clicked. The dialog will not be
     *         dismissed when an item is clicked. It will only be dismissed if
     *         clicked on a
     *         button, if no buttons are supplied it's up to the user to dismiss the
     *         dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setSingleChoiceItems(ListAdapter adapter, int checkedItem,
                                                     final DialogInterface.OnClickListener listener) {
        builder.setSingleChoiceItems(adapter, checkedItem, listener);
        return this;
    }

    /**
     * Sets a listener to be invoked when an item in the list is selected.
     *
     * @param listener
     *         The listener to be invoked.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     *
     * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
     */
    public MaterialDialogButton setOnItemSelectedListener(
            final AdapterView.OnItemSelectedListener listener) {
        builder.setOnItemSelectedListener(listener);
        return this;
    }

    /**
     * Set a custom view resource to be the contents of the Dialog. The
     * resource will be inflated, adding all top-level views to the screen.
     *
     * @param layoutResId
     *         Resource ID to be inflated.
     *
     * @return This Builder object to allow for chaining of calls to set
     * methods
     */
    public MaterialDialogButton setView(int layoutResId) {
        builder.setView(layoutResId);
        return this;
    }

    /**
     * Set a custom view to be the contents of the Dialog. If the supplied view is an instance
     * of a {@link ListView} the light background will be used.
     *
     * @param view
     *         The view to use as the contents of the Dialog.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setView(View view) {
        builder.setView(view);
        return this;
    }

    /**
     * Set a custom view to be the contents of the Dialog, specifying the
     * spacing to appear around that view. If the supplied view is an
     * instance of a {@link ListView} the light background will be used.
     *
     * @param view
     *         The view to use as the contents of the Dialog.
     * @param viewSpacingLeft
     *         Spacing between the left edge of the view and
     *         the dialog frame
     * @param viewSpacingTop
     *         Spacing between the top edge of the view and
     *         the dialog frame
     * @param viewSpacingRight
     *         Spacing between the right edge of the view
     *         and the dialog frame
     * @param viewSpacingBottom
     *         Spacing between the bottom edge of the view
     *         and the dialog frame
     *
     * @return This Builder object to allow for chaining of calls to set
     * methods
     * <p/>
     * <p/>
     * This is currently hidden because it seems like people should just
     * be able to put padding around the view.
     *
     * @hide
     */
    public MaterialDialogButton setView(View view, int viewSpacingLeft, int viewSpacingTop,
                                        int viewSpacingRight, int viewSpacingBottom) {
        builder.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
        return this;
    }

    /**
     * Sets the Dialog to use the inverse background, regardless of what the
     * contents is.
     *
     * @param useInverseBackground
     *         Whether to use the inverse background
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public MaterialDialogButton setInverseBackgroundForced(boolean useInverseBackground) {
        builder.setInverseBackgroundForced(useInverseBackground);
        return this;
    }

    /**
     * @hide
     */
    public MaterialDialogButton setRecycleOnMeasureEnabled(boolean enabled) {
        builder.setRecycleOnMeasureEnabled(enabled);
        return this;
    }


    /**
     * Creates a {@link AlertDialog} with the arguments supplied to this builder. It does not
     * {@link Dialog#show()} the dialog. This allows the user to do any extra processing
     * before displaying the dialog. Use {@link #show()} if you don't have any other processing
     * to do and want this to be created and displayed.
     */
    public AlertDialog create() {
        dialog = builder.create();
        return dialog;
    }

    /**
     * Creates a {@link AlertDialog} with the arguments supplied to this builder and
     * {@link Dialog#show()}'s the dialog.
     */
    public AlertDialog show() {
        create();
        dialog.show();
        return dialog;
    }
}
