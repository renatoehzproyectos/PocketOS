package com.pocketos.vd;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * DisplayManager checks that the caller's package matches its uid. The service runs as the
 * shell uid, so every package lookup must say "com.android.shell". getOpPackageName is a
 * hidden Context method, which is why this class is Java (no @Override needed).
 */
public final class FakeContext extends ContextWrapper {
    public static final String PKG = "com.android.shell";
    public FakeContext(Context base) { super(base); }
    public String getPackageName() { return PKG; }
    public String getOpPackageName() { return PKG; }
    public Context getApplicationContext() { return this; }
}
