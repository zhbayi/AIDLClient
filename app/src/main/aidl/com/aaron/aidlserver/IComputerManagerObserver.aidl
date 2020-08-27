// IComputerManagerObserver.aidl
package com.aaron.aidlserver;

import com.aaron.aidlserver.entity.ComputerEntity;
import com.aaron.aidlserver.IOnComputerArrivedListener;
// Declare any non-default types here with import statements

interface IComputerManagerObserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void addComputer(in ComputerEntity computer);
     List<ComputerEntity> getComputerList();
     void registerUser(IOnComputerArrivedListener listener);
     void unRegisterUser(IOnComputerArrivedListener listener);
}
