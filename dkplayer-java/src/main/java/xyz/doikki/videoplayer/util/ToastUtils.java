package xyz.doikki.videoplayer.util;

import android.app.Application;
import android.content.res.Resources;
//import com.hjq.toast.config.IToastInterceptor;
//import com.hjq.toast.config.IToastStrategy;
//import com.hjq.toast.config.IToastStyle;
//import com.hjq.toast.style.BlackToastStyle;
//import com.hjq.toast.style.LocationToastStyle;
//import com.hjq.toast.style.ViewToastStyle;


public final class ToastUtils {
//    private static Application sApplication;
//    private static Boolean sDebugMode;
//    private static IToastInterceptor sToastInterceptor;
//    private static IToastStrategy sToastStrategy;
//    private static IToastStyle<?> sToastStyle;
//
//    private ToastUtils() {
//    }
//
//    public static void cancel() {
//        sToastStrategy.cancelToast();
//    }
//
//    public static void debugShow(Object obj) {
//        if (isDebugMode()) {
//            show(obj);
//        }
//    }
//
//    public static IToastInterceptor getInterceptor() {
//        return sToastInterceptor;
//    }
//
//    public static IToastStrategy getStrategy() {
//        return sToastStrategy;
//    }
//
//    public static IToastStyle<?> getStyle() {
//        return sToastStyle;
//    }
//
//    public static void init(Application application) {
//        init(application, sToastStyle);
//    }
//
//    public static boolean isDebugMode() {
//        if (sDebugMode == null) {
//            sDebugMode = Boolean.valueOf((sApplication.getApplicationInfo().flags & 2) != 0);
//        }
//        return sDebugMode.booleanValue();
//    }
//
//    public static boolean isInit() {
//        return (sApplication == null || sToastStrategy == null || sToastStyle == null) ? false : true;
//    }
//
//    public static void setDebugMode(boolean z) {
//        sDebugMode = Boolean.valueOf(z);
//    }
//
//    public static void setGravity(int i2) {
//        setGravity(i2, 0, 0);
//    }
//
//    public static void setInterceptor(IToastInterceptor iToastInterceptor) {
//        sToastInterceptor = iToastInterceptor;
//    }
//
//    public static void setStrategy(IToastStrategy iToastStrategy) {
//        sToastStrategy = iToastStrategy;
//        iToastStrategy.registerStrategy(sApplication);
//    }
//
//    public static void setStyle(IToastStyle<?> iToastStyle) {
//        sToastStyle = iToastStyle;
//        sToastStrategy.bindStyle(iToastStyle);
//    }
//
//    public static void setView(int i2) {
//        if (i2 <= 0) {
//            return;
//        }
//        setStyle(new ViewToastStyle(i2, sToastStyle));
//    }
//
//    public static void show(Object obj) {
//        show((CharSequence) (obj != null ? obj.toString() : "null"));
//    }
//
//    public static void init(Application application, IToastStrategy iToastStrategy) {
//        init(application, iToastStrategy, null);
//    }
//
//    public static void setGravity(int i2, int i3, int i4) {
//        setGravity(i2, i3, i4, 0.0f, 0.0f);
//    }
//
//    public static void show(int i2) {
//        try {
//            show(sApplication.getResources().getText(i2));
//        } catch (Resources.NotFoundException unused) {
//            show((CharSequence) String.valueOf(i2));
//        }
//    }
//
//    public static void debugShow(int i2) {
//        if (isDebugMode()) {
//            show(i2);
//        }
//    }
//
//    public static void init(Application application, IToastStyle<?> iToastStyle) {
//        init(application, null, iToastStyle);
//    }
//
//    public static void setGravity(int i2, int i3, int i4, float f2, float f3) {
//        sToastStrategy.bindStyle(new LocationToastStyle(sToastStyle, i2, i3, i4, f2, f3));
//    }
//
//    public static void init(Application application, IToastStrategy iToastStrategy, IToastStyle<?> iToastStyle) {
//        sApplication = application;
//        if (iToastStrategy == null) {
//            iToastStrategy = new ToastStrategy();
//        }
//        setStrategy(iToastStrategy);
//        if (iToastStyle == null) {
//            iToastStyle = new BlackToastStyle();
//        }
//        setStyle(iToastStyle);
//    }
//
//    public static void show(CharSequence charSequence) {
//        if (charSequence == null || charSequence.length() == 0) {
//            return;
//        }
//        if (sToastInterceptor == null) {
//            sToastInterceptor = new ToastLogInterceptor();
//        }
//        if (sToastInterceptor.intercept(charSequence)) {
//            return;
//        }
//        sToastStrategy.showToast(charSequence);
//    }
//
//    public static void debugShow(CharSequence charSequence) {
//        if (isDebugMode()) {
//            show(charSequence);
//        }
//    }
}

