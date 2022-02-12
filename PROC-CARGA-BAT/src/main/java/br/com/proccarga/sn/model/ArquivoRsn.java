package br.com.proccarga.sn.model;

import java.io.File;

import br.com.proccarga.core.enumeration.SituacaoImportacaoArqRsnEnum;
import br.com.proccarga.core.enumeration.TipoArquivosRsnEnum;

public class ArquivoRsn {
	private Long id;
	private String nomeArquivo;
	private String cpfUsuario;
	private TipoArquivoRsn tipoArquivoRsn;
	private SituacaoImportacaoArqRsn situacaoImportacao;
	private File fisico;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public File getArquivoFisico() {
		return fisico;
	}

	public void setArquivoFisico(File fisico) {
		this.fisico = fisico;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCpfUsuario() {
		return cpfUsuario;
	}

	public void setCpfUsuario(String cpfUsuario) {
		this.cpfUsuario = cpfUsuario;
	}

	public TipoArquivoRsn getTipoArquivoRsn() {
		return tipoArquivoRsn;
	}

	public void setTipoArquivoRsn(TipoArquivoRsn tipoArquivoRsn) {
		this.tipoArquivoRsn = tipoArquivoRsn;
	}

	public void setTipoArquivoRsn(TipoArquivosRsnEnum tipoArquivosRsnEnum) {
		setTipoArquivoRsn(new TipoArquivoRsn(tipoArquivosRsnEnum));
	}

	public SituacaoImportacaoArqRsn getSituacaoImportacao() {
		return situacaoImportacao;
	}

	public void setSituacaoImportacao(SituacaoImportacaoArqRsn situacaoImportacao) {
		this.situacaoImportacao = situacaoImportacao;
	}

	public void setSituacaoImportacao(SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum) {
		setSituacaoImportacao(new SituacaoImportacaoArqRsn(situacaoImportacaoArqRsnEnum));
	}
}