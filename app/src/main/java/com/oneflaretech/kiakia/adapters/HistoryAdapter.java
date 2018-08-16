package com.oneflaretech.kiakia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oneflaretech.kiakia.Popup;
import com.oneflaretech.kiakia.QuickSearchPopup;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.models.ProfessionModel;

import java.util.ArrayList;


public class HistoryAdapter extends ArrayAdapter<ProfessionModel> {
private static class ViewHolder {
    TextView title;
}
    private final ArrayList<ProfessionModel> data;
    private final Context context;
    private final QuickSearchPopup fragment;
    private final Popup popup;


    public HistoryAdapter(ArrayList<ProfessionModel> data,
                          Context context,
                          QuickSearchPopup fragment,
                          Popup popup) {
        super(context, R.layout.professions_item, data);
        this.data = data;
        this.fragment = fragment;
        this.popup = popup;
        this.context = context;
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
                popup.dialog.hide();
                fragment.startSearch(data.get(position));

            }
        });
        convertView.setTag(viewHolder);

        ProfessionModel profession = data.get(position);
        //================= SET THE VALUES OF THE ROW =================
//        viewHolder.title.setText(professionsModels.title);
        viewHolder.title.setText(profession.profession);
        return convertView;
    }
}