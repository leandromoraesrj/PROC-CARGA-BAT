package br.com.proccarga.core.util;

import java.util.Comparator;

import br.com.proccarga.sn.model.ArquivoRsn;

public class SortArquivoByName implements Comparator<ArquivoRsn> { 
	@Override
	public int compare(ArquivoRsn arquivo1, ArquivoRsn arquivo2) {
		return arquivo1.getNomeArquivo().compareTo(arquivo2.getNomeArquivo());
	} 
} 