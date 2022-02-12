package br.com.proccarga.sn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.proccarga.core.enumeration.BancoDeDadosEnum;
import br.com.proccarga.core.enumeration.SituacaoImportacaoArqRsnEnum;
import br.com.proccarga.core.enumeration.TipoArquivosRsnEnum;
import br.com.proccarga.core.jdbc.ConexaoJDBC;
import br.com.proccarga.sn.model.ArquivoRsn;
import br.com.proccarga.sn.model.ArquivoRsnMensagem;
import br.com.proccarga.sn.model.Mensagem;
import br.com.proccarga.sn.model.Parametro;

/**
 * Classe DAO utilizada pela aplicação para o processamento de arquivos do
 * simples nacional
 * 
 * @author Leandro Moraes
 */
public class ProcessamentoArquivoPeriodoDAO extends ConexaoJDBC {
	private static final Logger LOGGER = Logger.getLogger(ProcessamentoArquivoPeriodoDAO.class);

	private static final String OBTEM_PARAMETRO = "SELECT * FROM SCAD.PARAMETRO";
	private static final String CONSULTA_ARQUIVO_SITUACAO_VIGENTE = "SELECT * FROM SCAD.ARQUIVO_RSN A WHERE A.DH_IMPORTACAO = (SELECT MAX(B.DH_IMPORTACAO) FROM SCAD.ARQUIVO_RSN B WHERE UPPER(B.NO_ARQUIVO_RSN) = ?) AND UPPER(A.NO_ARQUIVO_RSN) = ?";
	private static final String ENVIA_EMAIL = "CALL SCAD.PK_COMUM.PR_ENVIA_EMAIL(?,?,?)";
	private static final String TRUNCA_TABELA_TEMPORARIA = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_TRUNCATE_TEMPORARIA(?)";
	private static final String ATUALIZA_SITUACAO_ARQUIVO = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_ATUALIZA_SITUACAO_ARQUIVO(?,?)";
	private static final String VALIDA_DADOS_ARQUIVO = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_VALIDA_DADOS_ARQUIVO_RSN(?)";
	private static final String OBTEM_MENSAGENS_ARQUIVO_REJEITADOS = "SELECT * FROM  SCAD.ARQUIVO_RSN_MENSAGEM A INNER JOIN SCAD.MENSAGEM M ON M.SQ_MENSAGEM = A.SQ_MENSAGEM WHERE A.SQ_ARQUIVO_RSN = ?";
	private static final String ATUALIZA_TOTALIZADOR = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_ATUALIZA_TOTALIZADOR(?,?)";
	private static final String PERSISTE_DADOS = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_PERSISTE_DADOS_ARQUIVO_RSN(?)";
	private static final String PROCESSA_ARQUIVOS_AGENDADOS = "CALL SCAD.PK_SCAD_PROCESSA_ARQUIVO_RSN.PR_PROCESSA_ARQUIVO_RSN_AGENDA()";
	private static final String PERSISTE_ARQUIVO_RSN = "INSERT INTO SCAD.ARQUIVO_RSN (SQ_ARQUIVO_RSN, SQ_SITUACAO_IMPORTACAO_ARQ_RSN, SQ_TIPO_ARQUIVO_RSN, NO_ARQUIVO_RSN) VALUES (SCAD.SE_ARQUIVO_RSN.NEXTVAL,?,?,?)";
	private static final String PROCESSA_EVENTO_390 = "CALL SCAD.PK_CTRL_PROCESSA_DAC_AUTO.PR_PROCESSA_BAIXA_IE_EVENTO390()";

	public void persistirArquivoRsn(ArquivoRsn arquivoRsn) throws SQLException {
		try (PreparedStatement statement = conexao.prepareStatement(PERSISTE_ARQUIVO_RSN)) {
			adicionarParametro(statement, 1, arquivoRsn.getSituacaoImportacao().getId(), java.sql.Types.NUMERIC);
			adicionarParametro(statement, 2, arquivoRsn.getTipoArquivoRsn().getId(), java.sql.Types.NUMERIC);
			adicionarParametro(statement, 3, arquivoRsn.getNomeArquivo(), java.sql.Types.VARCHAR);
			LOGGER.info(MessageFormat.format("Persistindo arquivo {0}", arquivoRsn.getNomeArquivo()));
			statement.executeUpdate();
			LOGGER.info("Arquivo persistido.");
		}
	}

