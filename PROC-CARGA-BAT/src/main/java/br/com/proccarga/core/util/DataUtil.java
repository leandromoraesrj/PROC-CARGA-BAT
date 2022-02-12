package br.com.proccarga.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Classe utilária para a manipulação de datas e tempo
 * 
 * @author Leandro Moraes
 */
public final class DataUtil {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("uuuuMMdd")
			.withResolverStyle(ResolverStyle.STRICT);

	private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
			.withResolverStyle(ResolverStyle.STRICT);

	/**
	 * Construtor privado vazio para evitar que a classe seja indevidamente /
	 * desnecessariamente instanciada
	 */
	private DataUtil() {
	}

	/**
	 * Converte um objeto do tipo texto no formato "uuuuMMdd" para um objeto do
	 * tipo data
	 * 
	 * @param data
	 *            - Texto no formato "uuuuMMdd"
	 * @return Objeto do tipo data
	 */
	public static LocalDate converterParaLocalDate(String data) {
		return converterParaLocalDate(data, FORMATO_DATA);
	}

	/**
	 * Converte um objeto do tipo texto no formato especificado para um objeto
	 * do tipo data
	 * 
	 * @param data
	 *            - Texto no formato especificado
	 * @param formatoData
	 *            - Formato da data
	 * @return Objeto do tipo data
	 */
	public static LocalDate converterParaLocalDate(String data, DateTimeFormatter formatoData) {
		return LocalDate.parse(data, formatoData);
	}

	/**
	 * Converte um objeto do tipo texto no formato "uuuuMMddHHmmss" para um
	 * objeto do tipo data hora
	 * 
	 * @param dataHora
	 *            - Texto no formato "uuuuMMddHHmmss"
	 * @return Objeto do tipo data hora
	 */
	public static LocalDateTime converterParaLocalDateTime(String dataHora) {
		return converterParaLocalDateTime(dataHora, FORMATO_DATA_HORA);
	}

	/**
	 * Converte um objeto do tipo texto no formato especificado para um objeto
	 * do tipo data hora
	 * 
	 * @param dataHora
	 *            - Texto no formato especificado
	 * @param formatoDataHora
	 *            - Formato da data
	 * @return Objeto do tipo data hora
	 */
	public static LocalDateTime converterParaLocalDateTime(String dataHora, DateTimeFormatter formatoDataHora) {
		return LocalDateTime.parse(dataHora, formatoDataHora);
	}

}
