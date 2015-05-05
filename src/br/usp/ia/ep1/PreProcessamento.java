package br.usp.ia.ep1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import br.usp.ia.ep1.utils.ES;

/*
 * Depois pensar num nome pra classe
 * Essa classe eh responsavel pelo pre-processamento dos dados
 */
public class PreProcessamento {
	
	public static final String CHR_DELIMIT = ",";
	
	// Quantidade de atributos por instancia no conjunto de dados
	public static final int numeroAtributosPorInstancia = 65;
	
	// Menor valor que um atributo pode assumir no conjunto de dados
	private static final int valorMinimoAtributo = 0;
	
	// Maior valor que um atributo pode assumir no conjunto de dados
	private static final int valorMaximoAtributo = 16;
	
	// Menor valor que um atributo classe pode assumir no conjunto de dados
	public static final int valorMinimoClasse = 0;
	
	// Maior valor que um atributo classe pode assumir no conjunto de dados
	public static final int valorMaximoClasse = 9;
	
	public float[][] transformarArrayStringParaFloat(String[] dados, String chrDelimit) {
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
	
	/*
	 * Esse eh um dos requisitos desejaveis do pre-processamento
	 * Selecao de Atributos
	 * Usar arvore de decisao (?) - Entropia
	 * Attribute Subset Selection
	 * Bom link: http://www.public.asu.edu/~huanliu/papers/tkde05.pdf
	*/
	
	// transforma uma matriz tridimencional em bidimencional, em relacao a qual arquivo escolhido
	private float[][] transformaBidimensional(float[][][] tridimencional, int arquivo){
			float[][] aux = new float[tridimencional[arquivo].length][tridimencional[arquivo][0].length];
		
            //System.out.println("Transformando Bidimencional");
			//System.out.println(aux.length);
			//System.out.println(aux[0].length);
			for(int i = 0; i < aux.length; i++){
				for( int j = 0; j < aux[i].length; j++){
					aux[i][j] = tridimencional [arquivo][i][j]; // passa o valor de um para o outro
				}
			}
		
		return aux;
	}
	
	// transforma tres matrizes tridimencionais em uma tridimencional
	private float[][][] transformaTridimensional(float[][] primeira, float[][] segunda, float[][] terceira){
			float[][][] aux = new float[3][][]; // instancia a auxiliar (a terceira dimencao, que e a dos arquivos)
		
			aux[0] = new float[primeira.length][primeira[0].length]; // instancia a dimencao dos dados dos valores
			aux[1] = new float[segunda.length][segunda[0].length];
			aux[2] = new float[terceira.length][terceira[0].length];
		
			for(int i = 0; i < primeira.length; i++){ // copia o valor para a auxiliar
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
	
	private float[][] SelecionarAtributos(float[][] dados) {
		return null;
	}
	
	/*
	 * Exclusao de atributos desnecessarios
	 * Precisa dar uma ajeitada nisso aqui
	 * Comeca a iterar partir da segunda dimensao da matriz, nao ficou mto bom
	 */
	private float[][] excluirAtrib(float[][] dados) {
		float[] qtdVlr;
		float limite = dados.length * 0.9F;
		boolean ehAtribOk;
		List<Integer> atribsOk = new ArrayList<Integer>();
		float[][] novosDados;
		Iterator<Integer> iterador;
		int indAtrib, atribSelect;
		
		atribsOk.add(PreProcessamento.numeroAtributosPorInstancia-1);
		qtdVlr = new float[PreProcessamento.valorMaximoAtributo + ((int)Math.pow(0, PreProcessamento.valorMinimoAtributo))];
		
		for(int j = PreProcessamento.numeroAtributosPorInstancia-2; j > -1; j--) 
		{
			ehAtribOk = true;
			for(int k = qtdVlr.length-1; k > -1; k--) 
				qtdVlr[k] = 0F;

			for(int i = dados.length-1; i > -1; i--) 
			{
				qtdVlr[(int)dados[i][j]] += 1F;
				if(qtdVlr[(int)dados[i][j]] > limite) 
				{
					i = -1;
					ehAtribOk = false;
				}
			}
			if(ehAtribOk)
				atribsOk.add(j);
		}

		novosDados = new float[dados.length][atribsOk.size()];
		
		iterador = atribsOk.iterator();
		indAtrib = atribsOk.size()-1;
		while(iterador.hasNext()) {
			atribSelect = iterador.next();
			for(int i = dados.length-1; i > -1; i--) {
				novosDados[i][indAtrib] = dados[i][atribSelect];
			}
			indAtrib--;
		}

		return novosDados;
	}
	
	// Normalizacao dos dados
	private void minMaxNormal(float[][] dados, float novoMin, float novoMax) {
		float difAnt = PreProcessamento.valorMaximoAtributo - PreProcessamento.valorMinimoAtributo;
		float difNovo = novoMax - novoMin;
		float difDif = difNovo / difAnt;
		float difAux = (PreProcessamento.valorMinimoAtributo * difNovo + difAnt * novoMin) / difAnt;
		
		for(int i = dados.length-1; i > -1; i--) {
			for(int j = dados[i].length-2; j > -1; j--) {
				dados[i][j] = dados[i][j] * difDif - difAux;
			}
		}
	}
	
	private void zScoreNormal(float[][] dados) {
		float media, desvioPadrao;
		for(int j = dados[0].length-2; j > -1; j--) {
			media = 0F;
			for(int i = dados.length-1; i > -1; i--) {
				media += Math.pow(dados[i][j], 2F);
			}
			media /= dados.length;
			desvioPadrao = 0F;
			for(int i = dados.length-1; i > -1; i--) {
				desvioPadrao += Math.pow(dados[i][j] - media, 2F);
			}
			desvioPadrao = (float) Math.sqrt(desvioPadrao / dados.length);
			for(int i = dados.length-1; i > -1; i--) {
				dados[i][j] = (dados[i][j] - media) / desvioPadrao;
			}
		}
	}
	
	//Particao balanceada de arquivos
	private float[][][] particaoBalanceada(float[][] dados, float[] porcentagem){
		float[][][] dadosSeparados = new float[porcentagem.length][][]; // instancia a matriz tridimencional que vai amazenar os [arquivos][numero][atributos]
		
		int quantidadeTotal = dados.length;
		int[] quantidadeClasse = new int[10]; // array que vai armazenar quanto tem de cada classe no total
		
		for(int i = 0; i < quantidadeTotal; i++){ // acha quanto tem de cada classe e bota no array quantidadeClasse
			int aux = (int) dados[i][dados[i].length-1];
			quantidadeClasse[aux]++;
		}
                
			//for (int i = 0; i < quantidadeClasse.length; i++) System.out.print(quantidadeClasse[i] + " ");
			//System.out.println();
		
		int [][] quantidadesFinais = new int [3][10]; // array que contem quanto pode ter de cada atributo em cada arquivo
		for (int i = 0; i < quantidadesFinais[0].length; i++){ // contas para determinar a quantidade de valores para cada arquivo
			quantidadesFinais[0][i] = (int) Math.round(quantidadeClasse[i]*porcentagem[0]);
			quantidadesFinais[1][i] = quantidadeClasse[i] - quantidadesFinais[0][i] - (int) Math.round(quantidadeClasse[i]*porcentagem[2]);
			quantidadesFinais[2][i] = quantidadeClasse[i] - quantidadesFinais[1][i] - quantidadesFinais[0][i];
		}
                
		//for (int i = 0; i < quantidadesFinais.length; i++){
		//	for (int j = 0; j < quantidadesFinais[i].length; j++){
		//		System.out.println(i + " " + j + " " + quantidadesFinais[i][j] );
		//	}
		//}
                
		//System.out.println("----------------------------");
		//System.out.println((int) Math.floor(quantidadeTotal*porcentagem[0]) + " " + (int) Math.floor(quantidadeTotal*porcentagem[1]) + " " + (int) Math.floor(quantidadeTotal*porcentagem[2]));
		//System.out.println("----------------------------");
		
		dadosSeparados[0] = new float[(int) Math.floor(quantidadeTotal*porcentagem[0])][dados[0].length];
		dadosSeparados[1] = new float[(int) Math.floor(quantidadeTotal*porcentagem[1])][dados[0].length];
		dadosSeparados[2] = new float[(int) Math.floor(quantidadeTotal*porcentagem[2])][dados[0].length];
		
		int posicaoMatriz1 = 0;
		int posicaoMatriz2 = 0;
		int posicaoMatriz3 = 0;
		
		//int contador = 0;
		
		
		for (int i = 0; i < dados.length; i++){
			//contador++;
			int aux = (int) dados[i][dados[i].length-1]; // valor da classe
			//System.out.println(contador);
            //System.out.println("Classe entrando: " + aux);
			//System.out.println(posicaoMatriz1 + " / " + Math.floor(quantidadeTotal*porcentagem[0]));
			//System.out.println(posicaoMatriz2 + " / " + Math.floor(quantidadeTotal*porcentagem[1]));
			//System.out.println(posicaoMatriz3 + " / " + Math.floor(quantidadeTotal*porcentagem[2]));
                        
            //System.out.println(quantidadesFinais[0][0]+" "+quantidadesFinais[0][1]+" "+quantidadesFinais[0][2]+" "+quantidadesFinais[0][3]+" "+quantidadesFinais[0][4]+" "+quantidadesFinais[0][5]+" "+quantidadesFinais[0][6]+" "+quantidadesFinais[0][7]+" "+quantidadesFinais[0][8]+" "+quantidadesFinais[0][9]);
            //System.out.println(quantidadesFinais[1][0]+" "+quantidadesFinais[1][1]+" "+quantidadesFinais[1][2]+" "+quantidadesFinais[1][3]+" "+quantidadesFinais[1][4]+" "+quantidadesFinais[1][5]+" "+quantidadesFinais[1][6]+" "+quantidadesFinais[1][7]+" "+quantidadesFinais[1][8]+" "+quantidadesFinais[1][9]);
            //System.out.println(quantidadesFinais[2][0]+" "+quantidadesFinais[2][1]+" "+quantidadesFinais[2][2]+" "+quantidadesFinais[2][3]+" "+quantidadesFinais[2][4]+" "+quantidadesFinais[2][5]+" "+quantidadesFinais[2][6]+" "+quantidadesFinais[2][7]+" "+quantidadesFinais[2][8]+" "+quantidadesFinais[2][9]);
                        
			switch ((int) aux){ // switch case para ver qual classe esta sendo iterada
				case 0: // a classe em questao tem valor 0
					if(quantidadesFinais[0][0] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){ // ve se ja foram todos que devem ser no arquivo de treino (arredondado pro teto)
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2]; // copia todos os valores pra matriz final
						posicaoMatriz1++; // acrecenta o contador para saber onde inserir o proximo
						quantidadesFinais[0][0]--; // diminui o contador pois ja acrecentamos um
						//System.out.println("Entrou na classe 0 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][0]);
						break;
					}
					if(quantidadesFinais[1][0] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){ // ve se ja foram todos que devem ser no arquivo de validacao (arredondado pro teto)
						for(int aux2 = 0; aux2 < dados[i].length; aux2++ )	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][0]--;
						//System.out.println("Entrou na classe 0 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][0]);
						break;
					}
					if(quantidadesFinais[2][0] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){ // ve se ja foram todos que devem ser no arquivo de teste (arredondado pro chao)
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][0]--;
						//System.out.println("Entrou na classe 0 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][0]);
						break;
					}				
					break;
				case 1:
					if(quantidadesFinais[0][1] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][1]--;
						//System.out.println("Entrou na classe 1 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][1]);
						break;
					}
					if(quantidadesFinais[1][1] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][1]--;
						//System.out.println("Entrou na classe 1 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][1]);
						break;
					}
					if(quantidadesFinais[2][1] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][1]--;
						//System.out.println("Entrou na classe 1 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][1]);
						break;
					}				
					break;
				case 2:
					if(quantidadesFinais[0][2] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][2]--;
						//System.out.println("Entrou na classe 2 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][2]);
						break;
					}
					if(quantidadesFinais[1][2] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][2]--;
						//System.out.println("Entrou na classe 2 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][2]);
						break;
					}
					if(quantidadesFinais[2][2] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][2]--;
						//System.out.println("Entrou na classe 2 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][2]);
						break;
					}				
					break;
				case 3:
					if(quantidadesFinais[0][3] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][3]--;
						//System.out.println("Entrou na classe 3 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][3]);
						break;
					}
					if(quantidadesFinais[1][3] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][3]--;
						//System.out.println("Entrou na classe 3 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][3]);
						break;
					}
					if(quantidadesFinais[2][3] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][3]--;
						//System.out.println("Entrou na classe 3 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][3]);
						break;
					}				
					break;
				case 4:
					if(quantidadesFinais[0][4] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][4]--;
						//System.out.println("Entrou na classe 4 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][4]);
						break;
					}
					if(quantidadesFinais[1][4] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][4]--;
						//System.out.println("Entrou na classe 4 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][4]);
						break;
					}
					if(quantidadesFinais[2][4] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][4]--;
						//System.out.println("Entrou na classe 4 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][4]);
						break;
					}				
					break;
				case 5:
					if(quantidadesFinais[0][5] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][5]--;
						//System.out.println("Entrou na classe 5 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][5]);
						break;
					}
					if(quantidadesFinais[1][5] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][5]--;
						//System.out.println("Entrou na classe 5 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][5]);
						break;
					}
					if(quantidadesFinais[2][5] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][5]--;
						//System.out.println("Entrou na classe 5 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][5]);
						break;
					}				
					break;
				case 6:
					if(quantidadesFinais[0][6] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][6]--;
						//System.out.println("Entrou na classe 6 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][6]);
						break;
					}
					if(quantidadesFinais[1][6] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][6]--;
						//System.out.println("Entrou na classe 6 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][6]);
						break;
					}
					if(quantidadesFinais[2][6] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][6]--;
						//System.out.println("Entrou na classe 6 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][6]);
						break;
					}				
					break;
				case 7:
					if(quantidadesFinais[0][7] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][7]--;
						//System.out.println("Entrou na classe 7 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][7]);
						break;
					}
					if(quantidadesFinais[1][7] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][7]--;
						//System.out.println("Entrou na classe 7 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][7]);
						break;
					}
					if(quantidadesFinais[2][7] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][7]--;
						//System.out.println("Entrou na classe 7 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][7]);
						break;
					}				
					break;
				case 8:
					if(quantidadesFinais[0][8] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][8]--;
						//System.out.println("Entrou na classe 8 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][8]);
						break;
					}
					if(quantidadesFinais[1][8] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][8]--;
						//System.out.println("Entrou na classe 8 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][8]);
						break;
					}
					if(quantidadesFinais[2][8] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][8]--;
						//System.out.println("Entrou na classe 8 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][8]);
						break;
					}				
					break;
				case 9:
					if(quantidadesFinais[0][9] != 0 && posicaoMatriz1 < Math.floor(quantidadeTotal*porcentagem[0])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[0][posicaoMatriz1][aux2] = dados[i][aux2];
						posicaoMatriz1++;
						quantidadesFinais[0][9]--;
						//System.out.println("Entrou na classe 9 e tentou inserir no arq 1");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[0][9]);
						break;
					}
					if(quantidadesFinais[1][9] != 0 && posicaoMatriz2 < Math.floor(quantidadeTotal*porcentagem[1])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[1][posicaoMatriz2][aux2] = dados[i][aux2];
						posicaoMatriz2++;
						quantidadesFinais[1][9]--;
						//System.out.println("Entrou na classe 9 e tentou inserir no arq 2");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[1][9]);
						break;
					}
					if(quantidadesFinais[2][9] != 0 && posicaoMatriz3 < Math.floor(quantidadeTotal*porcentagem[2])){
						for(int aux2 = 0; aux2 < dados[i].length; aux2++)	dadosSeparados[2][posicaoMatriz3][aux2] = dados[i][aux2];
						posicaoMatriz3++;
						quantidadesFinais[2][9]--;
						//System.out.println("Entrou na classe 9 e tentou inserir no arq 3");
						//System.out.println("falta inserir nessa classe nesse arquivo: " + quantidadesFinais[2][9]);
						break;
					}				
					break;
			}
	
		}
		
		//System.out.println("Valores Finais:");
		//System.out.println(contador);
		//System.out.println(posicaoMatriz1 + " / " + Math.floor(quantidadeTotal*porcentagem[0]));
		//System.out.println(posicaoMatriz2 + " / " + Math.floor(quantidadeTotal*porcentagem[1]));
		//System.out.println(posicaoMatriz3 + " / " + Math.floor(quantidadeTotal*porcentagem[2]));
                       
		//System.out.println(quantidadesFinais[0][0]+" "+quantidadesFinais[0][1]+" "+quantidadesFinais[0][2]+" "+quantidadesFinais[0][3]+" "+quantidadesFinais[0][4]+" "+quantidadesFinais[0][5]+" "+quantidadesFinais[0][6]+" "+quantidadesFinais[0][7]+" "+quantidadesFinais[0][8]+" "+quantidadesFinais[0][9]);
		//System.out.println(quantidadesFinais[1][0]+" "+quantidadesFinais[1][1]+" "+quantidadesFinais[1][2]+" "+quantidadesFinais[1][3]+" "+quantidadesFinais[1][4]+" "+quantidadesFinais[1][5]+" "+quantidadesFinais[1][6]+" "+quantidadesFinais[1][7]+" "+quantidadesFinais[1][8]+" "+quantidadesFinais[1][9]);
		//System.out.println(quantidadesFinais[2][0]+" "+quantidadesFinais[2][1]+" "+quantidadesFinais[2][2]+" "+quantidadesFinais[2][3]+" "+quantidadesFinais[2][4]+" "+quantidadesFinais[2][5]+" "+quantidadesFinais[2][6]+" "+quantidadesFinais[2][7]+" "+quantidadesFinais[2][8]+" "+quantidadesFinais[2][9]);
                
		//System.out.println(dadosSeparados[0][0].length);
		//for(int i = 0; i < dadosSeparados[0].length; i++)	System.out.println(dadosSeparados[0][i][48]);
		
       	//System.out.println(dadosSeparados[1][0].length);
		//for(int i = 0; i < dadosSeparados[1].length; i++)	System.out.println(dadosSeparados[1][i][48]);
		        		
		//System.out.println(dadosSeparados[2][0].length);
		//for(int i = 0; i < dadosSeparados[2].length; i++)	System.out.println(dadosSeparados[2][i][48]);
		
		return dadosSeparados;
	}
	
	// Preprocessamento dos dados obtidos apos a leitura do arquivo
	private float[][][] processarDados(String[] dados, float[] porcentagem) throws FileNotFoundException {
		float[][] dadosCrus = transformarArrayStringParaFloat(dados, PreProcessamento.CHR_DELIMIT);
		dadosCrus = excluirAtrib(dadosCrus);
		
		float[][][] dadosParticionados = particaoBalanceada(dadosCrus, porcentagem);
		//minMaxNormal(dadosProc, 0, 1);
		
		
		//System.out.println("Chamou transforma Bi pro arquivo 1");
		float[][] aux1 = transformaBidimensional(dadosParticionados, 0);
        //System.out.println("Chamou transforma Bi pro arquivo 2");
		float[][] aux2 = transformaBidimensional(dadosParticionados, 1);
        //System.out.println("Chamou transforma Bi pro arquivo 3");
		float[][] aux3 = transformaBidimensional(dadosParticionados, 2);
		
		//for( int i = 0; i < aux1.length; i++){
		//	System.out.println(aux1[1][48]);
		//}
		
		zScoreNormal(aux1);
		zScoreNormal(aux2);
		zScoreNormal(aux3);
                
        //System.out.println(aux1[0].length);
        //for(int i = 0; i < aux1[0].length; i++){
        //	System.out.println(aux1[0][i]);
        //}
                
                
		dadosParticionados = transformaTridimensional(aux1, aux2, aux3);
		
		//System.out.println(dadosParticionados[0][0].length);
		//for(int i = 0; i < dadosParticionados[0].length; i++){
		//	System.out.println(dadosParticionados[0][i][48]);
		//}
		
		//System.out.println(dadosParticionados[1][0].length);
		//for(int i = 0; i < dadosParticionados[1].length; i++){
		//	System.out.println(dadosParticionados[1][i][48]);
		//}
		
		//System.out.println(dadosParticionados[2][0].length);
		//for(int i = 0; i < dadosParticionados[2].length; i++){
		//	System.out.println(dadosParticionados[2][i][48]);
		//}
                
		return dadosParticionados;
	}

	private float[][][] splitDados(float[][] dados, float[] pcts) {
		float[][][] dadosSplit = new float[pcts.length][][];
		int[] splitLengths = new int[pcts.length];
		int totLength = 0;
		int from = 0;
		
		for(int i = pcts.length-1; i > -1; i--) {
			splitLengths[i] = Math.round(dados.length * Math.abs(pcts[i]));
			if((splitLengths[i]+totLength) > dados.length) {
				splitLengths[i] = dados.length - totLength;
				i = -1;
			} else {
				totLength += splitLengths[i];
			}
		}
		
		for(int i = 0; i < splitLengths.length; i++) {
			dadosSplit[i] = Arrays.copyOfRange(dados, from, from+splitLengths[i]);
			from = splitLengths[i];
		}
		
		return dadosSplit;
	}
	
	/*
	 * Ta demorando pra criar o arquivo. Tentei colocar tudo em uma 
	 * String soh e fazer io apenas uma vez mas tava demorando demais.
	 * Dai fiz um meio termo (soh faz io depois de iterar 10% das linhas da matriz).
	 * Se pensarem em uma maneira mais eficiente implementem ai!
	 * 
	 * Lembrete: o atributo classe esta sendo gravado no arquivo como float
	 */
	private void criarArquivo(float[][] dados, String arq) throws IOException {
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
		}

		//System.out.println(dadosArq[0]);
		//System.out.println(dadosArq[dadosArq.length-1]);
		
		
		ES.escreverDados(arq, dadosArq);
	}
	
	public float[] descDados(float[][] dados, int posClasse) {
		float[] minMaxClasse = new float[] { Float.MAX_VALUE, Float.MIN_VALUE };
		float[] minMaxAtrib = new float[] { Float.MAX_VALUE, Float.MIN_VALUE };
		float[] minMaxRef;
		float qtdAtrib = 0F;
		float qtdDados = 0F;
		boolean qtdAtribOK = true;
		
		for(float[] dado : dados) {
			for(int i = dado.length-1; i > -1; i--) {
				if(i == posClasse) {
					minMaxRef = minMaxClasse;
				} else {
					minMaxRef = minMaxAtrib;
				}
				if(dado[i] < minMaxRef[0]) {
					minMaxRef[0] = dado[i];
				}
				if(dado[i] > minMaxRef[1]) {
					minMaxRef[1] = dado[i];
				}
			}
			if(qtdAtribOK) {
				if(qtdAtrib == 0) {
					qtdAtrib = dado.length;
				}
				qtdAtribOK = dado.length == qtdAtrib;
				qtdAtrib = dado.length;
			}
			qtdDados++;
		}
		
		if(!qtdAtribOK) {
			qtdAtrib = -1F;
		}
		
		return new float[] { minMaxAtrib[0], minMaxAtrib[1], minMaxClasse[0], minMaxClasse[1], qtdAtrib, qtdDados };
	}
	
	public PreProcessamento(String[] arqsDados, String[] arqsSaida, float[] pctsDadosSaida) throws IOException {
		if(arqsSaida.length == pctsDadosSaida.length) {
			
			//System.out.println(pctsDadosSaida[0]);
			//System.out.println(pctsDadosSaida[1]);
			//System.out.println(pctsDadosSaida[2]);
			//System.out.println(pctsDadosSaida.length);
			
			float[][][] dadosSet = processarDados(ES.lerArquivos(arqsDados), pctsDadosSaida);
			//float[][][] dadosSet = splitDados(processarDados(ES.lerArquivos(arqsDados)), pctsDadosSaida);

			for(int i = dadosSet.length-1; i > -1; i--) {
				criarArquivo(dadosSet[i], arqsSaida[i]);
			}
			
			
		}
	}
}
