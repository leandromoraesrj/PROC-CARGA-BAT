package br.com.proccarga.core.enumeration;

/**
 * Enumeração dos tipos de arquivos a serem importados/processados
 * 
 * @author Leandro Moraes
 */
public enum TipoArquivosRsnEnum {

	RAIZ(0L, "RAIZ", "RAIZ"), // Não utilizar o tipo "RAIZ"

	// 1 - Arquivo de cadastro do MEI
	ACM(1L, "MEI", "Arquivo eventos MEI"),

	// 2 - Arquivo de eventos do Simples Nacional
	AESN(2L, "Arquivo de eventos do Simples Nacional", "Arquivo eventos SN"),

	// 3 - Arquivo de Períodos do Simples Nacional
	APSN(3L, "Simples Nacional", "Arquivo per\u00edodos SN"),

	// 4 - Arquivo de eventos do SIMEI
	AES(4L, "Arquivo de eventos do SIMEI", "Arquivo eventos SIMEI"),

	// 5 - Arquivo de Períodos do SIMEI
	APS(5L, "SIMEI", "Arquivo per\u00edodos SIMEI"),

	// 6-
	APSNSIMEI(6L, "Simples Nacional / SIMEI", "Arquivo de eventos e per\u00edodos SIMEI e SN");

	private final Long codigo;
	private final String label;
	private final String nomeCampo;

	TipoArquivosRsnEnum(Long codigo, String label, String nomeCampo) {
		this.codigo = codigo;
		this.label = label;
		this.nomeCampo = nomeCampo;
	}

	public Long getCodigo() {
		return codigo;
	}

	public String getLabel() {
		return label;
	}

	public String getNomeCampo() {
		return nomeCampo;
	}

	public static TipoArquivosRsnEnum getEnum(Long codigo) {
		for (TipoArquivosRsnEnum enumVal : TipoArquivosRsnEnum.values()) {
			if (enumVal.codigo == codigo) {
				return enumVal;
			}
		}
		return null;
	}
}