	public Parametro obterParametros() throws SQLException {
		try (Statement statement = conexao.createStatement()) {
			try (ResultSet rs = statement.executeQuery(OBTEM_PARAMETRO)) {
				while (rs.next()) {
					Parametro pa = new Parametro();
					pa.setDiretorioArquivosMEI(rs.getString("DIR_ARQUIVOS_MEI"));
					pa.setDiretorioArquivosSN(rs.getString("DIR_ARQUIVOS_SN"));
					pa.setEmailTecnicoSATI(rs.getString("EMAIL_TECNICO_SATI"));
					pa.setEmailGestor(rs.getString("EMAIL_GESTOR"));
					pa.setNomeArquivoEventosSIMEI(rs.getString("NO_ARQ_EVENTOS_SIMEI"));
					pa.setNomeArquivoEventosSN(rs.getString("NO_ARQ_EVENTOS_SN"));
					pa.setNomeArquivoMei(rs.getString("NO_ARQ_MEI"));
					pa.setNomeArquivoPeriodoSIMEI(rs.getString("NO_ARQ_PER_SIMEI"));
					pa.setNomeArquivoPeriodoSN(rs.getString("NO_ARQ_PER_SN"));

					return pa;
				}
			}
		}

		return null;
	}

	public ArquivoRsn consultarArquivoSituacaoVigente(String name) throws SQLException {
		try (PreparedStatement statement = conexao.prepareStatement(CONSULTA_ARQUIVO_SITUACAO_VIGENTE)) {
			adicionarParametro(statement, 1, name, java.sql.Types.VARCHAR);
			adicionarParametro(statement, 2, name, java.sql.Types.VARCHAR);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					ArquivoRsn arq = new ArquivoRsn();
					arq.setId(rs.getLong("sq_arquivo_rsn"));

					arq.setSituacaoImportacao(
							SituacaoImportacaoArqRsnEnum.getEnum(rs.getLong("sq_situacao_importacao_arq_rsn")));
					arq.setTipoArquivoRsn(TipoArquivosRsnEnum.getEnum(rs.getLong("sq_tipo_arquivo_rsn")));

					arq.setNomeArquivo(rs.getString("no_arquivo_rsn"));

					arq.setCpfUsuario(rs.getString("nu_cpf_usuario"));

					return arq;
				}
			}
		}

		return null;
	}

	public List<ArquivoRsn> recuperarArquivosSituacao(String situacaoImportacao) throws SQLException {
		try (ResultSet rs = conexao.createStatement()
				.executeQuery(MessageFormat.format(
						"SELECT * FROM SCAD.ARQUIVO_RSN A WHERE A.SQ_SITUACAO_IMPORTACAO_ARQ_RSN IN ({0}) ORDER BY A.SQ_TIPO_ARQUIVO_RSN",
						situacaoImportacao))) {
			List<ArquivoRsn> lst = new ArrayList<>();

			while (rs.next()) {
				ArquivoRsn arq = new ArquivoRsn();
				arq.setId(rs.getLong("sq_arquivo_rsn"));

				arq.setSituacaoImportacao(
						SituacaoImportacaoArqRsnEnum.getEnum(rs.getLong("sq_situacao_importacao_arq_rsn")));
				arq.setTipoArquivoRsn(TipoArquivosRsnEnum.getEnum(rs.getLong("sq_tipo_arquivo_rsn")));

				arq.setNomeArquivo(rs.getString("no_arquivo_rsn"));

				arq.setCpfUsuario(rs.getString("nu_cpf_usuario"));

				lst.add(arq);
			}

			return lst;
		}
	}

	public void atualizarStatusSituacaoArquivo(ArquivoRsn arquivoRsn,
			SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum) throws SQLException {
		try (PreparedStatement statement = conexao.prepareCall(ATUALIZA_SITUACAO_ARQUIVO)) {
			adicionarParametro(statement, 1, arquivoRsn.getId(), java.sql.Types.NUMERIC);
			adicionarParametro(statement, 2, situacaoImportacaoArqRsnEnum.getCodigo(), java.sql.Types.NUMERIC);
			LOGGER.info(MessageFormat.format("Atualizando situação do arquivo `{0}` para {1}",
					arquivoRsn.getNomeArquivo(), situacaoImportacaoArqRsnEnum));
			statement.execute();
			LOGGER.info("Tabela alterada.");
		}
	}

	public void truncarTabelaTemporaria(TipoArquivosRsnEnum tipoArquivosRsnEnum) throws SQLException {
		try (PreparedStatement statement = conexao.prepareCall(TRUNCA_TABELA_TEMPORARIA)) {
			adicionarParametro(statement, 1, tipoArquivosRsnEnum.getCodigo(), java.sql.Types.NUMERIC);
			LOGGER.info(MessageFormat.format("Truncando tabela temporária do tipo `{0}`",
					tipoArquivosRsnEnum.getNomeCampo()));
			statement.execute();
			LOGGER.info("Tabela truncada.");
		}
	}

	public void validarDadosArquivosImportado(ArquivoRsn arq) throws SQLException {
		try (PreparedStatement statement = conexao.prepareCall(VALIDA_DADOS_ARQUIVO)) {
			adicionarParametro(statement, 1, arq.getId(), java.sql.Types.NUMERIC);
			LOGGER.info(MessageFormat.format("Validando dados do arquivo `{0}`", arq.getNomeArquivo()));
			statement.execute();
			LOGGER.info("Dados validados.");
		}
	}

	public List<ArquivoRsnMensagem> recuperarMensagemArquivosRejeitados(ArquivoRsn arquivoRsn) throws SQLException {
		try (PreparedStatement statement = conexao.prepareStatement(OBTEM_MENSAGENS_ARQUIVO_REJEITADOS)) {
			adicionarParametro(statement, 1, arquivoRsn.getId(), java.sql.Types.NUMERIC);
			try (ResultSet rs = statement.executeQuery()) {
				List<ArquivoRsnMensagem> lst = new ArrayList<>();

				while (rs.next()) {
					ArquivoRsnMensagem arqm = new ArquivoRsnMensagem(arquivoRsn,
							new Mensagem(rs.getString("CO_REFERENCIA"), rs.getString("TX_MENSAGEM")));
					arqm.setTextoComplementar(rs.getString("TX_COMPLEMENTO_MENSAGEM"));

					lst.add(arqm);
				}

				return lst;
			}
		}
	}

	public void atualizarTotalizador(ArquivoRsn arquivoRsn, float totalizador) throws SQLException {
		try (PreparedStatement statement = conexao.prepareCall(ATUALIZA_TOTALIZADOR)) {
			adicionarParametro(statement, 1, arquivoRsn.getId(), java.sql.Types.NUMERIC);
			adicionarParametro(statement, 2, totalizador, java.sql.Types.NUMERIC);
			LOGGER.info(MessageFormat.format("Atualizando totalizador `{0}` do arquivo `{1}`", totalizador,
					arquivoRsn.getNomeArquivo()));
			statement.execute();
			LOGGER.info("Totalizador atualizado.");
		}
	}

	public void persistirDadosTabelaTemporaria(ArquivoRsn arquivoRsn) throws SQLException {
		try (PreparedStatement statement = conexao.prepareCall(PERSISTE_DADOS)) {
			adicionarParametro(statement, 1, arquivoRsn.getId(), java.sql.Types.NUMERIC);
			LOGGER.info(MessageFormat.format("Persistindo dados do arquivo {0} localizados na tabela temporária",
					arquivoRsn.getNomeArquivo()));
			statement.execute();
			LOGGER.info("Dados do arquivo persistido.");
		}
	}

	public void processarRegistrosAgendados() throws Exception {
		try (PreparedStatement statement = conexao.prepareCall(PROCESSA_ARQUIVOS_AGENDADOS)) {
			LOGGER.info("Processando arquivo agendados");
			statement.execute();
			LOGGER.info("Arquivos agendados processados.");
		}
	}

	public void processarEvento390() throws Exception {
		try (PreparedStatement statement = conexao.prepareCall(PROCESSA_EVENTO_390)) {
			LOGGER.info("Processando evento 390.");
			statement.execute();
			LOGGER.info("Evento 390 processado.");
		}
	}

	public void enviarEmail(String destinatario, String assunto, String mensagem) {
		try (PreparedStatement statement = conexao.prepareCall(ENVIA_EMAIL)) {
			adicionarParametro(statement, 1, destinatario, java.sql.Types.VARCHAR);
			adicionarParametro(statement, 2, assunto, java.sql.Types.VARCHAR);
			adicionarParametro(statement, 3, mensagem, java.sql.Types.VARCHAR);
			LOGGER.info(MessageFormat.format("Enviando email para {0} com o assunto {1}", destinatario, assunto));
			statement.execute();
			LOGGER.info("Email enviado.");
		} catch (SQLException e) {
			LOGGER.info("Problema ao enviar email", e);
		}
	}

	public Connection getConnection() {
		return conexao;
	}

	public boolean conectar() {
		return super.conectar(BancoDeDadosEnum.CARGA);
	}

	/**
	 * Adiciona um parâmetro na declaração SQL informada
	 * 
	 * @param statement
	 *            - Declaração SQL onde será inserido o parâmetro
	 * @param posicaoParametro
	 *            - Posição do parâmetro dentro da declaração SQL, começando em
	 *            1
	 * @param parametro
	 *            - Parâmetro que será inserido
	 * @param tipoSQL
	 *            - Tipo SQL do parâmetro que será inserido
	 * @throws SQLException
	 */
	private static void adicionarParametro(PreparedStatement statement, int posicaoParametro, Object parametro,
			int tipoSQL) throws SQLException {

		if (parametro != null) {
			if (java.sql.Types.TIMESTAMP == tipoSQL) {
				statement.setTimestamp(posicaoParametro, java.sql.Timestamp.valueOf((LocalDateTime) parametro));
			} else if (java.sql.Types.DATE == tipoSQL) {
				java.util.Date data = (java.util.Date) parametro;
				// statement.setDate(posicaoParametro,
				// Date.valueOf(data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));

				statement.setDate(posicaoParametro, new java.sql.Date(data.getTime()));
			} else {
				statement.setObject(posicaoParametro, parametro, tipoSQL);
			}
		} else {
			statement.setNull(posicaoParametro, tipoSQL);
		}

	}

}
