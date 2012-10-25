/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.tradefed.device;

import com.android.ddmlib.IDevice;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for monitoring state of devices.  Intended to be passed to an {@link IDeviceManager}
 * instance, at which point the {@link IDeviceManager} will invoke callbacks as the related events
 * are triggered.  Any caching or batching needs to be performed within the {@link IDeviceMonitor}
 * instance.
 */
public interface IDeviceMonitor {
    /*  (not javadoc)
     * Interesting states:
     *   Avail
     *   Unavail
     *   Unresponsive
     *   Allocated
     *   Ignore?
     */

    /**
     * A {@link Runnable}-like class that should return the known devices and their states.
     * This class allows the {@link IDeviceMonitor} to fetch device info from its own thread, which
     * should avoid deadlocks that may occur while listing devices.
     */
    public static abstract class DeviceLister {
        public abstract Map<IDevice, String> listDevices();
    }

    /**
     * Allows the {@link DeviceLister} to be set.  After a successful attempt to set the Lister,
     * implementations may discard all subsequent attempts.
     */
    public void setDeviceLister(DeviceLister lister);

    /**
     * Allows one or more labels to be set which may be used to identify the host, and group it with
     * or disambiguate it from other hosts.
     */
    public void setHostLabels(Collection<String> labels);

    /**
     * Signals the {@link IDeviceMonitor} that device states may have been updated.  The Monitor
     * may observe the new device states by calling its DeviceLister
     */
    public void updateFullDeviceState();

    public void deviceAllocated(IDevice device);

//    public void deviceFreed(IDevice device);
}

