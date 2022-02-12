package br.com.proccarga.core.enumeration;

/**
 * Enumeração das menssagens utilizadas pela aplicação
 * 
 * @author Leandro Moraes
 */
public enum MensagemEnum {
	MSG663("MSG663", "Existem mais de um arquivo do mesmo tipo para serem processados: {0}."),
	MSG632("MSG632", "O arquivo {0} está com o nome fora do padrão esperado."),
	MSG208("MSG208", "Arquivo {0} já importado anteriormente."),
	MSG234("MSG234", "Arquivo sem conteúdo."),
	MSG081("MSG081", "Erro no processamento. {0}."),
	MSG665("MSG665", "Importação do {0} realizada com sucesso.");

	private final String label;
	private final String descricao;

	MensagemEnum(String label, String descricao) {
		this.label = label;
		this.descricao = descricao;
	}

	public String getLabel() {
		return label;
	}

	public String getDescricao() {
		return descricao;
	}
}
