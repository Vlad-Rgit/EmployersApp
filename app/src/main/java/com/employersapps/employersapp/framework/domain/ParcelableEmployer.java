package com.employersapps.employersapp.framework.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.employersapps.core.domain.Employer;
import com.employersapps.core.domain.Post;
import com.employersapps.core.domain.Status;

import java.time.Instant;
import java.time.ZoneId;

public class ParcelableEmployer implements Parcelable {

    private final Employer employer;

    public ParcelableEmployer(Employer employer) {
        this.employer = employer;
    }

    protected ParcelableEmployer(Parcel in) {
        employer = new Employer(
                in.readLong(),
                in.readInt(),
                in.readInt(),
                in.readInt(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readByte() == 1,
                new Post(
                        in.readInt(),
                        in.readString()
                ),
                new Status(
                        in.readInt(),
                        in.readString()
                ),
                Instant.ofEpochSecond(in.readLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                Instant.ofEpochSecond(in.readLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                in.readString()
        );
    }

    public Employer getEmployer() {
        return employer;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(employer.getId());
        dest.writeInt(employer.getPostId());
        dest.writeInt(employer.getRoleId());
        dest.writeInt(employer.getStatusId());
        dest.writeString(employer.getLastName());
        dest.writeString(employer.getFirstName());
        dest.writeString(employer.getLogin());
        dest.writeString(employer.getPhotoPath());
        dest.writeByte((byte) (employer.isLocationPublic() ? 1 : 0));
        dest.writeInt(employer.getPost().getId());
        dest.writeString(employer.getPost().getName());
        dest.writeInt(employer.getStatusId());
        dest.writeString(employer.getStatus().getName());
        if(employer.getStartVacationDate() == null) {
            dest.writeLong(0);
        }
        else {
            dest.writeLong(employer.getStartVacationDate().atTime(0, 0)
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond());
        }

        if(employer.getEndVacationDate() == null) {
            dest.writeLong(0);
        }
        else {
            dest.writeLong(employer.getEndVacationDate().atTime(0, 0)
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond());
        }

        dest.writeString(employer.getVacationComment());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableEmployer> CREATOR = new Creator<ParcelableEmployer>() {
        @Override
        public ParcelableEmployer createFromParcel(Parcel in) {
            return new ParcelableEmployer(in);
        }

        @Override
        public ParcelableEmployer[] newArray(int size) {
            return new ParcelableEmployer[size];
        }
    };
}
