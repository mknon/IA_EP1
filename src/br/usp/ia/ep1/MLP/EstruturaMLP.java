package br.usp.ia.ep1.MLP;
import br.usp.ia.ep1.*;

public class EstruturaMLP {
	
	private Neuronio[] camadaEscondida;
	private Neuronio[] camadaDeSaida;
	private int qtdEpocaTreino = 0;

	public EstruturaMLP(){ }
	
	/*O construtor inicializara a estrutura da MLP com os valores de peso e a quantidade de neuronios tanto da camada
	 * de entrada quanto a de saida, assim como os valores de bias de ambas as camadas, especificados pelo usuario.
	 * A classe 'DadosProcessados' eh onde estes valores serao armazenados para uma melhor visibiliade*/	
	public EstruturaMLP(InformacoesDaCamada camadaEscondida, InformacoesDaCamada camadaSaida, double[] biasCamadaEscondida, double[] biasCamadaSaida, int quantidadeValoresNeuronios)
	{
		this.camadaEscondida = new Neuronio[camadaEscondida.getQuantidadeDeNeuronios()];
		this.camadaDeSaida = new Neuronio[camadaSaida.getQuantidadeDeNeuronios()];
		
		criarNeuroniosCamadaEscondida(camadaEscondida, biasCamadaEscondida, quantidadeValoresNeuronios);
		criarNeuroniosCamadaSaida(camadaSaida, biasCamadaSaida, camadaEscondida.getQuantidadeDeNeuronios());	
	}
	
	public EstruturaMLP(int qtdeNeuroniosCamadaEscondida, int qtdeNeuroniosCamadaSaida, boolean inicializarAleatorio, int quantidadeValoresNeuronios)
	{
		this.camadaEscondida = new Neuronio[qtdeNeuroniosCamadaEscondida];
		this.camadaDeSaida = new Neuronio[qtdeNeuroniosCamadaSaida];
		
		if(inicializarAleatorio)
		{
			criarNeuroniosCamadaEscondidaPesosComValoresAleatorios(quantidadeValoresNeuronios);
			criarNeuroniosCamadaSaidaPesosComValoresAleatorios(qtdeNeuroniosCamadaEscondida);	
		}
		else
		{
			criarNeuroniosCamadaEscondidaPesosEBiasComZeros(quantidadeValoresNeuronios);
			criarNeuroniosCamadaSaidaPesosEBiasComZeros(qtdeNeuroniosCamadaEscondida);
		}
	}
	
	public EstruturaMLP(Neuronio[] camadaEscondida, Neuronio[] camadaSaida)
	{
		this.camadaEscondida = new Neuronio[camadaEscondida.length];
		this.camadaDeSaida = new Neuronio[camadaSaida.length];
		
		for(int i = 0; i < camadaEscondida.length; i++)
			this.camadaEscondida[i] = new Neuronio(camadaEscondida[i].getBias(), camadaEscondida[i].getTodosOsPesos() ,camadaEscondida[i].getTermoDeErro());
		
		for(int i = 0; i < camadaSaida.length; i++)
			this.camadaDeSaida[i] = new Neuronio(camadaSaida[i].getBias(), camadaSaida[i].getTodosOsPesos(), camadaSaida[i].getTermoDeErro());
	}
	
	public int getTamanhoCamadaEscondida()
	{
		return this.camadaEscondida.length;
	}
	
	public int getTamanhoCamadaSaida()
	{
		return this.camadaDeSaida.length;
	}
	
	public int getTamanhoCamadaEntrada()
	{
		return this.camadaEscondida[0].QuantidadePesos();
	}
	
	public Neuronio getNeuronioCamadaEscondida(int indexNeuronio)
	{
		return this.camadaEscondida[indexNeuronio];
	}
	
	public Neuronio getNeuronioCamadaSaida(int indexNeuronio)
	{
		return this.camadaDeSaida[indexNeuronio];
	}
	
	
	
	public PesosCalculados ExecutarFeedFowardCamadaEscondida(DadosDeEntradaProcessados entrada, int index)
	{
		return this.camadaEscondida[index].FeedFoward(entrada.getDadosDeEntrada());	
	}
	
