package com.olgag.mngclients.controler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.olgag.mngclients.R;
import com.olgag.mngclients.activities.WorkCard;
import com.olgag.mngclients.db.OrderDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private Context context;
    private String userId;
    private ArrayList<Order> arrOrderList = new ArrayList<>();
    private ArrayList<Order> arrDateList = new ArrayList<>();
    private int position;
    private OnOrderClickListener listener;
    private boolean isComeFromeOrder;

    public OrderAdapter(Context context, boolean comeFromeOrder, String userId) {
        this.context = context;
        this.listener = (OnOrderClickListener) context;
        this.isComeFromeOrder = comeFromeOrder;
        this.userId = userId;
    }


    public void add(Order order){
        arrOrderList.add(order);
        arrDateList.add(order);
        notifyItemInserted(arrOrderList.size()-1);
    }

    public void addAll(ArrayList<Order> orders){
        this.arrDateList.addAll(orders);
        this.arrOrderList.addAll(orders);
    }

    public void del(Order order){
        arrOrderList.remove(order);
        arrDateList.remove(order);
        notifyItemRemoved(position);
    }

    public void delAll(){
        int size = arrOrderList.size();
        arrOrderList.clear();
        arrDateList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public ArrayList<Order> getClientOrders() {
        return arrOrderList;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHolder(LayoutInflater.from(context).inflate(R.layout.order_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderHolder holder, int position) {
        holder.bind(arrOrderList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrOrderList.size();
    }


    public interface OnOrderClickListener{
        void openOrderForUpdate(Order order, int position, boolean isOrderForUpdate);
    }

    public void filterName(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrOrderList.clear();
        if (charText.length() == 0) {
            arrOrderList.addAll(arrDateList);
        } else {
            for (Order or : arrDateList) {
                if (or.getPrdNameAndModel().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrOrderList.add(or);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter(long fromdDate, long toDate) {
        arrOrderList.clear();
        for (Order or : arrDateList) {
            if (or.getOrdDateCreated() >= fromdDate && or.getOrdDateCreated()<=toDate) {
                arrOrderList.add(or);
            }
        }
        notifyDataSetChanged();
    }

    // ************************************** HOLDETR *********************************************
    public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout layoutOrder;
        private TextView txtPrdName, txtCostOrder, txtDateOrder, txtViewAddText;
        private ImageButton btnPDFBill;
        private FloatingActionButton btnUpdateOrder;
        private Order curentOrder;


        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            this.txtPrdName = itemView.findViewById(R.id.txtViewPrdName);
            this.txtCostOrder = itemView.findViewById(R.id.txtViewCostOrder);
            this.txtDateOrder =  itemView.findViewById(R.id.txtViewDateOrder);
            this.btnUpdateOrder = itemView.findViewById(R.id.imgBtnDeleteOrder);
            this.txtViewAddText = itemView.findViewById(R.id.txtViewAddText);
            btnUpdateOrder.setOnClickListener(this);
            this.btnPDFBill = itemView.findViewById(R.id.img_btn_bill);
            btnPDFBill.setOnClickListener(this);
            this.layoutOrder = itemView.findViewById(R.id.layoutOrderRow);
            layoutOrder.setOnClickListener(this);

        }

        public void bind(Order order) {
            curentOrder = order;
            txtPrdName.setText(curentOrder.getPrdNameAndModel());
            float costOrder = curentOrder.getCostOrder() / 100f;
            txtCostOrder.setText(costOrder + " ");
            txtDateOrder.setText(MethodsForApp.formatDateToString(curentOrder.getOrdDateCreated()));
            String addText = "";
            if(!isComeFromeOrder){
                addText=context.getString(R.string.order) + " ";
                addText += curentOrder.isClosed() ?   context.getString(R.string.closed) :  context.getString(R.string.is_open) ;
                addText += curentOrder.isRequestBid() ?   " " + context.getString(R.string.and_request_bid) :  "";
            }
            else {
                addText =  context.getString(R.string.order_owner)  +" " + curentOrder.getClientName();
            }
            txtViewAddText.setText(addText);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgBtnDeleteOrder:
                    OrderDBHelper orderDB = new OrderDBHelper(context, userId);
                    position = getAdapterPosition();
                    del(curentOrder);
                    orderDB.delCurrentOrderByID(curentOrder.getIdOrder());
                    Snackbar snackbar = Snackbar.make(v,MethodsForApp.mskeMessageForSnack(context.getString(R.string.order_deleted)),
                            Snackbar.LENGTH_LONG).
                            setAction(context.getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            add(curentOrder);
                            orderDB.addUpdateNewOrder(curentOrder);

                        }
                    });
                    snackbar.show();
                    break;
                case R.id.layoutOrderRow:
                    //Toast.makeText(context, "update"+ curentOrder.toString(), Toast.LENGTH_SHORT).show();
                    listener.openOrderForUpdate(curentOrder, getAdapterPosition(), true);
                    break;
                case R.id.img_btn_bill:
                   position = getAdapterPosition();
               //    if(listener.checkPermisseon()){
                       Intent intent;
                       intent = new Intent(context, WorkCard.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      intent.putExtra("user", userId);
//                    intent.putExtra("client", currentClient);
                       intent.putExtra("gotoclient", (!isComeFromeOrder)?true:false);
                        intent.putExtra("curentOrder", curentOrder);
//                    intent.putExtra("isOrderForUpdate", isOrderForUpdate);
                       context.startActivity(intent);
              //     }

                    break;
            }
        }
    }
}
