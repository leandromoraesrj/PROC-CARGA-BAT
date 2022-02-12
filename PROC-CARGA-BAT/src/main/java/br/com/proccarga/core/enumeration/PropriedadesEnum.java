package br.com.proccarga.core.enumeration;

import br.com.proccarga.core.util.PropriedadesUtil;

/**
 * Enumeração dos propriedades da aplicação obtidas através de arquivos *.properties
 * 
 * @author Leandro Moraes
 */
public enum PropriedadesEnum {

  BANCO_DE_DADOS_SINCAD_SERVIDOR("bancodedados.proccarga.servidor"),
  BANCO_DE_DADOS_SINCAD_PORTA("bancodedados.proccarga.porta"),
  BANCO_DE_DADOS_SINCAD_SERVICO("bancodedados.proccarga.servico"),
  BANCO_DE_DADOS_SINCAD_USUARIO("bancodedados.proccarga.usuario"),
  BANCO_DE_DADOS_SINCAD_SENHA("bancodedados.proccarga.senha"),
   
  EMAIL_SMTP_ENDERECO("email.smtp.endereco"),
  EMAIL_SMTP_PORTA("email.smtp.porta"),
  EMAIL_DESTINATARIO("email.destinatario"),
  EMAIL_REMETENTE("email.remetente");

  private String nome;

  /**
   * Construtor
   * 
   * @param nome
   *          - Nome da propriedade
   */
  PropriedadesEnum(String nome) {
    this.nome = nome;
  }

  /**
   * Obtém o nome da propriedade
   * 
   * @return Nome da propriedade
   */
  public String getNome() {
    return nome;
  }

  /**
   * Obtém o valor da propriedade
   * 
   * @return Valor da propriedade
   */
  public String get() {
    return PropriedadesUtil.obterPropriedade(this.nome);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.get();
  }

}
