package com.golovin.fluentstackbar;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import com.golovin.fluentstackbar.helpers.ThreadHelper;
import com.golovin.snackbarmanager.R;

public final class FluentSnackbar {
    private final View mView;

    private final SnackbarHandler mSnackbarHandler;

    public static FluentSnackbar create(Activity activity) {
        ThreadHelper.verifyMainThread();

        return new FluentSnackbar(activity.findViewById(android.R.id.content));
    }

    public static FluentSnackbar create(View view) {
        ThreadHelper.verifyMainThread();

        return new FluentSnackbar(view);
    }

    private FluentSnackbar(View view) {
        mView = view;
        mSnackbarHandler = new SnackbarHandler(this);
    }

    private void putToMessageQueue(Builder builder) {
        Message message = mSnackbarHandler.obtainMessage(SnackbarHandler.MESSAGE_NEW, builder);

        mSnackbarHandler.sendMessage(message);
    }

    void showSnackbar(Builder builder) {
        Snackbar snackbar = Snackbar.make(mView, builder.getText(), builder.getDuration());

        View view = snackbar.getView();
        view.setBackgroundColor(builder.getBackgroundColor());

        TextView textView = (TextView) view.findViewById(R.id.snackbar_text);
        textView.setMaxLines(builder.getMaxLines());
        textView.setTextColor(builder.getTextColor());

        if (builder.hasAction()) {
            snackbar.setAction(builder.getActionText(), builder.getActionListener());

            if (builder.hasActionTextColor()) {
                snackbar.setActionTextColor(builder.getActionTextColor());
            } else if (builder.hasActionTextColors()) {
                snackbar.setActionTextColor(builder.getActionColors());
            }
        }

        if (builder.isImportant()) {
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Message message = mSnackbarHandler.obtainMessage(SnackbarHandler.MESSAGE_DISMISSED);
                    mSnackbarHandler.sendMessage(message);
                }
            });
        }

        snackbar.show();
    }

    public Builder create(@StringRes int text) {
        return create(mView.getContext().getString(text));
    }

    public Builder create(String text) {
        return new Builder(text);
    }

    public class Builder {
        private CharSequence mText;

        private int mMaxLines;

        @ColorInt
        private int mTextColor;

        @ColorInt
        private int mBackgroundColor;

        private boolean mIsImportant;

        private int mDuration;

        private CharSequence mActionText;

        private View.OnClickListener mActionListener;

        @ColorInt
        private int mActionTextColor;
        private ColorStateList mActionColors;
        private boolean mHasActionTextColor;

        private Builder(CharSequence text) {
            mText = text;
            mMaxLines = 1;
            mTextColor = Color.WHITE;
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), R.color.default_background);
            mIsImportant = false;
            mDuration = Snackbar.LENGTH_LONG;
            mActionText = mView.getContext().getString(R.string.default_action);
        }

        public Builder maxLines(int maxLines) {
            mMaxLines = maxLines;
            return this;
        }

        public Builder textColorRes(@ColorRes int color) {
            mTextColor = ContextCompat.getColor(mView.getContext(), color);
            return this;
        }

        public Builder textColor(@ColorInt int color) {
            mTextColor = color;
            return this;
        }

        public Builder successBackgroundColor() {
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), R.color.green_500);
            return this;
        }

        public Builder errorBackgroundColor() {
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), R.color.red_500);
            return this;
        }

        public Builder warningBackgroundColor() {
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), R.color.yellow_700);
            return this;
        }

        public Builder neutralBackgroundColor() {
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), R.color.default_background);
            return this;
        }

        public Builder backgroundColorRes(@ColorRes int color) {
            mBackgroundColor = ContextCompat.getColor(mView.getContext(), color);
            return this;
        }

        public Builder backgroundColor(@ColorInt int color) {
            mBackgroundColor = color;
            return this;
        }

        public Builder important() {
            return important(true);
        }

        public Builder important(boolean isImportant) {
            mIsImportant = isImportant;
            return this;
        }

        public Builder duration(int duration) {
            mDuration = duration;
            return this;
        }

        public Builder action(View.OnClickListener listener) {
            mActionListener = listener;
            return this;
        }

        public Builder actionTextRes(@StringRes int text) {
            mActionText = mView.getContext().getString(text);
            return this;
        }

        public Builder actionText(String text) {
            mActionText = text;
            return this;
        }

        public Builder actionTextColorRes(@ColorRes int color) {
            return actionTextColor(ContextCompat.getColor(mView.getContext(), color));
        }

        public Builder actionTextColor(@ColorInt int color) {
            mActionTextColor = color;
            mHasActionTextColor = true;
            return this;
        }

        public Builder actionTextColors(ColorStateList actionColors) {
            mActionColors = actionColors;
            return this;
        }

        public void show() {
            putToMessageQueue(this);
        }

        CharSequence getText() {
            return mText;
        }

        int getMaxLines() {
            return mMaxLines;
        }

        int getDuration() {
            return mDuration;
        }

        @ColorInt
        int getBackgroundColor() {
            return mBackgroundColor;
        }

        @ColorInt
        int getTextColor() {
            return mTextColor;
        }

        @ColorInt
        int getActionTextColor() {
            return mActionTextColor;
        }

        CharSequence getActionText() {
            return mActionText;
        }

        View.OnClickListener getActionListener() {
            return mActionListener;
        }

        ColorStateList getActionColors() {
            return mActionColors;
        }

        boolean hasAction() {
            return mActionListener != null;
        }

        boolean isImportant() {
            return mIsImportant;
        }

        boolean hasActionTextColor() {
            return mHasActionTextColor;
        }

        boolean hasActionTextColors() {
            return mActionColors != null;
        }
    }
}
