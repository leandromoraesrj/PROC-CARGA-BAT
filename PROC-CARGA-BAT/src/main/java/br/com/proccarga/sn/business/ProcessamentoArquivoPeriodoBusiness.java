package br.com.proccarga.sn.business;

import java.util.concurrent.RejectedExecutionException;

import org.apache.log4j.Logger;

/**
 * Classe business utilizada pela aplicação para o processamento de arquivos do
 * simples nacional
 * 
 * @author Leandro Moraes
 */
public class ProcessamentoArquivoPeriodoBusiness extends ImportacaoArquivoPeriodoBusiness {
	private static final Logger LOGGER = Logger.getLogger(ProcessamentoArquivoPeriodoBusiness.class);

	/**
	 * Inicia o processamento
	 * 
	 * @return Objeto representativo do processamento
	 * @throws Exception
	 */
	public void iniciaProcessamentoSN() throws Exception {
		try {
			if (dao.conectar()) {
				LOGGER.info("Inicio da importação dos arquivos SNMEI");
				inicializaProcessamento();

				LOGGER.info("Inicio da validação dos arquivos SNMEI");
				validaArquivosNaoProcessados();
				LOGGER.info("Fim da validação dos arquivos SNMEI");

				LOGGER.info("Inicio da importação dos arquivos SNMEI");
				importaArquivosValidados();
				LOGGER.info("Fim da importação dos arquivos SNMEI");

				LOGGER.info("Inicio do processamento dos importados SNMEI");
				persisteRegistrosImportados();
				LOGGER.info("Fim do  processamento dos importados SNMEI");

				LOGGER.info("Inicio de processamento dos registros agendados SNMEI");
				processaRegistrosAgendados();
				LOGGER.info("Fim de processamento dos registros agendados SNMEI");

				LOGGER.info("Inicio do processamento dos Eventos 390");
				processaEvento390();
				LOGGER.info("Fim do processamentos dos Eventos 390");
			}
		} catch (RejectedExecutionException e) {
			LOGGER.info("Processamento ainda em execução no banco de dados");
		} finally {
			LOGGER.info("Fim da importação dos arquivos SNMEI");
		}
	}
}
