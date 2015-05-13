package br.usp.ia.ep1;

public class RespostaClassificador {

	private float[][] dados;
	private int qtdAcertos;
	private int qtdErros;
	private float erroQuadrado;
	private float txAprend;
	
	public float[][] getDados() {
		return dados;
	}
	
	public void setDados(float[][] dados) {
		this.dados = dados;
	}
	
	public int getQtdAcertos() {
		return qtdAcertos;
	}
	
	public void setQtdAcertos(int qtdAcertos) {
		this.qtdAcertos = qtdAcertos;
	}
	
	public int getQtdErros() {
		return qtdErros;
	}
	
	public void setQtdErros(int qtdErros) {
		this.qtdErros = qtdErros;
	}
	
	public float getErroQuadrado() {
		return erroQuadrado;
	}
	
	public void setErroQuadrado(float erroQuadrado) {
		this.erroQuadrado = erroQuadrado;
	}
	
	public float getTxAprend() {
		return txAprend;
	}
	
	public void setTxAprend(float txAprend) {
		this.txAprend = txAprend;
	}
}
