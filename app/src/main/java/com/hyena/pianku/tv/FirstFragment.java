package com.hyena.pianku.tv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.hyena.framework.network.HttpProvider;
import com.hyena.framework.network.HttpResult;
import com.hyena.framework.network.listener.DataHttpListener;
import com.hyena.framework.service.dlna.cybergarage.DLNAContainer;
import com.hyena.framework.service.dlna.cybergarage.DLNAService;
import com.hyena.framework.utils.UiThreadHandler;
import com.mumu.dialog.MMLoading;

import org.cybergarage.upnp.Device;

import java.util.List;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment {

    private WebView mWebView;
    private ProgressBar mPbProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    private String sourceUrl;
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DLNAContainer.getInstance().setDeviceChangeListener(
                new DLNAContainer.DeviceChangeListener() {

                    @Override
                    public void onDeviceChange(Device device) {
                        UiThreadHandler.post(new Runnable() {
                            public void run() {
                                refresh();
                            }
                        });
                    }
                });
        mWebView = view.findViewById(R.id.wv);
        mPbProgress = view.findViewById(R.id.pb_progress);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mPbProgress.setProgress(newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final String url = String.valueOf(request.getUrl());
                    if (url.contains("https://m.pianku.tv/py/")) {
                        showLoading();
                        sourceUrl = null;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("yangzc", url);
                                HttpProvider provider = new HttpProvider();
                                DataHttpListener httpListener = new DataHttpListener();
                                HttpResult result = provider.doGet(url, 30, httpListener);

                                if (result.isSuccess()) {
                                    String body = new String(httpListener.getData());
                                    body = body.substring(body.indexOf("geturl('") + "geturl('".length());
                                    body = body.substring(0, body.indexOf("'"));
                                    Log.v("yangzc", body);
                                    sourceUrl = body;
                                } else {
                                    Log.v("yangzc", "fail!!!");
                                }
                                UiThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoading();
                                        if (sourceUrl != null && !sourceUrl.isEmpty()) {
                                            showDeviceList();
                                        }
                                    }
                                });
                            }
                        }).start();
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        mWebView.loadUrl("http://www.pianku.tv");
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP &&
                        i == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Refresh");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.v("yangzc", "item: " + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.v("yangzc", "item: " + item.getItemId());
        return super.onContextItemSelected(item);
    }

    private ArrayAdapter<String> mAdapter;

    private void showDeviceList() {
        startDLNAService();
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(getContext());
        listDialog.setTitle("设备列表");
        listDialog.setAdapter(mAdapter, (dialog, which) -> {
            List<Device> devices = DLNAContainer.getInstance().getDevices();
//            Toast.makeText(getContext(),
//                    "你点击了" + devices.get(which).getFriendlyName(),
//                    Toast.LENGTH_SHORT).show();
//            NavHostFragment.findNavController(FirstFragment.this)
//                    .navigate(R.id.action_FirstFragment_to_SecondFragment);

            DLNAContainer.getInstance().setSelectedDevice(devices.get(which));
            startControlActivity();
        });
        listDialog.setCancelable(false);
        AlertDialog dialog = listDialog.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dialogInterface.dismiss();
                }
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        refresh();
    }

    private void refresh() {
        if (mAdapter != null) {
            List<Device> devices = DLNAContainer.getInstance().getDevices();
            List<String> items = devices.stream().map(Device::getFriendlyName)
                    .collect(Collectors.toList());
            mAdapter.clear();
            mAdapter.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void startDLNAService() {
        Intent intent = new Intent(getContext(), DLNAService.class);
        getContext().startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        startDLNAService();
    }

    @Override
    public void onDestroy() {
        stopDLNAService();
        super.onDestroy();
    }

    private void stopDLNAService() {
        Intent intent = new Intent(getContext(), DLNAService.class);
        getContext().stopService(intent);
    }

    private MMLoading mmLoading;

    protected void showLoading() {
        if (mmLoading == null) {
            MMLoading.Builder builder = new MMLoading.Builder(getContext())
                    .setMessage("加载中...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            mmLoading = builder.create();
        } else {
            mmLoading.dismiss();
            MMLoading.Builder builder = new MMLoading.Builder(getContext())
                    .setMessage("加载中...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            mmLoading = builder.create();
        }
        mmLoading.show();
    }

    protected void hideLoading() {
        if (mmLoading != null && mmLoading.isShowing()) {
            mmLoading.dismiss();
        }
    }

    private void startControlActivity() {
        Intent intent = new Intent(getContext(),
                ControlActivity.class);
        intent.putExtra("url", sourceUrl);
        startActivity(intent);
    }
}