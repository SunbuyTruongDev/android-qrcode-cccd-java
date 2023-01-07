package com.sunbuy.qrcardid_java.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
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
import com.sunbuy.qrcardid_java.databinding.FragmentImportanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ImportanceFragment extends Fragment {

    private ResultAdapter resultAdapter;
    private List<QRCodeResult> listResult = new ArrayList<>();
    private FragmentImportanceBinding binding ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImportanceBinding.inflate(inflater,container,false) ;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observersData();
        setUpViews();
    }

    public void observersData() {
        resultAdapter = new ResultAdapter(requireActivity());
        QRScanDatabase.getInstance().getAllFavorite(resultList -> {
            listResult.addAll(resultList);
            resultAdapter.setData(listResult, ResultAdapter.TYPE_IMPORTANT);
        });
    }

    public void setUpViews() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorA8A4A4)), 0, 0, 0, 0);
        dividerItemDecoration.setDrawable(insetDrawable);

        binding.rvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvContent.setAdapter(resultAdapter);
        binding.rvContent.setPadding((int) Utils.dipToPix(16f, requireContext()), 0, (int) Utils.dipToPix(16f, requireContext()), 0);

        resultAdapter.setResultListener(new ResultAdapterListener() {
            @Override
            public void onResultClick(QRCodeResult qrCodeResult) {
                ShowResultContentActivity.newInstance(requireContext(),qrCodeResult,qrCodeResult.getType());
            }

            @Override
            public void onRemoveFavorite(QRCodeResult qrCodeResult) {
                resultAdapter.removeFavorite(qrCodeResult);
            }

            @Override
            public void onCountSelected(ArrayList<QRCodeResult> list) {

            }
        });

        binding.imgBack.setOnClickListener(v -> ((MainActivity) requireActivity()).backToScanFragment());
    }
}
