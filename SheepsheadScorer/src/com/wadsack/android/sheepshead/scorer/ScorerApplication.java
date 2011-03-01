package com.wadsack.android.sheepshead.scorer;

import com.google.inject.Module;
import roboguice.application.GuiceApplication;

import java.util.List;

/**
 * Author: Jeremy Wadsack
 */
public class ScorerApplication extends GuiceApplication {

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new ConfigurationModule());
    }
}
