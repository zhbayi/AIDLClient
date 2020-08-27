// IComputerManager.aidl
package com.aaron.aidlserver;

import com.aaron.aidlserver.entity.ComputerEntity;
// Declare any non-default types here with import statements

interface IComputerManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void addComputer(in ComputerEntity computer);
     List<ComputerEntity> getComputerList();
}
