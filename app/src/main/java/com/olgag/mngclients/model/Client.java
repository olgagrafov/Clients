package com.olgag.mngclients.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Client implements Parcelable {
    private String clientId, clientName,
            clientAddress, clientDescription,
            clientPhonNumber1, clientPhonNumber2;
    private long latestOrder;
    private boolean isSomeOrderOpen, isSomeBidRequest;

    public Client(){ }

    public Client(String clientId, String clientName, String clientAddress,
                  String clientDescription, String clientPhonNumber1, String clientPhonNumber2,
                  long latestOrder, boolean isSomeOrderOpen, boolean isSomeBidRequest) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientAddress = clientAddress;
        this.clientDescription = clientDescription;
        this.clientPhonNumber1 = clientPhonNumber1;
        this.clientPhonNumber2 = clientPhonNumber2;
        this.latestOrder = latestOrder;
        this.isSomeOrderOpen = isSomeOrderOpen;
        this.isSomeBidRequest = isSomeBidRequest;
    }

    public static Comparator<Client> DateCreated = new Comparator<Client>() {

        public int compare(Client c1, Client c2) {
            return  Long.compare(c2.getLatestOrder(), c1.getLatestOrder());
        }};

    protected Client(Parcel in) {
        clientId = in.readString();
        clientName = in.readString();
        clientAddress = in.readString();
        clientDescription = in.readString();
        clientPhonNumber1 = in.readString();
        clientPhonNumber2 = in.readString();
        latestOrder = in.readLong();
        isSomeOrderOpen = in.readByte() != 0;
        isSomeBidRequest = in.readByte() != 0;
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

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

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientDescription() {
        return clientDescription;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    public String getClientPhonNumber1() {
        return clientPhonNumber1;
    }

    public void setClientPhonNumber1(String clientPhonNumber1) {
        this.clientPhonNumber1 = clientPhonNumber1;
    }

    public String getClientPhonNumber2() {
        return clientPhonNumber2;
    }

    public void setClientPhonNumber2(String clientPhonNumber2) {
        this.clientPhonNumber2 = clientPhonNumber2;
    }

    public long getLatestOrder() {
        return latestOrder;
    }

    public void setLatestOrder(long latestOrder) {
        this.latestOrder = latestOrder;
    }

    public boolean isSomeOrderOpen() {
        return isSomeOrderOpen;
    }

    public void setSomeOrderOpen(boolean someOrderOpen) {
        isSomeOrderOpen = someOrderOpen;
    }

    public boolean isSomeBidRequest() {
        return isSomeBidRequest;
    }

    public void setSomeBidRequest(boolean someBidRequest) {
        isSomeBidRequest = someBidRequest;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientAddress='" + clientAddress + '\'' +
                ", clientDescription='" + clientDescription + '\'' +
                ", clientPhonNumber1='" + clientPhonNumber1 + '\'' +
                ", clientPhonNumber2='" + clientPhonNumber2 + '\'' +
                ", latestOrder" + latestOrder +
                ", isSomeOrderOpen" + isSomeOrderOpen +
                ", isSomeBidRequest" + isSomeBidRequest +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clientId);
        dest.writeString(clientName);
        dest.writeString(clientAddress);
        dest.writeString(clientDescription);
        dest.writeString(clientPhonNumber1);
        dest.writeString(clientPhonNumber2);
        dest.writeLong(latestOrder);
        dest.writeByte((byte) (isSomeOrderOpen ? 1 : 0));
        dest.writeByte((byte) (isSomeBidRequest ? 1 : 0));
    }
}

