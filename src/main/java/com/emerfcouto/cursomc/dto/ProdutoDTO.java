package com.emerfcouto.cursomc.dto;

import java.io.Serializable;

import com.emerfcouto.cursomc.domain.Produto;

public class ProdutoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer Id;
	private String nome;
	private Double preco;
	
	public ProdutoDTO() {
	}

	public ProdutoDTO(Produto obj) {
		this.Id = obj.getId();
		this.nome = obj.getNome();
		this.preco = obj.getPreco();
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}
}
