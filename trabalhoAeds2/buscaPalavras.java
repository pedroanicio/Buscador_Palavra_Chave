/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trabalhoAeds2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author pedro
 */
class Palavra {

    String palavraChave;
    ArrayList<Ocorrencia> ocorrencias;
    
    //construtor
    public Palavra(String palavraChave) {
        this.palavraChave = palavraChave;
        this.ocorrencias = new ArrayList<>();
    }
}

class Ocorrencia {

    String nomeArquivo;
    int numeroOcorrencias;

    public Ocorrencia(String nomeArquivo, int numeroOcorrencias) {
        this.nomeArquivo = nomeArquivo;
        this.numeroOcorrencias = numeroOcorrencias;
    }
}

class BuscadorPalavrasChave {
    
    //o hashMap é uma estrutura de dados que mapeia chaves para valores. Neste caso, estamos usando uma 
    //chave do tipo String e um valor do tipo Palavra. A variável "tabelaHash" será usada para armazenar 
    //as palavras-chave e seus objetos correspondentes da classe "Palavra".
    HashMap<String, Palavra> tabelaHash;

    public BuscadorPalavrasChave() {
        this.tabelaHash = new HashMap<>();
    }
    public double calcularFatorDeCarga() {
        int numElementos = tabelaHash.size();
        int tamanhoTabela = tabelaHash.keySet().size(); // Número de buckets é igual ao número de chaves distintas na tabela hash.

        return (double) numElementos / tamanhoTabela;
    }
    

