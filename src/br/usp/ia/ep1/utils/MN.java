package br.usp.ia.ep1.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import br.usp.ia.ep1.DescDados;
import br.usp.ia.ep1.PreProcessamento;
import br.usp.ia.ep1.MLP.DadosDeEntradaProcessados;
import br.usp.ia.ep1.MLP.DadosDeTeste;

public class MN {
	
	/* Transforma uma String em um Array de Floats, separando os valores pelo char escolhido para limite */
	public static float[][] transformarArrayStringParaFloat(String[] dados, String chrDelimit) {
		float[][] dadosFloat = new float[dados.length][];
		float[] dadoFloat;
		String[] atribs;
		int i = 0;
		for(String linha : dados) {
			atribs = linha.split(chrDelimit);
			dadoFloat = new float[atribs.length];
			for(int j = dadoFloat.length-1; j > -1; j--) {
				dadoFloat[j] = Float.valueOf(atribs[j]);
			}
			dadosFloat[i] = dadoFloat;
			i++;
		}	
		return dadosFloat;
	}
	
	public static int valorMaximoClasse(float[][] dados){
		int valorMaximo = 0;
		for (int i = 0; i < dados.length; i++){
			if(dados[i][dados[i].length-1] > valorMaximo) valorMaximo = (int) dados[i][dados[i].length-1];
		}		
		return valorMaximo;
	}
	
	public static int valorMinimoClasse(float[][] dados){
		int valorMinimo = 0;
		for (int i = 0; i < dados.length; i++){
			if(dados[i][dados[i].length-1] < valorMinimo) valorMinimo = (int) dados[i][dados[i].length-1];
		}		
		return valorMinimo;
	}
	
	// Transforma uma matriz tridimencional em bidimencional, em relação a qual arquivo escolhido.
	public static float[][] transformaBidimensional(float[][][] tridimencional, int arquivo){
		float[][] aux = new float[tridimencional[arquivo].length][tridimencional[arquivo][0].length];
		
		for(int i = 0; i < aux.length; i++){
			for( int j = 0; j < aux[i].length; j++){
				aux[i][j] = tridimencional [arquivo][i][j]; // Passa o valor de um para o outro.
			}
		}
		
		return aux;
	}
	
	// Transforma tres matrizes tridimencionais em uma tridimencional.
	public static float[][][] transformaTridimensional(float[][] primeira, float[][] segunda, float[][] terceira){
			float[][][] aux = new float[3][][]; // Instancia a auxiliar (a terceira dimencao, que é a dos arquivos).
		
			aux[0] = new float[primeira.length][primeira[0].length]; // Instancia a dimenção dos dados dos valores.
			aux[1] = new float[segunda.length][segunda[0].length];
			aux[2] = new float[terceira.length][terceira[0].length];
		
			for(int i = 0; i < primeira.length; i++){ // Copia o valor para a auxiliar.
				for( int j = 0; j < primeira[i].length; j++){
					aux[0][i][j] = primeira[i][j];
				}
			}
			for(int i = 0; i < segunda.length; i++){
				for( int j = 0; j < segunda[i].length; j++){
					aux[1][i][j] = segunda[i][j];
				}
			}
			for(int i = 0; i < terceira.length; i++){
				for( int j = 0; j < terceira[i].length; j++){
					aux[2][i][j] = terceira[i][j];
				}
			}	
		
		return aux;
	}
	
	/* Método que escreve os arquivos, usando métodos auxiliares da classe "ES" */
	public static void criarArquivo(float[][] dados, String arq) throws IOException {
		String dado = new String();
		int limite = Math.round(dados.length * 0.1F);
		String[] dadosArq = new String[((int)Math.floor(dados.length/limite)) + 1];
		int cnt = 0;
		
		for(int i = dados.length-1; i > -1; i--) {
			for(int j = 0; j < dados[i].length-1; j++) {
				dado += String.valueOf(dados[i][j]) + PreProcessamento.CHR_DELIMIT;
			}
			dado += String.valueOf(dados[i][dados[i].length-1]) + "\n";
			if((i+1)%limite == 0) {
				dadosArq[cnt++] = dado;
				dado = new String();
			}
		}
		
		if(!dado.isEmpty()) {
			dadosArq[cnt] = dado;
		} else {
			dadosArq[cnt] = "";
		}

		ES.escreverDados(arq, dadosArq);
	}
	
