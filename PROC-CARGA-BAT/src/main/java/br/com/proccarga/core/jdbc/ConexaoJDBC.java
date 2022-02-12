package br.com.proccarga.core.jdbc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import br.com.proccarga.core.enumeration.BancoDeDadosEnum;
import br.com.proccarga.core.util.Constantes;

/**
 * Fábrica de conexões utilizada pela aplicação
 * 
 * @author Leandro Moraes
 */
public abstract class ConexaoJDBC {
	protected Connection conexao = null;
	private static final Logger LOGGER = Logger.getLogger(ConexaoJDBC.class);

	/**
	 * Efetua uma conexão com o banco de dados informado
	 * 
	 * @param banco
	 *            - Banco de dados desejado
	 * @return Indicador de conexão estabelecida
	 */
	protected boolean conectar(BancoDeDadosEnum banco) {
		try {
			if (conexao == null) {
				StringBuilder recurso = new StringBuilder().append("jdbc:oracle:thin:@")
						.append(InetAddress.getByName(banco.getServidor()).getHostAddress()).append(":")
						.append(banco.getPorta()).append("/").append(banco.getServico());
				LOGGER.info("CONECTANDO AO RECURSO: " + recurso);
				LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);
				conexao = DriverManager.getConnection(recurso.toString(), banco.getUsuario(), banco.getSenha());
			}
		} catch (UnknownHostException | SQLException e) {
			LOGGER.warn("NAO FOI POSSIVEL INICIAR A CONEXAO COM O BANCO DE DADOS", e);
			LOGGER.info(Constantes.BATCH_SEPARADOR_LOG);
		}

		return conexao != null;
	}
}
