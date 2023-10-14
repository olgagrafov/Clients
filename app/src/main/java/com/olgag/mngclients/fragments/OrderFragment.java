package com.olgag.mngclients.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.controler.OrderAdapter;
import com.olgag.mngclients.db.OrderDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class OrderFragment extends Fragment implements OrderDBHelper.OnGetAllClientsOrderSuccess,
        View.OnClickListener, DatePickerFragment.OnDateSetClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TAB_NUMBER = "tab_number";
    private static final String TAB_NAME = "orders";
    private static final String USER_ID = "userid";

    private int mParam1, startWidth;
    private String mParam2;
    private String userId;
    private ArrayList<Order> arrClientsOrder = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private RecyclerView recOrderList;
    private SearchView searchOrderByName;
    private TextView dateFrom, dateTo,allOrderss, requestBids, openedOrders;
    private ImageButton searchOrder;
    private Animation myAnim;
    private DialogFragment dateFragment;
    private boolean isClickOnFrom;
    private long fromdDate, toDate;
    private Calendar calendar;
    private OrderDBHelper orderDB;


    public OrderFragment() {
    }


    public static OrderFragment newInstance(int param1, String param2,String userId) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_NUMBER, param1);
        args.putString(TAB_NAME, param2);
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(TAB_NUMBER);
            mParam2 = getArguments().getString(TAB_NAME);
            userId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vw = inflater.inflate(R.layout.fragment_order, container, false);

        searchOrderByName =  vw.findViewById(R.id.searchOrderByName);
        searchOrderByName.setOnQueryTextListener(this);

        searchOrderByName.setOnSearchClickListener(this);
        searchOrderByName.setOnCloseListener(this);
        startWidth = searchOrderByName.getLayoutParams().width;

        calendar = Calendar.getInstance();
        dateFrom = vw.findViewById(R.id.date_from);
        dateFrom.setOnClickListener(this);
        dateTo = vw.findViewById(R.id.date_to);
        dateTo.setOnClickListener(this);
        toDate= calendar.getTimeInMillis();
        dateTo.setText(MethodsForApp.formatDateToString(toDate));
        calendar.add(Calendar.MONTH, -2);
       // calendar.add(Calendar.HOUR, -72);
        fromdDate = calendar.getTimeInMillis();
        dateFrom.setText(MethodsForApp.formatDateToString(fromdDate));
        searchOrder = vw.findViewById(R.id.searchOrder);
        searchOrder.setOnClickListener(this);
        myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.img_click);
        dateFragment = new DatePickerFragment(getContext());

        openedOrders =  vw.findViewById(R.id.link_opened_orders);
        openedOrders.setOnClickListener(this);
        requestBids =  vw.findViewById(R.id.link_request_bids);
        requestBids.setOnClickListener(this);
        allOrderss =  vw.findViewById(R.id.link_all_orderss);
        allOrderss.setOnClickListener(this);

        recOrderList = vw.findViewById(R.id.recycViewClientsOrders);
        recOrderList.setLayoutManager(new GridLayoutManager(getContext(), 1));

        orderDB = new OrderDBHelper(vw.getContext(), userId);
        orderAdapter = new OrderAdapter(getContext(), true, userId);

        return vw;
    }

    @Override
    public void onResume() {
        super.onResume();
        orderDB.getAllClientsOpenOrders();
        openedOrders.setTextAppearance(getContext(), R.style.txtLinkClicked);
        openedOrders.setBackgroundResource(R.drawable.textlines);
        allOrderss.setTextAppearance(getContext(), R.style.txtLink);
        allOrderss.setBackgroundColor(Color.TRANSPARENT);
        requestBids.setTextAppearance(getContext(), R.style.txtLink);
        requestBids.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchOrder:
                searchOrder.startAnimation(myAnim);
                if(fromdDate<toDate) {
                    orderAdapter.filter(fromdDate, toDate);
                }
                else {
                    new Alert(getContext(), getString(R.string.date_error), Gravity.CENTER,
                            Toast.LENGTH_LONG, ContextCompat.getColor(getContext(), R.color.error_color));
                }
                break;
            case R.id.date_from:
                dateFragment.show(getFragmentManager(), "datePicker");
                isClickOnFrom = true;
                break;
            case R.id.date_to:
                dateFragment.show(getFragmentManager(), "datePicker");
                isClickOnFrom = false;
                break;
            case R.id.link_opened_orders:
                orderDB.getAllClientsOpenOrders();
                openedOrders.setTextAppearance(getContext(), R.style.txtLinkClicked);
                openedOrders.setBackgroundResource(R.drawable.textlines);
                allOrderss.setTextAppearance(getContext(), R.style.txtLink);
                allOrderss.setBackgroundColor(Color.TRANSPARENT);
                requestBids.setTextAppearance(getContext(), R.style.txtLink);
                requestBids.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.link_request_bids:
                orderDB.getAllClientsRequestBids();
                requestBids.setTextAppearance(getContext(), R.style.txtLinkClicked);
                requestBids.setBackgroundResource(R.drawable.textlines);
                allOrderss.setTextAppearance(getContext(), R.style.txtLink);
                allOrderss.setBackgroundColor(Color.TRANSPARENT);
                openedOrders.setTextAppearance(getContext(), R.style.txtLink);
                openedOrders.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.link_all_orderss:
                orderDB.getAllOrders();
                allOrderss.setTextAppearance(getContext(), R.style.txtLinkClicked);
                allOrderss.setBackgroundResource(R.drawable.textlines);
                openedOrders.setTextAppearance(getContext(), R.style.txtLink);
                openedOrders.setBackgroundColor(Color.TRANSPARENT);
                requestBids.setTextAppearance(getContext(), R.style.txtLink);
                requestBids.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.searchOrderByName:
                MethodsForApp.animateWidth(searchOrderByName.getMeasuredWidth(),recOrderList.getMeasuredWidth(), searchOrderByName, 1000);
            break;

        }

     }
    @Override
    public void setDate( int year, int month, int dayOfMonth) {
        calendar =new GregorianCalendar(year, month, dayOfMonth);
        if(isClickOnFrom){
            fromdDate = calendar.getTimeInMillis();
            dateFrom.setText(MethodsForApp.formatDateToString(fromdDate));
        }
        else {
            calendar.add(Calendar.HOUR, 12);
            toDate = calendar.getTimeInMillis();
            dateTo.setText(MethodsForApp.formatDateToString(toDate));
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        orderAdapter.filterName(newText);
        return false;
    }

    @Override
    public boolean onClose() {
        MethodsForApp.animateWidth(searchOrderByName.getMeasuredWidth(),startWidth, searchOrderByName, 1000);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void setClientOrdersAdapter(ArrayList<Order> orders) {

        arrClientsOrder.clear();
        orderAdapter.delAll();
        arrClientsOrder.addAll(orders);

        orderAdapter.addAll(arrClientsOrder);
        orderAdapter.filter(fromdDate, toDate);
        recOrderList.setAdapter(orderAdapter);
    }


}