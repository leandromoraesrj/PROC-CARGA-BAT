package br.com.proccarga.sn.model;

import br.com.proccarga.core.enumeration.SituacaoImportacaoArqRsnEnum;

public class SituacaoImportacaoArqRsn {
	private Long id;
	private String descricao;
	private SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum;

	public SituacaoImportacaoArqRsn() {
	}

	public SituacaoImportacaoArqRsn(SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum) {
		this.situacaoImportacaoArqRsnEnum = situacaoImportacaoArqRsnEnum;
		this.id = situacaoImportacaoArqRsnEnum.getCodigo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public SituacaoImportacaoArqRsnEnum getSituacaoImportacaoArqRsnEnum() {
		return situacaoImportacaoArqRsnEnum;
	}

	public void setSituacaoImportacaoArqRsnEnum(SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum) {
		this.situacaoImportacaoArqRsnEnum = situacaoImportacaoArqRsnEnum;
	}
}
