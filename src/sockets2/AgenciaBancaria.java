package sockets2;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

public class AgenciaBancaria extends Thread {

    private String DEFAULT_IP = Config.DEFAULT_IP;
    private int numero;
    private Communicator canalDoServidor;
    private LinkedHashMap<Integer, ContaBancaria> listaContas;
    private short tipoMsg;
    private int conta;
    private float valorSaque;
    private float valorDeposito;
    private int tamMsg;
    private ContaBancaria cb;

    public AgenciaBancaria(int numero) {
        try {
            this.setNumero(numero);
            this.canalDoServidor = new Communicator(this.DEFAULT_IP);
            this.listaContas = new LinkedHashMap<Integer, ContaBancaria>();
            System.out.println("Servidor da agência: " + this.getNumero()
                    + " Iniciado no canal de Atendimento: " + this.canalDoServidor.serverChannelDescription());
            this.start();///Inicia a thread
        } catch (Exception e) {
            System.out.println("Erro a Conectar no Canal: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            ByteBuffer buf = null;
            System.out.println("Agencia Central =>");
            System.out.println("\t Recebendo as Mensagens ... \n");
            while (true) {
                buf = this.canalDoServidor.receiveMessages();
                this.tipoMsg = buf.getShort();
                this.tamMsg = buf.getInt();
                switch (this.tipoMsg) {
                    case Config.DEPOSITO:
                        this.conta = buf.getInt();
                        this.valorDeposito = buf.getFloat();
                        System.out.println("Cliente =>");
                        System.out.println("\t Recebi Mensagem: DEPOSITO");
                        System.out.println("\t Conta Bancaria: " + this.conta);
                        System.out.println("\t Valor do Deposito: " + this.valorDeposito);
                        break;
                    case Config.SAQUE:
                        this.conta = buf.getInt();
                        this.valorSaque = buf.getFloat();
                        System.out.println("\t Recebi Mensagem: SAQUE");
                        System.out.println("\t Conta Bancaria: " + this.conta);
                        System.out.println("\t Valor do Saque: " + this.valorSaque);
                        break;
                    case Config.EXTRATO:
                        this.conta = buf.getInt();
                        System.out.println("\t Recebi Mensagem: EXTRATO");
                        System.out.println("\t Conta Bancaria: " + this.conta);
                        System.out.println("Saldo: "+cb.getSaldo());
                        ///System.out.println("Limite: "+cb.getLimite());
                        System.out.println("\t Valor Depositado: " + this.valorDeposito);
                        System.out.println("\t Valor Sacado: " + this.valorSaque);
                        break;
                    default:
                        System.out.println("Cliente =>");
                        System.out.println("\t\t TIPO DE MENSAGEM INVALIDA: " + tipoMsg + "\n");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao mostrar os Dados! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getNumero() {
        return this.numero;
    }

    public void setContaBancaria(int numeroConta, ContaBancaria conta) {
        try {
            this.listaContas.put(numeroConta, conta);
        } catch (Exception e) {
            System.out.println("Erroa ao Setar a Conta!" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public ContaBancaria getContaBancaria(int numeroConta) {
        try {
            return this.listaContas.get(numeroConta);
        } catch (Exception e) {
            System.out.println("Erro ao Listar Contas! " + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void tratamentoMsgSaque(ContaBancaria conta, float valorSaque) {
        float valorSaldo = 0;
        valorSaldo = conta.getSaldo();
        try {
            if (conta.getNumero() == this.conta && valorSaque > 100 && valorSaque >= conta.getSaldo()) {
                valorSaldo -= valorSaque;
                System.out.println("Saque realizado com sucesso!");
            } else {
                System.out.println("Faça um saque com valor mior que R$50.00");
            }
        } catch (Exception e) {
            System.out.println("Erro ao Efetuar o Saque!" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void tratamentoMsgDeposito(ContaBancaria conta, float valorDeposito) {
        try {
            
        } catch (Exception e) {
            System.out.println("Erro ao fazer o Deposito! "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
