package com.hyena.pianku.tv;

import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.network.DefaultNetworkSensor;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;

public class App extends BaseApp {

    @Override
    public void initApp() {
        super.initApp();
        FrameworkConfig.getConfig().setDebug(true);
        NetworkProvider.getNetworkProvider().registNetworkSensor(new DefaultNetworkSensor());
    }
}
