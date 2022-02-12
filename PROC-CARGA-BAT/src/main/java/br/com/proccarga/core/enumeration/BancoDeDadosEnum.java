package br.com.proccarga.core.enumeration;

/**
 * Enumeração dos bancos de dados utilizados pela aplicação
 * 
 * @author Leandro Moraes
 */
public enum BancoDeDadosEnum {

	/**
	 * Banco de dados Oracle com acesso ao schema
	 * 
	 * @author Leandro Moraes
	 */
	SINCAD {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * br.gov.rj.sef.sincad.core.enumeration.BancoDeDadosEnum#getServidor()
		 */
		@Override
		public String getServidor() {
			return PropriedadesEnum.BANCO_DE_DADOS_SINCAD_SERVIDOR.get();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * br.gov.rj.sef.sincad.core.enumeration.BancoDeDadosEnum#getPorta()
		 */
		@Override
		public String getPorta() {
			return PropriedadesEnum.BANCO_DE_DADOS_SINCAD_PORTA.get();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * br.gov.rj.sef.sincad.core.enumeration.BancoDeDadosEnum#getServico()
		 */
		@Override
		public String getServico() {
			return PropriedadesEnum.BANCO_DE_DADOS_SINCAD_SERVICO.get();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * br.gov.rj.sef.sincad.core.enumeration.BancoDeDadosEnum#getUsuario()
		 */
		@Override
		public String getUsuario() {
			return PropriedadesEnum.BANCO_DE_DADOS_SINCAD_USUARIO.get();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * br.gov.rj.sef.sincad.core.enumeration.BancoDeDadosEnum#getSenha()
		 */
		@Override
		public String getSenha() {
			return PropriedadesEnum.BANCO_DE_DADOS_SINCAD_SENHA.get();
		}
	};

	/**
	 * Obtém o servidor da conexão com o banco de dados
	 * 
	 * @return Servidor da conexão com o banco de dados
	 */
	public abstract String getServidor();

	/**
	 * Obtém a porta de conexão com o banco de dados
	 * 
	 * @return Porta de conexão com o banco de dados
	 */
	public abstract String getPorta();

	/**
	 * Obtém o serviço da conexão com o banco de dados
	 * 
	 * @return Serviço da conexão com o banco de dados
	 */
	public abstract String getServico();

	/**
	 * Obtém o usuário de conexão com o banco de dados
	 * 
	 * @return Usuário de conexão com o banco de dados
	 */
	public abstract String getUsuario();

	/**
	 * Obtém a senha de conexão com o banco de dados
	 * 
	 * @return Senha de conexão com o banco de dados
	 */
	public abstract String getSenha();

}
