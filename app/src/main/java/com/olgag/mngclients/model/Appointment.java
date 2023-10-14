package com.olgag.mngclients.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable  {
    private String appointmentId, clientId, clientName,clientPhonNumber ;
    private long appointmentStart;
    private boolean isSMSSent;

    public Appointment(){}

    public Appointment(String appointmentId, String clientId,
                       long appointmentStart, boolean isSMSSent) {
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.appointmentStart = appointmentStart;
        this.isSMSSent = isSMSSent;
    }

    public Appointment(String appointmentId, String clientId,
                       String clientName, String clientPhonNumber, long appointmentStart, boolean isSMSSent) {
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientPhonNumber = clientPhonNumber;
        this.appointmentStart = appointmentStart;
        this.isSMSSent = isSMSSent;
    }

    protected Appointment(Parcel in) {
        appointmentId = in.readString();
        clientId = in.readString();
        clientName = in.readString();
        clientPhonNumber = in.readString();
        appointmentStart = in.readLong();
        isSMSSent = in.readByte() != 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhonNumber() {
        return clientPhonNumber;
    }

    public void setClientPhonNumber(String clientPhonNumber) {
        this.clientPhonNumber = clientPhonNumber;
    }

    public long getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(long appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public boolean isSMSSent() {
        return isSMSSent;
    }

    public void setSMSSent(boolean SMSSent) {
        isSMSSent = SMSSent;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientPhonNumber='" + clientPhonNumber + '\'' +
                ", appointmentStart=" + appointmentStart +
                ", isSMSSent=" + isSMSSent +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appointmentId);
        dest.writeString(clientId);
        dest.writeString(clientName);
        dest.writeString(clientPhonNumber);
        dest.writeLong(appointmentStart);
        dest.writeByte((byte) (isSMSSent ? 1 : 0));
    }
}
