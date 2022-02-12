package br.com.proccarga.core.main;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.com.proccarga.core.exception.InstanciaEmExecucaoException;
import br.com.proccarga.core.exception.ParametrosInsuficientesException;
import br.com.proccarga.core.util.Constantes;
import br.com.proccarga.core.util.CronometroUtil;
import br.com.proccarga.core.util.PropriedadesUtil;
import br.com.proccarga.core.util.ValidacaoUtil;
import br.com.proccarga.sn.business.ProcessamentoArquivoPeriodoBusiness;

/**
 * Classe principal da aplicação
 * 
 * Mode de uso: java -jar ./PROC-CARGA-BAT-1.0.0.jar local teste
 * OBS:a pasta confirma tem que estar no mesmo local do .JAR
 *
 * @author Leandro Moraes
 */
public final class ProcCargaJobMain {

	private static final Logger LOGGER = Logger.getLogger(ProcCargaJobMain.class);

	static {
		/*
		 * <!> Define um UUID na thread para facilitar a busca nos logs destacando
		 * unicamente cada execução
		 */
		Thread.currentThread().setName(UUID.randomUUID().toString());
	}

	/**
	 * Método main
	 * 
	 */
	public static void main(String[] args) {
		String ambienteExecucao;
		String identificadorInstancia;
		int status = 0;

		CronometroUtil cronometro = CronometroUtil.iniciar();

		try {

			if (args.length < Constantes.QTDE_PARAMETROS_MINIMA)
				throw new ParametrosInsuficientesException("PARAMETROS NAO INFORMADOS");

			ambienteExecucao = args[0];
			identificadorInstancia = args[1];

			PropertyConfigurator.configure("config/" + ambienteExecucao + "/log4j.properties");

			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);
			LOGGER.info("INICIANDO O JOB");
			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);

			PropriedadesUtil.carregarPropriedades(ambienteExecucao, Constantes.PROPRIEDADES_CONFIGURACAO);

			ValidacaoUtil.validarExecucaoInstancia(identificadorInstancia);

			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);

			new ProcessamentoArquivoPeriodoBusiness().iniciaProcessamentoSN();

		} catch (InstanciaEmExecucaoException e) {

			LOGGER.error("JA EXISTE UMA INSTANCIA EM EXECUCAO PARA ESSA TAREFA");
			status = Constantes.CODIGO_ERRO_INSTANCIA_EM_EXECUCAO;

		} catch (ParametrosInsuficientesException e) {

			LOGGER.error("PARAMETROS INSUFICIENTES PARA EXECUCAO DO JOB");
			status = Constantes.CODIGO_ERRO_PARAMETROS_INSUFICIENTES;

		} catch (Exception e) {

			LOGGER.error("EXCECAO FATAL NAO MAPEADA A NIVEL DE NEGOCIO", e);
			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);
			status = Constantes.CODIGO_ERRO_EXCECAO_NAO_MAPEADA;

		} finally {

			cronometro.parar();

			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);
			LOGGER.info("JOB EXECUTADO - TEMPO DE EXECUCAO: " + cronometro.obterTempoFormatado());
			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);

			System.exit(status);
		}
	}
}
