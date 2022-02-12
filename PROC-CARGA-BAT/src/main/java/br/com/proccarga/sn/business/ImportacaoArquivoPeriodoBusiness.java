package br.com.proccarga.sn.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import br.com.proccarga.core.enumeration.MensagemEnum;
import br.com.proccarga.core.enumeration.SituacaoImportacaoArqRsnEnum;
import br.com.proccarga.core.enumeration.TipoArquivosRsnEnum;
import br.com.proccarga.core.util.SortArquivoByName;
import br.com.proccarga.sn.dao.ProcessamentoArquivoPeriodoDAO;
import br.com.proccarga.sn.model.ArquivoRsn;
import br.com.proccarga.sn.model.ArquivoRsnMensagem;
import br.com.proccarga.sn.model.Parametro;

public class ImportacaoArquivoPeriodoBusiness {
	private static final Logger logger = Logger.getLogger(ImportacaoArquivoPeriodoBusiness.class);

	protected ProcessamentoArquivoPeriodoDAO dao = new ProcessamentoArquivoPeriodoDAO();

	public static final String INSERT_APSN = "INSERT INTO SCAD.PER_SN_SIMEI ( " + " NU_LINHA, " + " NU_RAIZ_CNPJ, "
			+ " DT_INICIO_PERIODO, " + " DT_FIM_PERIODO, " + " ID_CANCELAMENTO, " + " NU_OPCAO, " + " TX_OBSERVACAO "
			+ " ) VALUES (?,?,?,?,?,?,?) ";

	public static final String INSERT_APS = "INSERT INTO SCAD.PERMEI_SN_SIMEI ( " + " NU_LINHA, " + " NU_RAIZ_CNPJ, "
			+ " DT_INICIO_PERIODO, " + " DT_FIM_PERIODO, " + " ID_CANCELAMENTO, " + " NU_OPCAO, " + " TX_OBSERVACAO, "
			+ " NU_OPCAO_SIMEI " + " ) VALUES (?,?,?,?,?,?,?,?) ";
	
	public static final String INSERT_AESN = "INSERT INTO SCAD.TMP_ARQUIVO_EVENTO_RFB ( " + " NU_LINHA_ARQ, " + " NU_RAIZ_CNPJ, " 
			+ " NU_OPCAO_SN, " + " DT_EFEITO_EVENTO_RFB, " + " CO_EVENTO_RFB, " + " DH_OCORRENCIA_RFB " + ") VALUES (?,?,?,?,?,?) ";

	private static final String REGEX_DATA = ".*-(19[7-9]\\d|20\\d{2})(0[1-9]|1[0-2])(0\\d|1\\d|2\\d|3[0-1])\\.txt";

	private static final String ASSUNTO_EMAIL = "Simples Nacional/SIMEI e MEI";
	private static final String CORPO_EMAIL = "<html><head><meta charset=`UTF-8`></head><body>Início do processamento: <b>{1}</b><br/>{0}<br/>Fim do processamento: <b>{2}</b></body></html>";

	private static final SimpleDateFormat ddMMyyyyHHmmss = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

	private static final int maxQuantidadeLinhaLidas = 1000000;

	private Parametro parametro;

	private Date dataInicio;

	private Map<String, TipoArquivosRsnEnum> tipoArquivoRsnMap;
	private Map<TipoArquivosRsnEnum, String> queryArquivoRsnMap;

	private Path diretorioProcessar;
	private Path diretorioImportado;
	private Path diretorioProcessado;
	private Path diretorioRejeitado;
	private Path diretorioValidado;

	private String diretorioArquivo;

	private enum NomeDiretorioArquivosEnum {
		NAO_PROCESSADOS, IMPORTADOS, PROCESSADOS, REJEITADOS, VALIDADOS
	}

	private void obterDadosDeInicializacao() throws SQLException {

		dataInicio = new Date();
		parametro = dao.obterParametros();

		diretorioArquivo = parametro.getDiretorioArquivosSN();
	}

