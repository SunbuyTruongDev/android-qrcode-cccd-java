package com.sunbuy.qrcardid_java.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sunbuy.qrcardid_java.MainActivity;
import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.ShowResultContentActivity;
import com.sunbuy.qrcardid_java.Utils;
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

public class HistoryFragment extends Fragment {

    private ResultAdapter adapterResult ;
    private final List<QRCodeResult> listResult = new ArrayList<>() ;
    private int countSelected = 0 ;

    private FragmentHistoryBinding binding ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater,container,false);
        return binding.getRoot() ;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observersData();
        setUpViews();
    }


    public void observersData() {
        adapterResult = new ResultAdapter(requireActivity());
        QRScanDatabase.getInstance().getAllHistory(resultList -> {
            listResult.addAll(resultList) ;
            adapterResult.setData(listResult,ResultAdapter.TYPE_HISTORY);
        });
    }

    public void setUpViews() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(),R.color.colorA8A4A4)),0,0,0,0);
        dividerItemDecoration.setDrawable(insetDrawable) ;

        binding.rvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvContent.setAdapter(adapterResult);
        binding.rvContent.setPadding((int) Utils.dipToPix(16f,requireContext()),0,(int)Utils.dipToPix(16f,requireContext()),0);
        binding.rvContent.addItemDecoration(dividerItemDecoration);

        binding.tvItemCount.setText(getString(R.string.txt_delete_item_count,0));

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
                binding.tvItemCount.setText(getString(R.string.txt_delete_item_count,countSelected));
            }
        });

        binding.clDelete.setOnClickListener(view -> {
            if (binding.tvItemCount.getVisibility() == View.VISIBLE){
                if (countSelected <= 0){
                    return;
                }
                adapterResult.removeHistory(adapterResult.getListSelected());
                binding.tvItemCount.setVisibility(View.GONE);
                adapterResult.clearSelected();
                adapterResult.setShowDeleteHistory(false);
                binding.tvSelectAll.setVisibility(View.GONE);
            }else {
                binding.tvItemCount.setVisibility(View.VISIBLE);
                adapterResult.setShowDeleteHistory(true);
                binding.tvSelectAll.setVisibility(View.VISIBLE);
            }

            if (binding.imgBack.getVisibility() == View.VISIBLE){
                binding.imgBack.setVisibility(View.GONE);
                binding.imgClose.setVisibility(View.VISIBLE);
            }else {
                binding.imgBack.setVisibility(View.VISIBLE);
                binding.imgClose.setVisibility(View.GONE);
            }

        });

        binding.imgBack.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).backToScanFragment();
        });


        binding.imgClose.setOnClickListener(view -> {
            adapterResult.clearSelected();
            binding.tvItemCount.setVisibility(View.GONE);
            binding.imgClose.setVisibility(View.GONE);
            binding.imgBack.setVisibility(View.VISIBLE);
            adapterResult.setShowDeleteHistory(false);
            binding.tvItemCount.setText(getString(R.string.txt_delete_item_count,0));
            binding.tvSelectAll.setVisibility(View.GONE);
        });

        binding.tvSelectAll.setOnClickListener(view -> {
            adapterResult.selectAll();
        });
    }

    @Override
    public void onPause() {
        adapterResult.clearSelected();
        adapterResult.setShowDeleteHistory(false);
        binding.tvItemCount.setVisibility(View.GONE);
        binding.imgClose.setVisibility(View.GONE);
        binding.imgBack.setVisibility(View.VISIBLE);
        binding.tvSelectAll.setVisibility(View.GONE);
        super.onPause();
    }
}
