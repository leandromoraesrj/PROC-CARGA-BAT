package br.com.proccarga.core.util;

import java.io.File;

/**
 * Constantes utilizadas pela aplicação
 * 
 * @author Leandro Moraes
 */
public final class Constantes {

	/**
	 * Output utilizado para criar "seções" no log
	 */
	public static final String BATCH_SEPARADOR_LOG = "------------------------------------------------------------------";

	/**
	 * Código de status (erro) utilizado pela aplicação quando não são
	 * informados todos os parâmetros
	 */
	public static final int CODIGO_ERRO_PARAMETROS_INSUFICIENTES = 1;

	/**
	 * Código de status (erro) utilizado pela aplicação quando INSTANCIA ESTIVER
	 * EM EXECUÇÃO
	 */
	public static final int CODIGO_ERRO_INSTANCIA_EM_EXECUCAO = 2;

	/**
	 * Código de status (erro) utilizado pela aplicação quando ocorre uma
	 * exceção fatal não mapeada a nível de negócio
	 */
	public static final int CODIGO_ERRO_EXCECAO_NAO_MAPEADA = 3;

	/**
	 * Quantidade de parâmetros exigidos para a execução da aplicação
	 */
	public static final int QTDE_PARAMETROS_MINIMA = 2;

	/**
	 * Diretório com os arquivos de configuração (properties) da aplicação
	 */
	public static final String DIRETORIO_CONFIGURACAO = "config" + File.separator;

	/**
	 * Nome do arquivo de propriedades principal da aplicação
	 */
	public static final String PROPRIEDADES_CONFIGURACAO = "application.properties";

	/**
	 * Construtor privado vazio para evitar que a classe seja indevidamente /
	 * desnecessariamente instanciada
	 */
	private Constantes() {
	}

}
