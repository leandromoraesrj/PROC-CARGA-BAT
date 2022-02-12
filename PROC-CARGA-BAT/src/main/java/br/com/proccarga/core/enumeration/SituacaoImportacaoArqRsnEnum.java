package br.com.proccarga.core.enumeration;

/**
 * Enumeração dos status da importação/processamento do arquivo
 * 
 * @author Leandro Moraes
 */
public enum SituacaoImportacaoArqRsnEnum {
	
	RAIZ(0L), //Não utilizar
	IMPORTADO(1L),
	REJEITADO(2L),
	PROCESSADO(3L),
	VALIDADO(4L),
	IMPORTANDO(5L),
	PROCESSANDO(6L);
	
	private Long codigo;
	
	SituacaoImportacaoArqRsnEnum(Long codigo){
		this.codigo = codigo;		
	}
	
	
	public Long getCodigo() {
		return codigo;
	}


	public static SituacaoImportacaoArqRsnEnum getEnum(Long codigo){
		for (SituacaoImportacaoArqRsnEnum enumVal : SituacaoImportacaoArqRsnEnum.values()) {
			if(enumVal.getCodigo() == codigo){
				return enumVal;
			}			
		}
		return null;
	}
}
