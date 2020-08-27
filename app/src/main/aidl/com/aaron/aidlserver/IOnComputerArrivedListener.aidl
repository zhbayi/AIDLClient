// IOnComputerArrivedListener.aidl
package com.aaron.aidlserver;

import com.aaron.aidlserver.entity.ComputerEntity;
// Declare any non-default types here with import statements

interface IOnComputerArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onComputerArrived(in ComputerEntity computer);
}
