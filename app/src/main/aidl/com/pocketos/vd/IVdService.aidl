package com.pocketos.vd;

import android.view.Surface;
import android.view.KeyEvent;
import android.view.MotionEvent;

// Runs inside a Shizuku "user service" (shell uid). All transaction ids are explicit,
// as Shizuku requires; 16777114 is reserved for destroy().
interface IVdService {
    void destroy() = 16777114;
    int createDisplay(in Surface surface, int width, int height, int dpi, String name) = 2;
    void resizeDisplay(int displayId, int width, int height, int dpi) = 3;
    void releaseDisplay(int displayId) = 4;
    String startActivity(int displayId, String component) = 5;
    String lastError() = 6;
    oneway void injectMotion(int displayId, in MotionEvent event) = 7;
    oneway void injectKey(int displayId, in KeyEvent event) = 8;
}
