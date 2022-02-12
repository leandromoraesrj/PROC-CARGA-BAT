package br.com.proccarga.core.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.com.proccarga.core.exception.InstanciaEmExecucaoException;

/**
 * Classe utilária para validar, através de um arquivo, se uma instância está em execução
 * 
 * @author Leandro Moraes
 */
public final class ValidacaoUtil {
    private ValidacaoUtil() {
	}
	
	  private static final Logger LOGGER = Logger.getLogger(ValidacaoUtil.class);
	  
	  /**
	   * Valida se já existe um processo para a instância informada
	   */
	  public static final void validarExecucaoInstancia(String instancia) {

	    if (StringUtils.isEmpty(instancia)) {
	      throw new InstanciaEmExecucaoException("INSTANCIA NAO INFORMADA");
	    }

	    try {	    	
	      Files.createFile(Paths.get(instancia + ".lock")).toFile().deleteOnExit();
	    } catch (FileAlreadyExistsException e) {
	      throw new InstanciaEmExecucaoException("JA EXISTE UMA INSTANCIA EM EXECUCAO PARA ESSA TAREFA");
	    } catch (IOException | SecurityException e) {
	      throw new InstanciaEmExecucaoException("NAO FOI POSSIVEL DETERMINAR SE A INSTANCIA ESTA EM EXECUCAO");
	    }

	    LOGGER.info("NAO EXISTE INSTANCIA EM EXECUCAO PARA ESSA TAREFA");

	  }
}
