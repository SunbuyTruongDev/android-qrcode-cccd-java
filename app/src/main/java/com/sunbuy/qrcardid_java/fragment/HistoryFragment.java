package com.sunbuy.qrcardid_java.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewKt;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.common.base.BaseFragment;
import com.base.common.extensions.DimenExtensionKt;
import com.base.common.extensions.ViewExtensionsKt;
import com.sunbuy.qrcardid_java.MainActivity;
import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.ShowResultContentActivity;
import com.sunbuy.qrcardid_java.adapter.ResultAdapter;
import com.sunbuy.qrcardid_java.adapter.ResultAdapterListener;
import com.sunbuy.qrcardid_java.data.DataBaseCallback;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HistoryFragment extends BaseFragment<FragmentHistoryBinding> {

    private ResultAdapter adapterResult ;
    private final List<QRCodeResult> listResult = new ArrayList<>() ;
    private int countSelected = 0 ;

    @Override
    public void observersData() {
        super.observersData();
        adapterResult = new ResultAdapter(requireActivity());
        QRScanDatabase.getInstance().getAllHistory(resultList -> {
            listResult.addAll(resultList) ;
            adapterResult.setData(listResult,ResultAdapter.TYPE_HISTORY);
        });
    }

    @Override
    public void setUpViews() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(),R.color.colorA8A4A4)),0,0,0,0);
        dividerItemDecoration.setDrawable(insetDrawable) ;

        getBinding().rvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        getBinding().rvContent.setAdapter(adapterResult);
        getBinding().rvContent.setPadding((int)DimenExtensionKt.dipToPix(16f,requireContext()),0,(int)DimenExtensionKt.dipToPix(16f,requireContext()),0);
        getBinding().rvContent.addItemDecoration(dividerItemDecoration);

        getBinding().tvItemCount.setText(getString(R.string.txt_delete_item_count,0));

        adapterResult.setResultListener(new ResultAdapterListener() {
            @Override
            public void onResultClick(QRCodeResult qrCodeResult) {
                ShowResultContentActivity.newInstance(requireContext(), qrCodeResult, qrCodeResult.getType()) ;
            }

            @Override
            public void onRemoveFavorite(QRCodeResult qrCodeResult) {

            }

            @Override
            public void onCountSelected(ArrayList<QRCodeResult> list) {
                countSelected = list.size() ;
                getBinding().tvItemCount.setText(getString(R.string.txt_delete_item_count,countSelected));
            }
        });

        ViewExtensionsKt.setSingleClickListener(getBinding().clDelete, view -> {
            if (ViewKt.isVisible(getBinding().tvItemCount)){
                if (countSelected <= 0){
                    return null;
                }
                adapterResult.removeHistory(adapterResult.getListSelected());
                getBinding().tvItemCount.setVisibility(View.GONE);
                adapterResult.clearSelected();
                adapterResult.setShowDeleteHistory(false);
                getBinding().tvSelectAll.setVisibility(View.GONE);
            }else {
                getBinding().tvItemCount.setVisibility(View.VISIBLE);
                adapterResult.setShowDeleteHistory(true);
                getBinding().tvSelectAll.setVisibility(View.VISIBLE);
            }

            if (getBinding().imgBack.getVisibility() == View.VISIBLE){
                getBinding().imgBack.setVisibility(View.GONE);
                getBinding().imgClose.setVisibility(View.VISIBLE);
            }else {
                getBinding().imgBack.setVisibility(View.VISIBLE);
                getBinding().imgClose.setVisibility(View.GONE);
            }
            return null ;
        });

        ViewExtensionsKt.setSingleClickListener(getBinding().imgBack,view ->{
            ((MainActivity) requireActivity()).backToScanFragment();
            return null;
        });

        ViewExtensionsKt.setSingleClickListener(getBinding().imgClose,view ->{
            adapterResult.clearSelected();
            getBinding().tvItemCount.setVisibility(View.GONE);
            getBinding().imgClose.setVisibility(View.GONE);
            getBinding().imgBack.setVisibility(View.VISIBLE);
            adapterResult.setShowDeleteHistory(false);
            getBinding().tvItemCount.setText(getString(R.string.txt_delete_item_count,0));
            getBinding().tvSelectAll.setVisibility(View.GONE);
            return null;
        });

        ViewExtensionsKt.setSingleClickListener(getBinding().tvSelectAll, view -> {
            adapterResult.selectAll();
            return  null ;
        });
    }

    @Override
    public void onFragmentPause() {
        adapterResult.clearSelected();
        adapterResult.setShowDeleteHistory(false);
        getBinding().tvItemCount.setVisibility(View.GONE);
        getBinding().imgClose.setVisibility(View.GONE);
        getBinding().imgBack.setVisibility(View.VISIBLE);
        getBinding().tvSelectAll.setVisibility(View.GONE);
        super.onFragmentPause();
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_history;
    }
}
