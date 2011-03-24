/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tradefed.testtype;

import com.android.ddmlib.Log;
import com.android.tradefed.build.IBuildInfo;
import com.android.tradefed.build.ISdkBuildInfo;
import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.result.ITestInvocationListener;
import com.android.tradefed.util.TestLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.Test;

/**
 * A class for running all the unit tests for SDK java libraries.
 * <p/>
 * It finds all libraries given a fixed path within a {@link ISdkBuildInfo}, then finds and runs
 * all the JUnit tests within them.
 */
public class SdkLibTest implements IRemoteTest, IBuildReceiver {

    private static final String LOG_TAG = "SdkLibTest";

    private ISdkBuildInfo mSdkBuild;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBuild(IBuildInfo buildInfo) {
        mSdkBuild = (ISdkBuildInfo)buildInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(ITestInvocationListener listener) throws DeviceNotAvailableException {
         Assert.assertNotNull("missing sdk build to test", mSdkBuild);
         Assert.assertNotNull("missing sdk build to test", mSdkBuild.getSdkDir());

         Log.i(LOG_TAG, String.format("Running library tests in sdk %s",
                 mSdkBuild.getSdkDir().getAbsolutePath()));

         // get the path to all the libraries under test
         File sdkLibDir = new File(mSdkBuild.getSdkDir(), "tools" + File.separator + "lib");
         Assert.assertTrue(String.format("could not find tools/lib folder in sdk %s",
                 mSdkBuild.getSdkDir()), sdkLibDir.exists());

         // get the path to the tests
         File sdkLibTestDir = new File(mSdkBuild.getSdkDir(), "tests" + File.separator +
                 "libtests");
         Assert.assertTrue(String.format("could not find tests/libtests folder in sdk %s",
                 mSdkBuild.getSdkDir()), sdkLibTestDir.exists());

         Collection<File> libraries = collectJars(sdkLibDir);
         Collection<File> testLibs = collectJars(sdkLibTestDir);
         TestLoader loader = new TestLoader();

         for (File testLib : testLibs) {
             Log.i(LOG_TAG, String.format("Running tests in %s", testLib.getName()));
             Test junitSuite = loader.loadTests(testLib, libraries);
             JUnitRunUtil.runTest(listener, junitSuite);
         }
    }

    /**
     * Returns all jar files found in given directory
     */
    private Collection<File> collectJars(File dir) {
        return Arrays.asList(dir.listFiles(new JarFilter()));
    }

    private static class JarFilter implements FilenameFilter {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(File dir, String name) {
           return name.endsWith(".jar");
        }
    }
}