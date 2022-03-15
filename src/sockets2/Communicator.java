package sockets2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Communicator extends Thread {

    private static final byte SAQUE = Config.SAQUE;
    private static final byte DEPOSITO = Config.DEPOSITO;
    private static final byte EXTRATO = Config.EXTRATO;

    private String defaultIP;
    private int DEFAULT_PORT = Config.DEFAULT_PORT;
    private SocketChannel clientChannel = null;
    private ServerSocketChannel serverChannel = null;
    private InetSocketAddress channelAddress = null;
    private BlockingQueue<ByteBuffer> incoming = new LinkedBlockingQueue<ByteBuffer>();
    private static Map<String, SocketChannel> clientSocketList;
    private boolean active;

    public Communicator(String defaultIP) {
        int defaultPORT = DEFAULT_PORT;
        this.defaultIP = defaultIP;
        boolean created = false;
        clientSocketList = new LinkedHashMap<String, SocketChannel>();
        while (!created) {
            try {
                serverChannel = ServerSocketChannel.open();
                channelAddress = new InetSocketAddress(this.defaultIP, defaultPORT);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            try {
                serverChannel.socket().bind(channelAddress);
                created = true;
            } catch (IOException e) {
                defaultPORT++;
            }
        }
        this.active = true;
        this.start();
    }

    ///construtor
    public Communicator() {

    }

    ///Executa a thread
    public void run() {
        try {
            while (this.active) {
                try {
                    clientChannel = serverChannel.accept();
                    clientSocketList.put(clientRemoteChannelDesc(), clientChannel);
                    rodaListener();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na execução! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void connectServer(String hostDescription) {
        try {
            String vet[] = hostDescription.split(":");
            String hostname = vet[0];
            int port = Integer.parseInt(vet[1].trim());
            clientChannel = SocketChannel.open(new InetSocketAddress(hostname, port));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        rodaListener();
    }

    private void rodaListener() {
        try {
            Listener l = new Listener(clientChannel, this.incoming);
            l.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String clientRemoteChannelDesc() {
        try {
            String hostAddress = clientChannel.socket().getInetAddress().getHostAddress();
            String portAddress = Integer.toString(clientChannel.socket().getPort());
            return hostAddress + ":" + portAddress;
        } catch (Exception e) {
            System.out.println("Erro do Cliente Remoto! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String serverChannelDescription() {
        try {
            String hostAddress = channelAddress.getAddress().getHostAddress();
            String portAddress = Integer.toString(channelAddress.getPort());
            return hostAddress + ":" + portAddress;
        } catch (Exception e) {
            System.out.println("Erro no Server Channel Description! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public SocketChannel getSocket() {
        try {
            return this.clientChannel;
        } catch (Exception e) {
            System.out.println("Erro ao Retornar o Socket do Canal do Cliente! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ByteBuffer receiveMessages() {
        try {
            return incoming.take();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    ///Mensagem Para Deposito
    public void MsgSend_Deposito(SocketChannel channel, int conta, float valorDeposito) {
        // TIPO_MSG, TAMANHO, CONTA, VALOR DO DEPOSITO
        // SHORTINT, INT, INT, FLOAT
        try {
            int tamMsg = 2 + 4 + 4 + 4;
            ByteBuffer writeBuffer = ByteBuffer.allocateDirect(tamMsg);
            writeBuffer.putShort(this.DEPOSITO);
            writeBuffer.putInt(tamMsg);
            writeBuffer.putInt(conta);
            writeBuffer.putFloat(valorDeposito);
            writeBuffer.rewind();
            channelWrite(channel, writeBuffer);
        } catch (Exception e) {
            System.out.println("Erro no Deposito! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    ///Mensagem Para Saaque
    public void MsgSend_Saque(SocketChannel channel, int conta, float valorSaque) {
        try {
            int tamMsg = 2 + 4 + 4 + 4;
            ByteBuffer writeBuffer = ByteBuffer.allocateDirect(tamMsg);
            writeBuffer.putShort(this.SAQUE);
            writeBuffer.putInt(tamMsg);
            writeBuffer.putInt(conta);
            writeBuffer.putFloat(valorSaque);
            writeBuffer.rewind();
            channelWrite(channel, writeBuffer);
        } catch (Exception e) {
            System.out.println("Erro no Saquue! "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    ///Mensagem Para Extrato
    public void MsgSend_Extrato(SocketChannel channel, int conta) {
        int tamMsg = 2 + 4 + 4 + 4;
        ByteBuffer writeBuffer = ByteBuffer.allocateDirect(tamMsg);
        writeBuffer.putShort(this.EXTRATO);
        writeBuffer.putInt(tamMsg);
        writeBuffer.putInt(conta);
        writeBuffer.rewind();
        channelWrite(channel, writeBuffer);
    }

    public void channelWrite(SocketChannel channel, ByteBuffer writeBuffer) {
        // TODO Auto-generated method stub
        try {
            long nbytes = 0;
            long toWrite = writeBuffer.remaining();
            try {
                while (nbytes != toWrite) {
                    nbytes += channel.write(writeBuffer);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (ClosedChannelException cce) {
                cce.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            writeBuffer.rewind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
