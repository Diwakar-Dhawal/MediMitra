package com.example.medi_mitra_v1.Filters;

import android.widget.Filter;

import com.example.medi_mitra_v1.Adapters.PdfAdapter;
import com.example.medi_mitra_v1.Models.Reports;

import java.util.ArrayList;

public class FilterPdf extends Filter {
    ArrayList<Reports> filterList;
    PdfAdapter adapterCategory;
    String type="";
    public FilterPdf(ArrayList<Reports> filterList, PdfAdapter adapterCategory,String type) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
        this.type=type;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults resuts = new FilterResults();
        ArrayList<Reports> filterModels = new ArrayList<>();
        if(type.equals("Search")) {

            if (charSequence != null && charSequence.length() > 0) {
                charSequence = charSequence.toString().toUpperCase();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getPdfName().toUpperCase().contains(charSequence) || filterList.get(i).getDocName().toUpperCase().contains(charSequence) || filterList.get(i).getHosName().toUpperCase().contains(charSequence)) {
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
                if (filterList.get(i).getIsfav().equals("true")) {
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
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterCategory.list = (ArrayList<Reports>) filterResults.values;
        adapterCategory.notifyDataSetChanged();

    }
}
