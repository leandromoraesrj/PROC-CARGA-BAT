package br.com.proccarga.sn.model;

public class Mensagem {
	private String coReferencia;
	private String textoMensagem;

	public Mensagem(String coReferencia, String textoMensagem) {
		this.coReferencia = coReferencia;
		this.textoMensagem = textoMensagem;
	}

	public String getCoReferencia() {
		return coReferencia;
	}

	public void setCoReferencia(String coReferencia) {
		this.coReferencia = coReferencia;
	}

	public String getTextoMensagem() {
		return textoMensagem;
	}

	public void setTextoMensagem(String textoMensagem) {
		this.textoMensagem = textoMensagem;
	}

}
