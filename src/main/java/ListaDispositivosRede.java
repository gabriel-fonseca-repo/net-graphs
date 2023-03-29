import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class ListaDispositivosRede {

    public static void main(String[] args) throws SocketException {
        Graph<String, Integer> grafo = new SparseGraph<>();
        List<String> enderecos = new ArrayList<>();

        // Obter endereços IP de dispositivos conectados
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                if (address.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                    enderecos.add(address.getHostAddress());
                }
            }
        }

        // Adicionar nós do grafo
        for (String endereco : enderecos) {
            grafo.addVertex(endereco);
        }

        // Adicionar arestas do grafo
        for (int i = 0; i < enderecos.size() - 1; i++) {
            for (int j = i + 1; j < enderecos.size(); j++) {
                grafo.addEdge(i * enderecos.size() + j, enderecos.get(i), enderecos.get(j));
            }
        }

        // Exibir grafo visualmente
        CircleLayout<String, Integer> layout = new CircleLayout<>(grafo);
        layout.setSize(new Dimension(300, 300));
        BasicVisualizationServer<String, Integer> visualizacao = new BasicVisualizationServer<>(layout);
        visualizacao.setPreferredSize(new Dimension(350, 350));
        visualizacao.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        JFrame janela = new JFrame("Dispositivos de Rede");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.getContentPane().add(visualizacao);
        janela.pack();
        janela.setVisible(true);
    }
}