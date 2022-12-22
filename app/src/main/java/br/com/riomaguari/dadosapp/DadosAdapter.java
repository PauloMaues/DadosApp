package br.com.riomaguari.dadosapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.com.riomaguari.dadosapp.R;

public class DadosAdapter extends ArrayAdapter<Dados> {

    public static ArrayList<Dados> dadosList;
    public static ArrayList<Dados> dadosListTemp;

    public DadosDataFilter dadosDataFilter;

    public static final String FORMAT_R$ = "###,###,###.##";

    //private ColorStateList defaultColors1, defaultColors2, defaultColors3;

    public DadosAdapter(Context context, int id, ArrayList<Dados> dadosArrayList) {

        super(context, id, dadosArrayList);

        dadosListTemp = new ArrayList<Dados>();
        dadosListTemp.addAll(dadosArrayList);
        dadosList = new ArrayList<Dados>();
        dadosList.addAll(dadosArrayList);
    }

    @Override
    public Filter getFilter() {
        if (dadosDataFilter == null){
            dadosDataFilter = new DadosDataFilter();
        }
        return dadosDataFilter;
    }

    public class ViewHolder {

        TextView tvId;
        TextView tvEndereco;
        TextView tvBairro;
        TextView tvCidade;
        TextView tvUf;
        TextView tvComplemento;
        TextView tvQtde;
        TextView tvHorario;
        TextView tvCaracteristicas;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        DecimalFormat df = new DecimalFormat("###,###.##");

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.layout_dados, null);

            holder = new ViewHolder();

            holder.tvId = convertView.findViewById(R.id.tvId);
            holder.tvEndereco = convertView.findViewById(R.id.tvEndereco);
            holder.tvComplemento = convertView.findViewById(R.id.tvComplemento);
            holder.tvBairro = convertView.findViewById(R.id.tvBairro);
            holder.tvCidade = convertView.findViewById(R.id.tvCidade);
            holder.tvUf = convertView.findViewById(R.id.tvUf);
            holder.tvQtde = convertView.findViewById(R.id.tvQtde);
            holder.tvHorario = convertView.findViewById(R.id.tvHorario);
            holder.tvCaracteristicas = convertView.findViewById(R.id.tvCaracteristicas);

            convertView.setTag(holder);
            convertView.setTag(R.id.tvId, holder.tvId);
            convertView.setTag(R.id.tvEndereco, holder.tvEndereco);
            convertView.setTag(R.id.tvComplemento, holder.tvComplemento);
            convertView.setTag(R.id.tvBairro, holder.tvBairro);
            convertView.setTag(R.id.tvCidade, holder.tvCidade);
            convertView.setTag(R.id.tvUf, holder.tvUf);
            convertView.setTag(R.id.tvQtde, holder.tvQtde);
            convertView.setTag(R.id.tvHorario, holder.tvHorario);
            convertView.setTag(R.id.tvCaracteristicas, holder.tvCaracteristicas);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Dados dados = dadosListTemp.get(position);

        holder.tvId.setText(dados.id);
        holder.tvEndereco.setText(dados.endereco);
        holder.tvComplemento.setText(dados.complemento);
        holder.tvBairro.setText(dados.bairro);
        holder.tvCidade.setText(dados.cidade);
        holder.tvUf.setText(dados.uf);
        holder.tvQtde.setText(dados.qtde);
        holder.tvHorario.setText(dados.horario);
        holder.tvCaracteristicas.setText(dados.caracteristicas);

        return convertView;

    }

    public void updateItens(ArrayList<Dados> itens) {
        /*      this.updateItens(itens);*/
        notifyDataSetChanged();
    }

/*    private void atualizatvSelecionados() {
        int nSelecionados = 0;
        for (int i = 0; i < dadosListTemp.toArray().length; i++) {
            if (dadosListTemp.get(i).isSelected)
                nSelecionados ++;
        }
        DadosActivity.tvSelecionados.setText(Html.fromHtml(String.format("<b>%s</b> Selecionados",nSelecionados)));
    }*/

    private class DadosDataFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.toString().length() > 0)
            {
                ArrayList<Dados> arrayList1 = new ArrayList<Dados>();

                for(int i = 0, l = dadosList.size(); i < l; i++)
                {
                    Dados dados = dadosList.get(i);

                    if( dados.endereco.toLowerCase().contains(((String) charSequence).toLowerCase()) ||
                        dados.bairro.contains((String) charSequence) ||
                        dados.cidade.contains((String) charSequence) ||
                        dados.complemento.toLowerCase().contains((String) charSequence)
                    )
                        arrayList1.add(dados);

                }

                filterResults.count = arrayList1.size();
                filterResults.values = arrayList1;
            }
            else
            {
                synchronized(this)
                {
                    filterResults.values = dadosList;
                    filterResults.count = dadosList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            dadosListTemp = (ArrayList<Dados>)filterResults.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = dadosListTemp.size(); i < l; i++)
                add(dadosListTemp.get(i));

            notifyDataSetInvalidated();
        }

    }

}