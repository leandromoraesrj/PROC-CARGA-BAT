package br.com.proccarga.core.util;

import java.util.concurrent.TimeUnit;

/**
 * Classe utilária para determinar o tempo de execução da aplicação
 * 
 * @author Leandro Moraes
 */
public final class CronometroUtil {

	private long inicio;
	private long fim;

	/**
	 * Construtor
	 */
	private CronometroUtil() {
		resetar();
	}

	/**
	 * Inicia o cronômetro
	 * 
	 * @return Objeto representativo do cronômetro
	 */
	public static CronometroUtil iniciar() {
		return new CronometroUtil();
	}

	/**
	 * Reseta o cronômetro
	 * 
	 * @return Objeto representativo do cronômetro
	 */
	public CronometroUtil resetar() {
		inicio = System.nanoTime();
		fim = 0;
		return this;
	}

	/**
	 * Para o cronômetro
	 */
	public void parar() {
		fim = System.nanoTime();
	}

	/**
	 * Obtém os nanosegundos passados desde o início do cronômetro
	 * 
	 * @return Número de nanosegundos passados desde o início do cronômetro
	 */
	public long obterTempo() {
		return fim - inicio;
	}

	/**
	 * Obtém o tempo passado desde o início do cronômetro na unidade
	 * especificada
	 * 
	 * @param unidade
	 *            - Unidade de tempo desejada
	 * @return Número de unidades de tempo passadas desde o início do cronômetro
	 */
	public long obterTempo(TimeUnit unidade) {
		return unidade.convert(obterTempo(), TimeUnit.NANOSECONDS);
	}

	/**
	 * Obtém o tempo passado desde o início do cronômetro no formato "X h, X
	 * min, X s, X ms"
	 * 
	 * @return Tempo passado desde o início do cronômetro no formato "X h, X
	 *         min, X s, X ms"
	 */
	public String obterTempoFormatado() {

		final long horas = obterTempo(TimeUnit.HOURS);
		final long minutos = obterTempo(TimeUnit.MINUTES) % 60;
		final long segundos = obterTempo(TimeUnit.SECONDS) % 60;
		final long milisegundos = obterTempo(TimeUnit.MILLISECONDS) % 1000;

		final StringBuilder tempo = new StringBuilder().append(horas).append(" H, ").append(minutos).append(" MIN, ")
				.append(segundos).append(" S, ").append(milisegundos).append(" MS");

		return tempo.toString();

	}

}