    public void lerArquivo(String nomeArquivo) {
        //Caminho dos arquivos a serem indexados.
        String caminho = "/Users/pedra/OneDrive/Documentos/NetBeansProjects/AEDS2/src/trabalhoAeds2/arquivos/";
        String arquivo = nomeArquivo;
        String txt = null;
        File file = new File(caminho + arquivo);
        //l er o arquivo e armazená-lo numa variavel String
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                //ignora todos as virgula, pontos, etc
                line = line.replaceAll("[,.:;]", "");
                txt = line + " " + txt;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Arquivo nao encontrado");
        }
        //chamar metodo para indexar 
        indexarDocumento(arquivo, txt);
    }

    public void indexarDocumento(String nomeArquivo, String textoDocumento) {
        //dividir a string textoDocumento em um array de palavras, utilizando o método split("\\s+"). A expressão \\s+ é usada para dividir a string em espaços em branco.
        String[] palavras = textoDocumento.split("\\s+");

        for (String palavra : palavras) {
            palavra = palavra.toLowerCase();

            // Ignorar stop words
            if (ehStopWord(palavra)) {
                continue;
            }

            //Essas linhas recuperam a palavra indexada correspondente à palavra da tabela hash chamada tabelaHash e a 
            //atribui a uma variável chamada palavraIndexada. 
            //Se a palavra indexada não existir na tabela hash, é criado um novo objeto Palavra com a palavra e é adicionado à tabela hash.
            Palavra palavraIndexada = tabelaHash.get(palavra);
            if (palavraIndexada == null) {
                palavraIndexada = new Palavra(palavra);
                tabelaHash.put(palavra, palavraIndexada);

            }
            //Essas linhas procuram uma ocorrência associada à palavraIndexada com base no nomeArquivo. Utilizando a funcionalidade de stream e 
            //operações de filtragem, a primeira ocorrência que corresponder ao nomeArquivo é recuperada. 
            //Se nenhuma ocorrência for encontrada, é atribuído null à variável ocorrencia.
            Ocorrencia ocorrencia = palavraIndexada.ocorrencias.stream()
                    .filter(o -> o.nomeArquivo.equals(nomeArquivo))
                    .findFirst()
                    .orElse(null);
            
            
            //Essas linhas verificam se a variável ocorrencia é null. Se for null, significa que não existe uma ocorrência para o nomeArquivo na palavraIndexada. 
            //Nesse caso, é criado um novo objeto Ocorrencia com o nomeArquivo e um número de ocorrências inicial de 1, e é adicionado à lista ocorrencias da 
            //palavraIndexada. Caso contrário, se a ocorrencia não for null, significa que já existe uma ocorrência para o nomeArquivo, então incrementa-se o número de ocorrências.
            if (ocorrencia == null) {
                ocorrencia = new Ocorrencia(nomeArquivo, 1);
                palavraIndexada.ocorrencias.add(ocorrencia);
            } else {
                ocorrencia.numeroOcorrencias++;
            }

            //System.out.println(tabelaHash.values());
        }
    }

    public void buscarPalavraChave(String palavraChave) {
        
        long startTime = System.nanoTime(); // Captura o tempo de início da busca

        // deixar letras minusculas
        palavraChave = palavraChave.toLowerCase();
       
        
        //Essa linha procura a palavra indexada correspondente à palavraChave na tabela hash chamada tabelaHash e atribui o resultado a uma 
        //variável chamada palavraIndexada.
        Palavra palavraIndexada = tabelaHash.get(palavraChave);
        
        if (palavraIndexada == null) {
            //Essas linhas verificam se a palavraIndexada é nula. Se for nula, significa que a palavra-chave buscada não foi encontrada 
            //na tabela hash. Nesse caso, exibe uma mensagem de diálogo informando que a palavra-chave 
            //não foi encontrada usando a classe JOptionPane e, em seguida, o método retorna, encerrando a execução.
            JOptionPane.showMessageDialog(null, "A palavra-chave não foi encontrada.");
            return;
        }
        
        
        //Essa linha classifica as ocorrências associadas à palavraIndexada.
        //Essa linha usa o método sort da classe Collections para ordenar a lista de ocorrências com base no número de ocorrências. 
        //A expressão (o1, o2) -> o2.numeroOcorrencias - o1.numeroOcorrencias é uma expressão lambda que especifica a lógica de comparação para ordenar os elementos.
        Collections.sort(palavraIndexada.ocorrencias, (o1, o2) -> o2.numeroOcorrencias - o1.numeroOcorrencias);

        
        //Essa linha inicia um loop for que itera sobre cada Ocorrencia na lista ocorrencias associada à palavraIndexada. 
        //A cada iteração, exibe uma mensagem de diálogo usando a classe JOptionPane, mostrando o nome do arquivo e o número de ocorrências para cada Ocorrencia encontrada.
        for (Ocorrencia ocorrencia : palavraIndexada.ocorrencias) {
            //JOptionPane.showMessageDialog(null, "Arquivo " + ocorrencia.nomeArquivo + "; número de ocorrências: " + ocorrencia.numeroOcorrencias);
            System.out.println("Arquivo " + ocorrencia.nomeArquivo + "; número de ocorrências: " + ocorrencia.numeroOcorrencias);
         
        }
        
        long endTime = System.nanoTime(); // Captura o tempo de término da busca em nanossegundos
        long elapsedTime = endTime - startTime; // Calcula o tempo decorrido da busca em nanossegundos

        // Convertendo o tempo para milissegundos antes de exibir no console
        double elapsedTimeMillis = elapsedTime / 1_000_000.0;
        System.out.println("Tempo de busca com Hash: " + elapsedTimeMillis + " milissegundos");

    }
    
        public void buscarPalavraChaveSequencial(String palavraChave) {
        long startTime = System.nanoTime(); // Captura o tempo de início da busca sequencial

        // deixar letras minusculas
        palavraChave = palavraChave.toLowerCase();

        boolean palavraEncontrada = false;

        // Percorre todos os arquivos para fazer a busca sequencial palavra por palavra
        for (int cont = 1; cont <= 5; cont++) {
            String caminho = "/Users/pedra/OneDrive/Documentos/NetBeansProjects/AEDS2/src/trabalhoAeds2/arquivos/";
            String arquivo = cont + ".txt";
            try (BufferedReader br = new BufferedReader(new FileReader(caminho + arquivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] palavras = line.split("\\s+");
                    for (String palavra : palavras) {
                        palavra = palavra.toLowerCase();
                        if (palavra.equals(palavraChave)) {
                            palavraEncontrada = true;
                            // Exibir as ocorrências associadas à palavra-chave
                            Palavra palavraIndexada = tabelaHash.get(palavra);
                            Collections.sort(palavraIndexada.ocorrencias, (o1, o2) -> o2.numeroOcorrencias - o1.numeroOcorrencias);
                            for (Ocorrencia ocorrencia : palavraIndexada.ocorrencias) {
                                System.out.println("Arquivo " + ocorrencia.nomeArquivo + "; número de ocorrências: " + ocorrencia.numeroOcorrencias);
                            }
                            break; // Sai do loop de palavras do arquivo
                        }
                    }
                    if (palavraEncontrada) {
                        break; // Sai do loop de leitura do arquivo
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Arquivo não encontrado");
            }
            if (palavraEncontrada) {
                break; // Sai do loop de arquivos
            }
        }

        if (!palavraEncontrada) {
            JOptionPane.showMessageDialog(null, "A palavra-chave não foi encontrada.");
        }

        long endTime = System.nanoTime(); // Captura o tempo de término da busca sequencial em nanossegundos
        long elapsedTime = endTime - startTime; // Calcula o tempo decorrido da busca sequencial em nanossegundos

        // Convertendo o tempo para milissegundos antes de exibir no console
        double elapsedTimeMillis = elapsedTime / 1_000_000.0;
        System.out.println("Tempo de busca sequencial: " + elapsedTimeMillis + " milissegundos");
    }
    
    private boolean ehStopWord(String palavra) {
        return false;
    }
}

public class buscaPalavras {

    public static void main(String[] args) {
        
        //Instancia nova classe NuscadorPalavrasChave
        BuscadorPalavrasChave buscador = new BuscadorPalavrasChave();
        double fatorCargaHash;

        String palavra = JOptionPane.showInputDialog("Digite a palavra a ser buscada: ");

        //ler e indexar os documentos
        //POG básica
        int cont = 1;
        do{
            // lê todos os arquivos da pasta automaticamente 
            buscador.lerArquivo(cont+".txt");
            cont++;
        }while(cont != 6);

        // buscar pela palavra chave
        buscador.buscarPalavraChave(palavra);
        buscador.buscarPalavraChaveSequencial(palavra);
        
        
        System.out.println("Fator de carga tabela hash: "+buscador.calcularFatorDeCarga());

    }
}
