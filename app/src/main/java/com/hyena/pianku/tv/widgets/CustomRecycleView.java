package com.hyena.pianku.tv.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hyena.framework.clientlog.LogUtil;

public class CustomRecycleView extends RecyclerView {

    public CustomRecycleView(@NonNull Context context) {
        super(context);
    }

    public CustomRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LogUtil.v("yangzc", "new CustomRecycleView2");
    }

    public CustomRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtil.v("yangzc", "new CustomRecycleView");
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        LogUtil.v("yangzc", "onSaveInstanceState");

        String track = Log.getStackTraceString(new RuntimeException("yangzc"));
        LogUtil.v("yangzc", track);
        return super.onSaveInstanceState();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        LogUtil.v("yangzc", "onRestoreInstanceState");
    }
}
