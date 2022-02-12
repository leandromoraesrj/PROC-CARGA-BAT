package br.com.proccarga.core.exception;

/**
 * Classe que representa uma exceção de parâmetros insuficientes ao executar a
 * aplicação
 * 
 * @author Leandro Moraes
 */
public final class ParametrosInsuficientesException extends RuntimeException {

	private static final long serialVersionUID = 6735169881284026415L;

	/**
	 * Construtor
	 * 
	 * @param message
	 *            - Mensagem da exceção
	 */
	public ParametrosInsuficientesException(String message) {
		super(message);
	}

}
