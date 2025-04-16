// IBPersistentDataBlockService.aidl
package com.hello.sandbox.core.system.sevice.persistentdata;

// Declare any non-default types here with import statements

interface IBPersistentDataBlockService {
        int getDataBlockSize();

        long getMaximumDataBlockSize();

        boolean getOemUnlockEnabled();

        byte[] read();

        void setOemUnlockEnabled(boolean z);

        void wipe();

        int write(in byte[] bArr);
}