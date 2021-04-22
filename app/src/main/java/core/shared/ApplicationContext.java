package core.shared;

import android.content.Context;

public final class ApplicationContext {

    private Context appContext;

    private ApplicationContext() {
    }

    public void init(Context context) {
        if (appContext == null) {
            appContext = context;
        }
    }

    private Context getContext() {
        return appContext;
    }

    public static Context get() {
        return getInstance().getContext();
    }

    private static ApplicationContext instance;

    public static ApplicationContext getInstance() {
        return instance == null ?
                (instance = new ApplicationContext()) :
                instance;
    }
}
