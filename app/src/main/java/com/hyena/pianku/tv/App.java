package com.hyena.pianku.tv;

import com.hyena.framework.network.DefaultNetworkSensor;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;

public class App extends BaseApp {

    @Override
    public void initApp() {
        super.initApp();
        NetworkProvider.getNetworkProvider().registNetworkSensor(new DefaultNetworkSensor());
    }
}
