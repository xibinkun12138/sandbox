package com.android.internal.infra;

/**
 * @author virtual_space
 * @function
 **/
import android.os.Parcel;
import android.os.Parcelable;
import java.util.concurrent.CompletableFuture;

public class AndroidFuture<T> extends CompletableFuture<T> implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