	public PesosCalculados ExecutarFeedFowardCamadaEscondida(DadosDeTeste entrada, int index)
	{
		return this.camadaEscondida[index].FeedFoward(entrada.getDadosDeTeste());	
	}
	
	public PesosCalculados ExecutarFeedFowarCamadaSaida(PesosCalculados[] entrada, int index)
	{
		return this.camadaDeSaida[index].FeedFoward(this.PesosCalculadosToDouble(entrada));
	}
	
	public PesosCalculados ExecutarFeedFowardCamadaSaida(double[] entrada, int index)
	{
		return this.camadaDeSaida[index].FeedFoward(entrada);
	}
	
	
	
	
	
	public double DerivadaFuncaoBinariaSigmoid(double valor)
	{
		return this.camadaEscondida[0].DerivadaFuncaoDeAtivacaoBinariaDeSigmoid(valor);
	}
	
	public RespostaClassificador ExecutarRede(DadosDeTeste[] dados)
	{
		double[] saidasCamEscondida = new double[this.getTamanhoCamadaEscondida()];
		double[] saidasCamSaida = new double[this.getTamanhoCamadaSaida()];
		RespostaClassificador resposta = new RespostaClassificador();
		int qtdAcertos = 0;
		int qtdErros = 0;
		int resultadoEsperado;
		double erroQuadrado = 0;
		
		int[][] matrizConfusao = new int[PreProcessamento.valorMaximoClasse + 1][PreProcessamento.valorMaximoClasse + 1];
		
		for(int i = matrizConfusao.length-1; i > -1; i--) {
			for(int j = matrizConfusao[i].length-1; j > -1; j--) {
				matrizConfusao[i][j] = 0;
			}
		}
		
		for (int i = 0; i < dados.length; i++) {
			
			saidasCamEscondida = new double[this.getTamanhoCamadaEscondida()];
			saidasCamSaida = new double[this.getTamanhoCamadaSaida()];
			
			//Realiza feedFoward na camada de entrada
			for (int j = 0; j < this.getTamanhoCamadaEscondida(); j++) 
			{
				saidasCamEscondida[j] += this.ExecutarFeedFowardCamadaEscondida(dados[i], j).getOutput();
			}
			
			//Realiza feedFoward na camada de saída
			for (int j = 0; j < this.getTamanhoCamadaSaida(); j++) 
			{
				saidasCamSaida[j] += this.ExecutarFeedFowardCamadaSaida(saidasCamEscondida, j).getOutput();
				
				if(dados[i].getClasseReal() == j)
					resultadoEsperado = 1;
				else
					resultadoEsperado = 0;
				
				erroQuadrado += Math.sqrt((resultadoEsperado - saidasCamSaida[j]) * (resultadoEsperado - saidasCamSaida[j]));
			}	

			dados[i].setClassePredita(extrairMaiorValorDoArray(saidasCamSaida));
			matrizConfusao[(int)dados[i].getClasseReal()][(int)dados[i].getClassePredita()]++;
			
			if (dados[i].getClasseReal() == dados[i].getClassePredita()) {
				qtdAcertos++;				
			} else {
				qtdErros++;
			}
			
		}
		resposta.setDadosDeTesteMLP(dados);
		resposta.setMatrizConfusao(new MatrizConfusao(matrizConfusao));
		resposta.setQtdAcertos(qtdAcertos);
		resposta.setQtdErros(qtdErros);
		resposta.setErroQuadrado((float) erroQuadrado);
		resposta.setEpocasTreinoRede(qtdEpocaTreino);
		
		return resposta;
	}
	
	public int extrairMaiorValorDoArray(double[] saidas)
	{
		int index = 0;
		double maiorValor = -100;
		int classe = 0;
		
		for(double saida: saidas)
		{
			if(saida > maiorValor)
			{
				maiorValor = saida;
				classe = index;
			}
			index++;
		}	
		
		return classe;
	}
	
	
	
	
	
	
	public EstruturaMLP ClonarRede(EstruturaMLP redeAntiga)
	{
		EstruturaMLP novaRede = new EstruturaMLP(redeAntiga.getCamadaEscondida(), redeAntiga.getCamadaSaida());
		
		return novaRede;
	}
	
