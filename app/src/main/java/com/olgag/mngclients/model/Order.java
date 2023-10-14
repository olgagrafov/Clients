package com.olgag.mngclients.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Order implements Parcelable {
    private int costOrder;
    private String idOrder, clientID, prdNameAndModel,
            orderReference, serialNumber, orderDescription, clientName;
    private boolean isGuarantee, isClosed, isRequestBid, isReceivedDevice;
    private long ordDateCreated, ordDateClosed;


    public Order(){};

    public Order(int costOrder, String idOrder, String clientID,
                 String prdNameAndModel, String orderReference, String serialNumber,
                 String orderDescription, String clientName, boolean isGuarantee,
                 boolean isClosed, boolean isRequestBid, long ordDateCreated,
                 long ordDateClosed, boolean isReceivedDevice) {
        this.costOrder = costOrder;
        this.idOrder = idOrder;
        this.clientID = clientID;
        this.prdNameAndModel = prdNameAndModel;
        this.orderReference = orderReference;
        this.serialNumber = serialNumber;
        this.orderDescription = orderDescription;
        this.clientName = clientName;
        this.isGuarantee = isGuarantee;
        this.isClosed = isClosed;
        this.isRequestBid = isRequestBid;
        this.ordDateCreated = ordDateCreated;
        this.ordDateClosed = ordDateClosed;
        this.isReceivedDevice = isReceivedDevice;
    }

    public static Comparator<Order> DateCreated = new Comparator<Order>() {

        public int compare(Order o1, Order o2) {
            return  Long.compare(o2.getOrdDateCreated(), o1.getOrdDateCreated());
        }};

    public static Comparator<Order> DateCreatedLow = new Comparator<Order>() {

        public int compare(Order o1, Order o2) {
            return  Long.compare(o1.getOrdDateCreated(), o2.getOrdDateCreated());
        }};

    protected Order(Parcel in) {
        costOrder = in.readInt();
        idOrder = in.readString();
        clientID = in.readString();
        prdNameAndModel = in.readString();
        orderReference = in.readString();
        serialNumber = in.readString();
        orderDescription = in.readString();
        clientName = in.readString();
        isGuarantee = in.readByte() != 0;
        isClosed = in.readByte() != 0;
        isRequestBid = in.readByte() != 0;
        ordDateCreated = in.readLong();
        ordDateClosed = in.readLong();
        isReceivedDevice = in.readByte() != 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public int getCostOrder() {
        return costOrder;
    }

    public void setCostOrder(int costOrder) {
        this.costOrder = costOrder;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getPrdNameAndModel() {
        return prdNameAndModel;
    }

    public void setPrdNameAndModel(String prdNameAndModel) {
        this.prdNameAndModel = prdNameAndModel;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isGuarantee() {
        return isGuarantee;
    }

    public void setGuarantee(boolean guarantee) {
        isGuarantee = guarantee;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isRequestBid() {
        return isRequestBid;
    }

    public void setRequestBid(boolean requestBid) {
        isRequestBid = requestBid;
    }

    public long getOrdDateCreated() {
        return ordDateCreated;
    }

    public void setOrdDateCreated(long ordDateCreated) {
        this.ordDateCreated = ordDateCreated;
    }

    public long getOrdDateClosed() {
        return ordDateClosed;
    }

    public void setOrdDateClosed(long ordDateClosed) {
        this.ordDateClosed = ordDateClosed;
    }

    public boolean isReceivedDevice() {
        return isReceivedDevice;
    }

    public void setReceivedDevice(boolean receivedDevice) {
        isReceivedDevice = receivedDevice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(costOrder);
        dest.writeString(idOrder);
        dest.writeString(clientID);
        dest.writeString(prdNameAndModel);
        dest.writeString(orderReference);
        dest.writeString(serialNumber);
        dest.writeString(orderDescription);
        dest.writeString(clientName);
        dest.writeByte((byte) (isGuarantee ? 1 : 0));
        dest.writeByte((byte) (isClosed ? 1 : 0));
        dest.writeByte((byte) (isRequestBid ? 1 : 0));
        dest.writeLong(ordDateCreated);
        dest.writeLong(ordDateClosed);
        dest.writeByte((byte) (isReceivedDevice ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Order{" +
                "costOrder=" + costOrder +
                ", idOrder='" + idOrder + '\'' +
                ", clientID='" + clientID + '\'' +
                ", prdNameAndModel='" + prdNameAndModel + '\'' +
                ", orderReference='" + orderReference + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", orderDescription='" + orderDescription + '\'' +
                ", clientName='" + clientName + '\'' +
                ", isDuarantee=" + isGuarantee +
                ", isClosed=" + isClosed +
                ", isRequestBid=" + isRequestBid +
                ", ordDateCreated=" + ordDateCreated +
                ", ordDateClosed=" + ordDateClosed +
                ", isReceivedDevice=" + isReceivedDevice +
                '}';
    }
}
