package com.emerfcouto.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.emerfcouto.cursomc.domain.Cliente;
import com.emerfcouto.cursomc.domain.ItemPedido;
import com.emerfcouto.cursomc.domain.PagamentoComBoleto;
import com.emerfcouto.cursomc.domain.Pedido;
import com.emerfcouto.cursomc.domain.enums.EstadoPagamento;
import com.emerfcouto.cursomc.repositories.ClienteRepository;
import com.emerfcouto.cursomc.repositories.ItemPedidoRepository;
import com.emerfcouto.cursomc.repositories.PagamentoRepository;
import com.emerfcouto.cursomc.repositories.PedidoRepository;
import com.emerfcouto.cursomc.repositories.ProdutoRepository;
import com.emerfcouto.cursomc.secutiry.UserSS;
import com.emerfcouto.cursomc.services.exceptions.AuthorizationException;
import com.emerfcouto.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		
		Pedido obj = repo.findOne(id);
		
		if(obj == null) {
			throw new ObjectNotFoundException("Objeto não encontrado! Id: " + id
					+ ", Tipo: " + Pedido.class.getName());
		}
		
		return obj;
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		
		obj.setCliente(clienteRepository.findOne(obj.getCliente().getId()));
		
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preenchePagamentoComBoleto(pagto, obj.getInstante());
		}
		
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for(ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoRepository.findOne(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		
		itemPedidoRepository.save(obj.getItens());
		
		emailService.sendOrderConfirmationHtmlEmail(obj);
		
		return obj;
	}
	
	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		
		UserSS user = UserService.authenticated();
		
		if(user == null) {
			
			throw new AuthorizationException("Acesso negado");
		}
		
		PageRequest pageRequest = new PageRequest(page, linesPerPage, Direction.valueOf(direction), orderBy);

		Cliente cli = clienteRepository.findOne(user.getId());
		
		return repo.findByCliente(cli, pageRequest);
	}
}
