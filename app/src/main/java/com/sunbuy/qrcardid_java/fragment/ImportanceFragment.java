package com.sunbuy.qrcardid_java.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;
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
import com.sunbuy.qrcardid_java.databinding.FragmentImportanceBinding;

import java.util.ArrayList;
import java.util.List;

public class ImportanceFragment extends BaseFragment<FragmentImportanceBinding> {

    private ResultAdapter resultAdapter;
    private List<QRCodeResult> listResult = new ArrayList<>();

    @Override
    public void observersData() {
        super.observersData();
        resultAdapter = new ResultAdapter(requireActivity());
        QRScanDatabase.getInstance().getAllFavorite(resultList -> {
            listResult.addAll(resultList);
            resultAdapter.setData(listResult, ResultAdapter.TYPE_IMPORTANT);
        });
    }

    @Override
    public void setUpViews() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorA8A4A4)), 0, 0, 0, 0);
        dividerItemDecoration.setDrawable(insetDrawable);

        getBinding().rvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
        getBinding().rvContent.setAdapter(resultAdapter);
        getBinding().rvContent.setPadding((int) DimenExtensionKt.dipToPix(16f, requireContext()), 0, (int) DimenExtensionKt.dipToPix(16f, requireContext()), 0);

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

        getBinding().imgBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).backToScanFragment();
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_importance;
    }
}