	private void criarDiretorios() throws Exception {

		diretorioProcessar = Paths.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.NAO_PROCESSADOS);
		diretorioImportado = Paths.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.IMPORTADOS);

		Path diretorioProcessadoRaiz = Paths
				.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.PROCESSADOS);

		diretorioProcessado = Paths.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.PROCESSADOS
				+ File.separator + yyyy.format(dataInicio));
		diretorioRejeitado = Paths.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.REJEITADOS);
		diretorioValidado = Paths.get(diretorioArquivo + File.separator + NomeDiretorioArquivosEnum.VALIDADOS);

		gerenciaDiretorio(diretorioProcessar);
		gerenciaDiretorio(diretorioImportado);
		gerenciaDiretorio(diretorioProcessadoRaiz);
		gerenciaDiretorio(diretorioProcessado);
		gerenciaDiretorio(diretorioRejeitado);
		gerenciaDiretorio(diretorioValidado);
	}

	private void gerenciaDiretorio(Path folder) throws Exception {

		if (!Files.isDirectory(folder)) {
			throw new Exception(MessageFormat.format(
					"Não foi possível criar o diretório, existe um arquivo com o mesmo nome no caminho especificado: {0}",
					folder.toAbsolutePath().toString()));
		}

		try {
			Files.createDirectory(folder);
		} catch (FileAlreadyExistsException e) {
			logger.info(MessageFormat.format("Diretório {0} já existia ou foi criado com sucesso",
					folder.toAbsolutePath().toString()));
		}

	}

	private void inicializaInstancias() {

		tipoArquivoRsnMap = new HashMap<>();
		queryArquivoRsnMap = new HashMap<>();

		tipoArquivoRsnMap.put(parametro.getNomeArquivoPeriodoSN(), TipoArquivosRsnEnum.APSN);
		tipoArquivoRsnMap.put(parametro.getNomeArquivoPeriodoSIMEI(), TipoArquivosRsnEnum.APS);
		tipoArquivoRsnMap.put(parametro.getNomeArquivoEventosSN(), TipoArquivosRsnEnum.AESN);

		queryArquivoRsnMap.put(TipoArquivosRsnEnum.APSN, INSERT_APSN);
		queryArquivoRsnMap.put(TipoArquivosRsnEnum.APS, INSERT_APS);
		queryArquivoRsnMap.put(TipoArquivosRsnEnum.AESN, INSERT_AESN);
	}

	private boolean existeArquivo(String fileName, File folderDest) {
		for (String fileNameDest : folderDest.list()) {
			if (fileNameDest.equalsIgnoreCase(fileName)) {
				return true;
			}
		}
		return false;
	}

	private boolean verificaArquivoEmUso(File file) {
		return !file.renameTo(file);
	}

	private void moveArquivos(File folderSource, File folderDest) throws Exception {

		if (folderSource == null || !folderSource.exists()) {
			logger.warn("Objeto nulo ou diretório/arquivo de origem não existe.");
			return;
		}

		if (folderDest == null || !folderDest.exists()) {
			logger.warn("Objeto nulo ou diretório/arquivo de destino não existe.");
			return;
		}

		boolean sobrescrever = folderDest.equals(diretorioValidado.toFile())
				|| folderDest.equals(diretorioProcessado.toFile());
		boolean manterCopia = folderDest.equals(diretorioRejeitado.toFile());

		for (File fileSource : folderSource.isDirectory() ? folderSource.listFiles() : new File[] { folderSource }) {

			int qntCopias = 0;

			String fileNameDest = fileSource.getName();

			if (sobrescrever && existeArquivo(fileNameDest, folderDest)) {

				FileUtils.deleteQuietly(new File(folderDest.getPath() + File.separator + fileNameDest));
			}

			if (manterCopia) {

				String fileNamePrincipal = fileNameDest;

				while (existeArquivo(fileNameDest, folderDest)) {
					qntCopias++;
					String ext[] = fileNamePrincipal.split("\\.");
					fileNameDest = ext[0].concat("(").concat(Integer.toString(qntCopias)).concat(").").concat(ext[1]);
				}
			}

			FileUtils.moveFile(fileSource, new File(folderDest.getPath() + File.separator + fileNameDest));
			logger.info(MessageFormat.format("Arquivo `{0}` movido para o diretório {1}", fileSource.getName(),
					folderDest.getName()));
		}
	}

	private long importarArquivo(ArquivoRsn arquivoRsn) throws SQLException, IOException, ParseException {

		logger.info(MessageFormat.format("Importando o arquivo `{0}`", arquivoRsn.getNomeArquivo()));

		// TODO para evitar alterar o código orignal da carga do arquivo, será
		// usada a conexão da DAO
		Connection cc = dao.getConnection();

		String query = queryArquivoRsnMap.get(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum());

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(arquivoRsn.getArquivoFisico().getAbsolutePath()),
				Charset.forName("UTF-8")); PreparedStatement preparedStatement = cc.prepareStatement(query)) {

			String linha;
			long nuLinha = 0;

			while ((linha = reader.readLine()) != null) {
				
				if(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum().equals(TipoArquivosRsnEnum.AESN)) {
					nuLinha = parseArquivoEventosSN(arquivoRsn, preparedStatement, linha, nuLinha);
				}else  {
					nuLinha = parseArquivoPeriodosSN(arquivoRsn, preparedStatement, linha, nuLinha);
				}

				preparedStatement.addBatch();

				if (nuLinha % maxQuantidadeLinhaLidas == 0) {
					preparedStatement.executeBatch();
					preparedStatement.clearBatch();

					logger.info(MessageFormat.format("Arquivo `{0}` importado até a linha {1}",
							arquivoRsn.getNomeArquivo(), nuLinha));
				}
			}

			if (nuLinha % maxQuantidadeLinhaLidas != 0) {
				preparedStatement.executeBatch();

				logger.info(MessageFormat.format("Arquivo `{0}` importado até a linha {1}", arquivoRsn.getNomeArquivo(),
						nuLinha));
			}

			logger.info(MessageFormat.format("Arquivo `{0}` importado com sucesso. ", arquivoRsn.getNomeArquivo()));

			return nuLinha;
		}
	}

	private long parseArquivoEventosSN(ArquivoRsn arquivoRsn, PreparedStatement preparedStatement, String linha,
			long nuLinha) throws ParseException, SQLException {
		final String raizCnpj = linha.substring(0, 8);
		final String coEvento = linha.substring(9, 12);
		final Date dataEfeito = yyyyMMdd.parse(linha.substring(20, 28));
		final String nuOpcao = linha.substring(380, 389);
		final Date dataOcorrencia = yyyyMMddHHmmss.parse(linha.substring(366, 380));
		nuLinha++;

		preparedStatement.clearParameters();

		preparedStatement.setLong(1, nuLinha);
		preparedStatement.setString(2, raizCnpj);
		preparedStatement.setString(3, nuOpcao);
		preparedStatement.setDate(4, new java.sql.Date(dataEfeito.getTime()));
		preparedStatement.setString(5, coEvento);
		preparedStatement.setTimestamp(6, new java.sql.Timestamp(dataOcorrencia.getTime()));

		return nuLinha;
	}

	private long parseArquivoPeriodosSN(ArquivoRsn arquivoRsn, PreparedStatement preparedStatement, String linha, long nuLinha)
			throws ParseException, SQLException {
		final String raizCnpj = linha.substring(0, 8);
		final Date dataInicio = yyyyMMdd.parse(linha.substring(8, 16));
		final Date dataFim = yyyyMMdd.parse(linha.substring(16, 24));
		final String cancelamento = linha.substring(24, 25);
		final String opcao = linha.substring(25, 34);

		final String opcaoSimei = arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum()
				.equals(TipoArquivosRsnEnum.APS) ? linha.substring(34, 43) : "";

		final String txMensagem = "";

		nuLinha++;

		preparedStatement.clearParameters();

		// Numero da linha
		preparedStatement.setLong(1, nuLinha);

		// Raiz CNPJ
		preparedStatement.setString(2, raizCnpj);

		// Data Inicio
		if (dataInicio.after(new Date(0))) {
			preparedStatement.setDate(3, new java.sql.Date(dataInicio.getTime()));
		} else {
			preparedStatement.setNull(3, Types.DATE);
		}

		// Data Fim
		if (dataFim.after(new Date(0))) {
			preparedStatement.setDate(4, new java.sql.Date(dataFim.getTime()));
		} else {
			preparedStatement.setNull(4, Types.DATE);
		}

		// Cancelamento
		preparedStatement.setString(5, cancelamento);

		// Opção
		preparedStatement.setLong(6, Long.parseLong(opcao));

		preparedStatement.setString(7, txMensagem);

		// Opção Simei
		if (!opcaoSimei.isEmpty()) {
			preparedStatement.setString(8, opcaoSimei);
		}
		return nuLinha;
	}

	public void inicializaProcessamento() throws Exception {
		obterDadosDeInicializacao();
		criarDiretorios();
		inicializaInstancias();
	}

	public void validaArquivosNaoProcessados() throws Exception {

		List<ArquivoRsn> arquivoRsnList = listaArquivosParaValidacao();

		if (arquivoRsnList.isEmpty()) {
			logger.info("Nenhum arquivo para validação foi encontrado");
		}

		Collections.sort(arquivoRsnList, new SortArquivoByName());
		
		for (ArquivoRsn arquivoRsn : arquivoRsnList) {

			try {

				// FA01 - Verificar tipo de arquivo a ser carregado
				if (!verificaArquivoMesmoTipo(arquivoRsn, arquivoRsnList)) {
					rejeitaArquivo(
							MessageFormat.format(MensagemEnum.MSG663.getDescricao(), arquivoRsn.getNomeArquivo()),
							arquivoRsn.getArquivoFisico());
					continue;
				}

				// FA02 - Arquivos do Simples Nacional com nome incorreto
				if (!validaNomeArquivo(arquivoRsn)) {
					rejeitaArquivo(
							MessageFormat.format(MensagemEnum.MSG632.getDescricao(), arquivoRsn.getNomeArquivo()),
							arquivoRsn.getArquivoFisico());
					continue;
				}

				// FA03 - Arquivo do Simples Nacional de mesmo nome já
				// cadastrado
				if (!validaSituacaoArquivo(arquivoRsn)) {
					rejeitaArquivo(
							MessageFormat.format(MensagemEnum.MSG208.getDescricao(), arquivoRsn.getNomeArquivo()),
							arquivoRsn.getArquivoFisico());
					continue;
				}

				// FA05 - Arquivo não possui conteúdo
				if (!validaTamanhoArquivo(arquivoRsn)) {
					rejeitaArquivo(
							MessageFormat.format(MensagemEnum.MSG234.getDescricao(), arquivoRsn.getNomeArquivo()),
							arquivoRsn.getArquivoFisico());
					continue;
				}

				moveArquivos(arquivoRsn.getArquivoFisico(), diretorioValidado.toFile());

				dao.persistirArquivoRsn(arquivoRsn);

				logger.info(MessageFormat.format("Arquivo `{0}` validado e aguardando importação",
						arquivoRsn.getNomeArquivo()));

			} catch (Exception e) {
				rejeitaArquivo(MessageFormat.format(MensagemEnum.MSG081.getDescricao(), e.getLocalizedMessage()),
						arquivoRsn.getArquivoFisico());
				throw e;
			}
		}
	}

	private boolean verificaArquivoMesmoTipo(ArquivoRsn arquivoRsn1, List<ArquivoRsn> arquivoRsnList) {

		for (ArquivoRsn arquivoRsn2 : arquivoRsnList) {
			if (arquivoRsn1.getNomeArquivo().equals(arquivoRsn2.getNomeArquivo())
					|| arquivoRsn1.getTipoArquivoRsn() == null || arquivoRsn2.getTipoArquivoRsn() == null) {
				continue;
			}

			if (arquivoRsn1.getTipoArquivoRsn().getId().equals(arquivoRsn2.getTipoArquivoRsn().getId())) {
				return false;
			}
		}

		return true;
	}

	private boolean validaTamanhoArquivo(ArquivoRsn arquivoRsn) {
		return arquivoRsn.getArquivoFisico().length() != 0;
	}

	private boolean validaNomeArquivo(ArquivoRsn arquivoRsn) {

		String fileName = arquivoRsn.getArquivoFisico().getName();

		return isArquivoSN(fileName) && fileName.matches(REGEX_DATA);
	}

	private boolean validaSituacaoArquivo(ArquivoRsn arquivoRsn) throws SQLException {

		ArquivoRsn arq = dao.consultarArquivoSituacaoVigente(arquivoRsn.getNomeArquivo().toUpperCase());

		return arq == null || arq.getSituacaoImportacao().getSituacaoImportacaoArqRsnEnum()
				.equals(SituacaoImportacaoArqRsnEnum.REJEITADO);
	}

	private boolean isArquivoSN(String fileName) {
		return obterTipoArquivosRsn(fileName) != null;
	}

	private List<ArquivoRsn> listaArquivosParaValidacao() {

		List<ArquivoRsn> arquivoRsnList = new ArrayList<>();

		for (File file : diretorioProcessar.toFile().listFiles()) {

			if (verificaArquivoEmUso(file)) {
				continue;
			}

			String fileName = file.getName();

			TipoArquivosRsnEnum tipoArquivosRsnEnum = obterTipoArquivosRsn(fileName);

			ArquivoRsn arquivoRsn = new ArquivoRsn();

			if (tipoArquivosRsnEnum != null) {
				arquivoRsn.setTipoArquivoRsn(tipoArquivosRsnEnum);
			}

			arquivoRsn.setNomeArquivo(file.getName());
			arquivoRsn.setArquivoFisico(file);

			arquivoRsn.setSituacaoImportacao(SituacaoImportacaoArqRsnEnum.VALIDADO);

			arquivoRsnList.add(arquivoRsn);
		}

		return arquivoRsnList;
	}

	private TipoArquivosRsnEnum obterTipoArquivosRsn(String fileName) {
		for (String nomeArquivo : tipoArquivoRsnMap.keySet()) {

			if (fileName.toUpperCase().contains(nomeArquivo)) {
				return tipoArquivoRsnMap.get(nomeArquivo);
			}
		}

		return null;
	}

	private Map<TipoArquivosRsnEnum, ArquivoRsn> recuperarArquivoFisicoArquivoRsn(List<ArquivoRsn> arquivoRsnList,
			List<File> filesList, boolean rejeitarSeNaoEncontrado) throws Exception {

		boolean arquivoInvalido = true;

		Map<TipoArquivosRsnEnum, ArquivoRsn> arquivoRsnMap = new HashMap<>();

		for (ArquivoRsn arquivoRsn : arquivoRsnList) {

			for (File file : filesList) {
				if (file.getName().equalsIgnoreCase(arquivoRsn.getNomeArquivo())) {
					arquivoRsn.setArquivoFisico(file);
					arquivoRsnMap.put(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum(), arquivoRsn);
					arquivoInvalido = false;
					logger.info(
							MessageFormat.format("Arquivo `{0}` encontrado no diretório", arquivoRsn.getNomeArquivo()));
				}
			}

			if (arquivoInvalido && rejeitarSeNaoEncontrado) {
				rejeitaArquivo(
						MessageFormat.format("Arquivo `{0}` não encontrado no diretório", arquivoRsn.getNomeArquivo()),
						arquivoRsn);
				break;
			}
		}

		return arquivoRsnMap;
	}

	public List<ArquivoRsn> recuperarArquivosSituacao(SituacaoImportacaoArqRsnEnum... situacaoImportacaoArqRsnEnumList)
			throws SQLException {

		List<Long> situacaoImportacaoArqList = new ArrayList<>();

		StringBuilder situacaoImportacaoArqRsnStringBuilder = new StringBuilder();

		for (SituacaoImportacaoArqRsnEnum situacaoImportacaoArqRsnEnum : situacaoImportacaoArqRsnEnumList) {

			situacaoImportacaoArqList.add(situacaoImportacaoArqRsnEnum.getCodigo());

			if (situacaoImportacaoArqList.size() > 1) {
				situacaoImportacaoArqRsnStringBuilder.append(", ");
			}

			situacaoImportacaoArqRsnStringBuilder.append(situacaoImportacaoArqRsnEnum.getCodigo());
		}

		return dao.recuperarArquivosSituacao(situacaoImportacaoArqRsnStringBuilder.toString());
	}

	public void importaArquivosValidados() throws Exception {

		List<ArquivoRsn> arquivoRsnList = recuperarArquivosSituacao(SituacaoImportacaoArqRsnEnum.VALIDADO,
				SituacaoImportacaoArqRsnEnum.IMPORTANDO);

		Map<TipoArquivosRsnEnum, ArquivoRsn> arquivoRsnMap = recuperarArquivoFisicoArquivoRsn(arquivoRsnList,
				Arrays.asList(diretorioValidado.toFile().listFiles()), true);
		
		if (arquivoRsnMap.isEmpty()) {
			logger.info("Nenhum arquivo para importação encontrado");
		}

		for (ArquivoRsn arquivoRsn : arquivoRsnMap.values()) {
			try {
				dao.truncarTabelaTemporaria(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum());
				importaArquivoValidado(arquivoRsn);
			} catch (Exception e) {
				rejeitaArquivo(MessageFormat.format(MensagemEnum.MSG081.getDescricao(), e.getLocalizedMessage()),
						arquivoRsn);
			}
		}

		// Limpar todos os arquivos que não foram necessários
		moveArquivos(diretorioValidado.toFile(), diretorioRejeitado.toFile());
	}

	private void importaArquivoValidado(ArquivoRsn arquivoRsn) throws Exception {

		dao.atualizarStatusSituacaoArquivo(arquivoRsn, SituacaoImportacaoArqRsnEnum.IMPORTANDO);

		long nuLinha = importarArquivo(arquivoRsn);

		dao.validarDadosArquivosImportado(arquivoRsn);

		List<ArquivoRsnMensagem> arquivoRsnMensagemList = dao.recuperarMensagemArquivosRejeitados(arquivoRsn);

		if (arquivoRsnMensagemList != null && !arquivoRsnMensagemList.isEmpty()) {
			rejeitaArquivo(arquivoRsnMensagemList, arquivoRsn);
		} else {
			moveArquivos(arquivoRsn.getArquivoFisico(), diretorioImportado.toFile());
			dao.atualizarTotalizador(arquivoRsn, nuLinha);
			arquivoRsn.setSituacaoImportacao(SituacaoImportacaoArqRsnEnum.IMPORTADO);
			dao.atualizarStatusSituacaoArquivo(arquivoRsn, SituacaoImportacaoArqRsnEnum.IMPORTADO);
			
		}
	}

	public void persisteRegistrosImportados() throws Exception {

		List<ArquivoRsn> arquivoRsnList = recuperarArquivosSituacao(SituacaoImportacaoArqRsnEnum.IMPORTADO,
				SituacaoImportacaoArqRsnEnum.PROCESSANDO);

		Map<TipoArquivosRsnEnum, ArquivoRsn> arquivoRsnMap = recuperarArquivoFisicoArquivoRsn(arquivoRsnList,
				Arrays.asList(diretorioImportado.toFile().listFiles()), false);

		TreeMap<TipoArquivosRsnEnum, ArquivoRsn> arquivosRsnMapSorted = ordenaArquivosImportados(arquivoRsnMap);
		
		for (ArquivoRsn arquivoRsn : arquivosRsnMapSorted.values()) {
			try {
				dao.persistirDadosTabelaTemporaria(arquivoRsn);
				dao.enviarEmail(parametro.getEmailGestor(), ASSUNTO_EMAIL,
						formataCorpoEmail(MessageFormat.format(MensagemEnum.MSG665.getDescricao(), arquivoRsn.getNomeArquivo())));
				moveArquivos(arquivoRsn.getArquivoFisico(), diretorioProcessado.toFile());
			} catch (Exception e) {
				rejeitaArquivo(MessageFormat.format(MensagemEnum.MSG081.getDescricao(), e.getLocalizedMessage()),
						arquivoRsn);
			}
		}

		// Limpar todos os arquivos que não foram necessários
		moveArquivos(diretorioImportado.toFile(), diretorioRejeitado.toFile());
	}
	
	//Ordeno os arquivos importados para que os arquivos PER sejam carregados antes dos PERMEI
	private TreeMap<TipoArquivosRsnEnum, ArquivoRsn> ordenaArquivosImportados(
			Map<TipoArquivosRsnEnum, ArquivoRsn> arquivoRsnMap) {
		TreeMap<TipoArquivosRsnEnum, ArquivoRsn> arquivosOrdenados = new TreeMap<>();
		TreeMap<TipoArquivosRsnEnum, ArquivoRsn> arquivosOrdenadosPermei = new TreeMap<>();

		for (ArquivoRsn arquivoRsn : arquivoRsnMap.values()) {
			if(arquivoRsn.getNomeArquivo().contains("PERMEI")){
				arquivosOrdenadosPermei.put(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum(), arquivoRsn);
			}else{
				arquivosOrdenados.put(arquivoRsn.getTipoArquivoRsn().getTipoArquivosRsnEnum(), arquivoRsn);
			}
		}
		arquivosOrdenados.putAll(arquivosOrdenadosPermei);
		
		return arquivosOrdenados;
	}

	public void processaRegistrosAgendados() throws Exception {
		dao.processarRegistrosAgendados();
	}

	private void rejeitaArquivo(List<ArquivoRsnMensagem> mensagemList, ArquivoRsn arquivoRsn) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();

		for (ArquivoRsnMensagem arquivoRsnMensagem : mensagemList) {

			String mensagenFormatada = arquivoRsnMensagem.getMensagenFormatada();

			logger.error(mensagenFormatada);
			stringBuilder.append(mensagenFormatada);
			stringBuilder.append("<br/>");
		}

		dao.atualizarStatusSituacaoArquivo(arquivoRsn, SituacaoImportacaoArqRsnEnum.REJEITADO);
		moveArquivos(arquivoRsn.getArquivoFisico(), diretorioRejeitado.toFile());
		dao.enviarEmail(parametro.getEmailGestor(), ASSUNTO_EMAIL, formataCorpoEmail(stringBuilder.toString()));
	}

	private void rejeitaArquivo(String mensagem, ArquivoRsn arquivoRsn) throws Exception {
		dao.atualizarStatusSituacaoArquivo(arquivoRsn, SituacaoImportacaoArqRsnEnum.REJEITADO);
		moveArquivos(arquivoRsn.getArquivoFisico(), diretorioRejeitado.toFile());
		logger.error(mensagem);
		dao.enviarEmail(parametro.getEmailGestor(), ASSUNTO_EMAIL, formataCorpoEmail(mensagem));
	}

	private void rejeitaArquivo(String mensagem, File file) throws Exception {
		logger.warn(mensagem);
		moveArquivos(file, diretorioRejeitado.toFile());
		dao.enviarEmail(parametro.getEmailGestor(), ASSUNTO_EMAIL, formataCorpoEmail(mensagem));
	}

	private String formataCorpoEmail(String mensagem) {
		return MessageFormat.format(CORPO_EMAIL, mensagem, ddMMyyyyHHmmss.format(dataInicio),
				ddMMyyyyHHmmss.format(new Date()));
	}

	protected void processaEvento390() throws Exception {
		dao.processarEvento390();
	}
}
