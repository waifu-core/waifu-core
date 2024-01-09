package org.waifucore.qt;

import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;

import org.qtproject.qt5.android.bindings.QtActivity;

import java.io.File;

public class WaifuQtActivity extends QtActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        final File waifuDir = new File(getFilesDir().getAbsolutePath() + "/.waifu");
        if (!waifuDir.exists()) {
            waifuDir.mkdir();
        }

        super.onCreate(savedInstanceState);
    }
}
