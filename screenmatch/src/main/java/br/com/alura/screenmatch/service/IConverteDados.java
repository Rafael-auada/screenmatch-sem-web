package br.com.alura.screenmatch.service;

public interface IConverteDados {
    // usando um generics pois assim passamos um tipo de dado, não sabendo qual, para a classe classe
    <T> T obterDados(String json, Class<T> classe);
}
