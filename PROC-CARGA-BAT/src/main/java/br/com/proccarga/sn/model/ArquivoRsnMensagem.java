package br.com.proccarga.sn.model;

import java.util.Comparator;

public class ArquivoRsnMensagem {
	private String textoComplementar;
	private ArquivoRsn arquivoRsn;
	private Mensagem mensagem;
	
	public String getTextoComplementar() {
		return textoComplementar;
	}
	public void setTextoComplementar(String textoComplementar) {
		this.textoComplementar = textoComplementar;
	}
	public ArquivoRsn getArquivoRsn() {
		return arquivoRsn;
	}
	public void setArquivoRsn(ArquivoRsn arquivoRsn) {
		this.arquivoRsn = arquivoRsn;
	}
	public Mensagem getMensagem() {
		return mensagem;
	}
	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}
	
    public String getMensagenFormatada(){
        String novaMensagem = mensagem.getTextoMensagem(); 
        String[] parametros = textoComplementar != null ? textoComplementar.split(";") : new String[0];

        int a = 0;
        for(String p : parametros){
        	novaMensagem = novaMensagem.replace("{"+a+"}", p);
            a++;
        }
        return novaMensagem;
    }
	
	public long compareTo(ArquivoRsnMensagem arquivo) {
		return this.arquivoRsn.getTipoArquivoRsn().getCodigo() - arquivo.getArquivoRsn().getTipoArquivoRsn().getCodigo();
	}

	public static Comparator<ArquivoRsnMensagem> MensagemErroComparator = new Comparator<ArquivoRsnMensagem>() { 
		@Override
		public int compare(ArquivoRsnMensagem msgErro1, ArquivoRsnMensagem msgErro2) { 
			Long mensagemErro1 = msgErro1.getArquivoRsn().getTipoArquivoRsn().getCodigo(); 
			Long mensagemErro2 = msgErro2.getArquivoRsn().getTipoArquivoRsn().getCodigo();
			return mensagemErro1.compareTo(mensagemErro2); 
			} };
	
	
	public ArquivoRsnMensagem(ArquivoRsn arquivoRsn, Mensagem mensagem){
		this.arquivoRsn = arquivoRsn;
		this.mensagem = mensagem;
	}
	
	public ArquivoRsnMensagem(ArquivoRsn arquivoRsn, Mensagem mensagem, String textoComplementar){
		this(arquivoRsn, mensagem);
		this.textoComplementar = textoComplementar;
	}
	
}
