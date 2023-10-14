package com.olgag.mngclients.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.ClientAdapter;
import com.olgag.mngclients.db.ClientDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Client;

import java.util.ArrayList;

public class ClientFragment extends Fragment implements SearchView.OnQueryTextListener,
        View.OnClickListener, SearchView.OnCloseListener {

    private static final String TAB_NUMBER = "tab_number";
    private static final String TAB_NAME = "clients";
    private static final String USER_ID = "userid";

    private ArrayList<Client> clients = new ArrayList<Client>();
    private ClientAdapter clientAdapter;
    private SearchView searchClient;
    private RecyclerView recClienttList;
    private TextView linkClientsOpenedOrder, linkClientsRequestBid, linkAllClients;
    ClientDBHelper clienttDB;

    private int mParam1;
    private String mParam2;
    private String userId;
    private int startWidth;

    public ClientFragment() {
    }

    public static ClientFragment newInstance(int param1, String param2,String userId) {
        ClientFragment fragment = new ClientFragment();
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
        View vw = inflater.inflate(R.layout.fragment_client, container, false);

        linkClientsOpenedOrder =  vw.findViewById(R.id.link_clients_opened_order);
        linkClientsOpenedOrder.setOnClickListener(this);
        linkClientsRequestBid =  vw.findViewById(R.id.link_clients_request_bid);
        linkClientsRequestBid.setOnClickListener(this);
        linkAllClients =  vw.findViewById(R.id.link_all_clients);
        linkAllClients.setOnClickListener(this);

        searchClient =  vw.findViewById(R.id.searchClient);
        searchClient.setOnQueryTextListener(this);
        searchClient.setOnSearchClickListener(this);
        searchClient.setOnCloseListener(this);
        startWidth = searchClient.getLayoutParams().width;


        recClienttList = vw.findViewById(R.id.recycViewClientss);
        recClienttList.setLayoutManager(new GridLayoutManager(vw.getContext(), 1));
        clientAdapter = new ClientAdapter(vw.getContext(), userId);
        clienttDB = new ClientDBHelper(userId, vw.getContext(),clientAdapter, recClienttList);
        clienttDB.getAllClientsWithOpenOrders();

        return vw;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        clientAdapter.filter(newText);
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchClient:
                MethodsForApp.animateWidth(searchClient.getMeasuredWidth(),recClienttList.getMeasuredWidth(), searchClient, 1000);
                break;
            case R.id.link_clients_opened_order:
                searchClient.setQuery("", true);
                clienttDB.getAllClientsWithOpenOrders();
                linkClientsOpenedOrder.setTextAppearance(getContext(), R.style.txtLinkClicked);
                linkClientsOpenedOrder.setBackgroundResource(R.drawable.textlines);
                linkAllClients.setTextAppearance(getContext(), R.style.txtLink);
                linkAllClients.setBackgroundColor(Color.TRANSPARENT);
                linkClientsRequestBid.setTextAppearance(getContext(), R.style.txtLink);
                linkClientsRequestBid.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.link_clients_request_bid:
                searchClient.setQuery("", true);
                clienttDB.getAllClientsWithRequstedBid();
                linkClientsRequestBid.setTextAppearance(getContext(), R.style.txtLinkClicked);
                linkClientsRequestBid.setBackgroundResource(R.drawable.textlines);
                linkClientsOpenedOrder.setTextAppearance(getContext(), R.style.txtLink);
                linkClientsOpenedOrder.setBackgroundColor(Color.TRANSPARENT);
                linkAllClients.setTextAppearance(getContext(), R.style.txtLink);
                linkAllClients.setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.link_all_clients:
               searchClient.setQuery("", true);
               clienttDB.getAllClients();
               linkAllClients.setTextAppearance(getContext(), R.style.txtLinkClicked);
               linkAllClients.setBackgroundResource(R.drawable.textlines);
               linkClientsOpenedOrder.setTextAppearance(getContext(), R.style.txtLink);
               linkClientsOpenedOrder.setBackgroundColor(Color.TRANSPARENT);
               linkClientsRequestBid.setTextAppearance(getContext(), R.style.txtLink);
               linkClientsRequestBid.setBackgroundColor(Color.TRANSPARENT);
            break;
        }
    }

    @Override
    public boolean onClose() {
        MethodsForApp.animateWidth(searchClient.getMeasuredWidth(),startWidth, searchClient, 1000);
        return false;
        }

}