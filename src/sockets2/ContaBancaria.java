package sockets2;

///classe de Conta Bancaria
public class ContaBancaria {

    ///Atributos
    private int numero;
    private float saldo;
    private float limite;
    private AgenciaBancaria agencia;

    ///Construtor Publico da classe
    public ContaBancaria(int numero) {
        ///Autoreferência o número e limite
        this.numero = numero;
        this.limite = 100;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void deposita(float valor) {
        this.saldo += valor;
    }

    public void saca(float valor) {
        this.saldo -= valor;
    }

    public AgenciaBancaria getAgencia() {
        return agencia;
    }

    public void setAgencia(AgenciaBancaria agencia) {
        this.agencia = agencia;
    }

    public float getSaldo() {
        return this.saldo + this.limite;
    }
    
    public void setLimite(float limite) {
        this.limite = limite;
    }
    
    public float getLimite() {
        return this.limite;
    }
}
