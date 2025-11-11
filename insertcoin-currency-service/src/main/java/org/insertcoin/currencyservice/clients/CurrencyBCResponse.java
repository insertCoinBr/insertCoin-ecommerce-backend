package org.insertcoin.currencyservice.clients;

import java.util.List;

public class CurrencyBCResponse {

    private List<Value> value;

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public static class Value {
        private double cotacaoCompra;
        private double cotacaoVenda;
        private String dataHoraCotacao;

        public double getCotacaoCompra() {
            return cotacaoCompra;
        }

        public void setCotacaoCompra(double cotacaoCompra) {
            this.cotacaoCompra = cotacaoCompra;
        }

        public double getCotacaoVenda() {
            return cotacaoVenda;
        }

        public void setCotacaoVenda(double cotacaoVenda) {
            this.cotacaoVenda = cotacaoVenda;
        }

        public String getDataHoraCotacao() {
            return dataHoraCotacao;
        }

        public void setDataHoraCotacao(String dataHoraCotacao) {
            this.dataHoraCotacao = dataHoraCotacao;
        }
    }
}
