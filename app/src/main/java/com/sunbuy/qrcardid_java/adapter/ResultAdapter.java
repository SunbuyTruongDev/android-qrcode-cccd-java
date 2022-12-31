package com.sunbuy.qrcardid_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.base.common.extensions.ViewExtensionsKt;
import com.sunbuy.qrcardid_java.R;
import com.sunbuy.qrcardid_java.data.QRScanDatabase;
import com.sunbuy.qrcardid_java.data.entities.QRCodeResult;
import com.sunbuy.qrcardid_java.databinding.ItemResultBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<QRCodeResult> list = new ArrayList<>() ;
    private ResultAdapterListener listener ;
    private int type = TYPE_HISTORY ;
    private ArrayList<QRCodeResult> listSelected = new ArrayList<>() ;
    private boolean showDeleteHistory = false ;
    private Context context ;

    public ResultAdapter(Context context) {
        this.context = context;
    }

    public void setResultListener(ResultAdapterListener listener){
        this.listener = listener ;
    }

    public void setShowDeleteHistory(boolean show){
        this.showDeleteHistory =show ;
        notifyDataSetChanged();
    }

    public void setData(List<QRCodeResult> list, int type){
        this.type =type ;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ResultDiffUtil(this.list,list)) ;
        this.list.clear();
        this.list.addAll(list) ;
        diffResult.dispatchUpdatesTo(this);
    }

    public ArrayList<QRCodeResult> getListSelected(){
        return listSelected ;
    }

    public void clearSelected(){
        listSelected.clear();
        if (listener != null){
            listener.onCountSelected(listSelected);
        }
        notifyDataSetChanged();
    }

    public void removeFavorite(QRCodeResult item){
        int removeIndex = -1 ;
        for (int i =0 ; i < list.size() ; i++){
            if (item == list.get(i)){
                removeIndex = i ;
                break;
            }
        }

        if (removeIndex != 1){
            QRScanDatabase.getInstance().resultDao().update(list.get(removeIndex).getId(),0);
            list.remove(removeIndex) ;
            notifyItemRemoved(removeIndex) ;
            notifyItemRangeChanged(removeIndex, getItemCount() - removeIndex) ;
        }
    }

    public void removeHistory(ArrayList<QRCodeResult> listSelected){
        ArrayList<Long> ids =new ArrayList<>() ;
        for (int i = 0 ; i < listSelected.size() ; i++){
            ids.add(listSelected.get(i).getId()) ;
            list.remove(listSelected.get(i)) ;
        }
        QRScanDatabase.getInstance().resultDao().deleteHistories(ids);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResultBinding binding = ItemResultBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false) ;
        return new ResultViewHolder(binding) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        QRCodeResult result = list.get(position) ;
        holder.mBinding.tvType.setText(context.getString(R.string.txt_card_id));
        holder.mBinding.tvTime.setText(formatDate(result.getTime()));
        if (!showDeleteHistory){
            holder.mBinding.imgSelect.setImageResource(R.drawable.ic_unselected);
            holder.mBinding.imgSelect.setTag("unselected");
        }else {
            holder.mBinding.imgSelect.setImageResource(R.drawable.ic_selected);
            holder.mBinding.imgSelect.setTag("selected");
        }

        if (showDeleteHistory){
            holder.mBinding.imgSelect.setVisibility(View.VISIBLE);
        }else {
            holder.mBinding.imgSelect.setVisibility(View.GONE);
        }

        ViewExtensionsKt.setSingleClickListener(holder.mBinding.getRoot(), view -> {
                if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION){
                    if (showDeleteHistory){
                        selectItem(holder);
                    }else {
                        listener.onResultClick(list.get(holder.getBindingAdapterPosition()));
                    }
                }
            return null;
        });

        ViewExtensionsKt.setSingleClickListener(holder.mBinding.imgImportance,view -> {
            if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION){
                listener.onRemoveFavorite(list.get(holder.getBindingAdapterPosition()));
            }
            return null;
        });
    }

    public void selectItem(ResultViewHolder holder){
        if (holder.mBinding.imgSelect.getTag() == "unselected"){
            holder.mBinding.imgSelect.setImageResource(R.drawable.ic_selected);
            holder.mBinding.imgSelect.setTag("selected");
            listSelected.add(list.get(holder.getBindingAdapterPosition())) ;
        }else {
            holder.mBinding.imgSelect.setImageResource(R.drawable.ic_unselected);
            holder.mBinding.imgSelect.setTag("unselected");
            listSelected.remove(list.get(holder.getBindingAdapterPosition())) ;
        }
        if (listener != null){
            listener.onCountSelected(getListSelected());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder{

        ItemResultBinding mBinding;

        public ResultViewHolder( ItemResultBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

    class ResultDiffUtil extends DiffUtil.Callback {
        List<QRCodeResult> oldList ;
        List<QRCodeResult> newList ;

        public ResultDiffUtil(List<QRCodeResult> oldList, List<QRCodeResult> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId()){
                return true ;
            }else {
                return false ;
            }
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
           if (oldList.get(oldItemPosition).getId() != newList.get(newItemPosition).getId()){
               return false;
           }else {
               return true;
           }
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }

    public String formatDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd - MM - yyyy HH:mm", Locale.getDefault()) ;
        Date date = new Date(time) ;
        return simpleDateFormat.format(date) ;
    }


    public static int TYPE_HISTORY = 0;
    public static int TYPE_IMPORTANT = 1;

}
