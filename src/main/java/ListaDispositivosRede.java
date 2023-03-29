import java.awt.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.net.util.SubnetUtils;

public class ListaDispositivosRede {

    public static void main(String[] args) {
        List<String> enderecos = new ArrayList<>();
        Graph<String, Integer> grafo = new SparseGraph<>();
        popularListaDeEnderecos(enderecos);
        popularGrafoComEnderecos(enderecos, grafo);
        renderizarJanela(grafo);
    }

    private static void popularGrafoComEnderecos(List<String> enderecos, Graph<String, Integer> grafo) {
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
    }

    private static void renderizarJanela(Graph<String, Integer> grafo) {
        CircleLayout<String, Integer> layout = new CircleLayout<>(grafo);
        layout.setSize(new Dimension(500, 300));
        BasicVisualizationServer<String, Integer> visualizacao = new BasicVisualizationServer<>(layout);
        visualizacao.setPreferredSize(new Dimension(350, 350));
        visualizacao.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        JFrame janela = new JFrame("Dispositivos de Rede");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.getContentPane().add(visualizacao);
        janela.pack();
        janela.setVisible(true);
    }

    private static void popularListaDeEnderecos(List<String> enderecos) {
        // Descobrir endereço IP e máscara de sub-rede da rede pública
        SubnetUtils subnet = new SubnetUtils("192.168.1.0/24");
        String[] ips = subnet.getInfo().getAllAddresses();
        List<String[]> ipsRepartidos = repartirArray(ips, 255);
        List<Thread> threads = new ArrayList<>();

        // Verificar quais dispositivos estão ativos na rede pública
        for (String[] listaIps : ipsRepartidos) {
            Thread thread = new Thread(() -> {
                for (String ip : listaIps) {
                    try {
                        if (InetAddress.getByName(ip).isReachable(1000)) {
                            enderecos.add(ip);
                            System.out.println(ip + " - Adicionado");
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (Exception ignored) {
            }
        }
    }

    public static List<String[]> repartirArray(String[] input, int quantidade) {
        List<String[]> retorno = new ArrayList<>();
        int size = input.length / quantidade;
        int remainder = input.length % quantidade;
        int offset = 0;

        for (int i = 0; i < quantidade; i++) {
            int arraySize = size + (i < remainder ? 1 : 0);
            String[] subArray = new String[arraySize];
            System.arraycopy(input, offset, subArray, 0, arraySize);
            offset += arraySize;
            retorno.add(subArray);
        }
        return retorno;
    }
}