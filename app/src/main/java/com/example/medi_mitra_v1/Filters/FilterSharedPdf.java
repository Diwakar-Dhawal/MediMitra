package com.example.medi_mitra_v1.Filters;

import android.widget.Filter;

import com.example.medi_mitra_v1.Adapters.SharedPdfAdapter;
import com.example.medi_mitra_v1.Models.SharedReports;

import java.util.ArrayList;

public class FilterSharedPdf extends Filter{
    ArrayList<SharedReports> filterList;
    SharedPdfAdapter adapterCategory;
    String type="";
    public FilterSharedPdf(ArrayList<SharedReports> filterList, SharedPdfAdapter adapterCategory,String type) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
        this.type=type;
    }


    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        Filter.FilterResults resuts = new Filter.FilterResults();
        ArrayList<SharedReports> filterModels = new ArrayList<>();
        if(type.equals("Search")) {

            if (charSequence != null && charSequence.length() > 0) {
                charSequence = charSequence.toString().toUpperCase();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getSharedpdfName().toUpperCase().contains(charSequence) || filterList.get(i).getShareddocName().toUpperCase().contains(charSequence) || filterList.get(i).getSharedhosName().toUpperCase().contains(charSequence) || filterList.get(i).getShareduid().toUpperCase().contains(charSequence)) {
                        filterModels.add(filterList.get(i));
                    }
                }
                resuts.count = filterModels.size();
                resuts.values = filterModels;
            } else {
                resuts.count = filterList.size();
                resuts.values = filterList;
            }

        }
        else if(type.equals("FavoritesOn"))
        {
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getSharedisfav().equals("true")) {
                    filterModels.add(filterList.get(i));
                }
            }
            resuts.count = filterModels.size();
            resuts.values = filterModels;

        }
        else if(type.equals("FavoritesOff"))
        {
            resuts.count = filterList.size();
            resuts.values = filterList;
        }
        return resuts;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        adapterCategory.list = (ArrayList<SharedReports>) filterResults.values;
        adapterCategory.notifyDataSetChanged();

    }
}
