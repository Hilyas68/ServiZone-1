package com.oneflaretech.kiakia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.oneflaretech.kiakia.Popup;
import com.oneflaretech.kiakia.QuickSearchPopup;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.activities.ProfileActivity;
import com.oneflaretech.kiakia.interfaces.ChooseProfession;
import com.oneflaretech.kiakia.models.ProfessionModel;

import java.util.ArrayList;
import java.util.List;


public class ProfessionsAdapter extends ArrayAdapter<ProfessionModel> implements Filterable{

    private static class ViewHolder {
        TextView title;
    }
    private List<ProfessionModel> data;
    private List<ProfessionModel> originalList = new ArrayList<ProfessionModel>();
    private final Context context;
    private QuickSearchPopup fragment = null;
    private Popup popup = null;
    ChooseProfession activity;
    private ItemFilter mFilter = new ItemFilter();


    public ProfessionsAdapter(List<ProfessionModel> data,
                              Context context,
                              QuickSearchPopup fragment,
                              Popup popup) {
        super(context, R.layout.professions_item, data);
        this.data = data;
        this.fragment = fragment;
        this.popup = popup;
        this.context = context;
        this.originalList.addAll(data);
    }

    public ProfessionsAdapter(List<ProfessionModel> data, Context context, ChooseProfession activity) {
        super(context, R.layout.professions_item, data);
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.originalList.addAll(data);
    }

    public ProfessionsAdapter(List<ProfessionModel> data, Context context, ProfileActivity activity) {
        super(context, R.layout.professions_item, data);
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.originalList.addAll(data);
    }



    public long getItemId(int position) {
        return position;
    }

    //========================= GET THE ROW  LOGIC HAPPENS HERE=============================
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; // view lookup cache stored in tag

        //================ GET REFERENCE TO ROW VIEWS ================
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.professions_item, parent, false);
        viewHolder.title = convertView.findViewById(R.id.professionsitem);

        //===================== ROW CLICK LISTENER =====================
        viewHolder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popup != null) {
                    popup.dialog.hide();
                    fragment.startSearch(data.get(position));
                }
                else{
                    activity.selectProfession(data.get(position));
                }

            }
        });
        convertView.setTag(viewHolder);

        try{
            ProfessionModel profession = data.get(position);
            //================= SET THE VALUES OF THE ROW =================
//        viewHolder.title.setText(professionsModels.title);
            viewHolder.title.setText(profession.profession);
        }catch (Exception ex){}
        return convertView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                List<ProfessionModel> filteredItems = new ArrayList<ProfessionModel>();

                for(int i = 0, l = originalList.size(); i < l; i++)
                {
                    ProfessionModel country = originalList.get(i);
                    if(country.profession.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            data = (ArrayList<ProfessionModel>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = data.size(); i < l; i++)
                add(data.get(i));
            notifyDataSetInvalidated();
        }
    }
}