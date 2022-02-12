package br.com.proccarga.sn.model;

import br.com.proccarga.core.enumeration.TipoArquivosRsnEnum;

public class TipoArquivoRsn {
	private Long id;
	private Long codigo;
	private String descricao;
	private TipoArquivosRsnEnum tipoArquivosRsnEnum;

	public TipoArquivoRsn() {
	}

	public TipoArquivoRsn(TipoArquivosRsnEnum tipoArquivosRsnEnum) {
		this.tipoArquivosRsnEnum = tipoArquivosRsnEnum;
		this.id = tipoArquivosRsnEnum.getCodigo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoArquivosRsnEnum getTipoArquivosRsnEnum() {
		return tipoArquivosRsnEnum;
	}

	public void setTipoArquivosRsnEnum(TipoArquivosRsnEnum tipoArquivosRsnEnum) {
		this.tipoArquivosRsnEnum = tipoArquivosRsnEnum;
	}
}