	/* Método que retorna a descrição dos dados para usar em log e prints eventualmente */
	public static DescDados descDados(float[][] dados, int posClasse) {
		DescDados desc = new DescDados();
		
		float[] minMaxVlrClasses = new float[] { Float.MAX_VALUE, Float.MIN_VALUE };
		float[] minMaxVlrAtribs = new float[] { Float.MAX_VALUE, Float.MIN_VALUE };
		float[] minMaxRef;
		int qtdDados = 0;
		int qtdAtribs = 0;
		boolean qtdAtribOK = true;
		
		Set<Float> qtdVlrAtribs = new HashSet<Float>();
		Set<Float> qtdVlrClasses = new HashSet<Float>();
		
		for(float[] dado : dados) {
			for(int i = dado.length-1; i > -1; i--) {
				if(i == posClasse) {
					minMaxRef = minMaxVlrClasses;
					qtdVlrClasses.add(dado[i]);
				} else {
					minMaxRef = minMaxVlrAtribs;
				}
				if(dado[i] < minMaxRef[0]) {
					minMaxRef[0] = dado[i];
				}
				if(dado[i] > minMaxRef[1]) {
					minMaxRef[1] = dado[i];
				}
			}
			if(qtdAtribOK) {
				if(qtdAtribs == 0) {
					qtdAtribs = dado.length;
				}
				qtdAtribOK = dado.length == qtdAtribs;
				qtdAtribs = dado.length;
			}
			qtdDados++;
		}
		
		if(!qtdAtribOK) {
			qtdAtribs = -1;
		}
		
		desc.setMinVlrAtrib(minMaxVlrAtribs[0]);
		desc.setMaxVlrAtrib(minMaxVlrAtribs[1]);
		desc.setMinVlrClasse(minMaxVlrClasses[0]);
		desc.setMaxVlrClasse(minMaxVlrClasses[1]);
		desc.setQtdVlrAtribs(qtdVlrAtribs.size());
		desc.setQtdVlrClasses(qtdVlrClasses.size());
		desc.setQtdAtribs(qtdAtribs);
		desc.setQtdDados(qtdDados);
		
		return desc;
	}

	/* Metodo que transforma a saida de ES.lerArquivo em um objeto DadosDeEntradaProcessados[] para ser passado para a MLP */
	public static DadosDeEntradaProcessados[] transformarDadosTreino(String[] linhasArquivo) {
		DadosDeEntradaProcessados[] dados = new DadosDeEntradaProcessados[linhasArquivo.length];
		for (int i = 0; i < linhasArquivo.length; i++) {
			String[] aux = linhasArquivo[i].split(",");
			double[] dado = new double[aux.length - 1];
			for (int x = 0; x < aux.length - 1; x++) {
				dado[x] = Double.valueOf(aux[x]);
			}
			double classe = Double.valueOf(aux[aux.length-1]);
			dados[i] = new DadosDeEntradaProcessados(classe, dado);
		}
		return dados;
	}
	
	/* Metodo que transforma a saida de ES.lerArquivo em um objeto DadosDeTeste[] para ser passado para a MLP */
	public static DadosDeTeste[] transformarDadosTeste(String[] linhasArquivo) {
		DadosDeTeste[] dados = new DadosDeTeste[linhasArquivo.length];
		for (int i = 0; i < linhasArquivo.length; i++) {
			String[] aux = linhasArquivo[i].split(",");
			double[] dado = new double[aux.length - 1];
			for (int x = 0; x < aux.length - 1; x++) {
				dado[x] = Double.valueOf(aux[x]);
			}
			double classeReal = Double.valueOf(aux[aux.length-1]);
			dados[i] = new DadosDeTeste(classeReal, dado);
		}
		return dados;
	}
	
	public static void EscreverRespostaTesteMLP(DadosDeTeste[] resultado, String arquivo) throws IOException {
		String[] resultadoEscrever = dadosDeTesteParaStringArray(resultado);
		ES.escreverDadosPulandoLinha(arquivo, resultadoEscrever);
	}
	
	public static String[] dadosDeTesteParaStringArray(DadosDeTeste[] resultado) {
		String[] resultadoEmString = new String[resultado.length + 3]; // +3 para rotulos
		String header0 = "Respostas da rede MLP para o conjunto de testes:";
		String header1 = "CR = Classe Real, CP = Classe Predita, DT = Dados Testados";
		String header2 = "CR,CP,DT";
		resultadoEmString[0] = header0;
		resultadoEmString[1] = header1;
		resultadoEmString[2] = header2;
		String[] aux;
		for (int i = 0; i < resultado.length; i++) {
			aux = new String[resultado[i].getQuantidadeAtributos() + 2];
			aux[0] = String.valueOf((int)resultado[i].getClasseReal());
			aux[1] = "," + String.valueOf((int)resultado[i].getClassePredita());
			double[] dadosTestados = resultado[i].getDadosDeTeste();
			for (int y = 0; y < dadosTestados.length; y++) {
				aux[y+2] = "," + String.valueOf(dadosTestados[y]);
			}
			resultadoEmString[i+3] = StringArrayParaSring(aux);
		}
		
		return resultadoEmString;
	}
	
	/* Transforma uma String em array em uma String */
	public static String StringArrayParaSring(String[] array) {
		StringBuffer resultado = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
		   resultado.append( array[i] );
		}
		String stringResultante = resultado.toString();
		return stringResultante;
	}
}
