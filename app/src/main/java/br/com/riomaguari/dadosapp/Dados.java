package br.com.riomaguari.dadosapp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Dados implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("endereco")
    public String endereco;

    @SerializedName("bairro")
    public String bairro;

    @SerializedName("complemento")
    public String complemento;

    @SerializedName("cidade")
    public String cidade;

    @SerializedName("uf")
    public String uf;

    @SerializedName("qtde")
    public String qtde;

    @SerializedName("horario")
    public String horario;

    @SerializedName("caracteristicas")
    public String caracteristicas;

    @SerializedName("isSelected")
    public String isSelected;

}
