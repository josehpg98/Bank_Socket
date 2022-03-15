package sockets2;

///inicializa o lado server
public class Inicializador {
///classe principal inicia a agencia
    public static void main (String[] args) {
        ///inicia nova agencia com o numero tal
        AgenciaBancaria agenciaA = new AgenciaBancaria(12345);
        
        ContaBancaria contaA = new ContaBancaria(987);
        ///conta A recebe o numero 987
        contaA.setAgencia(agenciaA);   
        ///set a agencia
        agenciaA.setContaBancaria(contaA.getNumero(), contaA);
        ///a agencia seta a conta a com o numero 12345 e a conta 987
        
        ContaBancaria contaB = new ContaBancaria(989);
        contaB.setAgencia(agenciaA);   
        agenciaA.setContaBancaria(contaB.getNumero(), contaB);
    }
}
