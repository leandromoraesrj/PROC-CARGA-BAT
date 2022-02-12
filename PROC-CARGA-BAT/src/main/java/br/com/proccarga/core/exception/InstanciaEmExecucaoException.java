package br.com.proccarga.core.exception;

/**
 * Classe que representa uma exceção de instância em execução
 * 
 * @author Leandro Moraes
 */
public final class InstanciaEmExecucaoException extends RuntimeException {

	private static final long serialVersionUID = 8095059492321348232L;

	/**
	 * Construtor
	 * 
	 * @param message
	 *            - Mensagem da exceção
	 */
	public InstanciaEmExecucaoException(String message) {
		super(message);
	}

}
