package br.com.proccarga.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Classe utilária para carregar e ler arquivos de propriedades
 * 
 * @author Leandro Moraes
 */
public final class PropriedadesUtil {

	private static final Logger LOGGER = Logger.getLogger(PropriedadesUtil.class);

	private static final Properties PROPERTIES = new Properties();

	/**
	 * Construtor privado vazio para evitar que a classe seja indevidamente /
	 * desnecessariamente instanciada
	 */
	private PropriedadesUtil() {
	}

	/**
	 * Carrega as propriedades do arquivo especificado localizado no diretório
	 * de configuração default
	 * 
	 * @param arquivoProperties
	 *            - Nome do arquivo de propriedades que será carregado
	 */
	public static void carregarPropriedades(String arquivoProperties) {
		try (InputStream propriedades = Files
				.newInputStream(Paths.get(Constantes.DIRETORIO_CONFIGURACAO, arquivoProperties))) {
			PROPERTIES.load(propriedades);
		} catch (FileNotFoundException e) {
			LOGGER.warn("NAO FOI POSSIVEL LOCALIZAR O ARQUIVO DE PROPRIEDADES " + arquivoProperties, e);
		} catch (IOException e) {
			LOGGER.warn("NAO FOI POSSIVEL CARREGAR O ARQUIVO DE PROPRIEDADES " + arquivoProperties, e);
		}
	}

	/**
	 * Carrega as propriedades do arquivo especificado localizado no diretório
	 * correspondente ao ambiente de execução informado
	 * 
	 * @param ambienteExecucao
	 *            - Ambiente de execução (diretório) onde o arquivo está
	 *            localizado
	 * @param arquivoProperties
	 *            - Nome do arquivo de propriedades que será carregado
	 */
	public static void carregarPropriedades(String ambienteExecucao, String arquivoProperties) {
		try (InputStream propriedades = Files
				.newInputStream(Paths.get(Constantes.DIRETORIO_CONFIGURACAO, ambienteExecucao, arquivoProperties))) {
			PROPERTIES.load(propriedades);
		} catch (FileNotFoundException e) {
			LOGGER.warn("NAO FOI POSSIVEL LOCALIZAR O ARQUIVO DE PROPRIEDADES " + arquivoProperties, e);
		} catch (IOException e) {
			LOGGER.warn("NAO FOI POSSIVEL CARREGAR O ARQUIVO DE PROPRIEDADES " + arquivoProperties, e);
		}
	}

	/**
	 * Obtém o valor da propriedade informada
	 * 
	 * @param nomePropriedade
	 *            - Nome da propriedade desejada
	 * @return Valor da propriedade (nulo quando o nome da propriedade não
	 *         existe)
	 */
	public static String obterPropriedade(String nomePropriedade) {
		return PROPERTIES.getProperty(nomePropriedade);
	}

}
