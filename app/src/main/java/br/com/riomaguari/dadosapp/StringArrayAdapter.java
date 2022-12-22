package br.com.riomaguari.dadosapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class StringArrayAdapter extends ArrayAdapter<String> {

    public ArrayList<String> StringList;
    public ArrayList<String> StringListTemp;
    public StringDataFilter stringDataFilter;

    private int layoutId;

    private ColorStateList defaultColors;

    public StringArrayAdapter(Context context, int layoutId, ArrayList<String> stringArrayList) {

        super(context, layoutId, stringArrayList);

        this.StringListTemp = new ArrayList<String>();
        this.StringListTemp.addAll(stringArrayList);
        this.StringList = new ArrayList<String>();
        this.StringList.addAll(stringArrayList);

        this.layoutId = layoutId;
    }

    @Override
    public Filter getFilter() {

        if (stringDataFilter == null){
            stringDataFilter = new StringDataFilter();
        }
        return stringDataFilter;
    }

    public class ViewHolder {
        TextView tvCodigo;
        TextView tvNome;
    }

/*    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(layoutId, null);

            holder = new ViewHolder();

            holder.tvCodigo = convertView.findViewById(R.id.tvCodigo);
            holder.tvNome = convertView.findViewById(R.id.tvNome);

            convertView.setTag(holder);
            convertView.setTag(R.id.tvCodigo, holder.tvCodigo);
            convertView.setTag(R.id.tvNome, holder.tvNome);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String string = StringListTemp.get(position);

        holder.tvCodigo.setText(string);
        holder.tvNome.setVisibility(View.GONE);

        return convertView;

    }
*/
    private class StringDataFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.toString().length() > 0)
            {
                ArrayList<String> arrayList1 = new ArrayList<String>();

                for(int i = 0, l = StringList.size(); i < l; i++)
                {
                    String string= StringList.get(i);

                    if(string.toLowerCase().contains(charSequence))

                        arrayList1.add(string);
                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            }
            else
            {
                synchronized(this)
                {
                    filterResults.values = StringList;

                    filterResults.count = StringList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            StringListTemp = (ArrayList<String>)filterResults.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = StringListTemp.size(); i < l; i++)
                add(StringListTemp.get(i));

            notifyDataSetInvalidated();
        }
    }

}