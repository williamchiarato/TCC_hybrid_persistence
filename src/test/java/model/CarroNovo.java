package model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Calendar;

@Table(name = "carro_novo")
public class CarroNovo {

    @Id
    @Column(name = "_id")
    private String id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_compra")
    private Calendar dataCompra;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Calendar getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Calendar dataCompra) {
        this.dataCompra = dataCompra;
    }
}
