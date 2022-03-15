package sockets2;

import java.util.Scanner;
import java.nio.ByteBuffer;

public class Cliente extends Thread {

    private String DEFAULT_IP = Config.DEFAULT_IP;
    private int DEFAULT_PORT = Config.DEFAULT_PORT;
    private Communicator canalComServidor;
    private Scanner entrada;
    private short tipoMsg;
    private int tamMsg;
    ///private float tsaldo = 0;

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.conectarServidor();
        cliente.menu();
    }
    
    public void run() {///Executa a thread
        try {
            ByteBuffer buf = null;
            System.out.println("Cliente =>");
            System.out.println("\t Recebendo Mensagens ... \n");
            while (true) {
                buf = this.canalComServidor.receiveMessages();
                this.tipoMsg = buf.getShort();
                this.tamMsg = buf.getInt();
                switch (this.tipoMsg) {
                    case Config.EXTRATO:
                        System.out.println("Cliente =>");
                        System.out.println("\t Recebi Msg EXTRATO");
                        ///tsaldo = this.entrada.nextFloat();
                        break;
                    default:
                        System.out.println("Cliente =>");
                        System.out.println("\t\t TIPO DE MENSAGEM INVALIDA: " + tipoMsg + "\n");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro no Reccebimento de Mensagens do Servidor!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    public void conectarServidor() {
        try {
            this.canalComServidor = new Communicator();
            this.canalComServidor.connectServer(this.DEFAULT_IP + ":" + this.DEFAULT_PORT);
            this.start();
        } catch (Exception e) {
            System.out.println("Erro ao Conectar no Servidor!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void menu() {
        try {
            int opcao;
            this.entrada = new Scanner(System.in);
            System.out.println("==== Menu de Opções ===");
            System.out.println("\t 1 - Deposito ");
            System.out.println("\t 2 - Saque ");
            System.out.println("\t 3 - Consulta extrato ");
            System.out.println("\t 4 - Sair ");
            System.out.println("======================= ");
            do {
                System.out.println("Digite a opção:");
                opcao = this.entrada.nextInt();
                switch (opcao) {
                    case 1:
                        deposito();
                        break;
                    case 2:
                        saque();
                        break;
                    case 3:
                        extrato();
                        break;
                    case 4: 
                        sair();
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } while (opcao != 0);
        } catch (Exception e) {
            System.out.println("Erro ao Mostrar as Opções no Menu!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    ///Método para deposito
    public void deposito() {
        try {
            this.entrada = new Scanner(System.in);
            System.out.println("Informe a conta bancaria: ");
            int conta  = this.entrada.nextInt();
            System.out.println("Informe o valor para deposito: ");
            float valorDeposito = this.entrada.nextFloat();
            this.canalComServidor.MsgSend_Deposito(this.canalComServidor.getSocket(), conta, valorDeposito);
        } catch (Exception e) {
            System.out.println("Erro ao realizar o Deposito!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    ///Método Para Saque
    public void saque() {
        try {
            this.entrada = new Scanner(System.in);
            System.out.println("Informe a Conta Bancaria: ");
            int conta = this.entrada.nextInt();
            System.out.println("Informe o Valor que Deseja Sacar: ");
            float saquevalor = this.entrada.nextFloat();
            this.canalComServidor.MsgSend_Saque(this.canalComServidor.getSocket(), conta, saquevalor);
        } catch (Exception e) {
             System.out.println("Erro ao realizar o Saque!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    ///Método Para Extrato
    public void extrato() {
        try {
             System.out.println("Informe a Conta Bancaria: ");
            int conta = this.entrada.nextInt();
            this.canalComServidor.MsgSend_Extrato(this.canalComServidor.getSocket(), conta);
        } catch (Exception e) {
             System.out.println("Erro ao realizar o Extrato!"+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    ///Método que Força a Saida do cliente
    public void sair(){
        System.out.println("Saindo ->.....");
        Communicator.interrupted();
        System.exit(0);
    }
}