	public Neuronio[] getCamadaEscondida()
	{
		return this.camadaEscondida;
	}
	
	public Neuronio[] getCamadaSaida()
	{
		return this.camadaDeSaida;
	}
		
	private void criarNeuroniosCamadaEscondida(InformacoesDaCamada camadaEscondida, double[] biasDaCamadaEscondida, int quantidadeValoresNeuronios)
	{
		for(int i = 0; i < this.camadaEscondida.length; i++)
		{
			this.camadaEscondida[i] = new Neuronio(quantidadeValoresNeuronios);
			this.camadaEscondida[i].setBias(biasDaCamadaEscondida[i]);
			this.camadaEscondida[i].InicializarComPesosEspecificos(camadaEscondida.getPesos());;
		}
	}
	
	private void criarNeuroniosCamadaSaida(InformacoesDaCamada camadaSaida, double[] biasDaCamadaDeSaida, int quantidadeValoresNeuronios)
	{
		for(int i = 0; i < this.camadaDeSaida.length; i++)
		{
			this.camadaDeSaida[i] = new Neuronio(quantidadeValoresNeuronios);
			this.camadaDeSaida[i].setBias(biasDaCamadaDeSaida[i]);
			this.camadaDeSaida[i].InicializarComPesosEspecificos(camadaSaida.getPesos());;
		}
	}
	
	private void criarNeuroniosCamadaEscondidaPesosComValoresAleatorios(int quantidadeValoresNeuronios)
	{
		for(int i = 0; i < this.camadaEscondida.length; i++)
		{
			this.camadaEscondida[i] = new Neuronio(quantidadeValoresNeuronios);
			this.camadaEscondida[i].InicializarBiasComValorAleatoro();
			this.camadaEscondida[i].InicializarPesosComValoresAleatorios();
		}
	}
	
	private void criarNeuroniosCamadaSaidaPesosComValoresAleatorios(int qtdeNeuroniosCamadaEscondida)
	{
		for(int i = 0; i < this.camadaDeSaida.length; i++)
		{
			this.camadaDeSaida[i] = new Neuronio(qtdeNeuroniosCamadaEscondida);
			this.camadaDeSaida[i].InicializarBiasComValorAleatoro();
			this.camadaDeSaida[i].InicializarPesosComValoresAleatorios();
		}
	}
	
	private void criarNeuroniosCamadaEscondidaPesosEBiasComZeros(int quantidadeValoresNeuronios)
	{
		for(int i = 0; i < this.camadaEscondida.length; i++)
		{
			this.camadaEscondida[i] = new Neuronio(quantidadeValoresNeuronios);
			this.camadaEscondida[i].setBias(0);
			this.camadaEscondida[i].InicializarPesosComZeros();;
		}
	}	
	
	private void criarNeuroniosCamadaSaidaPesosEBiasComZeros(int qtdeNeuroniosCamadaEscondida)
	{
		for(int i = 0; i < this.camadaDeSaida.length; i++)
		{
			this.camadaDeSaida[i] = new Neuronio(qtdeNeuroniosCamadaEscondida);
			this.camadaDeSaida[i].setBias(0);;
			this.camadaDeSaida[i].InicializarPesosComZeros();
		}
	}
	
	//Converte a classe de apoio PesosCalculados para um formato de array de doubles. Utilizado antes de enviar o array para o feedFoward.
	private double[] PesosCalculadosToDouble(PesosCalculados[] entrada)
	{
		double[] resposta = new double[entrada.length];
		
		for(int i = 0; i < entrada.length; i++)
			resposta[i] = entrada[i].getOutput();
		
		return resposta;
	}

	public int getQtdEpocaTreino() {
		return qtdEpocaTreino;
	}

	public void setQtdEpocaTreino(int qtdEpocaTreino) {
		this.qtdEpocaTreino = qtdEpocaTreino;
	}
}
